package com.lp.flashremote.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lp.flashremote.R



/**
 * Created by PUJW on 2017/9/12.
 * 磁盘分区
 */
class DiskFragment: Fragment(){
    lateinit var  rootView:View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView=inflater!!.inflate(R.layout.disk_fagment,container,false)
        return rootView

    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {

    }
}