package com.lp.flashremote.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder

/**
 * Created by lzl on 17-12-4.
 */
class ConnectionManagerService : Service() {
    var callBack : ConnectionCallBack? = null
    lateinit var binder:ConnectionBinder


    override fun onCreate() {
        super.onCreate()
        binder = ConnectionBinder()
    }

    /**
     * @exception UnsupportedOperationException , call this method will cause this exception
     * this service not support started with this way . only support start with bindService()
     * */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        throw UnsupportedOperationException("ConnectionManagerService should not be start with startService()" +
                ", should start with bindService()")
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    inner class ConnectionBinder:Binder(){

        fun setConnectionCallBack(callBack: ConnectionCallBack){
            this@ConnectionManagerService.callBack = callBack
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        callBack?.serviceShutdodwn()
        callBack = null
    }

}

/**
 *  @author LZL
 *  ConnectionCallBack interface
 *  use it to communicate between Service and other Component (for example , Activity , Fragment ,etc..)
 * */
interface ConnectionCallBack{
    fun connectionSuccess()
    fun connectionFailed()
    fun connectionDisconnected()
    fun serviceShutdodwn()
}