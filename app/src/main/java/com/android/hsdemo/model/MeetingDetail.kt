package com.android.hsdemo.model

import com.google.gson.annotations.SerializedName

class MeetingDetail : Meeting() {
    @SerializedName("meetingStatus")
    var meetingStatus: Boolean? = null //创建者id
    @SerializedName("pwd")
    var pwd: String? = null //会议室密码
    @SerializedName("memberInfos")
    var memberInfos: ArrayList<MemberInfo>? = null
}