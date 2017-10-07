package com.lp.flashremote.utils;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.lp.flashremote.beans.PropertiesUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.LinkedList;
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
    private PhoneRemoteSocket(){
        messageQueue = new LinkedList<>();
    }

    public static PhoneRemoteSocket getInstance(Handler handler,String type){
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

    public static void clearSocket(){
        in = null;
        out = null;
        if(messageQueue!=null)
            messageQueue.clear();
        phoneRemoteSocket = null;
        handler = null;
        if(mSocket!=null)
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    @Override
    public void run() {
        try {
            if(initConnection()){
                if (mType.equals("WIFI")){

                }
                else if (mType.equals("ONLINE")){

                }else
                {

                }
            }else{

            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            clearSocket();
        }
    }

    public static boolean initConnection() throws IOException{
        mSocket = new Socket(PropertiesUtil.SERVER_IP,10085);
        in = mSocket.getInputStream();
        out = mSocket.getOutputStream();
        return true;
    }

    public String readLine() {
        String s = "";
        try {
            int msgSize = 0;
            byte[] msgSizeBytes = new byte[4];
            in.read(msgSizeBytes);
            msgSize = IntConvertUtils.getIntegerByByteArray(msgSizeBytes);
            if(msgSize<=0&&msgSize>=40*1024){
                /*setThreadStop();
                setmConnOk(false);*/
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

    public void addBytes(byte[] bytes){
        synchronized (messageQueue){
            if(messageQueue!=null)
                messageQueue.add(bytes);
        }
    }

    public void addMessage(String s){
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

    public byte[] getIntegerBytes(int data){
        byte[] s = {(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff};
        s[3] = (byte)((data)&s[3]);
        for(int i = 2;i>=0;i--){
            data = data>>8;
            s[i] = (byte)((data)&s[i]);
        }
        return s;
    }
}
