package com.lp.flashremote.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.lp.flashremote.R;

/**
 * Created by PUJW on 2017/8/22.
 *
 */

public class PcOperationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pcoperation);
         TextView textView=(TextView)findViewById(R.id.tv);
        textView.setText(getIntent().getStringExtra("operation"));

    }
}
