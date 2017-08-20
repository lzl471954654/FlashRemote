package com.lp.flashremote.activities

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.lp.flashremote.Model.FileManagerStatic
import com.lp.flashremote.R
import com.lp.flashremote.adapters.FIleExplorerAdapter
import com.lp.flashremote.beans.BaseFile
import kotlinx.android.synthetic.main.activity_file_explorer.*
import org.jetbrains.anko.bottomPadding
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.dip
import org.jetbrains.anko.px2dip

class FileExplorerActivity : AppCompatActivity() {
    companion object {
        val MODE_IN = 1
        val MODE_OUT = 0
    }

    var mode: Int = MODE_IN
    var dataType: Int = FIleExplorerAdapter.BASE_FILE_TYPE
    var title = ""
    lateinit var dataList: List<BaseFile>
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
        } else
            outModeRun()
    }


    private fun inModeRun() {
        if (dataList.isEmpty()) {
            file_exp_no_file_image.visibility = View.VISIBLE
            file_exp_bottom_bar.visibility = View.INVISIBLE
            return
        }
        var adapter = FIleExplorerAdapter(dataList,dataType,this::showBottomBar)
        file_exp_list.layoutManager = LinearLayoutManager(this)
        file_exp_list.adapter = adapter
    }

    private fun outModeRun() {

    }

    private fun showBottomBar(size:Int){
        if(size>0)
        {
            file_exp_list.bottomPadding = dip(48)
            file_exp_bottom_bar.visibility = View.VISIBLE
        }
        else
        {
            file_exp_list.bottomPadding = 0
            file_exp_bottom_bar.visibility = View.INVISIBLE
        }
    }
}
