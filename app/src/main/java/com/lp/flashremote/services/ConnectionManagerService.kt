package com.lp.flashremote.services

import NewVersion.ProtocolField
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.lp.flashremote.FlashApplication
import com.lp.flashremote.beans.PackByteArray
import com.lp.flashremote.thread.ClinetSocketOut
import com.lp.flashremote.utils.IntConvertUtils
import java.io.IOException
import java.io.OutputStream
import java.net.Socket
import java.net.SocketException
import java.net.SocketTimeoutException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.LinkedBlockingDeque

/**
 * Created by lzl on 17-12-4.
 */
class ConnectionManagerService : Service() {
    private lateinit var binder:ConnectionBinder
    private lateinit var map : ConcurrentHashMap<String,ConnectionCallBack>

    @Volatile
    private var stopFlag = false

    override fun onCreate() {
        super.onCreate()
        binder = ConnectionBinder()
        map = ConcurrentHashMap()
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        val name = intent?.getStringExtra("bindName") ?: throw NullPointerException("bindName should not be NULL !")
        val callback = intent.getSerializableExtra("callBack") as ConnectionCallBack
        map.put(name,callback)
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        val name = intent?.getStringExtra("bindName") ?: throw NullPointerException("bindName should not be NULL !")
        val callback = intent.getSerializableExtra("callBack") as ConnectionCallBack
        return map.remove(name,callback)
    }

    inner class ConnectionBinder:Binder(){
        public fun stopService(){
            stopFlag = true
        }

        public fun startConnection(){

        }
    }


    override fun onDestroy() {
        super.onDestroy()
        map.forEach { it.value.serviceShutdodwn() }
        map.clear()
    }


    inner class ConnectionThread(private val userName:String , private val passWord:String):Thread(){

        override fun run() {
            try{
                val socket = Socket(FlashApplication.ServerIP,10086)
                val input = socket.getInputStream()
                val out = socket.getOutputStream()
                val loginData = ("$userName|$passWord").toByteArray(charset("UTF-8"))
                out.sendDataWithFlag(ProtocolField.phoneOnline,loginData)
            }catch (e:SocketException){
                e.printStackTrace()
            }catch (e:SocketTimeoutException){
                e.printStackTrace()
            }
        }
    }



    inner class ClientOut(private val outputStream: OutputStream):Thread(), ClinetSocketOut {

        private val queue = LinkedBlockingDeque<PackByteArray>(1024)

        override fun run() {
            try {
                while (!Thread.interrupted()){
                    sendMessage()
                }
            }catch (e:InterruptedException){
                /*
                * 处理中断
                * */
            }catch (e: IOException){
                /*
                * 处理发送IO异常
                * */
            }catch (e: SocketException){
                /*
                * Socket Exception
                * */
            }
        }

        fun stopSend(){
            interrupt()
        }

        override fun sendMessage(){
            val bytes = queue.takeFirst()
            outputStream.sendDataWithFlag(bytes.flag,bytes.body)
        }

        override fun addMessage(byteArray: PackByteArray) {
            queue.putLast(byteArray)
        }

        override fun addMessageHighLevel(byteArray: PackByteArray) {
            queue.putFirst(byteArray)
        }
    }

}

fun OutputStream.sendDataWithFlag(flag: Byte , byteArray: ByteArray?){
    write(flag.toInt())
    if(byteArray != null){
        write(IntConvertUtils.getShortBytes(byteArray.size.toShort()))
        write(byteArray)
    }
}

fun OutputStream.sendFlag(flag: Byte){
    write(flag.toInt())
}

fun packArray(flag: Byte , byteArray: ByteArray?) = PackByteArray(flag,byteArray)

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
    fun getMessage(flag : Byte , byteArray: ByteArray)
}