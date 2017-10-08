package com.lp.flashremote.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

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
@RuntimePermissions
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.acticity_base);
        PropertiesUtil.getProperties(this);
        SharePerferenceUtil.getUserInfo(this);

        BaseActivityPermissionsDispatcher.grantPermissionWithPermissionCheck(this);
    }
    @NeedsPermission({
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
    })
    protected void grantPermission() {
        Timer timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent=new Intent(BaseActivity.this,MainActivity.class);
                startActivity(intent);
            }
        },2000);
    }


    @OnPermissionDenied({
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
    })
    void showDenied() {
        finish(); // 如果用户拒绝该权限执行的方法
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        BaseActivityPermissionsDispatcher.onRequestPermissionsResult(this,requestCode,grantResults);
    }



    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
