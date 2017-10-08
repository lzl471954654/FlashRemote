package com.lp.flashremote.utils;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.google.zxing.common.StringUtils;
import com.lp.flashremote.beans.Command;
import com.lp.flashremote.beans.Content;
import com.lp.flashremote.beans.FileInfo;
import com.lp.flashremote.beans.PropertiesUtil;
import com.lp.flashremote.beans.UserInfo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Queue;

/**
 * Created by LZL on 2017/10/7.
 */

public class PhoneRemoteSocket extends Thread {
    private static Socket mSocket;
    private static InputStream in;
    private static OutputStream out;
    static private Queue<byte[]> messageQueue;
    static private PhoneRemoteSocket phoneRemoteSocket;
    static private Handler handler;
    static private String mType = "";
    static private String ip = PropertiesUtil.SERVER_IP;
    static private Boolean loopFlag = false;
    static private Thread readThread;
    private PhoneRemoteSocket(){
        messageQueue = new LinkedList<>();
    }

    public static PhoneRemoteSocket getInstance(Handler handler,String type,String ip){
        PhoneRemoteSocket.ip = ip;
        mType = type;
        if(phoneRemoteSocket==null)
            phoneRemoteSocket = new PhoneRemoteSocket();
        else if (!type.equals(mType)){
            clearSocket();
            phoneRemoteSocket = new PhoneRemoteSocket();
        }
        PhoneRemoteSocket.handler = handler;
        return phoneRemoteSocket;
    }

    public static PhoneRemoteSocket getNowInstance(){
        return phoneRemoteSocket;
    }

