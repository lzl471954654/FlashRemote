package com.lp.flashremote.utils;


import android.util.Log;

import com.lp.flashremote.beans.PropertiesUtil;
import com.lp.flashremote.beans.UserInfo;
import com.lp.flashremote.fragments.Remote_Pc_Fragment;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

    private boolean mConnOk=false;


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

    public static SocketUtil getInstance(){
        return getInstance(UserInfo.getUsername(),UserInfo.getPassword());
    }

    public void clearSocketCon() {
        mSocketUtil = null;
        socketInput = null;
        socketOutput = null;
        if(mSendMessaggeQueue!=null){
            mSendMessaggeQueue.clear();
            mSendMessaggeQueue = null;
        }
        if(mSocket!=null){
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        if (initConn()) {
            try {
                setmConnOk(true);
                Remote_Pc_Fragment.connisok(mConnOk);
                loop();//开启消息队列
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                setmConnOk(false);
                Remote_Pc_Fragment.connisok(false);
            }
            Log.e("Thread-exit", "exit");
        } else {
            Log.e("init","11111111111111");
            setmConnOk(false);
            Remote_Pc_Fragment.connisok(mConnOk);
        }
    }

    private boolean initConn() {
        boolean conn_ok = false;
        try {
            Log.e("ip",PropertiesUtil.SERVER_IP);
            mSocket = new Socket(PropertiesUtil.SERVER_IP, 10086);

            OutputStream outputStream = mSocket.getOutputStream();
            InputStream inputStream = mSocket.getInputStream();
            socketInput = inputStream;
            socketOutput = outputStream;
            String logonString = StringUtil.stringAddUnderline(PropertiesUtil.CONNECTED_TO_USER,
                    username, password, PropertiesUtil.END_FLAG);
            byte[] bytes = logonString.getBytes();
            socketOutput.write(IntConvertUtils.getIntegerBytes(bytes.length));
            socketOutput.write(bytes);
            String result = readLine();
            if (StringUtil.startAndEnd(result)) {
                conn_ok = true;
            }
        } catch (Exception e) {
            Log.e("Exception",e.getMessage());
            e.printStackTrace();
        }
        return conn_ok;
    }

    private void loop() throws IOException,InterruptedException {
        while (!isInterrupted()) {
            if (getThreadState()) {
                break;
            }

            Thread.sleep(50);
            synchronized (mSendMessaggeQueue){
                if (!mSendMessaggeQueue.isEmpty()) {
                    byte[] bytes = mSendMessaggeQueue.remove();
                    socketOutput.write(bytes);
                    socketOutput.flush();
                }
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
        if(!mConnOk){
            connectListener.connectError();
            return;
        }
        addMessage(StringUtil
                .operateCmd(Command2JsonUtil.getJson("-1", "", true)));
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                result = readLine();
            }
        });
        thread.start();
        try {
            Thread.sleep(1000);
            Log.e("result",result);
            if (result.equals(StringUtil.addEnd_flag2Str(PropertiesUtil.CONNECTED_SUCCESS))) {
                connectListener.connectSusess();
            } else {
                Log.e("111111111","2222222222");
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
            if(msgSize<=0&&msgSize>=40*1024){
                setThreadStop();
                setmConnOk(false);
                interrupt();
                return "";
            }

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
        synchronized (mSendMessaggeQueue){
            if(mSendMessaggeQueue!=null)
                mSendMessaggeQueue.add(bytes);
        }
    }

    public void addMessage(String s) {
        synchronized (mSendMessaggeQueue){
            if(mSendMessaggeQueue==null)
                return;
            s = StringUtil.addEnd_flag2Str(s);
            Log.e("addMessage",s);
            try {
                byte[] stringData = s.getBytes("UTF-8");
                mSendMessaggeQueue.add(getIntegerBytes(stringData.length));
                mSendMessaggeQueue.add(stringData);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
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
            if(mSocket!=null)
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

    public void setmConnOk(boolean mConnOk) {
        this.mConnOk = mConnOk;
    }

    public boolean getmConnOk() {
        return mConnOk;
    }

}
