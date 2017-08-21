package com.lp.flashremote.activities

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.storage.StorageManager
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.FrameLayout
import com.lp.flashremote.Model.FileManagerModel
import com.lp.flashremote.Model.FileManagerStatic
import com.lp.flashremote.R
import com.lp.flashremote.adapters.FIleExplorerAdapter
import com.lp.flashremote.beans.BaseFile
import kotlinx.android.synthetic.main.activity_file_explorer.*
import org.jetbrains.anko.*
import java.io.File
import java.io.IOException
import java.util.*

class FileExplorerActivity : AppCompatActivity() {
    companion object {
        val MODE_IN = 1
        val MODE_OUT = 0
        val MODE_EXPLORER = -1
    }

    var mode: Int = MODE_IN
    var dataType: Int = FIleExplorerAdapter.BASE_FILE_TYPE
    var title = ""
    lateinit var dataList: List<BaseFile>
    lateinit var adapter:FIleExplorerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_explorer)
        mode = intent.getIntExtra("mode", MODE_IN)
        dataType = intent.getIntExtra("dataType", FIleExplorerAdapter.BASE_FILE_TYPE)
        title = intent.getStringExtra("title")
        supportActionBar?.title = title
        if (mode == MODE_IN) {
            when(title){
                getString(R.string.manager_video)-> dataList = FileManagerStatic.videoList
                getString(R.string.manager_pic)->dataList = FileManagerStatic.picList
                getString(R.string.manager_music)-> dataList = FileManagerStatic.musicList
                getString(R.string.manager_zip)->dataList = FileManagerStatic.zipList
                getString(R.string.manager_apk)->dataList = FileManagerStatic.apkList
                getString(R.string.manager_document)->dataList = FileManagerStatic.docList
            }
            inModeRun()
        } else if(mode == MODE_OUT)
            outModeRun()
        else if(mode == MODE_EXPLORER)
            explorerMode(intent.getStringExtra("rootPath"))
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
        var nowFile = File(rootPath)
        adapter = FIleExplorerAdapter(FIleExplorerAdapter.BASE_FILE_EXPLORER,this)
        adapter.setData(nowFile)
        file_exp_list.layoutManager = LinearLayoutManager(this)
        file_exp_list.adapter = adapter
        file_exp_list.recycledViewPool
    }

    private fun outModeRun() {

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
        if(adapter.canBack())
            adapter.backtoBeforFolder()
        else
            super.onBackPressed()
    }

}

