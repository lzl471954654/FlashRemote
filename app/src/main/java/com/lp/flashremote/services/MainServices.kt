package com.lp.flashremote.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder

/**
 * Created by xiyou3g on 2017/11/26.
 *  主服务
 *  连接socket主服务
 */
class MainServices: Service() {

    private val mBinder=SocketBinder()

    override fun onBind(intent: Intent?): IBinder {
        return mBinder
    }

    class SocketBinder : Binder(){
        /**
         * 连接时显示进度条
         */
        fun showProgress(){
        }

        fun hideProgress() {
        }

        /**
         * 发送通知到通知栏
         */
        fun sendnotify(){

        }

    }

}