package com.lp.flashremote

import android.app.Application
import com.lp.flashremote.beans.PropertiesUtil
import com.lp.flashremote.utils.SharePerferenceUtil
import java.io.File

/**
 * Created by LZL on 2017/9/12.
 *
 */

class FlashApplication:Application(){


    override fun onCreate() {
        super.onCreate()
        PropertiesUtil.getProperties(this)
        SharePerferenceUtil.getUserInfo(this)
        initDataDir()

    }

    private fun initDataDir(){
        val acceptFolder = File(applicationContext.filesDir.absolutePath+File.separator+"accept")
        if(!acceptFolder.exists())
            acceptFolder.mkdir()
    }
}