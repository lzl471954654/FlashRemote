package com.lp.flashremote.activities

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import com.google.gson.Gson
import com.lp.flashremote.FlashApplication

import com.lp.flashremote.R
import com.lp.flashremote.SocketInterface
import com.lp.flashremote.adapters.FilePcAdapter
import com.lp.flashremote.beans.*
import com.lp.flashremote.utils.*
import com.lp.flashremote.views.MyProgressDialog
import kotlinx.android.synthetic.main.activity_pc_file_dir.*
import org.jetbrains.anko.*
import java.io.File
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

class PcFileDirActivity : AppCompatActivity() , View.OnClickListener{


    var phoneSocket:PhoneRemoteSocket? = PhoneRemoteSocket.getNowInstance()
    lateinit var fileinfos:List<FileInfo>
    lateinit var result:String
    var adapter: FilePcAdapter? = null
    lateinit var mContext:Context

    lateinit var progress:MyProgressDialog
    lateinit var socket:SocketInterface
    var mode = "pc"

    val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pc_file_dir)
        val rootPath = intent.getStringExtra("ROOTPATH")
        mode = intent.getStringExtra("mode")
        if(mode == "pc"){
            socket = SocketUtil.getInstance()
        }else if(mode == "phone")
        {
            socket = PhoneRemoteSocket.getNowInstance()
        }
        else if(mode == "wifi"){
            socket = WifiSocketUtil.getNowInstance()
        }
        Log.e("PcFileAc","mode is "+mode+"\t socket is "+(socket==null))
        supportActionBar?.title="目录浏览"
        mContext=this
        initView()
        initData(rootPath)
    }


    private fun addMessage(msg: String){
        socket.addMessage(msg)
    }

    private fun readLine():String{
        return socket.readLine()
    }

    private fun writeBytes(bytes:ByteArray){
        socket.addBytes(bytes)
    }

    private fun initView() {
        val viewArray = arrayOf(file_pc_delete,file_pc_send)
        viewArray.forEach { it.setOnClickListener(this) }
    }

    private fun initData(rootPath:String) {
        /*if(mode == "pc")
            mSocket = SocketUtil.getInstance(UserInfo.getUsername(), UserInfo.getPassword())*/
        socket.addMessage(StringUtil.operateCmd(Command2JsonUtil
                .getJson("4", rootPath, true)))
        /*mSocket.addMessage(StringUtil.operateCmd(Command2JsonUtil
                .getJson("4", rootPath, true)))*/
        doAsync {
            /*result = mSocket.readLine()*/
            result = readLine()
            println("result:"+result)
            uiThread {
                if (result!=""){
                    fileinfos=GsonAnalysiUtil.getFileList(StringUtil.rmEnd_flagstr(result))
                    /*if(mode == "pc"){
                        adapter= FilePcAdapter(fileinfos.toMutableList(),this@PcFileDirActivity,socket)
                    }else{
                        adapter= FilePcAdapter(fileinfos.toMutableList(),this@PcFileDirActivity,PhoneRemoteSocket.getNowInstance())
                    }*/
                    adapter= FilePcAdapter(fileinfos.toMutableList(),this@PcFileDirActivity,socket)
                    file_pc__list.layoutManager= LinearLayoutManager(mContext)
                    file_pc__list.adapter=adapter
                }
            }
        }

    }

    public fun changeBottomBarState(size:Int){
        file_pc_bottom_bar.visibility =
                if (size==0)
                {
                    file_pc_bottom_bar.bottomPadding = dip(48)
                    View.INVISIBLE
                }
                else
                {
                    file_pc_bottom_bar.bottomPadding = 0
                    View.VISIBLE
                }
    }

    override fun onBackPressed() {
        println(adapter === null)
        if (adapter!=null)
        {
            if(adapter!!.canBack())
                adapter?.backToMother()
            else
                super.onBackPressed()
        }
        else
            finish()
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.file_pc_delete->{
                deleteFileOrDirs()
            }
            R.id.file_pc_send->{
                downloadFile()
            }
        }
    }

    private fun refreshProgress(double: Double){
        val s = (double*100).toInt().toString()
        progress.changeText("已完成$s%")
    }

    fun downloadFile(){
        progress = MyProgressDialog(this@PcFileDirActivity,"正在下载文件，请稍后")
        progress.show()
        doAsync {
            var dirFlag = false
            adapter?.chooseFile?.forEach {
                if (it.isType){
                    dirFlag = true
                    return@forEach
                }
            }
            if(dirFlag){
                uiThread { showSnackBar(file_pc_delete,"对不起不支持文件夹下载");progress.dismiss() }
                return@doAsync
            }
            val list = adapter?.chooseFile
            var command = "${PropertiesUtil.GET_FILE}_${gson.toJson(list)}"

            socket.addMessage(command)
            var result = socket.readLine()
            if(result.startsWith(PropertiesUtil.FILE_LIST_FLAG)){
                val content = StringUtil.getContent(result).content
                command = "${PropertiesUtil.FILE_READY}_$content"
                if (mode == "pc"){
                    val fileCommand = FileCommand("",gson.fromJson(content,Array<FileDescribe>::class.java),false)
                    command = PropertiesUtil.FILE_READY+"_"+gson.toJson(fileCommand)
                }
                socket.addMessage(command)
                val files:Array<FileDescribe> = gson.fromJson(content,Array<FileDescribe>::class.java)
                var restCount = 0L
                var restByte = ByteArray(4096)
                var totalSize = 0L
                var sumSize = 0L
                files.forEach { totalSize+=it.fileSize }
                files.forEach {
                    val file = File(FlashApplication.acceptFolder+File.separator+it.fileName+"."+it.fileType)
                    println("filePath:${file.absoluteFile}")
                    if(file.exists())
                        file.delete()
                    else
                        file.createNewFile()
                    var fileSize = it.fileSize
                    var count = 0
                    var sum = 0L
                    val input = socket.inputStream
                    val out = file.outputStream()
                    val bytes = ByteArray(4096)
                    if(restCount!=0L){
                        out.write(restByte,0,restCount.toInt())
                        sum+=restCount
                        restCount = 0L
                    }

                    while(true){
                        count = input.read(bytes)
                        if(count==-1)
                            break;
                        out.write(bytes,0,count)
                        out.flush()
                        sumSize+=count
                        if((sum+count)<=fileSize)
                            sum+=count
                        else{
                            restCount = (sum+count)-fileSize
                            sum+=(count-restCount)
                            var j = 0
                            for (i in count-restCount until count ){
                                restByte[j] = bytes[i.toInt()]
                                j++
                            }
                        }
                        //println("count is $count  sum is $sum  fileSize is $fileSize  restCount is $restCount")
                        if(sum>=fileSize){
                            break
                        }
                        uiThread { refreshProgress(((sumSize.toDouble())/totalSize.toDouble())) }
                    }
                    out.close()
                    println("sumSize is "+sum)
                }
                uiThread { progress.dismiss();showSnackBar(file_pc_delete,"下载完成") }
            }
        }
    }

    fun deleteFileOrDirs(){
        progress = MyProgressDialog(this@PcFileDirActivity,"正在删除，请稍后")
        progress.show()
        doAsync {
            val list = LinkedList<String>()
            adapter?.chooseFile?.forEach { list.add(it.path) }
            val command = "${PropertiesUtil.FILE_DELETE}_${Gson().toJson(list)}"

            socket.addMessage(command)
            val result = socket.readLine()
            val size = result.split("_")[1].toInt()
            println(result)
            if(size==0){
                uiThread { progress.dismiss();showSnackBar(file_pc_send,"删除失败") }
            }else{
                adapter?.data?.removeAll(adapter!!.chooseFile)
                adapter?.chooseFile?.clear()
                uiThread { progress.dismiss();showSnackBar(file_pc_send,"成功删除${size}个文件"); adapter?.notifyDataSetChanged();changeBottomBarState(0)}
            }
        }
    }

}
