package com.lp.flashremote.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lp.flashremote.R;
import com.lp.flashremote.bean.NetParameter;
import com.lp.flashremote.utils.QRcodeutil;
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
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
        myIpAddress=(TextView)view.findViewById(R.id.ipaddress);
        myIpAddress.setText(NetParameter.IPAddress);
        mQRcode=(LinearLayout)view.findViewById(R.id.codeimage);
        mScanQR=(TextView)view.findViewById(R.id.scanQR);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.codeimage:
                final String filepath=getFileRoot(getContext())
                        + File.separator+"qr"+System.currentTimeMillis()+".jpg";
                Bitmap bitmap=null;
                if (QRcodeutil.createQRcode(NetParameter.IPAddress,600,600,filepath)){
                    bitmap= BitmapFactory.decodeFile(filepath);
                }else{
                    bitmap=BitmapFactory.decodeResource(getResources(),R.drawable.code);
                }
                CodeDialog.Builder dialogbuilder=new CodeDialog.Builder(getContext());
                dialogbuilder.setBitmap(bitmap);
                CodeDialog dialog=dialogbuilder.create();
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
                break;
            case R.id.scanQR:
                Intent intent=new Intent(getActivity(), CaptureActivity.class);
                startActivityForResult(intent,QR_RESULT_CODE);
                break;
        }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==QR_RESULT_CODE && resultCode== Activity.RESULT_OK){
            Bundle bundle=data.getExtras();
            String QRcontent=bundle.getString("result");
            Toast.makeText(getActivity(),QRcontent,Toast.LENGTH_LONG).show();
        }
    }
}
