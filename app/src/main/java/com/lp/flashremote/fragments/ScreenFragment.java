package com.lp.flashremote.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.lp.flashremote.R;
import com.lp.flashremote.beans.UserInfo;
import com.lp.flashremote.utils.Command2JsonUtil;
import com.lp.flashremote.utils.SocketUtil;
import com.lp.flashremote.utils.StringUtil;

public class ScreenFragment extends Fragment {
    private String filename;
    private ProgressBar mPb;
    private ImageView mImage;
    private SocketUtil socket;
    private View rootView;
    public ScreenFragment(String f){
        this.filename=f;
        socket=SocketUtil.getInstance(UserInfo.getUsername(),UserInfo.getPassword());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        socket.addMessage(StringUtil.operateCmd(
                Command2JsonUtil.getJson("2",filename,true)));
        String content=socket.readLine();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView=inflater.inflate(R.layout.screenfragment,container,false);
        initView(rootView);
        return rootView;
    }

    private void initView(View rootView) {
        mPb=rootView.findViewById(R.id.progressbar);
        mImage=rootView.findViewById(R.id.operation);
    }
}
