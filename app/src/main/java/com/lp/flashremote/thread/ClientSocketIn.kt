package com.lp.flashremote.thread

/**
 * Created by LZL on 2017/11/25.
 */
interface ClientSocketIn {

    fun getMessage():ByteArray

    fun dispatchMessage()

    fun register(r : RegisterAble)

    fun unRegister(r : RegisterAble)
}

interface RegisterAble{

}