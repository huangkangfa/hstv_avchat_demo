package com.android.baselib

import android.app.Application
import com.android.baselib.global.AppGlobal
import initAutoSize
import initXLog

open class BaseApplication : Application(){

    override fun onCreate() {
        super.onCreate()
        AppGlobal.init(this)
        initXLog()
        initAutoSize()
    }

}