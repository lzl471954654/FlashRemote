package com.lp.flashremote.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.ImageView
import com.lp.flashremote.R
import com.lp.flashremote.activities.FileExplorerActivity
import com.lp.flashremote.beans.BaseFile
import com.lp.flashremote.beans.VideoFile
import kotlinx.android.synthetic.main.view_file_exp_item.view.*
import java.io.File
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates

/**
 * Created by LZL on 2017/8/20.
 */
class FIleExplorerAdapter(val dataType:Int,val context: Context) : RecyclerView.Adapter<FIleExplorerAdapter.NormalViewHolder>(){
    companion object {
        val BASE_FILE_TYPE = 1
        val IMAGE_TYPE = 2
        val VIDEO_TYPE = 3
        val BASE_FILE_EXPLORER = 4
    }

    val chooseList = ArrayList<BaseFile>()
    val chooseFile = ArrayList<File>()
    lateinit var fileFlag:BooleanArray
    lateinit var dataList:ArrayList<BaseFile>
    var rootDir:File by Delegates.observable(File("/")){
        property, oldValue, newValue ->
        fileList = newValue.listFiles()
        fileFlag = BooleanArray(fileList.size)
    }
    lateinit var fileList:Array<File>
    val stack:Stack<File> = Stack()

    public fun setData(list:ArrayList<BaseFile>){
        dataList = list
    }

    public fun setData(file:File){
        rootDir = file
    }

