package com.lp.flashremote.utils;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.lp.flashremote.SocketInterface;
import com.lp.flashremote.beans.Command;
import com.lp.flashremote.beans.Content;
import com.lp.flashremote.beans.FileDescribe;
import com.lp.flashremote.beans.FileInfo;
import com.lp.flashremote.beans.PropertiesUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import Utils.FileUtilsKt;

import static com.lp.flashremote.utils.IntConvertUtils.getIntegerBytes;

/**
 * Created by xiyou3g on 2017/9/19.
 * 同一网络情况下，使用的socket工具类
 *
 */

public class WifiSocketUtil extends Thread  implements SocketInterface {
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

    private static final Queue<byte[]> mSendMsgQueue = new LinkedList<>();
    private static WifiSocketUtil wifi;

    private Gson gson = new Gson();
    private static Handler handler;
    private static boolean loopFlag = false;


    private WifiSocketUtil(String mode,String ip){
        System.out.println("WIFISOCKET Start! "+mode+"\t"+ip);
        if(mode .equals(SERVER)){
            startflag = SERVER;
        }else {
            startflag = CLIENT;
            hotIP = ip;
        }
    }

    public static WifiSocketUtil getNowInstance() {
        return wifi;
    }

    public static WifiSocketUtil getInstance(String mode, String ip, Handler handler) {
        stopSocket();
        WifiSocketUtil.handler = handler;
        wifi = new WifiSocketUtil(mode,ip);
        return wifi;
    }

    private static void sendHandlerMessage(Message message){
        if (handler!=null){
            handler.sendMessage(message);
        }
    }

    public static void stopSocket(){
        loopFlag = false;
        if(wifi!=null){
            wifi.interrupt();
            synchronized (mSendMsgQueue){
                mSendMsgQueue.clear();
            }
            handler = null;
            inputStream = null;
            outputStream = null;
            wifi = null;
        }
    }



    @Override
    public void run() {
        System.out.println("WIFISOCKET RUN "+startflag+"\t"+hotIP);
        try{
            Message message = new Message();
            if (startflag.equals(CLIENT)){
                if (initSocketClient(hotIP)){
                    loopFlag = true;
                    message.what = 12;
                    System.out.println("server ok");
                    sendHandlerMessage(message);
                    serverloop();
                }
            }else if (startflag.equals(SERVER)){
                if (initSocketServer()){
                    loopFlag = true;
                    message.what = 18;
                    System.out.println("client ok");
                    sendHandlerMessage(message);
                    clientloop();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.e("wifi",""+e.getMessage());
        }
        finally {
            Message message = new Message();
            message.what = 17;
            sendHandlerMessage(message);
            stopSocket();
        }
    }

    /**
     * 被扫码端开启的loop
     *
     */
    private void serverloop() throws IOException{
        Thread thread = new Thread(listenLoop);
        thread.start();
        sendMessageLoop();
    }

    public void sendMessageLoop() throws IOException{
        while(!isInterrupted()){
            synchronized (mSendMsgQueue){
                if(!mSendMsgQueue.isEmpty()){
                    byte[] bytes = mSendMsgQueue.remove();
                    outputStream.write(bytes);
                }
            }
        }
    }



    /**
     * 扫码端开启的loop
     */
    private void clientloop() throws IOException{
        sendMessageLoop();
    }


    private boolean initSocketServer() throws IOException{
        boolean flag=false;
        ServerSocket serverSocket=new ServerSocket(10085);

        Socket socket=serverSocket.accept();
        inputStream=socket.getInputStream();
        outputStream=socket.getOutputStream();

        if (readString().equals(PropertiesUtil.HELLOSERVER)){
            byte[] helloClent= PropertiesUtil.HELLOCLIENT.getBytes("UTF-8");
            Log.e("222222222","fdsfdsfsdfsd");
            outputStream.write(getIntegerBytes(helloClent.length));
            outputStream.write(helloClent);
            flag=true;
        }
        return flag;
    }
    public static ArrayList<String> getConnectIp() throws IOException {
        ArrayList<String> connectIpList = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new FileReader("/proc/net/arp"));
        String line;
        while ((line = br.readLine()) != null) {
            String[] splitted = line.split(" +");
            if (splitted != null && splitted.length >= 4) {
                String ip = splitted[0];
                System.out.println(ip);
                String[] verify = ip.split("\\.");
                if (verify!=null&&verify.length>=4)
                    connectIpList.add(ip);
            }
        }
        br.close();
        return connectIpList;
    }


    private boolean initSocketClient(String ip) throws IOException{
        boolean flag=false;
        Log.e("1111111111","ip");
        Socket socket=new Socket(ip,10085);
        Log.e("1111111111","pppppppppppp");
        inputStream=socket.getInputStream();
        outputStream=socket.getOutputStream();
        byte[] helloServer= PropertiesUtil.HELLOSERVER.getBytes("UTF-8");
        Log.e("1111111111","fdsfdsfsdfsd");
        outputStream.write(getIntegerBytes(helloServer.length));
        outputStream.write(helloServer);
        if (readString().equals(PropertiesUtil.HELLOCLIENT)){
            flag=true;
        }
        return flag;
    }


    @Override
    public InputStream getInputStream() {
        return inputStream;
    }

    @Override
    public String readLine() {
        return readString();
    }

    public void addBytes(byte[] bytes){
        synchronized (mSendMsgQueue){
            mSendMsgQueue.add(bytes);
        }
    }

    public void addMessage(String s){
        s = StringUtil.addEnd_flag2Str(s);
        try {
            byte[] stringData = s.getBytes("UTF-8");
            synchronized (mSendMsgQueue){
                mSendMsgQueue.add(getIntegerBytes(stringData.length));
                mSendMsgQueue.add(stringData);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    private static String readString() {
        String s="";
        int msgSize=0;
        byte[] msgSizeBytes=new byte[4];
        try{
            int readSize=inputStream.read(msgSizeBytes);
            if(readSize<0){
                loopFlag = false;
                interrupted();
                return "";
            }
            msgSize=IntConvertUtils.getIntegerByByteArray(msgSizeBytes);
            if (msgSize<=0){
                return "";
            }
            if(msgSize>40*1024){
                Log.e("readMsg","msg is too larg , size is "+msgSize);
                return "";
            }
            int i = 0;
            byte[] dataBytes = new byte[msgSize];
            while(i<msgSize){
                dataBytes[i] = (byte)inputStream.read();
                i++;
            }
            s = new String(dataBytes);
        }catch (IOException e){
            e.printStackTrace();
            interrupted();
            loopFlag = false;
        }
        return s;
    }

    private void fileDelete(Content content){
        String[] paths = gson.fromJson(content.getContent(),String[].class);
        int originalCount = paths.length;
        int newCount = FileUtilsKt.deleteFileOrDirs(paths);
        sendMsgWithParamEND(new String[]{PropertiesUtil.COMMAND_RESULT,String.valueOf(newCount)});
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
                interrupt();
            }
        }
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
}
