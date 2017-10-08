package com.lp.flashremote.views

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.lp.flashremote.R
import kotlinx.android.synthetic.main.view_my_progress_dialog.view.*

/**
 * Created by LZL on 2017/8/22.
 */
class MyProgressDialog(context: Context,var msg:String):Dialog(context){
    lateinit var  root:View
    override fun show() {
        val view:View = View.inflate(context,R.layout.view_my_progress_dialog,null)
        setContentView(view)
        root = view
        view.dialog_msg.text = msg
        super.show()
    }

    public fun changeText(msg:String){
        root.dialog_msg.text = msg
    }
}