package com.android.hsdemo.network.http.base

import com.google.gson.annotations.SerializedName

data class Response<T> (
    @SerializedName("code")
    var code: Int? = null,
    @SerializedName("ret")
    var ret: T? = null,
    @SerializedName("msg")
    var msg: String? = null,
    @SerializedName("requestId")
    var requestId: String? = null
)