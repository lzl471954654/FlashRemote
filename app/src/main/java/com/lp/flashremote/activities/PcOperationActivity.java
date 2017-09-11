package com.lp.flashremote.activities;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;


import com.lp.flashremote.R;
import com.lp.flashremote.fragments.ToolsFragment;

/**
 * Created by PUJW on 2017/8/22.
 * 是否使用反射获取上一个活动中的socket??
 * 或者是传入上一个活动中的socket?????
 */

public class PcOperationActivity extends AppCompatActivity {
    private ToolsFragment mToolsFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pcoperation);
        // TextView textView=(TextView)findViewById(R.id.tv);
        String operation=getIntent().getStringExtra("operation");

       if (operation.equals("tools")){
            mToolsFragment=new ToolsFragment();
       }

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container,mToolsFragment)
                .commitNow();

    }
}
