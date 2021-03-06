package com.android.hsdemo.model

import com.google.gson.annotations.SerializedName

class User {
    @SerializedName("id")
    var id: String? = null

    @SerializedName("token")
    var token: String? = null

    @SerializedName("secretKey")
    var secretKey: String? = null

    @SerializedName("userSig")
    var userSig: String? = null

    @SerializedName("userName")
    var userName: String? = null

    @SerializedName("accid")
    var accid: String? = null

    @SerializedName("nickName")
    var nickName: String? = null

    @SerializedName("userAvatar")
    var userAvatar: String? = null

    @SerializedName("account")
    var account: Double? = null

    @SerializedName("meetingNoticeAccount")
    var meetingNoticeAccount: String? = null
}