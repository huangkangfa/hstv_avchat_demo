package com.android.hsdemo.network.http

const val CODE_SUCCESS = 200

const val CODE_UNKNOW = -1

fun getCodeMsg(code: Int): String {
    return when(code){
        CODE_SUCCESS -> "成功"
        CODE_UNKNOW -> "未知错误"
        else -> "未知错误"
    }
}