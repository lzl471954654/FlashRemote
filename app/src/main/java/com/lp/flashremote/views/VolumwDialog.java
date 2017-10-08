package com.lp.flashremote.views;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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
    private SocketUtil socket;//发送socket
    private ImageView imageView;
    private Drawable d;
    private int flag=0;

    public VolumwDialog(@NonNull Context context, SocketUtil s,Drawable d,int f) {
        super(context,R.style.VolumeDialog);
        this.d=d;
        this.socket=s;
        this.flag=f;
        this.mContext=context;
    }

    public VolumwDialog(@NonNull Context context, SocketUtil s) {
        super(context,R.style.VolumeDialog);
        new VolumwDialog(context,s, context.getDrawable(R.mipmap.vocontrol),0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setCanceledOnTouchOutside(true);
        setContentView(R.layout.volume_dialog);
        initView();
        imageView.setImageDrawable(d);
        initData();
        initEvent();
        super.onCreate(savedInstanceState);
    }


    private void initView() {
        mProgressBar=(SeekBar)findViewById(R.id.progressbar);
        imageView=findViewById(R.id.vl);
    }

    private void initData() {
        if (flag==0){
            socket.addMessage(StringUtil.operateCmd(
                    Command2JsonUtil.getJson("8","100",false)));
            socket.addMessage(StringUtil.operateCmd(
                    Command2JsonUtil.getJson("8","-80",false)));
            mProgressBar.setProgress(20);
        }else {
            socket.addMessage(StringUtil.operateCmd(
                    Command2JsonUtil.getJson("5","50",false)));
            mProgressBar.setProgress(50);
        }

    }

    private void initEvent() {

        mProgressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private int preProgress=20;
            private int progress = 20;
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
                if (flag==0){
                    socket.addMessage(StringUtil.operateCmd(
                            Command2JsonUtil.getJson("8",progress-preProgress+"",false)));
                }else{
                    socket.addMessage(StringUtil.operateCmd(
                            Command2JsonUtil.getJson("5",progress+"",false)));
                }
                Toast.makeText(mContext,"当前进度 = "+progress, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void dismiss() {
        super.dismiss();
        socket.addMessage(StringUtil.operateCmd(
                Command2JsonUtil.getJson("5","-1",false)));
    }
}
