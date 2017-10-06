package com.lp.flashremote.activities;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.Toast;


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
        BaseActivityPermissionsDispatcher.grantPermissionWithPermissionCheck(this);
    }

    @NeedsPermission({
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,

    })
    protected void grantPermission() {
    }


    @OnPermissionDenied({
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
}
