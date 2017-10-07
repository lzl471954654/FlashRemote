package com.lp.flashremote.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.lp.flashremote.R
import com.lp.flashremote.beans.UserInfo
import com.lp.flashremote.utils.SharePerferenceUtil
import kotlinx.android.synthetic.main.activity_account.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class AccountActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)
        initView()
    }

    private fun initView() {
        account_ac_commit.setOnClickListener(this@AccountActivity)
        supportActionBar?.title = "账户设置"
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.account_ac_commit -> {
                if (account_ac_id.text.toString() == "" || account_ac_pass.text.toString() == "") {
                    showSnackBar(account_ac_commit, "用户名密码不能为空！")
                    return
                }
                UserInfo.setUsername(account_ac_id.text.toString())
                UserInfo.setPassword(account_ac_pass.text.toString())
                SharePerferenceUtil.saveUserInfo(applicationContext,UserInfo.getUsername(),UserInfo.getPassword());
                showSnackBar(account_ac_commit, "设置完成！")

                doAsync {
                    Thread.sleep(500)
                    uiThread { finish() }
                }
            }
        }
    }
}
