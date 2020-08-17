package com.android.hsdemo.http.parser

import com.android.hsdemo.http.entry.Response
import com.android.hsdemo.http.exception.BusinessThrowable
import com.android.hsdemo.http.exception.ExceptionManager
import rxhttp.wrapper.annotation.Parser
import rxhttp.wrapper.entity.ParameterizedTypeImpl
import rxhttp.wrapper.parse.AbstractParser
import java.lang.reflect.Type

@Parser(name = "Response", wrappers = [List::class])
open class ResponseParser<T> : AbstractParser<T> {
    constructor() : super()
    constructor(type: Type) : super(type)

    override fun onParse(response: okhttp3.Response): T {
        val type: Type = ParameterizedTypeImpl[Response::class.java, mType] // 获取泛型类型
        val data: Response<T> = convert(response, type)
        val t: T? = data.ret
        if (data.code != 0 || t == null) {
            throw BusinessThrowable(
                data.code ?: ExceptionManager.UNKNOWN,
                data.code?.toString() ?: "未知错误"
            )
        }
        return t
    }
}