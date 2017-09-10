package com.lp.flashremote.utils;


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

public class SocketUtil extends Thread{
    private Socket mSocket;
    private PrintWriter writer;
    private BufferedReader reader;
    private String username;
    private String password;
    private static SocketUtil mSocketUtil;
    private static Queue<String> mSendMessaggeQueue;

    private ConnectListener mConnectListener;

    private SocketUtil( String u, String pwd) {
        this.username = u;
        this.password = pwd;
        mSendMessaggeQueue=new LinkedList<>();
    }

    public static SocketUtil getInstance(String u, String p) {
        if (mSocketUtil == null) {
            mSocketUtil = new SocketUtil(u, p);
        }
        return mSocketUtil;
    }

    @Override
    public void run() {
        super.run();
        if ( initConn() ){
            //开启消息队列
            loop();
        }else{

        }
    }

    @Override
    public synchronized void start() {
        super.start();
    }

    private boolean initConn() {
        boolean conn_ok = false;
        try {
            mSocket=new Socket(ServerProtocol.SERVER_IP,10086);
            OutputStream outputStream = mSocket.getOutputStream();
            InputStream inputStream = mSocket.getInputStream();
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

    /**
     *
     */
    private void loop(){
        while(true){
            if (!mSendMessaggeQueue.isEmpty()){
                writer.println(StringUtil.addEnd_flag2Str(mSendMessaggeQueue.remove()));
                writer.flush();
                String recive=readLine(reader);
                if (recive.equals(ServerProtocol.OK)){

                }
            }
        }
    }

    /**
     * 发送测试命令给 pc
     * @param t
     * @return 是否可以发送
     */
    private String result="";
    public void sendTestMessage(ConnectListener connectListener) {

        writer.println(StringUtil.addEnd_flag2Str(StringUtil.operateCmd("-1","test")));
        writer.flush();
        new Thread(new Runnable() {
            @Override
            public void run() {
                result=readLine(reader);
            }
        }).start();


    }

    /**
     * 读取流中的字符
     * @param reader
     * @return
     */

    private  String readLine( BufferedReader reader) {
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

    public void addMessage(String s){

        mSendMessaggeQueue.add(s);
    }

    public interface ConnectListener{
        void connectSusess();
        void connectError();
    }
}
