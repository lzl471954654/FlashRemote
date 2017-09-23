package com.lp.flashremote.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lp.flashremote.R;

/**
 * Created by xiyou3g on 2017/9/23.
 * 鼠标操作 fragment
 */

public class MouseFragment extends Fragment {
    private View rootView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView=inflater.inflate(R.layout.fargment_mouse,container,false);

        return rootView;
    }

}
