package com.lp.flashremote.thread

/**
 * Created by LZL on 2017/11/25.
 */
interface ClinetSocketOut {
    /**
    *  发送消息
    * */
    fun sendMessage()

    /**
    *  添加消息 普通优先级 默认进入队尾排队
     *  @param byteArray 要发送的数据
    * */
    fun addMessage(byteArray: ByteArray)

    /**
    *  添加消息 高优先级 进入对头 直接等待下一次发送 ，适合优先级高的操作
    *  @param byteArray 要发送的数据
    * */
    fun addMessageHighLevel(byteArray: ByteArray)
}