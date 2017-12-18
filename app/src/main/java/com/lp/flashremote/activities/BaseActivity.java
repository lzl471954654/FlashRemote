package com.lp.flashremote.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.lp.flashremote.BuildConfig;
import com.lp.flashremote.R;
import com.lp.flashremote.beans.PropertiesUtil;
import com.lp.flashremote.utils.SharePerferenceUtil;

import java.util.Timer;
import java.util.TimerTask;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;

/**
 * Created by xiyou3g on 2017/10/6.
 *
 */
//@RuntimePermissions
public class BaseActivity extends AppCompatActivity {

    String[] permissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.CHANGE_NETWORK_STATE
            //, Manifest.permission.WRITE_SETTINGS
    };


    private static final int REQUEST_CODE_WRITE_SETTINGS = 1;
    private void requestWriteSettings() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, REQUEST_CODE_WRITE_SETTINGS );
    }



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.acticity_base);
        PropertiesUtil.getProperties(this);
        SharePerferenceUtil.getUserInfo(this);
        getPermission();
        //BaseActivityPermissionsDispatcher.grantPermissionWithPermissionCheck(this);
    }
    /*@NeedsPermission({
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.CHANGE_NETWORK_STATE,
            Manifest.permission.WRITE_SETTINGS
    })*/
    protected void grantPermission() {
        System.out.println("grantPermission");
        Timer timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent=new Intent(BaseActivity.this,MainActivity.class);
                startActivity(intent);
            }
        },2000);
    }


    /*@OnPermissionDenied({
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.CHANGE_NETWORK_STATE,
            Manifest.permission.WRITE_SETTINGS
    })*/
    void showDenied() {
        finish(); // 如果用户拒绝该权限执行的方法
    }

    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        BaseActivityPermissionsDispatcher.onRequestPermissionsResult(this,requestCode,grantResults);
    }*/


    private boolean checkePermissionAllGet(String[] permissions){
        boolean flag = true;
        for (String s : permissions) {
            if(ContextCompat.checkSelfPermission(this,s)== PackageManager.PERMISSION_DENIED){
                flag = false;
                break;
            }
        }
        return flag;
    }

    private void getPermission(){
        boolean isAllGet = checkePermissionAllGet(permissions);
        if(!isAllGet){
            ActivityCompat.requestPermissions(this,permissions,100);
        }else
        {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Intent intent=new Intent(BaseActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }).start();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_WRITE_SETTINGS) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (Settings.System.canWrite(this)) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(1500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Intent intent=new Intent(BaseActivity.this,MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }).start();
                    Log.i("BaseActivity", "onActivityResult write settings granted" );
                }else{
                    openAppDetails();
                }
            }else{
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Intent intent=new Intent(BaseActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }).start();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==100){
            boolean flag = true;
            for (int result : grantResults) {
                if(result == PackageManager.PERMISSION_DENIED){
                    flag = false;
                    break;
                }
            }
            if(!flag){
                openAppDetails();
            }else {
                requestWriteSettings();
                /*new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Intent intent=new Intent(BaseActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }).start();*/
            }
        }
    }

    private void openAppDetails() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("本应用需要文件读写，网络状态改变，修改系统设置，录音权限，否则无法正常使用应用，请到 “应用信息 -> 权限” 中授予！");
        builder.setPositiveButton("去手动授权", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:" + getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
    }


    @Override
    protected void onPause() {
        super.onPause();
        //finish();
    }
}
