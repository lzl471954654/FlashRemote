package com.lp.flashremote.fragments;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lp.flashremote.R;
import com.lp.flashremote.beans.NetParameter;
import com.lp.flashremote.beans.WifiInfo;
import com.lp.flashremote.utils.QRcodeutil;
import com.lp.flashremote.utils.WifiConnectUtil;
import com.lp.flashremote.utils.WifiHostBiz;
import com.lp.flashremote.utils.WifiSocketUtil;
import com.lp.flashremote.views.CodeDialog;
import com.xys.libzxing.zxing.activity.CaptureActivity;

import java.io.File;

/**
 * Created by PUJW on 2017/8/14.
 *
 */

public class Remote_Phone_Fragment extends Fragment implements View.OnClickListener{
    private static final int QR_RESULT_CODE=1;
    private LinearLayout mQRcode;
    private TextView mScanQR;
    private TextView myIpAddress;
    private WifiSocketUtil mWifiSocket=null;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_phone,container,false);
        iniView(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bindEvent();
    }

    private void bindEvent() {
        mQRcode.setOnClickListener(this);
        mScanQR.setOnClickListener(this);
    }

    private void iniView(View view) {
        myIpAddress=view.findViewById(R.id.ipaddress);
        myIpAddress.setText(NetParameter.IPAddress);
        mQRcode=view.findViewById(R.id.codeimage);
        mScanQR=view.findViewById(R.id.scanQR);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.codeimage:

                /**
                 * 1 开启热点
                 * 2 本机WiFi ip
                 * 3 等待对方连接
                 */
                String hotIp=setwifiHot(true);   //打开热点，并开启socket
                initQRCode(hotIp);               //弹出二维码等待连接
                /*if (mWifiSocket==null){
                    mWifiSocket=new WifiSocketUtil();
                    mWifiSocket.start();
                }*/
                break;
            case R.id.scanQR:
                Intent intent=new Intent(getActivity(), CaptureActivity.class);
                startActivityForResult(intent,QR_RESULT_CODE);
                break;
        }
    }

    private void initQRCode(String hotIp) {
        final String filepath=getContext().getCacheDir().getAbsolutePath()
                +File.separator+"qr"+System.currentTimeMillis()+".jpg";
        System.out.println("code_path:\t"+filepath);

        Bitmap bitmap=null;
        if (QRcodeutil.createQRcode(new Gson().toJson(new WifiInfo(hotIp)),600,600,filepath)){
            bitmap= BitmapFactory.decodeFile(filepath);
        }else{
            bitmap=BitmapFactory.decodeResource(getResources(),R.drawable.code);
        }
        CodeDialog.Builder dialogbuilder=new CodeDialog.Builder(getContext());
        dialogbuilder.setBitmap(bitmap);
        CodeDialog dialog=dialogbuilder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    private String setwifiHot(boolean b) {
        WifiHostBiz wifiHostBiz=new WifiHostBiz(getContext());
        String hotIp;
        if (wifiHostBiz.isWifiApEnable()){
            wifiHostBiz.setWifiAPEnable(!b);
            hotIp=wifiHostBiz.setWifiAPEnable(b);
        }else{
            hotIp=wifiHostBiz.setWifiAPEnable(b);
        }
        return hotIp;
    }

    private String getFileRoot(Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            File external=context.getExternalFilesDir(null);
            if (external!=null){
                return external.getAbsolutePath();
            }
        }

        return context.getFilesDir().getAbsolutePath();
    }


    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==1){
                Toast.makeText(getActivity(),"连接成功",Toast.LENGTH_SHORT).show();
            }
        }
    };
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==QR_RESULT_CODE && resultCode== Activity.RESULT_OK){
            Bundle bundle=data.getExtras();
            String QRcontent=bundle.getString("result");
            WifiInfo wifiInfo=new Gson().fromJson(QRcontent,WifiInfo.class);
            WifiConnectUtil wifiConnectUtil=new WifiConnectUtil(getContext());

            if ( wifiConnectUtil.Connect(wifiInfo.getName(),wifiInfo.getPwd())){
                Message m=new Message();
                m.what=1;
                handler.sendMessage(m);
            }
            //initConnect(wifiInfo.getIp());
        }
    }

    /**
     * 开启同一热点下的socket
     * @param ip
     */
    private void initConnect(String ip) {

    }
}
