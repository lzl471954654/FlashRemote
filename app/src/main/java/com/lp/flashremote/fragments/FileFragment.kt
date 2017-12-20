package com.lp.flashremote.fragments

import android.app.Activity
import android.content.Context
import android.os.*
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lp.flashremote.Model.FileManagerModel
import com.lp.flashremote.Model.FileManagerStatic

import com.lp.flashremote.R
import com.lp.flashremote.activities.FileExplorerActivity
import com.lp.flashremote.adapters.FIleExplorerAdapter
import com.lp.flashremote.adapters.getSizeText
import com.lp.flashremote.beans.BaseFile
import com.lp.flashremote.beans.MusicFile
import com.lp.flashremote.beans.VideoFile
import kotlinx.android.synthetic.main.fragment_file_manager.*
import org.jetbrains.anko.contentView
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.support.v4.onUiThread
import org.jetbrains.anko.support.v4.startActivity

/**
 * Created by PUJW on 2017/8/14.

 */

class FileFragment : Fragment(), View.OnClickListener {
    lateinit var rootView: View
    lateinit var mContext: Context
    lateinit var fileManager: FileManagerModel
    var hasData = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fileManager = FileManagerModel(mContext, mHandler)
    }

    override fun onDestroy() {
        super.onDestroy()
        mHandler.removeCallbacksAndMessages(null)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_file_manager, container, false)
        rootView = view
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListener()
        fileManager.startCount()
    }

    override fun onResume() {
        super.onResume()
        doAsync {
            Thread.sleep(300)
            onUiThread { getStorageSize()}
        }
    }

    private fun getStorageSize(){
         var blockSize:Long
         var totalSize:Long
         var availableSize:Long
        val fs = StatFs(Environment.getExternalStorageDirectory().path)
        blockSize = fs.blockSizeLong
        totalSize = fs.blockCountLong
        availableSize = fs.availableBlocksLong

        println("BlockSize : $blockSize\tBlockCount : $totalSize\tAvailableBlocks : $availableSize")

        m_storage_size.text = getSizeText(availableSize*blockSize)+"可用"
        println("width : ${m_storage_line.width}\tNewWidth : ${((availableSize.toDouble()/totalSize.toDouble())*m_storage_base_line.width.toDouble()).toInt()}")
        m_storage_line.layoutParams.width = ((1.0-(availableSize.toDouble()/totalSize.toDouble()))*m_storage_base_line.width.toDouble()).toInt()
    }

    private fun setListener() {
        val buttonView = arrayOf(m_video, m_pic, m_apk, m_music, m_document, m_download, m_zip, m_bluetooth, m_remote,m_folder_layout)
        buttonView.forEach {
            it.setOnClickListener(this)
        }
    }

    override fun onClick(v: View?) {
        if (!FileManagerStatic.hasData) {
            showSnackBar(rootView,"请稍后，正在加载文件，加载完成方可浏览")
            return
        }
        when (v?.id) {
            R.id.m_music -> {
                startFileExplorer(getString(R.string.manager_music))
            }
            R.id.m_video -> {
                startFileExplorer(getString(R.string.manager_video))
            }
            R.id.m_document -> {
                startFileExplorer(getString(R.string.manager_document))
            }
            R.id.m_pic -> {
                startFileExplorer(getString(R.string.manager_pic))
            }
            R.id.m_apk -> {
                startFileExplorer(getString(R.string.manager_apk))
            }
            R.id.m_download -> {
                startActivity<FileExplorerActivity>("mode" to FileExplorerActivity.MODE_EXPLORER,
                        "dataType" to FIleExplorerAdapter.BASE_FILE_EXPLORER,"title" to "下载",
                        "rootPath" to Environment.getExternalStorageDirectory().path+"/Download")
            }
            R.id.m_bluetooth -> {
                startActivity<FileExplorerActivity>("mode" to FileExplorerActivity.MODE_EXPLORER,
                        "dataType" to FIleExplorerAdapter.BASE_FILE_EXPLORER,"title" to "蓝牙文件",
                        "rootPath" to Environment.getExternalStorageDirectory().path+"/bluetooth")
            }
            R.id.m_remote -> {

            }
            R.id.m_zip -> {
                startFileExplorer(getString(R.string.manager_zip))
            }
            R.id.m_folder_layout->{
                startActivity<FileExplorerActivity>("mode" to FileExplorerActivity.MODE_EXPLORER,
                        "dataType" to FIleExplorerAdapter.BASE_FILE_EXPLORER,"title" to "目录浏览",
                        "rootPath" to Environment.getExternalStorageDirectory().path)
            }

        }
    }

    private fun refreshCount() {
        if (!isResumed)
            return
        FileManagerStatic.hasData = true

        m_music_count.text = "${FileManagerStatic.musicList.size}项"
        m_music_count.visibility = View.VISIBLE
        m_video_count.text = "${FileManagerStatic.videoList.size}项"
        m_video_count.visibility = View.VISIBLE
        m_pic_count.text = "${FileManagerStatic.picList.size}项"
        m_pic_count.visibility = View.VISIBLE
        m_zip_count.text = "${FileManagerStatic.zipList.size}项"
        m_zip_count.visibility = View.VISIBLE
        m_document_count.text = "${FileManagerStatic.docList.size}项"
        m_document_count.visibility = View.VISIBLE
        m_apk_count.text = "${FileManagerStatic.apkList.size}项"
        m_apk_count.visibility = View.VISIBLE
    }

    private val mHandler = object : Handler() {
        override fun handleMessage(msg: Message?) {
            when (msg?.what) {
                REFRESH_COUNT -> {
                    if (msg != null)
                        refreshCount()
                }
                else -> {

                }
            }
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context != null)
            mContext = context
    }

    companion object {
        val REFRESH_COUNT = 1
    }
}

fun FileFragment.startFileExplorer(title:String){
    val mode = FileExplorerActivity.MODE_IN
    var dataType = FIleExplorerAdapter.BASE_FILE_TYPE
    when(title){
        getString(R.string.manager_pic)->{
            dataType = FIleExplorerAdapter.IMAGE_TYPE
        }
        getString(R.string.manager_video)->{
            dataType = FIleExplorerAdapter.VIDEO_TYPE
        }
    }
    startActivity<FileExplorerActivity>("mode" to mode,"dataType" to dataType,"title" to title)
}

fun Fragment.showSnackBar(view:View,msg: String) {
    Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).show()
}