    public static void clearSocket(){
        in = null;
        out = null;
        if(messageQueue!=null)
            messageQueue.clear();
        messageQueue = null;
        phoneRemoteSocket = null;
        handler = null;
        try {
            if(mSocket!=null)
                mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            if(initConnection()){
                loopFlag = true;
                switch (mType) {
                    case "WIFI_ONLINE":
                        wifiOnline();
                        break;
                    case "WIFI":
                        wifiLoop();
                        break;
                    case "REMOTE_ONLINE":
                        remoteOnline();
                        break;
                    case "REMOTE":
                        remoteLoop();
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            Message message = new Message();
            message.what = 17;
            handler.sendMessage(message);
            clearSocket();
            /*
            * 17 连接中断
            * */
        }
    }



    private void sendMessageLoop() throws IOException{
        while(loopFlag&&!isInterrupted()){
            synchronized (messageQueue){
                if(messageQueue!=null){
                    if(!messageQueue.isEmpty()){
                        byte[] bytes = messageQueue.remove();
                        out.write(bytes);
                    }
                }
            }
        }
    }
/*
* message.what
* what = 10 wifi上线成功
* what = 11 wifi控制链接成功
* what = 12 远程上线成功
* what = 13 远程上线失败
* what = 14 远程链接成功
* what = 15 远程链接失败
*
* */
    private void wifiOnline() throws IOException{
        Message message = new Message();
        message.what = 10;
        handler.sendMessage(message);
        readThread= new Thread(listenLoop);
        readThread.start();
        sendMessageLoop();
    }

    private void wifiLoop() throws IOException{
        Message message = new Message();
        message.what = 11;
        handler.sendMessage(message);
        sendMessageLoop();
    }

    private void remoteOnline() throws IOException{
        String login = "|ONLINE|_"+ UserInfo.getUsername()+"_"+UserInfo.getPassword()+"_"+PropertiesUtil.END_FLAG;
        System.out.println("online :"+login+"\n");
        byte[] bytes = login.getBytes("UTF-8");
        out.write(IntConvertUtils.getIntegerBytes(bytes.length));
        out.write(bytes);
        String result = readLine();
        System.out.println(result);
        Message message = new Message();
        if(result.startsWith("|ONLINE@SUCCESS|"))
        {
            message.what = 12;
            handler.sendMessage(message);
            readThread = new Thread(listenLoop);
            readThread.start();
            sendMessageLoop();
        }else {
            message.what = 13;
            handler.sendMessage(message);
        }
    }

    private void remoteLoop() throws  IOException{
        String login = PropertiesUtil.CONNECTED_TO_USER+"_"+UserInfo.getUsername()+"_"+UserInfo.getPassword()+"_"+PropertiesUtil.END_FLAG;
        byte[] bytes = login.getBytes("UTF-8");
        out.write(IntConvertUtils.getIntegerBytes(bytes.length));
        out.write(bytes);
        String result = readLine();
        Message message = new Message();
        if (result.startsWith(PropertiesUtil.CONNECTED_SUCCESS)){
            message.what = 14;
            handler.sendMessage(message);
            sendMessageLoop();
        }else{
            System.out.println("connected failed");
            message.what = 15;
            handler.sendMessage(message);
        }
    }

    @Override
    public void interrupt() {
        if(readThread!=null){
            readThread.interrupt();
        }
        super.interrupt();
    }

    private Runnable listenLoop = new Runnable() {
        @Override
        public void run() {
            Gson gson = new Gson();
            while(loopFlag&&!isInterrupted()){
                String is = readLine();
                if(is.startsWith(PropertiesUtil.COMMAND)){
                    Command command = gson.fromJson(StringUtil.getContent(is).getContent(),Command.class);
                    switch (command.getType()){
                        case "4":{
                            filePathOP(StringUtil.getContent(is));
                            break;
                        }
                    }
                }
            }
        }
    };

    private static void filePathOP(Content content){
        Gson gson = new Gson();
        Command command = gson.fromJson(content.getContent(),Command.class);
        File[] files = null;
        if(command.getDescribe().equals("")){
            /*
            * describe 为空  返回根目录
            * */
            files = Environment.getExternalStorageDirectory().listFiles();
        }else{
            /*
            * describe 不为空 返回要求目录
            * */
            files = (new File(command.getDescribe())).listFiles();
        }
        List<FileInfo> list = new ArrayList<>();
        for (File file : files) {
            FileInfo info = new FileInfo();
            info.setType(file.isDirectory());
            info.setName(file.getName());
            info.setPath(file.getAbsolutePath());
            list.add(info);
        }
        StringBuilder builder = new StringBuilder();
        builder.append(gson.toJson(list));
        builder.append("_");
        builder.append(PropertiesUtil.END_FLAG);
        addMessage(builder.toString());
    }

    private static boolean initConnection() throws IOException{
        mSocket = new Socket(ip,10085);
        in = mSocket.getInputStream();
        out = mSocket.getOutputStream();
        return true;
    }

    public String readLine() {
        String s = "";
        try {
            int msgSize = 0;
            byte[] msgSizeBytes = new byte[4];
            if(in==null){
                loopFlag = false;
                interrupt();
                return "";
            }
            in.read(msgSizeBytes);
            msgSize = IntConvertUtils.getIntegerByByteArray(msgSizeBytes);
            if(msgSize>=40*1024){
                return "";
            }
            if(msgSize<0){
                loopFlag = false;
                interrupt();
                return "";
            }
            int i = 0;
            byte[] dataBytes = new byte[msgSize];
            while(i<msgSize){
                dataBytes[i] = (byte)in.read();
                i++;
            }
            s = new String(dataBytes);
            Log.e("instruction:",s);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }

    public static void addBytes(byte[] bytes){
        synchronized (messageQueue){
            if(messageQueue!=null)
                messageQueue.add(bytes);
        }
    }

    public static void addMessage(String s){
        synchronized (messageQueue){
            if (messageQueue==null)
                return;
            s = StringUtil.addEnd_flag2Str(s);
            Log.e("addMessage:",s);
            try {
                byte[] stringData = s.getBytes("UTF-8");
                messageQueue.add(getIntegerBytes(stringData.length));
                messageQueue.add(stringData);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    private static byte[] getIntegerBytes(int data){
        byte[] s = {(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff};
        s[3] = (byte)((data)&s[3]);
        for(int i = 2;i>=0;i--){
            data = data>>8;
            s[i] = (byte)((data)&s[i]);
        }
        return s;
    }
}
