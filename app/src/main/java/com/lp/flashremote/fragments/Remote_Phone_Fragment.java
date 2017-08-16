package com.lp.flashremote.fragments;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.lp.flashremote.R;
import com.lp.flashremote.views.CodeDialog;

/**
 * Created by PUJW on 2017/8/14.
 *
 */

public class Remote_Phone_Fragment extends Fragment implements View.OnClickListener{
    private LinearLayout mQRcode;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_phone,container,false);
        iniView(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bindEvent();
    }

    private void bindEvent() {
        mQRcode.setOnClickListener(this);
    }

    private void iniView(View view) {
        mQRcode=(LinearLayout)view.findViewById(R.id.codeimage);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.codeimage:
                Resources res=getResources();
                Bitmap bitmap= BitmapFactory.decodeResource(res,R.drawable.code);
                CodeDialog.Builder dialogbuilder=new CodeDialog.Builder(getContext());
                dialogbuilder.setBitmap(bitmap);
                CodeDialog dialog=dialogbuilder.create();
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
                break;
        }
    }
}
