package com.lp.flashremote.thread

/**
 * Created by xiyou3g on 2017/11/24.
 * 命令接受发送线程
 */
class CommandThread :Thread(),BaseThread {
    /**
     * 命令优先级高于文件
     */
    val PRIORITY_TH=7

    init {
        this.priority=PRIORITY_TH
    }

    override fun sendMessage(b: ByteArray) {
    }

    override fun getMessage(b: ByteArray) {
    }


}