package com.lp.flashremote.thread

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.LinkedBlockingQueue


/**
 * Created by LZL on 2017/11/25.
 */
class ClientOut(private val outputStream: OutputStream):Thread(),ClinetSocketOut {

    private val queue = LinkedBlockingDeque<ByteArray>(1024)

    override fun run() {
        try {
            while (!Thread.interrupted()){
                sendMessage()
            }
        }catch (e:InterruptedException){
            /*
            * 处理中断
            * */
        }catch (e:IOException){
            /*
            * 处理发送IO异常
            * */
        }
    }

    fun stopSend(){
        interrupt()
    }

    override fun sendMessage(){
        val bytes = queue.takeFirst()
        outputStream.write(bytes)
    }

    override fun addMessage(byteArray: ByteArray) {
        queue.putLast(byteArray)
    }

    override fun addMessageHighLevel(byteArray: ByteArray) {
        queue.putFirst(byteArray)
    }
}