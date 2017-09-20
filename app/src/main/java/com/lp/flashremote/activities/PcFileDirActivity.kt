package com.lp.flashremote.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import com.lp.flashremote.R
import com.lp.flashremote.beans.UserInfo
import com.lp.flashremote.utils.Command2JsonUtil
import com.lp.flashremote.utils.SocketUtil
import com.lp.flashremote.utils.StringUtil
import kotlinx.android.synthetic.main.activity_pc_file_dir.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class PcFileDirActivity : AppCompatActivity() {

    lateinit var mSocket: SocketUtil
    lateinit var result:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pc_file_dir)
        val p = intent.getStringExtra("ROOTPATH")
        mSocket= SocketUtil.getInstance(UserInfo.getUsername(),UserInfo.getPassword());
        mSocket.addMessage(StringUtil.operateCmd(Command2JsonUtil
               .getJson("4", p, true)))
       doAsync {
           result = mSocket.readLine()
           uiThread {
              ppppp.text=result;
           }
       }
        doAsync {  }
    }
}
