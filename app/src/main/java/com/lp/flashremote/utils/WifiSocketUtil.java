package com.lp.flashremote.utils;

import android.text.TextUtils;

import com.lp.flashremote.beans.PropertiesUtil;
import com.lp.flashremote.beans.ServerProtocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

import static com.lp.flashremote.utils.IntConvertUtils.getIntegerBytes;

/**
 * Created by xiyou3g on 2017/9/19.
 * 同一网络情况下，使用的socket工具类
 *
 */

public class WifiSocketUtil extends Thread {
    /**
     * 1 两个loop
     * 2 两个socket,逻辑判断使用哪个
     * 3 一次是否开启一个线程？？？
     * @return
     */

    private static InputStream inputStream;
    private static OutputStream outputStream;
    private String hotIP;

    private String startflag;//无参构造创造一个服务端socket
    private static final String SERVER="SS";
    private static final String CLIENT="CS";

    private static Queue<byte[]> mSendMsgQueue;

    public WifiSocketUtil(){
        startflag=SERVER;
    }

    public WifiSocketUtil(String flag){
        if (TextUtils.isEmpty(flag)){
            hotIP=flag;
            startflag=CLIENT;
            mSendMsgQueue=new LinkedList<>();
        }
    }


    @Override
    public synchronized void start() {
        super.start();
    }

    @Override
    public void run() {
        super.run();
        if (startflag.equals(CLIENT)){
            if (initSocketClient(hotIP)){
                clientloop();
            }
        }else if (startflag.equals(SERVER)){
            if (initSocketServer()){
                serverloop();
            }
        }
    }

    /**
     * 被扫码端开启的loop
     *
     */
    private static void serverloop(){

        while (!interrupted()){
            String messagefromclient=readString();
            if (!messagefromclient.endsWith(ServerProtocol.END_FLAG)){
                continue;
            }
            if (!mSendMsgQueue.isEmpty()){

                try {
                    outputStream.write(mSendMsgQueue.remove());
                    outputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }



    /**
     * 扫码端开启的loop
     */
    private static void clientloop(){
        while (!interrupted()){
            String s=readString();
            //if ()
        }
    }


    private boolean initSocketServer(){
        boolean flag=false;
        try {
            ServerSocket serverSocket=new ServerSocket(10085);
            Socket socket=serverSocket.accept();
            inputStream=socket.getInputStream();
            outputStream=socket.getOutputStream();

            if (readString().equals(PropertiesUtil.HELLOSERVER)){
                byte[] helloClent= PropertiesUtil.HELLOCLIENT.getBytes("UTF-8");
                outputStream.write(getIntegerBytes(helloClent.length));
                outputStream.write(helloClent);
                flag=true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flag;
    }

    private boolean initSocketClient(String ip){
        boolean flag=false;
        try {
            Socket socket=new Socket(ip,10085);
            inputStream=socket.getInputStream();
            outputStream=socket.getOutputStream();
            byte[] helloServer= PropertiesUtil.HELLOSERVER.getBytes("UTF-8");
            outputStream.write(getIntegerBytes(helloServer.length));
            outputStream.write(helloServer);
            if (readString().equals(PropertiesUtil.HELLOCLIENT)){
                flag=true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flag;
    }


    public void addBytes(byte[] bytes){
        mSendMsgQueue.add(bytes);
    }

    public void addMessage(String s){
        s = StringUtil.addEnd_flag2Str(s);
        try {
            byte[] stringData = s.getBytes("UTF-8");
            mSendMsgQueue.add(getIntegerBytes(stringData.length));
            mSendMsgQueue.add(stringData);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    private static String readString() {
        String s="";
        int msgSize=0;
        byte[] msgSizeBytes=new byte[4];
        try {
            int readSize=inputStream.read(msgSizeBytes);
            msgSize=IntConvertUtils.getIntegerByByteArray(msgSizeBytes);
            if (msgSize<=0){
                return "";
            }
            int i = 0;
            byte[] dataBytes = new byte[msgSize];
            while(i<msgSize){
                dataBytes[i] = (byte)inputStream.read();
                i++;
            }
            s = new String(dataBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }
}
