package com.lp.flashremote.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.lp.flashremote.R
import com.lp.flashremote.activities.PcFileDirActivity
import com.lp.flashremote.beans.FileInfo
import kotlinx.android.synthetic.main.activity_pc_file_dir.view.*
import kotlinx.android.synthetic.main.activity_pcoperation.view.*
import kotlinx.android.synthetic.main.view_file_exp_item.view.*
import kotlinx.android.synthetic.main.view_file_pc_item.view.*
import org.jetbrains.anko.AnkoAsyncContext
import org.jetbrains.anko.image

/**
 * Created by xiyou3g on 2017/9/21.
 *
 */
class FilePcAdapter(val data:List<FileInfo>, val context: AnkoAsyncContext<PcFileDirActivity>):
        RecyclerView.Adapter<FilePcAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(parent?.context).inflate(R.layout.view_file_pc_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        when(holder){
            is ViewHolder -> holder.onBind(position,data)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ViewHolder(val view: View):RecyclerView.ViewHolder(view) {
        fun onBind(position: Int,list: List<FileInfo>){
            val b:Boolean
            b=list.get(position).isType
            if (list.get(position).isType){
                view.file_pc_item_icon.setImageResource(R.mipmap.icon_folder)
            }else{
                view.file_pc_item_icon.setImageResource(R.mipmap.icon_item_file)
            }

            view.file_pc_item_name.text=list.get(position).name
        }
    }

}