package com.lp.flashremote.views

import android.app.Dialog
import android.content.Context
import android.view.View
import com.lp.flashremote.R
import kotlinx.android.synthetic.main.view_sendd_dialog.view.*

/**
 * Created by LZL on 2017/9/21.
 */
class SendChoiceDialog(context: Context,val pcSendListener : (View)->Unit,val phoneSendListener : (View)->Unit):Dialog(context) {

    override fun show() {
        val view = View.inflate(context, R.layout.view_sendd_dialog,null)
        setContentView(view)
        view.send__to_pc.setOnClickListener(pcSendListener)
        view.send_to_phone.setOnClickListener(phoneSendListener)
        setCancelable(true)
        super.show()
    }
}