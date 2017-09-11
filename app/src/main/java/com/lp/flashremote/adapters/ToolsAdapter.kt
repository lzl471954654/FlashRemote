package com.lp.flashremote.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.lp.flashremote.R
import kotlinx.android.synthetic.main.toolrecycleview_item.view.*

/**
 * Created by PUJW on 2017/9/11.
 * tools 操作 adapter
 */
class ToolsAdapter(val mContext:Context,val mlists:List<String>):
        RecyclerView.Adapter<ToolsAdapter.MyViewHolder>() {



    override fun getItemCount(): Int {
        return mlists?.size
    }

    override fun onBindViewHolder(holder: MyViewHolder?, position: Int) {
        when(holder){
            is MyViewHolder-> holder.onBind(position,mlists)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.toolrecycleview_item,parent,false))
    }


    inner class MyViewHolder(var root: View) : RecyclerView.ViewHolder(root) {

        fun onBind(position: Int,list:List<String>)
        {
            root.tool_item.text=list.get(position)
            root.setOnClickListener {
                Toast.makeText(mContext,list[position],Toast.LENGTH_SHORT).show()
            }
        }
    }

}