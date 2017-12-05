package com.lp.flashremote.services

import NewVersion.ProtocolField
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.lp.flashremote.FlashApplication
import com.lp.flashremote.beans.PackByteArray
import com.lp.flashremote.beans.UserInfo
import com.lp.flashremote.thread.ClinetSocketOut
import com.lp.flashremote.utils.IntConvertUtils
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket
import java.net.SocketException
import java.net.SocketTimeoutException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.locks.ReentrantLock

/**
 * Created by lzl on 17-12-4.
 */
class ConnectionManagerService : Service() {
    private lateinit var binder: ConnectionBinder
    private lateinit var map: ConcurrentHashMap<String, ConnectionCallBack>
    private var connectionThread: ConnectionThread? = null
    private var clientOut: ClientOut? = null
    @Volatile
    private var stopFlag = false
    private val lock = ReentrantLock(false)
    override fun onCreate() {
        super.onCreate()
        binder = ConnectionBinder()
        map = ConcurrentHashMap()
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        lock.lock()
        try {
            if (connectionThread == null) {
                connectionThread = ConnectionThread(UserInfo.getUsername(), UserInfo.getPassword())
                connectionThread?.start()
            } else if (!connectionThread!!.isAlive) {
                connectionThread = ConnectionThread(UserInfo.getUsername(), UserInfo.getPassword())
                connectionThread?.start()
            }
        } finally {
            lock.unlock()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        val name = intent?.getStringExtra("bindName") ?: throw NullPointerException("bindName should not be NULL !")
        val callback = intent.getSerializableExtra("callBack") as ConnectionCallBack
        map.put(name, callback)
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        val name = intent?.getStringExtra("bindName") ?: throw NullPointerException("bindName should not be NULL !")
        val callback = intent.getSerializableExtra("callBack") as ConnectionCallBack
        return map.remove(name, callback)
    }

    inner class ConnectionBinder : Binder() {
        public fun stopService() {
            stopFlag = true
            clientOut?.stopSend()
        }

        public fun addData(data: PackByteArray) {

        }

        public fun isConnected(): Boolean = connectionThread != null && connectionThread!!.isAlive
    }


    override fun onDestroy() {
        super.onDestroy()
        map.forEach { it.value.serviceShutdodwn() }
        map.clear()
    }

    private fun disConnected(){
        map.forEach { it.value.connectionDisconnected() }
    }


    inner class ConnectionThread(private val userName: String, private val passWord: String) : Thread("ConnectionThread") {

        override fun run() {
            try {
                val socket = Socket(FlashApplication.ServerIP, 10086)
                val input = socket.getInputStream()
                val out = socket.getOutputStream()
                val loginData = ("$userName|$passWord").toByteArray(charset("UTF-8"))
                out.sendDataWithFlag(ProtocolField.phoneOnline, loginData)
                val flag = input.read().toByte()
                if (flag == ProtocolField.onlineSuccess) {
                    println("onlineSuccess")
                    try {
                        lock.lock()
                        clientOut = if (clientOut == null) {
                            ClientOut(socket.getOutputStream())
                        } else if (!clientOut!!.isAlive) {
                            ClientOut(socket.getOutputStream())
                        } else {
                            clientOut!!.interrupt()
                            ClientOut(socket.getOutputStream())
                        }
                        clientOut!!.start()
                    } finally {
                        lock.unlock()
                    }
                    map.forEach { it.value.connectionSuccess() }
                } else {
                    println("flag is $flag")
                }
            } catch (e: SocketException) {
                e.printStackTrace()
            } catch (e: SocketTimeoutException) {
                e.printStackTrace()
            }
        }
    }


    inner class ClientOut(private val outputStream: OutputStream) : Thread("ClientOut"), ClinetSocketOut {

        private val queue = LinkedBlockingDeque<PackByteArray>(1024)

        override fun run() {
            try {
                while (!Thread.interrupted() && !stopFlag) {
                    sendMessage()
                }
            } catch (e: InterruptedException) {
                /*
                * 处理中断
                * */
                logException(this@ConnectionManagerService, e)
            } catch (e: IOException) {
                /*
                * 处理发送IO异常
                * */
                logException(this@ConnectionManagerService, e)
            } catch (e: SocketException) {
                /*
                * Socket Exception
                * */
                logException(this@ConnectionManagerService, e)
            } finally {
                Log.e(name, "Client Out Stop")

            }
        }

        fun stopSend() {
            interrupt()
        }

        override fun sendMessage() {
            val bytes = queue.takeFirst()
            outputStream.sendDataWithFlag(bytes.flag, bytes.body)
        }

        override fun addMessage(byteArray: PackByteArray) {
            queue.putLast(byteArray)
        }

        override fun addMessageHighLevel(byteArray: PackByteArray) {
            queue.putFirst(byteArray)
        }
    }


    inner class ClientIn(private val inputStream: InputStream) : Thread("ClientIn") {
        override fun run() {
            try {
                while (!Thread.interrupted() && !stopFlag) {
                    val flag = readByte()
                    val sizeArray = readBySize(2)
                    val msgSize = IntConvertUtils.getShortByByteArray(sizeArray).toInt()
                    val packArray = if (msgSize == 0)
                        packArray(flag, null)
                    else
                        packArray(flag, readBySize(msgSize))
                    dispatchData(packArray)
                }
            } catch (e: InterruptedException) {
                /*
                * 处理中断
                * */
                logException(this@ConnectionManagerService, e)
            } catch (e: IOException) {
                /*
                * 处理IO异常
                * */
                logException(this@ConnectionManagerService, e)
            } catch (e: SocketException) {
                /*
                * Socket Exception
                * */
                logException(this@ConnectionManagerService, e)
            } finally {
                Log.e(name, "Client In Stop")
            }
        }

        private fun dispatchData(packByteArray: PackByteArray) {
            when (packByteArray.flag) {
                in 0x10..0x1f -> {
                    Log.e(name, "file command , id is ${packByteArray.flag}")
                }
                in 0x20..0x4f -> {
                    Log.e(name, "normal command , id is ${packByteArray.flag}")
                }
                else -> {
                    Log.e(name,"other command , id is ${packByteArray.flag}")
                }
            }
        }

        private fun readByte(): Byte {
            val flag = inputStream.read()
            if (flag == -1)
                throw IOException("read == -1")
            return flag.toByte()
        }

        private fun readBySize(size: Int): ByteArray {
            var count = 0
            val array = ByteArray(size)
            while (count < size) {
                array[count] = readByte()
            }
            return array
        }
    }

}

fun logException(any: Any, exception: Exception) {
    Log.e(any::javaClass.name, "${Thread.currentThread().name} has ${exception::javaClass.name} ${exception.message}")
}

fun OutputStream.sendDataWithFlag(flag: Byte, byteArray: ByteArray?) {
    write(flag.toInt())
    if (byteArray != null) {
        write(IntConvertUtils.getShortBytes(byteArray.size.toShort()))
        write(byteArray)
    }
}

fun OutputStream.sendFlag(flag: Byte) {
    write(flag.toInt())
}

fun packArray(flag: Byte, byteArray: ByteArray?) = PackByteArray(flag, byteArray)

/**
 *  @author LZL
 *  ConnectionCallBack interface
 *  use it to communicate between Service and other Component (for example , Activity , Fragment ,etc..)
 * */
interface ConnectionCallBack {
    fun connectionSuccess()
    fun connectionFailed()
    fun connectionDisconnected()
    fun serviceShutdodwn()
    fun getMessage(data: PackByteArray)
}