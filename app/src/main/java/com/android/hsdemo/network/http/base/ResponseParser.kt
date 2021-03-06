package com.android.hsdemo.network.http.base

import android.app.Activity
import com.android.baselib.custom.eventbus.EventBus
import com.android.hsdemo.EventKey
import com.android.hsdemo.network.http.CODE_UNKNOW
import com.android.hsdemo.network.http.getCodeMsg
import com.android.hsdemo.ui.login.ActivityLogin
import rxhttp.wrapper.annotation.Parser
import rxhttp.wrapper.entity.ParameterizedTypeImpl
import rxhttp.wrapper.parse.AbstractParser
import java.lang.reflect.Type

@Parser(name = "Response", wrappers = [List::class])
open class ResponseParser<T> : AbstractParser<T> {
    protected constructor() : super()
    constructor(type: Type) : super(type)

    override fun onParse(response: okhttp3.Response): T {
        val type: Type = ParameterizedTypeImpl[Response::class.java, mType] // 获取泛型类型
        val data: Response<T> = convert(response, type)
        val t: T? = data.ret
        if (data.code != 200 || t == null) {
            if (data.code == 2001) {
                EventBus.with(EventKey.LOGOUT, String::class.java).postValue("")
            }
            throw BusinessThrowable(
                data.code ?: CODE_UNKNOW,
                data.msg ?: getCodeMsg(CODE_UNKNOW)
            )
        }
        return t
    }

}