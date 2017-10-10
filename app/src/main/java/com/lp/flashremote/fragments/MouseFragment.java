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

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xiyou3g on 2017/9/23.
 * 鼠标操作 fragment
 */

public class MouseFragment extends Fragment {
    private View rootView;
    private MouseTouchView mMouseTouchView;
    private  SocketUtil mouseScoket;


    private TextView rightClick;
    private  int i=0;
    Map<Integer,Integer> map=new HashMap<>();
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
                i=0;
                map.put(0,0);
                String doublecClick=Command2JsonUtil.getMouseJson(map,true,false,true,false);
                map.clear();
                mouseScoket.addMessage(StringUtil.operateCmd(Command2JsonUtil.getJson("3",doublecClick,false)));
                Toast.makeText(getContext(), "双击", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSingleClick(View v) {

                i=0;
                map.put(0,0);
                String singleClick=Command2JsonUtil.getMouseJson(map,true,true,false,false);
                map.clear();
                mouseScoket.addMessage(StringUtil.operateCmd(Command2JsonUtil.getJson("3",singleClick,false)));
                Toast.makeText(getContext(), "单击", Toast.LENGTH_SHORT).show();
            }
        });


        mMouseTouchView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getAction()==MotionEvent.ACTION_DOWN ){
                    moveX[0]=moveX[1]=motionEvent.getX()/9;
                    moveY[0]=moveY[1]=motionEvent.getY()/9;
                }
                else{
                    moveY[1]=motionEvent.getY()/9;
                    moveX[1]=motionEvent.getX()/9;
                }
                if (motionEvent.getAction()==MotionEvent.ACTION_UP){
                    map.clear();
                    i=0;
                }
                map.put((int)(moveX[1]-moveX[0]),(int)(moveY[1]-moveY[0]));
                i++;
                if (i==12){
                    String move=Command2JsonUtil.getMouseJson(map,false,false,false,false);
                    map.clear();
                    i=0;
                    mouseScoket.addMessage(StringUtil.operateCmd(Command2JsonUtil.getJson("3",move,false)));
                }
                return false;
            }
        });

        rightClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map.clear();
                i=0;
                String rightClick=Command2JsonUtil.getMouseJson(map,true,false,false,true);
                mouseScoket.addMessage(StringUtil.operateCmd(Command2JsonUtil.getJson("3",rightClick,false)));
                Toast.makeText(getContext(), "右击", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
