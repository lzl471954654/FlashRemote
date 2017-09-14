package com.lp.flashremote.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.storage.StorageManager
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.FrameLayout
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.lp.flashremote.Model.FileManagerModel
import com.lp.flashremote.Model.FileManagerStatic
import com.lp.flashremote.R
import com.lp.flashremote.adapters.FIleExplorerAdapter
import com.lp.flashremote.beans.BaseFile
import com.lp.flashremote.beans.FileDescribe
import com.lp.flashremote.beans.ServerProtocol
import com.lp.flashremote.beans.UserInfo
import com.lp.flashremote.utils.SocketUtil
import com.lp.flashremote.views.MyProgressDialog
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.activity_file_explorer.*
import kotlinx.android.synthetic.main.view_file_exp_item.*
import org.jetbrains.anko.*
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class FileExplorerActivity : AppCompatActivity(),View.OnClickListener {
    companion object {
        val MODE_IN = 1
        val MODE_OUT = 0
        val MODE_EXPLORER = -1
        lateinit var copyFileList:ArrayList<File>
        lateinit var copyBaseFileList:ArrayList<BaseFile>
    }

    lateinit var progressDialog:MyProgressDialog
    var mode: Int = MODE_IN
    var dataType: Int = FIleExplorerAdapter.BASE_FILE_TYPE
    var title = ""
    lateinit var dataList: ArrayList<BaseFile>
    lateinit var adapter:FIleExplorerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_explorer)
        initView()
        mode = intent.getIntExtra("mode", MODE_IN)
        dataType = intent.getIntExtra("dataType", FIleExplorerAdapter.BASE_FILE_TYPE)
        title = intent.getStringExtra("title")
        supportActionBar?.title = title
        if (mode == MODE_IN) {
            when(title){
                getString(R.string.manager_video)-> dataList = FileManagerStatic.videoList as ArrayList<BaseFile>
                getString(R.string.manager_pic)->dataList = FileManagerStatic.picList
                getString(R.string.manager_music)-> dataList = FileManagerStatic.musicList as ArrayList<BaseFile>
                getString(R.string.manager_zip)->dataList = FileManagerStatic.zipList
                getString(R.string.manager_apk)->dataList = FileManagerStatic.apkList
                getString(R.string.manager_document)->dataList = FileManagerStatic.docList
            }
            inModeRun()
        } else if(mode == MODE_OUT)
            outModeRun(intent.getStringExtra("rootPath"))
        else if(mode == MODE_EXPLORER)
            explorerMode(intent.getStringExtra("rootPath"))
    }

    private fun initView(){
        val viewArray = arrayOf(file_exp_copy,file_exp_delete,file_exp_move,file_exp_send,file_exp_select_all,file_exp_paste)
        viewArray.forEach { it.setOnClickListener(this) }
    }

    private fun inModeRun() {
        if (dataList.isEmpty()) {
            file_exp_no_file_image.visibility = View.VISIBLE
            file_exp_bottom_bar.visibility = View.INVISIBLE
            return
        }
        adapter = FIleExplorerAdapter(dataType,this)
        adapter.setData(dataList)
        file_exp_list.layoutManager = LinearLayoutManager(this)
        file_exp_list.adapter = adapter
    }

    private fun explorerMode(rootPath:String){
        val nowFile = File(rootPath)
        adapter = FIleExplorerAdapter(FIleExplorerAdapter.BASE_FILE_EXPLORER,this)
        adapter.setData(nowFile)
        file_exp_list.layoutManager = LinearLayoutManager(this)
        file_exp_list.adapter = adapter
    }

    private fun outModeRun(rootPath: String) {
        val nowFile = File(rootPath)
        adapter = FIleExplorerAdapter(FIleExplorerAdapter.BASE_FILE_EXPLORER,this)
        adapter.hideSelector()
        adapter.setData(nowFile)
        file_exp_paste.visibility = View.VISIBLE
        file_exp_list.layoutManager = LinearLayoutManager(this)
        file_exp_list.adapter = adapter
    }

    fun copyFile(){
        showProgressDialog("正在复制")
        val parentPath = adapter.rootDir.absolutePath
        doAsync {
            when(intent.getIntExtra("srcMode",-1)){
                MODE_EXPLORER->{
                    copyFileList.forEach {
                        val file = File(parentPath+File.separator+it.name)
                        if(it.isDirectory){
                            it.copyRecursively(file,true){
                                dir: File, ioException: IOException ->
                                ioException.printStackTrace()
                                uiThread { showSnackBar(file_exp_bottom_bar,"文件${file.name}复制失败") }
                                OnErrorAction.SKIP
                            }
                        }else{
                            it.copyTo(file,true,4096)
                        }
                    }
                }
                MODE_IN->{
                    copyBaseFileList.forEach{
                        val srcFile = File(it.filePath)
                        val targetFile = File(parentPath+File.separator+it.fileName)
                        srcFile.copyTo(targetFile,true,4096)
                    }
                }
            }
            Thread.sleep(1000)
            uiThread { dissmisProgressDialog();finish() }
        }

    }
    fun moveFile(){
        showProgressDialog("正在移动")
        val parentPath = adapter.rootDir.absolutePath
        doAsync {
            when(intent.getIntExtra("srcMode",-1)){
                MODE_EXPLORER->{
                    copyFileList.forEach {
                        it.renameTo(File(parentPath+File.separator+it.name))
                    }
                }
                MODE_IN->{
                    copyBaseFileList.forEach {
                        val file = File(it.filePath)
                        file.renameTo(File(parentPath+File.separator+it.fileName))
                    }
                }
            }
            Thread.sleep(1000)
            uiThread { dissmisProgressDialog();finish()}
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.file_exp_paste->{
                when(intent.getStringExtra("action")){
                    "copy"-> copyFile()
                    "move"-> moveFile()
                    else-> showSnackBar(v,"action error")
                }
            }
            R.id.file_exp_copy->{
                if(mode == MODE_EXPLORER)
                    copyFileList = adapter.chooseFile
                else if(mode == MODE_IN)
                    copyBaseFileList = adapter.chooseList
                startActivity<FileExplorerActivity>("mode" to FileExplorerActivity.MODE_OUT,"srcMode" to mode,"action" to "copy","dataType" to FIleExplorerAdapter.BASE_FILE_EXPLORER,"title" to "选择粘贴目录","rootPath" to Environment.getExternalStorageDirectory().path)
            }
            R.id.file_exp_move->{
                if(mode == MODE_EXPLORER)
                    copyFileList = adapter.chooseFile
                else if(mode == MODE_IN)
                    copyBaseFileList = adapter.chooseList
                startActivity<FileExplorerActivity>("mode" to FileExplorerActivity.MODE_OUT,"srcMode" to mode,"action" to "move","dataType" to FIleExplorerAdapter.BASE_FILE_EXPLORER,"title" to "选择移动目录","rootPath" to Environment.getExternalStorageDirectory().path)
            }
            R.id.file_exp_select_all->{
                adapter.selectAll()
            }
            R.id.file_exp_send->{
                sendFile()
            }
            R.id.file_exp_delete->{
                try {
                    deleteFiles()
                }catch (e:IOException){
                    e.printStackTrace()
                    showSnackBar(file_exp_bottom_bar,"删除文件时出错")
                    if(progressDialog.isShowing)
                        progressDialog.dismiss()
                }
            }
        }
    }

    private fun sendFile(){
        val s = "hello World!"
        val bytes = s.toByteArray()
        val desc = FileDescribe("hello","txt",bytes.size.toLong())
        val socket = SocketUtil.getInstance(UserInfo.getUsername(),UserInfo.getPassword())
        val gson = Gson()
        val cmd = "201_${gson.toJson(desc)}_${ServerProtocol.END_FLAG}"
        doAsync {
            socket.addMessage(cmd)
            println("cmd : $cmd")
            val resp = socket.readLine(socket.reader)
            println("resp : $resp")
            if(resp.startsWith(ServerProtocol.FILE_READY))
            {
                socket.socketOutput.write(bytes)
                uiThread { showSnackBar(file_exp_copy,"发送成功！") }
            }
        }
    }

    public fun showNoFileImage(){
        file_exp_no_file_image.visibility = View.VISIBLE
    }

    public fun hideNoFileImage(){
        file_exp_no_file_image.visibility = View.INVISIBLE
    }
    public fun showBottomBar(size:Int){
        if(size>0)
        {
            file_exp_list.bottomPadding = dip(48)
            (file_exp_bottom_bar as FrameLayout).visibility = View.VISIBLE
        }
        else
        {
            file_exp_list.bottomPadding = 0
            (file_exp_bottom_bar as FrameLayout).visibility = View.INVISIBLE
        }
    }

    fun deleteFiles(){
        showProgressDialog("请稍后正在删除文件")
        if(mode == MODE_EXPLORER){
            doAsync {
                adapter.chooseFile.forEach {
                    if (it.isDirectory)
                        println(it.deleteRecursively())
                    else
                        println(it.delete())
                }
                adapter.chooseFile.clear()
                Thread.sleep(1000)
                uiThread {
                    dissmisProgressDialog()
                    adapter.rootDir = adapter.rootDir
                    adapter.notifyDataSetChanged()
                    showSnackBar(file_exp_bottom_bar,"删除完成")
                }
            }
        }
        else{
            doAsync {
                adapter.chooseList.forEach {
                    println(File(it.filePath).delete())
                }
                adapter.dataList.removeAll(adapter.chooseList)
                adapter.chooseList.clear()
                Thread.sleep(1000)
                uiThread {
                    dissmisProgressDialog()
                    showSnackBar(file_exp_bottom_bar,"删除完成")
                    adapter.notifyDataSetChanged()
                }
            }
        }
        showBottomBar(-1)
    }

    fun showProgressDialog(msg:String){
        progressDialog = MyProgressDialog(this@FileExplorerActivity,msg)
        progressDialog.show()
    }

    fun dissmisProgressDialog(){
        progressDialog.dismiss()
    }

    fun openFile(path:String){
        val type = FileManagerModel.getFileType(path)
        val intent = Intent()
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.setAction(Intent.ACTION_VIEW)
        try{
            intent.setDataAndType(Uri.fromFile(File(path)),type)
            startActivity(intent)
        }catch (e:Exception){
            e.printStackTrace()
            Snackbar.make(file_exp_list,getString(R.string.file_exp_no_open_style),Snackbar.LENGTH_SHORT).show()

        }
    }

    override fun onBackPressed() {
        if(mode == MODE_EXPLORER||mode == MODE_OUT){
            if(adapter.canBack())
                adapter.backtoBeforFolder()
            else
                super.onBackPressed()
        }
        else
            super.onBackPressed()
    }
    fun showSnackBar(view:View,msg:String){
        Snackbar.make(view,msg,Snackbar.LENGTH_SHORT).show()
    }

}


