package com.lp.flashremote.activities;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;


import com.lp.flashremote.R;
import com.lp.flashremote.beans.UserInfo;
import com.lp.flashremote.fragments.Remote_Pc_Fragment;
import com.lp.flashremote.fragments.ToolsFragment;
import com.lp.flashremote.utils.SocketUtil;

/**
 * Created by PUJW on 2017/8/22.
 * 是否使用反射获取上一个活动中的socket??
 * 或者是传入上一个活动中的socket?????
 */

public class PcOperationActivity extends AppCompatActivity {
    private ToolsFragment mToolsFragment;
    private TextView textView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
        localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS |
                localLayoutParams.flags);

        setContentView(R.layout.activity_pcoperation);
        String operation=getIntent().getStringExtra("operation");
        textView=(TextView) findViewById(R.id.text);

       if (operation.equals("tools")){
           textView.setText(getResources().getString(R.string.pctools));
           SocketUtil mSocketOP=SocketUtil.getInstance(UserInfo.getUsername(),UserInfo.getPassword());
            mToolsFragment=new ToolsFragment(mSocketOP);
       }
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container,mToolsFragment)
                .commitNow();

        findViewById(R.id.back_tools).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PcOperationActivity.this.finish();
            }
        });
    }
}
