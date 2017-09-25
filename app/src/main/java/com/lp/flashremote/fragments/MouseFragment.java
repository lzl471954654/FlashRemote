package com.lp.flashremote.fragments;

import android.app.usage.UsageEvents;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.lp.flashremote.R;
import com.lp.flashremote.beans.UserInfo;
import com.lp.flashremote.utils.Command2JsonUtil;
import com.lp.flashremote.utils.SocketUtil;
import com.lp.flashremote.utils.StringUtil;
import com.lp.flashremote.utils.ToastUtil;
import com.lp.flashremote.views.MouseTouchView;

/**
 * Created by xiyou3g on 2017/9/23.
 * 鼠标操作 fragment
 */

public class MouseFragment extends Fragment {
    private View rootView;
    private MouseTouchView mMouseTouchView;
    private  SocketUtil mouseScoket;


    private TextView rightClick;
    private String mouseClickCom=null;
    private float[] moveX=new float[2];
    private float[] moveY=new float[2];
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fargment_mouse, container, false);

        mouseScoket=SocketUtil.getInstance(UserInfo.getUsername(),UserInfo.getPassword());
        mMouseTouchView = rootView.findViewById(R.id.mouse_view);
        rightClick=rootView.findViewById(R.id.right_click);

        mouseScoket.sendTestMessage(new SocketUtil.ConnectListener() {
            @Override
            public void connectSusess() {
                initlistener();
            }

            @Override
            public void connectError() {
                ToastUtil.toastText(getContext(),"连接失败，请重新连接!");
            }
        });
        return rootView;
    }

    private void initlistener() {
        mMouseTouchView.setClickListener(new MouseTouchView.OnClickListener() {
            @Override
            public void onDoubleClick(View v) {
                String doublecClick=Command2JsonUtil.getMouseJson(0,0,true,false,true,false);
                mouseScoket.addMessage(StringUtil.operateCmd(Command2JsonUtil.getJson("3",doublecClick,false)));
                Toast.makeText(getContext(), "双击", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSingleClick(View v) {
                String singleClick=Command2JsonUtil.getMouseJson(0,0,true,true,false,false);
                mouseScoket.addMessage(StringUtil.operateCmd(Command2JsonUtil.getJson("3",singleClick,false)));
                Toast.makeText(getContext(), "单击", Toast.LENGTH_SHORT).show();
            }

        });

        mMouseTouchView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction()==MotionEvent.ACTION_DOWN ){
                    moveX[0]=moveX[1]=motionEvent.getX();
                    moveY[0]=moveY[1]=motionEvent.getY();
                }
                else{
                    moveY[1]=motionEvent.getX();
                    moveX[1]=motionEvent.getX();
                }
                if (moveX[1]-moveX[0]>5| moveY[1]-moveY[0]>5){
                    String move=Command2JsonUtil.getMouseJson((moveX[1]-moveX[0]),
                            (moveY[1]-moveY[0]),false,false,false,false);
                    mouseScoket.addMessage(StringUtil.operateCmd(Command2JsonUtil.getJson("3",move,false)));
                }

                return false;
            }
        });

        rightClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String rightClick=Command2JsonUtil.getMouseJson(0,0,true,false,false,true);
                mouseScoket.addMessage(StringUtil.operateCmd(Command2JsonUtil.getJson("3",rightClick,false)));
                Toast.makeText(getContext(), "右击", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
