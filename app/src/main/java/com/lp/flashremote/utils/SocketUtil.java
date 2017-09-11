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

public class SocketUtil extends Thread{
    private Socket mSocket;
    private PrintWriter writer;
    private BufferedReader reader;
    private String username;
    private String password;
    private Queue<String> mSendMessaggeQueue;
    private boolean threadStopState=false; //为true则终止，为false则继续

    public SocketUtil( String u, String pwd) {
        this.username = u;
        this.password = pwd;
        mSendMessaggeQueue=new LinkedList<>();
    }


    @Override
    public void run() {
        super.run();
        if ( initConn() ){
            loop();//开启消息队列
            Log.e("Thread-exit","exit");
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

    private void loop(){
        while(!isInterrupted()){
            if (getThreadState()){
                break;
            }
            if (!mSendMessaggeQueue.isEmpty()){
                String cmd=mSendMessaggeQueue.remove();
                String[] cmds=cmd.split("_");
                if (cmds[1].equals("@@op@@")){
                    writer.println(StringUtil.addEnd_flag2Str(cmd));
                    writer.flush();
                }else{
                    writer.println(StringUtil.addEnd_flag2Str(cmd));
                    writer.flush();
                    String s=readLine(reader);
                    Log.e("reulst",s);
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
        Thread thread= new Thread(new Runnable() {
            @Override
            public void run() {
                result=readLine(reader);
            }
        });
        thread.start();
        try {
            Thread.sleep(250);
            if (result.equals(StringUtil.addEnd_flag2Str(ServerProtocol.OK))){
                connectListener.connectSusess();
            }else{
                connectListener.connectError();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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

    public boolean getThreadState(){
        return threadStopState;
    }

    public void setThreadStop(){
        try {
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        threadStopState=true;
    }

    public interface ConnectListener{
        void connectSusess();
        void connectError();
    }
}
