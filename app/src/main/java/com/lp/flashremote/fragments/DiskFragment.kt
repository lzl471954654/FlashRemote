package com.lp.flashremote.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lp.flashremote.R
import com.lp.flashremote.utils.SocketUtil
import com.lp.flashremote.utils.StringUtil
import kotlinx.android.synthetic.main.disk_fagment.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


/**
 * Created by PUJW on 2017/9/12.
 * 磁盘分区
 */
class DiskFragment(val mdiskSocket:SocketUtil): Fragment(){
    lateinit var  rootView:View
     var result:String ?=null

    /*val handle: Handler = object :Handler(){
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            when(msg!!.what){
                1->rootView.textView.text=msg.obj.toString()

                0->rootView.textView.text="网络不好，请重新加载。。。"
            }
        }
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView=inflater!!.inflate(R.layout.disk_fagment,container,false)
        mdiskSocket.addMessage(StringUtil.operateCmd("4","getDisk"))
        doAsync {
            result=mdiskSocket.readLine(mdiskSocket.reader)
            uiThread {
                rootView.textView.text=result
            }

        }
        return rootView

    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {

    }
}