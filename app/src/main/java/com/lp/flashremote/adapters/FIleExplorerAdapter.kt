package com.lp.flashremote.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.ImageView
import com.lp.flashremote.R
import com.lp.flashremote.beans.BaseFile
import com.lp.flashremote.beans.VideoFile
import kotlinx.android.synthetic.main.view_file_exp_item.view.*
import java.text.DecimalFormat

/**
 * Created by LZL on 2017/8/20.
 */
class FIleExplorerAdapter(var dataList:List<BaseFile>,val dataType:Int,val showBottomBarFun:(Int)->Unit) : RecyclerView.Adapter<FIleExplorerAdapter.BaseFileViewHolder>(){
    companion object {
        val BASE_FILE_TYPE = 1
        val IMAGE_TYPE = 2
        val VIDEO_TYPE = 3
    }

    val chooseList = ArrayList<BaseFile>()

    override fun onBindViewHolder(holder: BaseFileViewHolder?, position: Int) {
        holder?.onBind(position,dataList,chooseList)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): BaseFileViewHolder {
        return BaseFileViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.view_file_exp_item,parent,false),showBottomBarFun)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }


    class BaseFileViewHolder: RecyclerView.ViewHolder{
        lateinit var root:View
        val KB = 1024L
        val MB = KB*1024L
        val GB = MB*1024L
        lateinit var showBottomBarFun:(Int)->Unit
        constructor(view: View,showBottomBarFun:(Int)->Unit):super(view){
            root = view
            this.showBottomBarFun = showBottomBarFun
        }

        fun onBind(position:Int,list:List<BaseFile>,chooseList:ArrayList<BaseFile>){
            println(list[position])
            root.file_exp_item_name.text = list[position].fileName
            root.file_exp_item_size.text = getSizeText(list[position].fileSize)
            root.file_exp_item_check.setOnCheckedChangeListener(){
                button: CompoundButton?, isChecked: Boolean ->
                if (isChecked)
                    chooseList.add(list[position])
                else
                    chooseList.remove(list[position])
                showBottomBarFun.invoke(chooseList.size)
            }

        }

        private fun getSizeText(size:Long):String{
            val df = DecimalFormat("#.00")
            return when {
                size in 0..KB -> "$size B"
                size in KB+1..MB -> "${df.format((size.toDouble()/KB))} KB"
                size in MB+1..GB -> "${df.format((size.toDouble()/MB))} MB"
                else -> "${df.format((size.toDouble()/GB))} GB"
            }
        }
    }
}