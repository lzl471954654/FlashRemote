package com.lp.flashremote.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lp.flashremote.R

/**
 * Created by PUJW on 2017/9/11.
 */
public class ToolsFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view=inflater!!.inflate(R.layout.fragment_tools_manager,container, false)
        return view
    }

}