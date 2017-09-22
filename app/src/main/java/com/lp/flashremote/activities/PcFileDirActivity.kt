package com.lp.flashremote.activities

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View

import com.lp.flashremote.R
import com.lp.flashremote.adapters.FilePcAdapter
import com.lp.flashremote.beans.FileInfo
import com.lp.flashremote.beans.UserInfo
import com.lp.flashremote.utils.Command2JsonUtil
import com.lp.flashremote.utils.GsonAnalysiUtil
import com.lp.flashremote.utils.SocketUtil
import com.lp.flashremote.utils.StringUtil
import kotlinx.android.synthetic.main.activity_pc_file_dir.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class PcFileDirActivity : AppCompatActivity() , View.OnClickListener{



    lateinit var mSocket: SocketUtil
    lateinit var fileinfos:List<FileInfo>
    lateinit var result:String
    lateinit var adapter: FilePcAdapter
    lateinit var mContext:Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pc_file_dir)
        val rootPath = intent.getStringExtra("ROOTPATH")
        supportActionBar?.title="目录浏览"
        mContext=this
        initView()
        initData(rootPath)
    }


    private fun initView() {
        val viewArray = arrayOf(file_pc_copy,file_pc_delete,file_pc_move,file_pc_send,file_pc_select_all)
        viewArray.forEach { it.setOnClickListener(this) }
    }

    private fun initData(rootPath:String) {
        mSocket = SocketUtil.getInstance(UserInfo.getUsername(), UserInfo.getPassword())
        mSocket.addMessage(StringUtil.operateCmd(Command2JsonUtil
                .getJson("4", rootPath, true)))
        doAsync {
            result = mSocket.readLine()
            uiThread {
                if (result!=null){
                    fileinfos=GsonAnalysiUtil.getFileList(StringUtil.rmEnd_flagstr(result))
                    adapter= FilePcAdapter(fileinfos,this,mSocket)
                    file_pc__list.layoutManager= LinearLayoutManager(mContext)
                    file_pc__list.adapter=adapter
                }
            }
        }

    }

    override fun onBackPressed() {
        if(adapter.canBack())
            adapter.backToMother()
        else
            super.onBackPressed()
    }

    override fun onClick(p0: View?) {

    }

}
