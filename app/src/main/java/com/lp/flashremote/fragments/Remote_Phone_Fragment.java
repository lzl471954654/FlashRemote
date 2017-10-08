package com.lp.flashremote.fragments;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lp.flashremote.R;
import com.lp.flashremote.activities.PcFileDirActivity;
import com.lp.flashremote.beans.NetParameter;
import com.lp.flashremote.beans.PropertiesUtil;
import com.lp.flashremote.beans.UserInfo;
import com.lp.flashremote.beans.WifiInfo;
import com.lp.flashremote.utils.PhoneRemoteSocket;
import com.lp.flashremote.utils.QRcodeutil;
import com.lp.flashremote.utils.ToastUtil;
import com.lp.flashremote.utils.WifiConnectUtil;
import com.lp.flashremote.utils.WifiHostBiz;
import com.lp.flashremote.utils.WifiSocketUtil;
import com.lp.flashremote.views.CodeDialog;
import com.lp.flashremote.views.MyProgressDialog;
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
    private TextView phoneOnline;
    private TextView phoneControl;

    private boolean isConnected = false;

    private MyProgressDialog progressDialog;
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
        phoneControl.setOnClickListener(this);
        phoneOnline.setOnClickListener(this);
    }

    private void iniView(View view) {
        myIpAddress=view.findViewById(R.id.ipaddress);
        myIpAddress.setText(NetParameter.IPAddress);
        mQRcode=view.findViewById(R.id.codeimage);
        mScanQR=view.findViewById(R.id.scanQR);
        phoneOnline = view.findViewById(R.id.phoneOnline);
        phoneControl = view.findViewById(R.id.phoneControl);
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
            case R.id.phoneOnline:{
                if(UserInfo.isEmpty()){
                    Snackbar.make(phoneOnline,"请先在设置中设置账号密码",Snackbar.LENGTH_SHORT).show();
                    return;
                }
                PhoneRemoteSocket socket = PhoneRemoteSocket.getInstance(handler,"REMOTE_ONLINE", PropertiesUtil.SERVER_IP);
                socket.start();
                break;
            }
            case R.id.phoneControl:{
                if(UserInfo.isEmpty()){
                    Snackbar.make(phoneOnline,"请先在设置中设置账号密码",Snackbar.LENGTH_SHORT).show();
                    return;
                }
                PhoneRemoteSocket socket = PhoneRemoteSocket.getInstance(handler,"REMOTE",PropertiesUtil.SERVER_IP);
                socket.start();
                break;
            }
        }
    }

    private void showProgressDialog(String s){
        progressDialog = new MyProgressDialog(getContext(),s);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                PhoneRemoteSocket socket = PhoneRemoteSocket.getNowInstance();
                if(socket!=null){
                    socket.interrupt();
                }
            }
        });
        progressDialog.show();
    }

    private void dissmissProgressDialog(){
        if(progressDialog!=null)
            if (progressDialog.isShowing())
                progressDialog.dismiss();
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
            switch (msg.what){
                case 1:{
                    Toast.makeText(getActivity(),"连接成功",Toast.LENGTH_SHORT).show();
                    break;
                }
                case 12:{
                    showToast("上线成功");
                    showProgressDialog("请不要退出当前界面，正在等待远程主机操作");
                    break;
                }
                case 13:{
                    showToast("上线失败");
                    break;
                }
                case 14:{
                    showToast("远程连接成功");
                    /*Intent intent = new Intent(getContext(),PcFileDirActivity.class);
                    intent.putExtra("ROOTPATH","");
                    startActivity(intent);*/
                    break;
                }
                case 15:{
                    showToast("远程链接失败");
                    break;
                }
                case 17:{
                    showToast("连接中断！");
                    dissmissProgressDialog();
                    break;
                }
            }
        }
    };

    private void showToast(String s){
        ToastUtil.toastText(getContext(),s);
    }

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(handler!=null){
            handler.removeCallbacksAndMessages(null);
        }
    }
}
