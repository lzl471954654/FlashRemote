package com.lp.flashremote.utils;


import android.util.Log;

import com.lp.flashremote.SocketInterface;
import com.lp.flashremote.beans.PackByteArray;
import com.lp.flashremote.beans.PropertiesUtil;
import com.lp.flashremote.beans.UserInfo;
import com.lp.flashremote.fragments.Remote_Pc_Fragment;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

import NewVersion.ProtocolField;
import Utils.DataUtil;

public class SocketUtil extends Thread{
    private Socket mSocket;
    public InputStream socketInput;
    private OutputStream socketOutput;
    private String username;
    private String password;

    private static LinkedBlockingDeque<PackByteArray> messageQueue;
    private EventBus mEventBus;

    private boolean threadStopState = false; //为true则终止，为false则继续

    private boolean mConnOk = false;

    private static SocketUtil mSocketUtil;

    private SocketUtil(String u, String pwd) {
        this.username = u;
        this.password = pwd;
         messageQueue = new LinkedBlockingDeque<>(1024);
    }


    public static SocketUtil getInstance(String u, String p) {
        if (mSocketUtil == null) {
            mSocketUtil = new SocketUtil(u, p);
        }
        return mSocketUtil;
    }

    public static SocketUtil getInstance() {
        return getInstance(UserInfo.getUsername(), UserInfo.getPassword());
    }

    public void clearSocketCon() {
        mSocketUtil = null;
        socketInput = null;
        socketOutput = null;
        if (messageQueue != null) {
            messageQueue.clear();
            messageQueue = null;
        }
        if (mSocket != null) {
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
                newLoop();//开启消息队列
            } catch (IOException e) {
                e.printStackTrace();
                setmConnOk(false);
                Remote_Pc_Fragment.connisok(false);
            }
            Log.e("Thread-exit", "exit");
        } else {
            Log.e("init", "11111111111111");
            setmConnOk(false);
            Remote_Pc_Fragment.connisok(mConnOk);
        }
    }

    private boolean initConn() {
        boolean conn_ok = false;
        try {
            Log.e("ip", PropertiesUtil.SERVER_IP);
            mSocket = new Socket(PropertiesUtil.SERVER_IP, 10086);

            OutputStream outputStream = mSocket.getOutputStream();
            InputStream is = mSocket.getInputStream();
            socketInput = is;
            socketOutput = outputStream;

            byte[] loginData = (username + "|" + password).getBytes("UTF-8");

            if (loginData.length == 0)
                return false;
            socketOutput.write(ProtocolField.phoneOnline);
            socketOutput.write(IntConvertUtils.getShortByByteArray(loginData));
            socketOutput.write(loginData);

            byte result = (byte) socketInput.read();
            if (result == ProtocolField.onlineSuccess) {

                mEventBus = EventBus.getDefault();
                mEventBus.register(this);

                conn_ok = true;
            }
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
            e.printStackTrace();
        }
        return conn_ok;
    }

    private void newLoop() throws IOException {
        while (!isInterrupted()) {
            if (getThreadState())
                break;
            if (messageQueue.isEmpty()) {
                PackByteArray pack = messageQueue.remove();
                socketOutput.write(pack.getFlag());
                socketOutput.write(pack.getLen());
                if (pack.getBody() != null)
                    socketOutput.write(pack.getBody());
                socketOutput.flush();
            }
        }
    }

    /**
     * 发送测试命令给 pc
     *
     * @return 是否可以发送
     */

    private PackByteArray testPack=null;
    public void sendTestMsg(ConnectListener connectListener){
       if (!mConnOk){
           connectListener.connectError();
           return;
       }
        byte[] bytes={0,0};
       PackByteArray pack=new PackByteArray(ProtocolField.isConn,bytes,null);
       addMessageHighLevel(pack);


       Thread thread=new Thread(new Runnable() {
           @Override
           public void run() {
               try {
                   testPack=read();
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
       });
       thread.start();

        try {
            Thread.sleep(1000);
            if (testPack.getFlag()== ProtocolField.isConn ) {
                connectListener.connectSusess();
            } else {
                connectListener.connectError();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * 如果是文件那么就分发到服务中执行，如果是命令就直接执行
     */

    public PackByteArray read() throws IOException {

        while (true) {
            byte flag = (byte) socketInput.read();
            int mark = DataUtil.getType(flag);
            if (mark == 1 || mark == 3) {//是文件操作，直接分发到服务线程中操作
                byte[] msgSizeBytes = new byte[2];
                int len = socketInput.read(msgSizeBytes);
                //System.out.println(len);
                byte[] body = newReadLine(msgSizeBytes);
                PackByteArray pack = new PackByteArray(flag,msgSizeBytes, body);
                mEventBus.post(pack);
            } else {     //文件以外的其他操作
                byte[] msgSizeBytes = new byte[2];
                int len = socketInput.read(msgSizeBytes);
                //System.out.println(len);
                byte[] body = newReadLine(msgSizeBytes);
                return new PackByteArray(flag,msgSizeBytes, body);
            }
        }
    }


    private byte[] newReadLine(byte[] msgSizeBytes) throws IOException {
        int msgSize;
        msgSize = IntConvertUtils.getShortByByteArray(msgSizeBytes);

        if (msgSize > 0) {
            int i = 0;
            byte[] body = new byte[msgSize];
            while (i < msgSize) {
                body[i] = (byte) socketInput.read();
                i++;
            }
            return body;
        }
        return null;
    }


    public void addMessage(PackByteArray pack){
        if (messageQueue!=null)
            messageQueue.addLast(pack);
    }


    public void addMessageHighLevel(PackByteArray pack){
        if (messageQueue!=null)
            messageQueue.addFirst(pack);
    }

    private boolean getThreadState() {
        return threadStopState;
    }

    public void setThreadStop() {
        try {
            if (mSocket != null)
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

    private void setmConnOk(boolean mConnOk) {
        this.mConnOk = mConnOk;
    }

    public boolean getmConnOk() {
        return mConnOk;
    }

}
