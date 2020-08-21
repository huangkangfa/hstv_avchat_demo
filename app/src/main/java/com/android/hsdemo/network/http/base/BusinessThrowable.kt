package com.android.hsdemo.network.http.base

class BusinessThrowable(var code: Int, override var message: String) : RuntimeException()
