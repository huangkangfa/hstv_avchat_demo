package com.android.hsdemo.http.exception

import android.util.MalformedJsonException
import com.google.gson.JsonParseException
import com.hiwitech.android.shared.http.exception.NimThrowable
import org.json.JSONException
import java.net.ConnectException
import java.text.ParseException

@Suppress("unused", "MemberVisibilityCanBePrivate")
object ExceptionManager {

    const val UNAUTHORIZED = 401
    const val FORBIDDEN = 403
    const val NOT_FOUND = 404
    const val REQUEST_TIMEOUT = 408
    const val INTERNAL_SERVER_ERROR = 500
    const val SERVICE_UNAVAILABLE = 503

    const val UNKNOWN = 1000
    const val PARSE_ERROR = 1001
    const val NETWORD_ERROR = 1002
    const val SSL_ERROR = 1005
    const val TIMEOUT_ERROR = 1006


    fun handleException(throwable: Throwable): BusinessThrowable {
        val ex: BusinessThrowable
        throwable.printStackTrace()
        if (throwable is NimThrowable) {
            ex = BusinessThrowable(throwable.code, throwable.message)
            return ex
        } else if (throwable is JsonParseException
            || throwable is JSONException
            || throwable is ParseException || throwable is MalformedJsonException
        ) {
            ex = BusinessThrowable(PARSE_ERROR, "解析错误")
            return ex
        } else if (throwable is ConnectException) {
            ex = BusinessThrowable(NETWORD_ERROR, "连接失败")
            return ex
        } else if (throwable is javax.net.ssl.SSLException) {
            ex = BusinessThrowable(SSL_ERROR, "证书验证失败")
            return ex
        } else if (throwable is java.net.SocketTimeoutException) {
            ex = BusinessThrowable(TIMEOUT_ERROR, "连接超时")
            return ex
        } else if (throwable is java.net.UnknownHostException) {
            ex = BusinessThrowable(TIMEOUT_ERROR, "主机地址未知")
            return ex
        } else if (throwable is BusinessThrowable) {
            ex = throwable
            return ex
        } else {
            ex = BusinessThrowable(UNKNOWN, "未知错误")
            return ex
        }
    }

}