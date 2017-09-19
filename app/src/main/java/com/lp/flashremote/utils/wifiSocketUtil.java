package com.lp.flashremote.utils;

import android.text.TextUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by xiyou3g on 2017/9/19.
 * 同一网络情况下，使用的socket工具类
 *
 */

public class wifiSocketUtil extends Thread {
    /**
     * 1 两个loop
     * 2 两个socket,逻辑判断使用哪个
     * 3 一次是否开启一个线程？？？
     * @return
     */

    private InputStream inputStream;
    private OutputStream outputStream;
    private String hotIP;

    private String startflag;//无参构造创造一个服务端socket
    private static final String SERVER="SS";
    private static final String CLIENT="CS";

    public wifiSocketUtil(){
        startflag=SERVER;
    }

    public wifiSocketUtil (String flag){
        if (TextUtils.isEmpty(flag)){
            hotIP=flag;
            startflag=CLIENT;
        }
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

    }

    /**
     * 扫码端开启的loop
     */
    private static void clientloop(){


    }


    private boolean initSocketServer(){
        boolean flag=false;
        try {
            ServerSocket serverSocket=new ServerSocket(10085);
            Socket socket=serverSocket.accept();
            inputStream=socket.getInputStream();
            outputStream=socket.getOutputStream();

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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flag;
    }
}
