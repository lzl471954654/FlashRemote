package com.lp.flashremote.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class SocketUtil {
    private Socket socket;
    private BufferedWriter writer;
    private BufferedReader reader;

    private static final String SERVER_IP="139.199.20.248";

    public boolean connPc(){
        boolean conn_ok=false;
        try {
            socket=new Socket(SERVER_IP,10086);
            OutputStream outputStream=socket.getOutputStream();
            InputStream inputStream=socket.getInputStream();
            writer=new BufferedWriter(new OutputStreamWriter(outputStream));
            reader=new BufferedReader(new InputStreamReader(inputStream));
            writer.write("|ONLINE|_username_password_@@|END@FLAG|@@");
            writer.flush();
            String result=readLine(reader);
            if (result.equals("|ONLINE@SUCCESS|_@@|END@FLAG|@@")){
                String conn=readLine(reader);
                if (conn.equals("|CONNECTED@SUCCESS|")){//绑定成功
                    conn_ok=true;
                }else if (conn.equals("|CONNECTED@FAILED|")){//绑定失败
                    conn_ok=false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return conn_ok;
    }


    public  void sendmessage(String m){
        try {
            writer.write(m);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static String readLine(BufferedReader reader) {
        StringBuilder sb=new StringBuilder();
        String temp="";
        try {
            while ((temp=reader.readLine())!=null){
                sb.append(temp);
            }
        } catch (IOException e) {
            System.out.println("读取数据失败。。。");
            e.printStackTrace();
        }
        return  sb.toString();
    }
}
