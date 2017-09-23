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
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.lp.flashremote.beans.UserInfo;
import com.lp.flashremote.utils.Command2JsonUtil;
import com.lp.flashremote.utils.SocketUtil;


public class MouseTouchView extends View {

    private SocketUtil mSocket;
    private OnDoubleClickListener mOnDoubleClickListener;
    // 统计500ms内的点击次数
    TouchEventCountThread mInTouchEventCount = new TouchEventCountThread();
    // 根据TouchEventCountThread统计到的点击次数, perform单击还是双击事件
    TouchEventHandler mTouchEventHandler = new TouchEventHandler();

    public MouseTouchView(Context context) {
        super(context);
    }

    public MouseTouchView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public float currentX = -100;
    public float currentY = -100;
    @Override
    protected void onDraw(Canvas canvas) {
        Paint p = new Paint();
        p.setColor(Color.BLUE);
        canvas.drawCircle(currentX, currentY, 70, p);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.currentX = event.getX(); //触摸座标X
        this.currentY = event.getY(); //触摸座标Y
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if (0==mInTouchEventCount.touchCount){
                    postDelayed(mInTouchEventCount,500);
                }
               // mSocket=SocketUtil.getInstance(UserInfo.getUsername(),UserInfo.getPassword());
               /**
                 * 判断是否连接成功？？
                 **/
                break;
            case MotionEvent.ACTION_UP:
                //是否断开？？
                // 一次点击事件要有按下和抬起, 有抬起必有按下, 所以只需要在ACTION_UP中处理
                mInTouchEventCount.touchCount++;
                // 如果是长按操作, 则Handler的消息,不能将touchCount置0, 需要特殊处理
                break;
        }
      /*  String mouseop=Command2JsonUtil.getMouseJson(currentX,currentY,false,false,false,false);
        mSocket.addMessage(StringUtil.operateCmd(Command2JsonUtil.getJson("3",mouseop,false)));*/
        Log.e("yyyyyyyyyy",currentX+"------"+currentY);
        invalidate();//重绘组件
        return true;//返回true：事件已处理，此处必须返回true，否则小球移动不了。
    }

    public void setOnDoubleClickListener(MouseTouchView.OnDoubleClickListener l) {
        mOnDoubleClickListener = l;
    }


    class TouchEventCountThread implements Runnable{
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

     class TouchEventHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            if(2 == msg.arg1)
                performDoubleClick();

        }
    }


    public boolean performDoubleClick() {
        boolean result = false;
        if(mOnDoubleClickListener != null) {
            mOnDoubleClickListener.onDoubleClick(this);
            result = true;
        }
        return result;
    }

    public interface OnDoubleClickListener{
        void onDoubleClick(View v);
    }

}
