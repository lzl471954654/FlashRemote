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

import com.lp.flashremote.R;

/**
 * Created by PUJW on 2017/8/23.
 *  弹出音量调节对话框
 */

public class VolumwDialog  extends Dialog{

    private ProgressBar mProgressBar;
    public VolumwDialog(@NonNull Context context) {
        super(context,R.style.VolumeDialog);
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
        mProgressBar=(ProgressBar)findViewById(R.id.progressbar);
    }

    private void initData() {
    }

    private void initEvent() {
    }

  /* public static class Builder{
        private Context mContext;
        private Bitmap bitmap;

        public void setBitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        public Bitmap getBitmap() {
            return bitmap;
        }
        public Builder(Context context){
            this.mContext=context;
        }

        public VolumwDialog create(){
            LayoutInflater layoutInflater=(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            VolumwDialog volumwDialog=new VolumwDialog(mContext, R.style.Dialog);
            View v=layoutInflater.inflate(R.layout.volume_dialog,null);
            volumwDialog.addContentView(v,new WindowManager.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT
                    , android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
            volumwDialog.setContentView(v);
           *//* ImageView img = (ImageView)v.findViewById(R.id.img_qrcode);
            img.setImageBitmap(getBitmap());*//*
            return volumwDialog;
        }
    }*/
}
