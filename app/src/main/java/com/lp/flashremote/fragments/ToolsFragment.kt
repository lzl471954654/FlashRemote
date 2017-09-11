package com.lp.flashremote.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lp.flashremote.R
import com.lp.flashremote.adapters.ToolsAdapter
import com.lp.flashremote.utils.ToastUtil
import kotlinx.android.synthetic.main.fragment_tools_manager.view.*

/**
 * Created by PUJW on 2017/9/11.
 *  工具操作
 */
class ToolsFragment : Fragment(), View.OnClickListener {

    lateinit var rootView: View
    lateinit var adapter: ToolsAdapter
    lateinit var mStringLists: MutableList<String>

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_tools_manager, container, false)
        rootView = view
        view.tools_rv.layoutManager=LinearLayoutManager(activity)
        view.tools_rv.adapter=adapter

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
        adapter = ToolsAdapter(activity, mStringLists)
    }

    fun initData() {
        mStringLists = ArrayList()
        mStringLists.add("任务管理器")
        mStringLists.add("写字板")
        mStringLists.add("画图")
        mStringLists.add("控制台")
        mStringLists.add("设备管理器")
        mStringLists.add("DVD播放器")
        mStringLists.add("记事本")
        mStringLists.add("讲述人")
        mStringLists.add("事件查看器")
        mStringLists.add("资源管理器")
        mStringLists.add("性能查看器")
        mStringLists.add("注册表")
        mStringLists.add("计算器")
        mStringLists.add("SQL SERVER 客户端网络实用程序")
        mStringLists.add("垃圾整理")
        mStringLists.add("屏幕键盘")
        mStringLists.add("ODBC数据源管理器")
        mStringLists.add("注销命令")
        mStringLists.add("共享文件夹管理器")
        mStringLists.add("辅助工具管理器")
        mStringLists.add("30秒后关机")
        mStringLists.add("30后重启")
        mStringLists.add("取消关机")
    }

    override fun onClick(p0: View?) {

    }


}