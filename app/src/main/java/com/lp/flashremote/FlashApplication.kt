package com.lp.flashremote

import android.app.Application
import android.os.Environment
import java.io.File



/**
 * Created by LZL on 2017/9/12.
 *
 */

class FlashApplication:Application(){


    override fun onCreate() {
        super.onCreate()
        initDataDir()
    }

    private fun initDataDir(){
        val acceptFolder = File(Environment.getExternalStorageDirectory().path+File.separator+"Consmitor")
        if(!acceptFolder.exists())
            acceptFolder.mkdir()
        FlashApplication.acceptFolder = acceptFolder.absolutePath
    }

    companion object {
        lateinit var acceptFolder:String
    }
}