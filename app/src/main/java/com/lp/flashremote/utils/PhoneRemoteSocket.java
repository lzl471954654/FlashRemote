package com.lp.flashremote.utils;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.google.zxing.common.StringUtils;
import com.lp.flashremote.SocketInterface;
import com.lp.flashremote.beans.Command;
import com.lp.flashremote.beans.Content;
import com.lp.flashremote.beans.FileDescribe;
import com.lp.flashremote.beans.FileInfo;
import com.lp.flashremote.beans.PropertiesUtil;
import com.lp.flashremote.beans.UserInfo;

import java.io.File;
import java.io.FileInputStream;
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
import java.util.concurrent.LinkedBlockingDeque;

import Utils.FileUtilsKt;

/**
 * Created by LZL on 2017/10/7.
 */

public class PhoneRemoteSocket extends Thread implements SocketInterface {
    private static Socket mSocket;
    private static InputStream in;
    private static OutputStream out;
    static private LinkedBlockingDeque<byte[]> messageQueue = new LinkedBlockingDeque<>(1024);
    static private PhoneRemoteSocket phoneRemoteSocket;
    static private Handler handler;
    static private String mType = "";
    static private String ip = PropertiesUtil.SERVER_IP;
    static private Boolean loopFlag = false;
    static private Thread readThread;
    private Gson gson = new Gson();

    public static Boolean getLoopFlag() {
        return loopFlag;
    }

    public static synchronized PhoneRemoteSocket getInstance(Handler handler, String type, String ip){
        clearSocket();
        PhoneRemoteSocket.ip = ip;
        mType = type;

        phoneRemoteSocket = new PhoneRemoteSocket();
        PhoneRemoteSocket.handler = handler;
        return phoneRemoteSocket;
    }

    public static PhoneRemoteSocket getNowInstance(){
        return phoneRemoteSocket;
    }

    @Override
    public InputStream getInputStream() {
        return in;
    }

    public static void clearSocket(){
        loopFlag = false;
        in = null;
        out = null;

        messageQueue.clear();
        if(phoneRemoteSocket!=null)
            phoneRemoteSocket.interrupt();
        phoneRemoteSocket = null;
        handler = null;
        try {
            if(mSocket!=null)
                mSocket.close();
            mSocket = null;
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
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }finally {
            Message message = new Message();
            message.what = 17;
            if(handler!=null)
                handler.sendMessage(message);
            clearSocket();
            /*
            * 17 连接中断
            * */
        }
    }



    private void sendMessageLoop() throws IOException,InterruptedException{
        while(loopFlag&&!isInterrupted()){
            byte[] bytes = messageQueue.takeFirst();
            out.write(bytes);
            /*synchronized (messageQueue){
                if(messageQueue!=null){
                    if(!messageQueue.isEmpty()){
                        byte[] bytes = messageQueue.remove();
                        out.write(bytes);
                    }
                }
            }*/
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
    private void wifiOnline() throws IOException,InterruptedException{
        Message message = new Message();
        message.what = 10;
        handler.sendMessage(message);
        readThread= new Thread(listenLoop);
        readThread.start();
        sendMessageLoop();
    }

    private void wifiLoop() throws IOException,InterruptedException{
        Message message = new Message();
        message.what = 11;
        handler.sendMessage(message);
        sendMessageLoop();
    }

    private void remoteOnline() throws IOException,InterruptedException{
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

    private void remoteLoop() throws  IOException,InterruptedException{
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
            if (handler != null) {
                handler.sendMessage(message);
            }
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
            while(loopFlag&&!isInterrupted()){
                String is = readLine();
                if(is==null||is.equals("")){
                    continue;
                }
                Content content = StringUtil.getContent(is);
                switch (content.getHead()){
                    case "|COMMAND|":{
                        Command command = gson.fromJson(content.getContent(),Command.class);
                        switch (command.getType()){
                            case "4":{
                                filePathOP(content);
                                break;
                            }
                        }
                        break;
                    }
                    case "|FILE@DELETE|":{
                        fileDelete(StringUtil.getContent(is));
                        break;
                    }
                    case "|GET@FILE|":{
                        getFile(content);
                        break;
                    }
                }
            }
        }
    };

    private void getFile(Content content){
        List<FileDescribe> list = new LinkedList<>();
        FileInfo[] fileInfos = gson.fromJson(content.getContent(),FileInfo[].class);
        for (FileInfo fileInfo : fileInfos) {
            File file = new File(fileInfo.getPath());
            FileDescribe describe = new FileDescribe();
            describe.setFileSize(file.length());
            String name = file.getName().substring(0,file.getName().lastIndexOf("."));
            String type = file.getName().substring(file.getName().lastIndexOf(".")+1,file.getName().length());
            describe.setFileName(name);
            describe.setFileType(type);
            list.add(describe);
        }
        String command = PropertiesUtil.FILE_LIST_FLAG+"_"+gson.toJson(list);
        addMessage(command);
        String reuslt = readLine();
        if(reuslt.startsWith(PropertiesUtil.FILE_READY)){
            try
            {
                for (FileInfo fileInfo : fileInfos) {
                    File file = new File(fileInfo.getPath());
                    int count = 0;
                    FileInputStream inputStream = new FileInputStream(file);
                    while(true){
                        byte[] bytes = new byte[4096];
                        count = inputStream.read(bytes);
                        if(count==-1)
                            break;
                        if(count==4096)
                            addBytes(bytes);
                        else{
                            byte[] newBytes = new byte[count];
                            int i = 0;
                            for (i = 0;i<count;i++)
                                newBytes[i] = bytes[i];
                            addBytes(newBytes);
                        }
                    }
                    inputStream.close();
                }
            }catch (IOException e){
                e.printStackTrace();
                loopFlag = false;
            }
        }
    }

    private void fileDelete(Content content){
        String[] paths = gson.fromJson(content.getContent(),String[].class);
        int originalCount = paths.length;
        int newCount = FileUtilsKt.deleteFileOrDirs(paths);
        sendMsgWithParamEND(new String[]{PropertiesUtil.COMMAND_RESULT,String.valueOf(newCount)});
    }

    private void sendMsgWithParamEND(String[] args){
        StringBuilder builder = new StringBuilder();
        for (String arg : args) {
            builder.append(arg);
            builder.append("_");
        }
        builder.append(PropertiesUtil.END_FLAG);
        String msg = builder.toString();
        try {
            byte[] bytes = msg.getBytes("UTF-8");
            addBytes(IntConvertUtils.getIntegerBytes(bytes.length));
            addBytes(bytes);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void filePathOP(Content content){
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
        addMessage(gson.toJson(list));
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
                return "";
            }
            int count = in.read(msgSizeBytes);
            if (count==-1){
                System.out.println("count is "+count);
                loopFlag = false;
                return "";
            }
            msgSize = IntConvertUtils.getIntegerByByteArray(msgSizeBytes);
            if(msgSize>=40*1024){
                return "";
            }
            if(msgSize<0){
                loopFlag = false;
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

    public  void addBytes(byte[] bytes){
        try {
            messageQueue.putLast(bytes);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public  void addMessage(String s){
        s = StringUtil.addEnd_flag2Str(s);
        Log.e("addMessage:",s);
        try {
            byte[] stringData = s.getBytes("UTF-8");
            messageQueue.putLast(getIntegerBytes(stringData.length));
            messageQueue.putLast(stringData);
        } catch (UnsupportedEncodingException | InterruptedException e) {
            e.printStackTrace();
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
