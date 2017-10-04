package com.lp.flashremote.views;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Toast;

import com.lp.flashremote.R;
import com.lp.flashremote.utils.Command2JsonUtil;
import com.lp.flashremote.utils.SocketUtil;
import com.lp.flashremote.utils.StringUtil;

import java.util.Random;

/**
 * Created by PUJW on 2017/8/23.
 *  弹出音量调节对话框
 */

public class VolumwDialog  extends Dialog{

    private SeekBar mProgressBar;
    private Context mContext;
    private SocketUtil socket;
    public VolumwDialog(@NonNull Context context, SocketUtil s) {
        super(context,R.style.VolumeDialog);
        mContext=context;
        this.socket=s;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setCanceledOnTouchOutside(true);
        setContentView(R.layout.volume_dialog);
        initView();
        initData();
        initEvent();
        super.onCreate(savedInstanceState);
    }


    private void initView() {
        mProgressBar=(SeekBar)findViewById(R.id.progressbar);
    }

    private void initData() {
        Random random = new Random();
        int s = random.nextInt(50)%(50-10+1) + 10;
        mProgressBar.setProgress(s);
    }

    private void initEvent() {
        mProgressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private int preProgress=0;
            private int progress = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progress=i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                preProgress=progress;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                socket.addMessage(StringUtil.operateCmd(
                            Command2JsonUtil.getJson("8",progress-preProgress+"",false)));
                Toast.makeText(mContext,"当前进度 = "+progress, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
