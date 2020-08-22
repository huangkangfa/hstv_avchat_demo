package com.android.hsdemo.model

import com.google.gson.annotations.SerializedName

open class Meeting {
    @SerializedName("id")
    var id: Long? = null //会议id
    @SerializedName("title")
    var title: String? = null //会议名称
    @SerializedName("meetingNo")
    var meetingNo: String? = null //会议编号
    @SerializedName("allMute")
    var allMute: Boolean? = null //全员静音
    @SerializedName("masterId")
    var masterId: String? = null //主持人Id
    @SerializedName("creater")
    var creater: String? = null //创建者id
}