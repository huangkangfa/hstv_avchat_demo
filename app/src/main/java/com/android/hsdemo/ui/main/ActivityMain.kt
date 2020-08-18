package com.android.hsdemo.ui.main

import com.android.baselib.base.BaseActivity
import com.android.hsdemo.BR
import com.android.hsdemo.R
import com.android.hsdemo.databinding.ActivityMainBinding

class ActivityMain : BaseActivity<VMMain, ActivityMainBinding>() {

    override fun getLayoutId(): Int = R.layout.activity_main

    override fun getVariableId(): Int = BR.vm

    override fun afterCreate() {

    }

}