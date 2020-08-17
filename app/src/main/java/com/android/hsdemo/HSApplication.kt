package com.android.hsdemo

import com.android.baselib.BaseApplication
import com.android.hsdemo.http.NetConfig

class HSApplication : BaseApplication(){

    override fun onCreate() {
        super.onCreate()
        NetConfig.init()
    }

}