    public fun selectAll(){
        println("fileList: ${fileList.size}  chooseFile: ${chooseFile.size}")
        if(fileList.size!=chooseFile.size){
            for(x in 0 until fileFlag.size)
                fileFlag[x] = true
            chooseFile.clear()
            fileList.forEach { chooseFile.add(it) }
            println("fileList: ${fileList.size}  chooseFile: ${chooseFile.size}")
        }
        else{
            for(x in 0 until fileFlag.size)
                fileFlag[x] = false
            chooseFile.clear()
            println("clear all")
        }
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: NormalViewHolder?, position: Int) {
        if(holder is BaseFileViewHolder)
            holder?.onBind(position,dataList,chooseList)
        if(holder is ExplorerFileViewHolder)
            holder?.onBind(fileList,position,chooseFile)
    }


    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): NormalViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.view_file_exp_item,parent,false)
        return if(dataType == BASE_FILE_EXPLORER)
            ExplorerFileViewHolder(view,context,this)
        else
            BaseFileViewHolder(view,context)
    }

    private fun moveToNextFolder(file: File){
        rootDir = file
        if (fileList.size==0)
            (context as FileExplorerActivity).showNoFileImage()
        notifyDataSetChanged()
    }

    public fun backtoBeforFolder(){
        rootDir = stack.pop()
        (context as FileExplorerActivity).hideNoFileImage()
        notifyDataSetChanged()
    }

    public fun canBack():Boolean{
        return !stack.isEmpty()
    }
    override fun getItemCount(): Int {
        return if (dataType == BASE_FILE_EXPLORER)
        {
            fileList.size
        }
        else dataList.size
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }


    class ExplorerFileViewHolder(view: View,context: Context,var adapter: FIleExplorerAdapter):NormalViewHolder(view,context){
        fun onBind(fileArray:Array<File>,position: Int,chooseFile:ArrayList<File>){
            val nowFile = fileArray[position]
            root.setOnClickListener{
                if (nowFile.isDirectory){
                    adapter.stack.push(nowFile.parentFile)
                    adapter.moveToNextFolder(nowFile)
                }
                else
                {
                    (context as FileExplorerActivity).openFile(nowFile.absolutePath)
                }
            }
            root.file_exp_item_name.text = nowFile.name
            root.file_exp_item_size.visibility = View.INVISIBLE
            root.file_exp_item_size.text = ""
            root.file_exp_item_date.text = getStringDate(nowFile.lastModified())
            if(nowFile.isDirectory){
                root.file_exp_item_icon.setImageResource(R.mipmap.icon_folder)
            }
            else{
                root.file_exp_item_icon.setImageResource(getFileIcon(nowFile.absolutePath))
            }
            root.file_exp_item_check.setOnCheckedChangeListener {
                button, isChecked ->
                adapter.fileFlag[position] = isChecked
                if(isChecked&&!chooseFile.contains(nowFile)){
                    chooseFile.add(nowFile)
                }
                if(!isChecked&&chooseFile.contains(nowFile))
                {
                    chooseFile.remove(nowFile)
                }
                (context as FileExplorerActivity).showBottomBar(chooseFile.size)
            }
            root.file_exp_item_check.isChecked = adapter.fileFlag[position]
        }
    }

    open class NormalViewHolder: RecyclerView.ViewHolder{
        lateinit var root:View
        lateinit var context:Context
        constructor(view: View,context:Context):super(view){
            root = view
            this.context = context
        }

        fun getFileIcon(path:String):Int{
            iconType.forEach {
                if (path.endsWith(it)){
                    return iconMap[it]!!
                }
            }
            return R.mipmap.icon_item_file
        }


        companion object {
            val iconType= arrayOf(".doc", ".docx", ".ppt", ".pptx", ".pdf", ".xlsx", ".xls", ".txt", ".zip", ".rar", ".cab", ".gzip", ".tar", ".apk")
            val iconMap = mapOf<String,Int>(
                    ".txt" to R.mipmap.txt,
                    ".doc" to R.mipmap.doc,
                    ".docx" to R.mipmap.docx,
                    ".ppt" to R.mipmap.ppt,
                    ".pptx" to R.mipmap.pptx,
                    ".xls" to R.mipmap.xls,
                    ".xlsx" to R.mipmap.xlsx,
                    ".zip" to R.mipmap.zip,
                    ".cab" to R.mipmap.cab,
                    ".tar" to R.mipmap.tar,
                    ".apk" to R.mipmap.apk,
                    ".pdf" to R.mipmap.pdf,
                    ".gzip" to R.mipmap.gzip,
                    ".rar" to R.mipmap.rar
            )

        }
    }
    open class BaseFileViewHolder: NormalViewHolder{
        constructor(view: View,context:Context):super(view,context){
            root = view
            this.context = context
        }

        open fun onBind(position:Int,list:ArrayList<BaseFile>,chooseList:ArrayList<BaseFile>){
            root.setOnClickListener{
                (context as FileExplorerActivity).openFile(list[position].filePath)
            }
            root.file_exp_item_date.text = getStringDate(list[position].fileCreateDate*1000)
            root.file_exp_item_name.text = list[position].fileName
            root.file_exp_item_size.text = getSizeText(list[position].fileSize)
            root.file_exp_item_icon.setImageResource(getFileIcon(list[position].filePath))
            root.file_exp_item_check.setOnCheckedChangeListener{
                button: CompoundButton?, isChecked: Boolean ->
                list[position].isChoosed = isChecked
                if (isChecked&&!chooseList.contains(list[position]))
                    chooseList.add(list[position])
                else
                    chooseList.remove(list[position])
                (context as FileExplorerActivity).showBottomBar(chooseList.size)
            }
            root.file_exp_item_check.isChecked = list[position].isChoosed
        }

    }
}

public fun getSizeText(size: Long):String{
    val KB = 1024L
    val MB = KB*1024L
    val GB = MB*1024L
    val df = DecimalFormat("#.00")
    return when {
        size in 0..KB -> "$size B"
        size in KB+1..MB -> "${df.format((size.toDouble()/KB))} KB"
        size in MB+1..GB -> "${df.format((size.toDouble()/MB))} MB"
        else -> "${df.format((size.toDouble()/GB))} GB"
    }
}

fun getStringDate(time:Long):String{
    val dataFormatter = SimpleDateFormat("yyyy-MM-dd")
    return if(time==0L) "-/-/-" else dataFormatter.format(Date(time))

}