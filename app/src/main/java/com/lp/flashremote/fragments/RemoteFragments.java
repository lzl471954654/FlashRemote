package com.lp.flashremote.fragments;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.lp.flashremote.R;
import com.lp.flashremote.adapters.Remote_Tab_Adapter;
import com.lp.flashremote.services.MainServices;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by PUJW on 2017/8/14.
 *
 */

public class RemoteFragments extends Fragment {
    private TabLayout mTabLayout;
    private ViewPager mVP;

    private List<String> mTabItem=new ArrayList<>();
    private List<Fragment> mTabsList=new ArrayList<>();


    private MainServices.SocketBinder socketBinder;
    private ServiceConnection serviceConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            socketBinder=(MainServices.SocketBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {}
    };
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_remote,container,false);
        initView(view);
        return view;
    }


    private void initView(View view) {
        mTabLayout=(TabLayout)view.findViewById(R.id.tablayout);
        mVP=(ViewPager)view.findViewById(R.id.remotetab);
        mTabsList.add(new Remote_Pc_Fragment());
         mTabsList.add(new Remote_Phone_Fragment());

        mTabItem.add(getContext().getString(R.string.string_pc));
        mTabItem.add(getContext().getString(R.string.string_phone));

        mTabLayout.addTab(mTabLayout.newTab().setText(mTabItem.get(0)));
        mTabLayout.addTab(mTabLayout.newTab().setText(mTabItem.get(1)));

        Remote_Tab_Adapter rta=new Remote_Tab_Adapter(getChildFragmentManager(),mTabsList,mTabItem);
        mVP.setAdapter(rta);
        mTabLayout.setupWithViewPager(mVP);
        mTabLayout.post(new Runnable() {
            @Override
            public void run() {
                setIndicator(mTabLayout,60,60);
            }
        });
    }
    public void setIndicator(TabLayout tabs, int leftDip, int rightDip) {
        Class<?> tabLayout = tabs.getClass();
        Field tabStrip = null;
        try {
            tabStrip = tabLayout.getDeclaredField("mTabStrip");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        tabStrip.setAccessible(true);
        LinearLayout llTab = null;
        try {
            llTab = (LinearLayout) tabStrip.get(tabs);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        int left = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, leftDip,
                Resources.getSystem().getDisplayMetrics());
        int right = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, rightDip,
                Resources.getSystem().getDisplayMetrics());

        for (int i = 0; i < llTab.getChildCount(); i++) {
            View child = llTab.getChildAt(i);
            child.setPadding(0, 0, 0, 0);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.MATCH_PARENT, 1);
            params.leftMargin = left;
            params.rightMargin = right;
            child.setLayoutParams(params);
            child.invalidate();
        }
    }
}
