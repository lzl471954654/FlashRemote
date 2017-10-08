package com.lp.flashremote.activities

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.google.gson.Gson

import com.lp.flashremote.R
import com.lp.flashremote.adapters.FilePcAdapter
import com.lp.flashremote.beans.FileInfo
import com.lp.flashremote.beans.PropertiesUtil
import com.lp.flashremote.beans.UserInfo
import com.lp.flashremote.utils.*
import com.lp.flashremote.views.MyProgressDialog
import kotlinx.android.synthetic.main.activity_pc_file_dir.*
import org.jetbrains.anko.bottomPadding
import org.jetbrains.anko.dip
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*

class PcFileDirActivity : AppCompatActivity() , View.OnClickListener{


    var phoneSocket:PhoneRemoteSocket? = PhoneRemoteSocket.getNowInstance()
    lateinit var mSocket: SocketUtil
    lateinit var fileinfos:List<FileInfo>
    lateinit var result:String
    var adapter: FilePcAdapter? = null
    lateinit var mContext:Context

    lateinit var progress:MyProgressDialog

    var mode = "pc"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pc_file_dir)
        val rootPath = intent.getStringExtra("ROOTPATH")
        mode = intent.getStringExtra("mode")
        supportActionBar?.title="目录浏览"
        mContext=this
        initView()
        initData(rootPath)
    }


    private fun addMessage(msg: String){
        if (mode == "phone"){
            PhoneRemoteSocket.getNowInstance().addMessage(msg)
        }else{
            mSocket.addMessage(msg)
        }
    }

    private fun readLine():String{
        var msg = ""
        if(mode == "phone"){
            msg = PhoneRemoteSocket.getNowInstance().readLine()
        }else{
            msg = mSocket.readLine()
        }
        return msg
    }

    private fun writeBytes(bytes:ByteArray){
        if(mode == "phone"){
            PhoneRemoteSocket.getNowInstance().addBytes(bytes)
        }else{
            mSocket.addBytes(bytes)
        }
    }

    private fun initView() {
        val viewArray = arrayOf(file_pc_delete,file_pc_send)
        viewArray.forEach { it.setOnClickListener(this) }
    }

    private fun initData(rootPath:String) {
        if(mode == "pc")
            mSocket = SocketUtil.getInstance(UserInfo.getUsername(), UserInfo.getPassword())
        addMessage(StringUtil.operateCmd(Command2JsonUtil
                .getJson("4", rootPath, true)))
        /*mSocket.addMessage(StringUtil.operateCmd(Command2JsonUtil
                .getJson("4", rootPath, true)))*/
        doAsync {
            /*result = mSocket.readLine()*/
            result = readLine()
            println("result:"+result)
            uiThread {
                if (result!=""){
                    fileinfos=GsonAnalysiUtil.getFileList(StringUtil.rmEnd_flagstr(result))
                    if(mode == "pc"){
                        adapter= FilePcAdapter(fileinfos.toMutableList(),this@PcFileDirActivity,mSocket)
                    }else{
                        adapter= FilePcAdapter(fileinfos.toMutableList(),this@PcFileDirActivity,PhoneRemoteSocket.getNowInstance())
                    }
                    file_pc__list.layoutManager= LinearLayoutManager(mContext)
                    file_pc__list.adapter=adapter
                }
            }
        }

    }

    public fun changeBottomBarState(size:Int){
        file_pc_bottom_bar.visibility =
                if (size==0)
                {
                    file_pc_bottom_bar.bottomPadding = dip(48)
                    View.INVISIBLE
                }
                else
                {
                    file_pc_bottom_bar.bottomPadding = 0
                    View.VISIBLE
                }
    }

    override fun onBackPressed() {
        println(adapter === null)
        if (adapter!=null)
        {
            if(adapter!!.canBack())
                adapter?.backToMother()
            else
                super.onBackPressed()
        }
        else
            finish()
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.file_pc_delete->{
                deleteFileOrDirs()
            }
            R.id.file_pc_send->{
                downloadFile()
            }
        }
    }

    fun downloadFile(){
        progress = MyProgressDialog(this@PcFileDirActivity,"正在下载文件，请稍后")
        progress.show()
        if(mode == "pc")
        {
            doAsync {
                SocketUtil.getInstance().sendTestMessage(object : SocketUtil.ConnectListener {
                    override fun connectError() {
                        uiThread { progress.dismiss();showSnackBar(file_pc_send,"对不起连接中断") }
                    }

                    override fun connectSusess() {

                    }
                })
            }
        }
    }

    fun deleteFileOrDirs(){
        progress = MyProgressDialog(this@PcFileDirActivity,"正在删除，请稍后")
        progress.show()
        if(mode == "pc"){
            doAsync {
                SocketUtil.getInstance(UserInfo.getUsername(),UserInfo.getPassword()).sendTestMessage(object : SocketUtil.ConnectListener {
                    override fun connectError() {
                        uiThread { progress.dismiss();showSnackBar(file_pc_send,"对不起连接中断") }
                    }

                    override fun connectSusess() {
                        val list = LinkedList<String>()
                        adapter?.chooseFile?.forEach { list.add(it.path) }
                        val command = "${PropertiesUtil.FILE_DELETE}_${Gson().toJson(list)}"
                        val socket = SocketUtil.getInstance()
                        socket.addMessage(command)
                        val result = socket.readLine()
                        val size = result.split("_")[1].toInt()
                        println(result)
                        if(size==0){
                            uiThread { progress.dismiss();showSnackBar(file_pc_send,"删除失败") }
                        }else{
                            adapter?.data?.removeAll(adapter!!.chooseFile)
                            adapter?.chooseFile?.clear()
                            uiThread { progress.dismiss();showSnackBar(file_pc_send,"成功删除${size}个文件"); adapter?.notifyDataSetChanged();changeBottomBarState(0)}
                        }

                    }
                })
            }
        }
    }

}
