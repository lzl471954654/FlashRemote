package com.lp.flashremote.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.lp.flashremote.fragments.FileFragment;
import com.lp.flashremote.fragments.RemoteFragments;
import com.lp.flashremote.fragments.SettingFragment;

/**
 * Created by PUJW on 2017/8/14.
 */

public class MainTabPagerAdapter extends FragmentPagerAdapter {
    public static final int INDEX_FILE= 0;
    public static final int INDEX_REMOTE = 1;
    public static final int INDEX_SETTING= 2;

    private Fragment[] mFragments = new Fragment[] {
           new FileFragment(),
            new RemoteFragments(),
            new SettingFragment()
    };
    public MainTabPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments[position];
    }

    @Override
    public int getCount() {
        return mFragments.length;
    }
}
