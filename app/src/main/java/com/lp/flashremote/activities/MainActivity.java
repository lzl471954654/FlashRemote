package com.lp.flashremote.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.lp.flashremote.R;
import com.lp.flashremote.beans.NetParameter;
import com.lp.flashremote.fragments.FileFragment;
import com.lp.flashremote.fragments.RemoteFragments;
import com.lp.flashremote.fragments.SettingFragment;
import com.lp.flashremote.utils.IpAddressUtil;


import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView mNavigation;
    private Fragment mFileFragment = new FileFragment();
    private Fragment mRemoteFragment = new RemoteFragments();
    private Fragment mSettingFragment = new SettingFragment();
    private Fragment mNowFragment = null;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            hideFragment();
            switch (item.getItemId()) {
                case R.id.navigation_file:
                    showFragment(mFileFragment);
                    return true;
                case R.id.navigation_remote:
                    showFragment(mRemoteFragment);
                    return true;
                case R.id.navigation_settings:
                    showFragment(mSettingFragment);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mNavigation = (BottomNavigationView) findViewById(R.id.navigation_bar);
        mNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        initFragment();
    }

    @Override
    protected void onStart() {
        super.onStart();
        String ip=IpAddressUtil.getIPAddress(this);
        if (ip!=null){
            NetParameter.IPAddress=getString(R.string.mIp)+ip;
        }else{
            NetParameter.IPAddress=getString(R.string.errorIp);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshFragment();
    }

    private void refreshFragment(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideFragment();
                        if(mNowFragment==null)
                            showFragment(mFileFragment);
                        else
                            showFragment(mNowFragment);
                    }
                });
            }
        }).start();
    }

    public void hideFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.hide(mFileFragment)
                .hide(mRemoteFragment)
                .hide(mSettingFragment)
                .commitNow();
    }

    public void showFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .show(fragment)
                .commitNow();
        mNowFragment = fragment;
    }

    public void initFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.content, mFileFragment)
                .add(R.id.content, mRemoteFragment)
                .add(R.id.content, mSettingFragment)
                .commitNow();
        getSupportFragmentManager()
                .beginTransaction()
                .hide(mRemoteFragment)
                .hide(mSettingFragment)
                .commitNow();
        mNowFragment = mFileFragment;
    }

}
