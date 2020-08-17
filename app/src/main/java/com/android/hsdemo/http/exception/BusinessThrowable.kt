package com.android.hsdemo.http.exception

class BusinessThrowable(var code: Int, override var message: String) : RuntimeException()
