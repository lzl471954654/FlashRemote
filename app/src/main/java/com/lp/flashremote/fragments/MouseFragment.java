package com.lp.flashremote.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.lp.flashremote.R;
import com.lp.flashremote.views.MouseTouchView;

/**
 * Created by xiyou3g on 2017/9/23.
 * 鼠标操作 fragment
 */

public class MouseFragment extends Fragment {
    private View rootView;
    private MouseTouchView mMouseTouchView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView=inflater.inflate(R.layout.fargment_mouse,container,false);
        mMouseTouchView=rootView.findViewById(R.id.mouse_view);
        mMouseTouchView.setOnDoubleClickListener(new MouseTouchView.OnDoubleClickListener(){

            @Override
            public void onDoubleClick(View v) {
                Log.e("1111111111","222222222222");
                Toast.makeText(getContext(),"double click", Toast.LENGTH_SHORT).show();
            }
        });
        return rootView;
    }

}
