package com.lp.flashremote.utils;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by xiyou3g on 2017/9/18.
 *
 */

public class PhoneSocketUtil {
    private Socket mPhoneSocket;
    private String username;
    private String password;
    public InputStream inputStream;
    public OutputStream outputStream;

    private static PhoneSocketUtil mPhoneSocketUtil;

    private PhoneSocketUtil(String u,String pwd){
        this.username=u;
        this.password=pwd;

    }
    public PhoneSocketUtil getPhoneSocketUtil(String username,String password){
        if (mPhoneSocketUtil==null){
            mPhoneSocketUtil=new PhoneSocketUtil(username,password);
        }
        return mPhoneSocketUtil;
    }

    public void clearSocketConn(){
        mPhoneSocketUtil=null;
    }
    private boolean initPhoneSocket(){

        mPhoneSocket=new Socket();

        return false;
    }

}
