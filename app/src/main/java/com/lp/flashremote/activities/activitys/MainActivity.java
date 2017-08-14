package com.lp.flashremote.activities.activitys;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import com.lp.flashremote.R;
import com.lp.flashremote.activities.adapters.MainTabPagerAdapter;


import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity{

    private BottomNavigationView mNavigation;
    private ViewPager mViewPager;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_file:
                    mViewPager.setCurrentItem(0);
                    return true;
                case R.id.navigation_remote:
                    mViewPager.setCurrentItem(1);
                    return true;
                case R.id.navigation_settings:
                    mViewPager.setCurrentItem(2);
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
        mViewPager=(ViewPager)findViewById(R.id.viewpager);
        mNavigation= (BottomNavigationView) findViewById(R.id.navigation_bar);
        mNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mViewPager.setAdapter(new MainTabPagerAdapter(getSupportFragmentManager()));
        //禁止滑动
        mViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
    }

}
