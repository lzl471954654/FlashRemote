package com.lp.flashremote.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.lp.flashremote.R;
import com.lp.flashremote.beans.UserInfo;
import com.lp.flashremote.utils.Command2JsonUtil;
import com.lp.flashremote.utils.SocketUtil;
import com.lp.flashremote.utils.StringUtil;
import com.lp.flashremote.views.MouseTouchView;

/**
 * Created by xiyou3g on 2017/9/23.
 * 鼠标操作 fragment
 */

public class MouseFragment extends Fragment {
    private View rootView;
    private MouseTouchView mMouseTouchView;
    private TextView rightClick;
    private String mouseClickCom=null;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fargment_mouse, container, false);

        final SocketUtil mouseScoket=SocketUtil.getInstance(UserInfo.getUsername(),UserInfo.getPassword());
        mMouseTouchView = rootView.findViewById(R.id.mouse_view);
        rightClick=rootView.findViewById(R.id.right_click);
        mMouseTouchView.setClickListener(new MouseTouchView.OnClickListener() {
            @Override
            public void onDoubleClick(View v) {
               // mouseClickCom=Command2JsonUtil.getMouseJson()
               // mouseScoket.addMessage(StringUtil.operateCmd(Command2JsonUtil.getJson("3",)));
                Toast.makeText(getContext(), "双击", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSingleClick(View v) {
                Toast.makeText(getContext(), "单击", Toast.LENGTH_SHORT).show();
            }
        });

        rightClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "右击", Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

}
