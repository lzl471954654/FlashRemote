package com.lp.flashremote.utils;


import android.util.Log;

import com.lp.flashremote.beans.ServerProtocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class SocketUtil extends Thread {
    private Socket mSocket;
    public InputStream socketInput;
    public OutputStream socketOutput;
    private String username;
    private String password;
    private static Queue<byte[]> mSendMessaggeQueue;
    private boolean threadStopState = false; //为true则终止，为false则继续

    private static SocketUtil mSocketUtil;

    private SocketUtil(String u, String pwd) {
        this.username = u;
        this.password = pwd;
        mSendMessaggeQueue = new LinkedList<>();
    }

    public static SocketUtil getInstance(String u, String p) {
        if (mSocketUtil == null) {
            mSocketUtil = new SocketUtil(u, p);
        }
        return mSocketUtil;
    }

    public void clearSocketCon() {
        mSocketUtil = null;
        socketInput = null;
        socketOutput = null;
        mSendMessaggeQueue.clear();
        mSendMessaggeQueue = null;
    }

    @Override
    public void run() {
        super.run();
        if (initConn()) {
            try {
                loop();//开启消息队列
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.e("Thread-exit", "exit");
        } else {

        }
    }

    @Override
    public synchronized void start() {
        super.start();
    }

    private boolean initConn() {
        boolean conn_ok = false;
        try {
            mSocket = new Socket(ServerProtocol.SERVER_IP, 10086);
            OutputStream outputStream = mSocket.getOutputStream();
            InputStream inputStream = mSocket.getInputStream();
            socketInput = inputStream;
            socketOutput = outputStream;
            String logonString = StringUtil.stringAddUnderline(ServerProtocol.CONNECTED_TO_USER,
                    username, password, ServerProtocol.END_FLAG);
            byte[] bytes = logonString.getBytes();
            socketOutput.write(IntConvertUtils.getIntegerBytes(bytes.length));
            socketOutput.write(bytes);
            String result = readLine();
            if (StringUtil.startAndEnd(result)) {
                conn_ok = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return conn_ok;
    }

    private void loop() throws IOException {
        while (!isInterrupted()) {
            if (getThreadState()) {
                break;
            }

            if (!mSendMessaggeQueue.isEmpty()) {
                byte[] bytes = mSendMessaggeQueue.remove();
                socketOutput.write(bytes);
                socketOutput.flush();
            }
            socketOutput.flush();
        }
    }

    /**
     * 发送测试命令给 pc
     *
     * @param t
     * @return 是否可以发送
     */
    private String result = "";

    public void sendTestMessage(ConnectListener connectListener) {

        addMessage(StringUtil.addEnd_flag2Str(StringUtil
                .operateCmd(Command2JsonUtil.getJson("-1", null, true))));
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                result = readLine();
            }
        });
        thread.start();
        try {
            Thread.sleep(250);
            if (result.equals(StringUtil.addEnd_flag2Str(ServerProtocol.OK))) {
                connectListener.connectSusess();
            } else {
                connectListener.connectError();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取流中的字符
     *
     */

    public String readLine() {
        String s = "";
        try {
            int msgSize = 0;
            byte[] msgSizeBytes = new byte[4];
            socketInput.read(msgSizeBytes);
            msgSize = IntConvertUtils.getIntegerByByteArray(msgSizeBytes);


            int i = 0;
            byte[] dataBytes = new byte[msgSize];
            while(i<msgSize){
                dataBytes[i] = (byte)socketInput.read();
                i++;
            }

            s = new String(dataBytes);
            Log.e("pppppppppppppp",s);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }


    public void addBytes(byte[] bytes){
        mSendMessaggeQueue.add(bytes);
    }

    public void addMessage(String s) {
        s = StringUtil.addEnd_flag2Str(s);
        try {
            byte[] stringData = s.getBytes("UTF-8");
            mSendMessaggeQueue.add(getIntegerBytes(stringData.length));
            mSendMessaggeQueue.add(stringData);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public byte[] getIntegerBytes(int data){
        byte[] s = {(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff};
        s[3] = (byte)((data)&s[3]);
        for(int i = 2;i>=0;i--){
            data = data>>8;
            s[i] = (byte)((data)&s[i]);
        }
        return s;
    }

    public boolean getThreadState() {
        return threadStopState;
    }

    public void setThreadStop() {
        try {
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        threadStopState = true;
    }

    public interface ConnectListener {
        void connectSusess();

        void connectError();
    }
}
