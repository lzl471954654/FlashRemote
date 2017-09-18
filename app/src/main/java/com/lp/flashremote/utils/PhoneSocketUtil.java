package com.lp.flashremote.utils;

import com.lp.flashremote.beans.NetParameter;
import com.lp.flashremote.beans.ServerProtocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by xiyou3g on 2017/9/18.
 *
 */

public class PhoneSocketUtil {

    private Socket mPhoneSocket;
    private String otherIP;
    private String password;
    public InputStream inputStream;
    public OutputStream outputStream;

    private static PhoneSocketUtil mPhoneSocketUtil;

    private PhoneSocketUtil(String u){
      this.otherIP=u;

    }
    public PhoneSocketUtil getPhoneSocketUtil(String userIP ){
        if (mPhoneSocketUtil==null){
            mPhoneSocketUtil=new PhoneSocketUtil(userIP);
        }
        return mPhoneSocketUtil;
    }

    public void clearSocketConn(){
        mPhoneSocketUtil=null;
    }
    private boolean initPhoneSocket(){

        try {

            mPhoneSocket=new Socket(ServerProtocol.SERVER_IP,10085);
            inputStream=mPhoneSocket.getInputStream();
            outputStream=mPhoneSocket.getOutputStream();
            String conString=StringUtil.stringAddUnderline(ServerProtocol.CONNECTED_TO_USER,otherIP,
                    NetParameter.IPAddress,ServerProtocol.END_FLAG);
            byte[] bytes=conString.getBytes();
            outputStream.write(IntConvertUtils.getIntegerBytes(bytes.length));
            String result=readLine();
            if (result.equals("对方已上线")){
                /**
                 * 不同wifi下传输文件
                 *
                 *  发送消息
                    接受消息
                 */

            }else{

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public String readLine() {
        String s = "";
        try {
            int msgSize = 0;
            byte[] msgSizeBytes = new byte[4];
            inputStream.read(msgSizeBytes);
            msgSize = IntConvertUtils.getIntegerByByteArray(msgSizeBytes);
            System.out.println("msgSize is "+msgSize);

            int i = 0;
            byte[] dataBytes = new byte[msgSize];
            while(i<msgSize){
                dataBytes[i] = (byte)inputStream.read();
                i++;
            }
            s = new String(dataBytes);
            System.out.println("msg is "+s);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }

}
