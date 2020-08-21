package com.android.hsdemo.model

import com.google.gson.annotations.SerializedName

class MeetingDetail : Meeting() {
    @SerializedName("meetingStatus")
    var meetingStatus: Boolean? = null //创建者id
    @SerializedName("memberInfos")
    var memberInfos: ArrayList<MemberInfo>? = null
}