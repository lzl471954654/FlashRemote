package com.lp.flashremote.utils

import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

/**
 * Created by LZL on 2017/9/12.
 */

class OkHttpUtils{
    companion object {
        @JvmField
        val client = OkHttpClient.Builder()
                .readTimeout(10,TimeUnit.SECONDS)
                .connectTimeout(10,TimeUnit.SECONDS)
                .build()

        @JvmStatic
        fun sendAdviceToServer(advice: String): Boolean {
            try {
                val body = FormBody.Builder()
                        .add("advice",advice)
                        .build()
                val request = Request.Builder()
                        .url("http://139.199.20.248:8080/WanXiyou/FlashRemote/feedback")
                        .post(body)
                        .build()
                val call = client.newCall(request)
                val response = call.execute()
                val code = response.code()
                val result = response.header("code")
                response.close()
                return code==200&&result=="1"
            }catch (e:SocketTimeoutException){
                e.printStackTrace()
                return false
            }
        }
    }
}