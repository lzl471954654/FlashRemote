package com.lp.flashremote.adapters

import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.lp.flashremote.R
import com.lp.flashremote.SocketInterface
import com.lp.flashremote.activities.PcFileDirActivity
import com.lp.flashremote.beans.FileInfo
import com.lp.flashremote.utils.Command2JsonUtil
import com.lp.flashremote.utils.GsonAnalysiUtil
import com.lp.flashremote.utils.SocketUtil
import com.lp.flashremote.utils.StringUtil
import kotlinx.android.synthetic.main.view_file_pc_item.view.*
import org.jetbrains.anko.AnkoAsyncContext
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*

/**
 * Created by xiyou3g on 2017/9/21.
 *
 */
class FilePcAdapter(var data:MutableList<FileInfo>, val context: Context, val socket:SocketInterface):
        RecyclerView.Adapter<FilePcAdapter.ViewHolder>(){


    val stack: Stack<MutableList<FileInfo>> = Stack() //存储各级列表的栈
    val chooseFile : LinkedList<FileInfo> = LinkedList<FileInfo>() //存储被选中的文件
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(parent?.context).inflate(R.layout.view_file_pc_item,parent,false)
        return ViewHolder(view,this,socket)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        when(holder){
            is ViewHolder -> holder.onBind(position,data)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    private fun moveToNextFolder(nextPath:String,socket: SocketInterface){

        var result:String
        socket.addMessage(StringUtil.operateCmd(Command2JsonUtil.getJson("4",nextPath,true)))
        doAsync {
            result=socket.readLine()
            uiThread {
                if (result!=""){
                    val temp = data;
                    data=GsonAnalysiUtil.getFileList(StringUtil.rmEnd_flagstr(result))
                    stack.push(temp)
                    notifyDataSetChanged()
                }
            }
        }
    }

    public fun canBack():Boolean{
        return !stack.isEmpty()
    }
    public fun backToMother(){
        data=stack.pop()
        notifyDataSetChanged()
    }

    inner class ViewHolder(val view: View ,val adapter:FilePcAdapter,val socket: SocketInterface ):
            RecyclerView.ViewHolder(view) {

        fun onBind(position: Int, list: MutableList<FileInfo>){
            if (list.get(position).isType){
                view.file_pc_item_icon.setImageResource(R.mipmap.icon_folder)
            }else{
                view.file_pc_item_icon.setImageResource(R.mipmap.nofloer)
            }
            view.file_pc_item_name.text=list.get(position).name
            view.setOnClickListener {
                if (list.get(position).isType){
                    //adapter.stack.push(list)
                    adapter.moveToNextFolder(list.get(position).path,socket)
                }
            }
            view.file_pc_item_check.setOnCheckedChangeListener{
                button, isChecked ->

                if(isChecked&&!chooseFile.contains(data[position]))
                    chooseFile.add(data[position])
                if(!isChecked)
                    chooseFile.remove(data[position])

                (context as PcFileDirActivity).changeBottomBarState(chooseFile.size)
            }
            view.file_pc_item_check.isChecked = chooseFile.contains(data[position])
        }

    }

}