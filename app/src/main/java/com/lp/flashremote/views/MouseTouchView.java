package com.lp.flashremote.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.lp.flashremote.beans.UserInfo;
import com.lp.flashremote.utils.Command2JsonUtil;
import com.lp.flashremote.utils.SocketUtil;
import com.lp.flashremote.utils.StringUtil;


public class MouseTouchView extends View {

    private SocketUtil mSocket;
    private OnClickListener mOnClickListener;

    float downX =0;
    float downY = 0;
    float upX=0;
    float upY =0;

    private float TOUCHSLOP= ViewConfiguration.get(getContext()).getScaledTouchSlop();//最小滑动位置
    TouchEventCountThread mInTouchEventCount = new TouchEventCountThread(); // 统计600ms内的点击次数
    TouchEventHandler mTouchEventHandler = new TouchEventHandler();

    public MouseTouchView(Context context) {
        super(context);
    }

    public MouseTouchView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public static float currentX = -100;
    public static float currentY = -100;

    @Override
    protected void onDraw(Canvas canvas) {
        Paint p = new Paint();
        p.setColor(Color.GRAY);
        canvas.drawCircle(currentX, currentY, 70, p);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        currentX = event.getX(); //触摸座标X
        currentY = event.getY(); //触摸座标Y

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                downX=0;
                downY=0;
                downX=event.getX();
                downY=event.getY();
                if (0==mInTouchEventCount.touchCount){
                    postDelayed(mInTouchEventCount,600);
                }
                break;
            case MotionEvent.ACTION_UP:
                upX=0;
                upY=0;
                upX=event.getX();
                upY=event.getY();
                if ((upY-downY<TOUCHSLOP) | upY-downY<TOUCHSLOP){
                    mInTouchEventCount.touchCount++;
                }else{
                    mInTouchEventCount.touchCount=0;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                //送你一行代码。。。。。。。。。
                break;
        }

        invalidate();//重绘组件
        return true;
    }


    public void setClickListener(MouseTouchView.OnClickListener l) {
        mOnClickListener = l;
    }

    public boolean performDoubleClick() {
        boolean result = false;
        if(mOnClickListener != null) {
            mOnClickListener.onDoubleClick(this);
            result = true;
        }
        return result;
    }
    public  boolean performSingleClick() {
        boolean result = false;
        if(mOnClickListener != null) {
            mOnClickListener.onSingleClick(this);
            result = true;
        }
        return result;
    }


    /**
     * 点击事件的接口
     */
    public interface OnClickListener{
        void onDoubleClick(View v);
        void onSingleClick(View v);
    }


    /**
     * 计时器
     */
   private class TouchEventCountThread implements Runnable{
        int touchCount = 0;
        @Override
        public void run() {
            Message message=new Message();
            if (touchCount!=0){
                message.arg1 = touchCount;
                mTouchEventHandler.sendMessage(message);
                touchCount = 0;
            }
        }
    }

    /**
     * 这是一个好玩的东西
     */
   private class TouchEventHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if(2 == msg.arg1)
                performDoubleClick();
            if (1 == msg.arg1)
                performSingleClick();

        }
    }

}
