package com.android.hsdemo.ui.login

import com.android.baselib.base.BaseActivity
import com.android.hsdemo.BR
import com.android.hsdemo.R
import com.android.hsdemo.databinding.ActivityLoginBinding

class ActivityLogin : BaseActivity<VMLogin, ActivityLoginBinding>() {

    override fun getLayoutId(): Int = R.layout.activity_login

    override fun getVariableId(): Int = BR.vm

    override fun afterCreate() {

    }
}