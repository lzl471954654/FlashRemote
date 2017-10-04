package com.lp.flashremote.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.lp.flashremote.R
import com.lp.flashremote.beans.ServerProtocol
import com.lp.flashremote.utils.Command2JsonUtil
import com.lp.flashremote.utils.SocketUtil
import com.lp.flashremote.utils.StringUtil
import kotlinx.android.synthetic.main.toolrecycleview_item.view.*

/**
 * Created by PUJW on 2017/9/11.
 * tools 操作 adapter
 */
class ToolsAdapter(val mContext:Context,val mlists:List<String>,val mSocket:SocketUtil):
        RecyclerView.Adapter<ToolsAdapter.MyViewHolder>() {

    override fun getItemCount(): Int {
        return mlists?.size
    }

    override fun onBindViewHolder(holder: MyViewHolder?, position: Int) {
        when(holder){
            is MyViewHolder-> holder.onBind(position,mlists,mSocket)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.toolrecycleview_item,parent,false))
    }

    inner class MyViewHolder(var root: View) : RecyclerView.ViewHolder(root) {

        fun onBind(position: Int,list:List<String>,mSocket: SocketUtil)
        {
            root.text_tool.text=list.get(position)
            val num=100+position
            val num2=200+position
            root.tool_item.setOnClickListener {
                if (root.close_tool.visibility == View.GONE){
                    root.close_tool.visibility=View.VISIBLE
                    root.open_tool.visibility=View.GONE
                    mSocket.addMessage(StringUtil
                            .operateCmd(Command2JsonUtil
                                    .getJson(num2.toString(),null,false)))

                }else{
                    root.open_tool.visibility=View.VISIBLE
                    root.close_tool.visibility=View.GONE
                    mSocket.addMessage(StringUtil
                            .operateCmd(Command2JsonUtil
                                    .getJson(num.toString(),null,false)))

                }
                Toast.makeText(mContext,"打开成功",Toast.LENGTH_SHORT).show()
            }
        }
    }

}