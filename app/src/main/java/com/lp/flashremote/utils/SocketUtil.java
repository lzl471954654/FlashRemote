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
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class SocketUtil extends Thread {
    private Socket mSocket;
    private PrintWriter writer;
    public InputStream socketInput;
    public OutputStream socketOutput;
    public BufferedReader reader;
    private String username;
    private String password;
    private static Queue<String> mSendMessaggeQueue;
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
        writer = null;
        reader = null;
        socketInput = null;
        socketOutput = null;
        mSendMessaggeQueue.clear();
        mSendMessaggeQueue = null;
    }

    @Override
    public void run() {
        super.run();
        if (initConn()) {
            loop();//开启消息队列
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
            writer = new PrintWriter(new OutputStreamWriter(outputStream));
            reader = new BufferedReader(new InputStreamReader(inputStream));
            writer.println(StringUtil.stringAddUnderline(ServerProtocol.CONNECTED_TO_USER,
                    username, password, ServerProtocol.END_FLAG));
            writer.flush();
            String result = readLine(reader);
            if (StringUtil.startAndEnd(result)) {
                conn_ok = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return conn_ok;
    }

    private void loop() {
        while (!isInterrupted()) {
            if (getThreadState()) {
                break;
            }

            if (!mSendMessaggeQueue.isEmpty()) {
                String cmd = mSendMessaggeQueue.remove();
                writer.println(StringUtil.addEnd_flag2Str(cmd));
                writer.flush();

            }
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

        writer.println(StringUtil.addEnd_flag2Str(StringUtil
                .operateCmd(Command2JsonUtil.getJson("-1", null, true))));
        writer.flush();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                result = readLine(reader);
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
     * @param reader
     * @return
     */

    public String readLine(BufferedReader reader) {
        StringBuilder sb = new StringBuilder();
        String temp = "";
        try {
            while (!(temp = reader.readLine()).endsWith(ServerProtocol.END_FLAG)) {
                sb.append(temp);
            }
            sb.append(temp);
        } catch (IOException e) {
            System.out.println("读取数据失败。。。");
            e.printStackTrace();
        }
        return sb.toString();
    }


    public void addMessage(String s) {
        mSendMessaggeQueue.add(s);
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
