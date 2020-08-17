package com.android.baselib.global

import android.app.Application
import android.content.Context

object AppGlobal {
    private lateinit var application: Application

    val context: Context by lazy {
        application.applicationContext
    }

    fun init(application: Application): AppGlobal {
        AppGlobal.application = application
        CacheGlobal.initDir()
        return this
    }
}