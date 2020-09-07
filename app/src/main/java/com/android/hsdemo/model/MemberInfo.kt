package com.android.hsdemo.model

import com.google.gson.annotations.SerializedName

class MemberInfo {
    @SerializedName("userName")
    var userName: String? = null //用户名（登录账号）

    @SerializedName("nickName")
    var nickName: String? = null //昵称

    @SerializedName("account")
    var account: String? = null //成员账号(IM账号)

    @SerializedName("avatar")
    var avatar: String? = null //成员头像

    @SerializedName("mute")
    var mute: Boolean? = false //静音 0未静音；1：静音

    @SerializedName("hostMute")
    var hostMute: Boolean? = false //被主持人静音 0未静音；1：静音

    @SerializedName("state")
    var state: String? = "1" //状态1-未加入，1-在线，2-离开

    @SerializedName("videoOpen")
    var videoOpen: Boolean? = true //摄像头是否开启

    override fun toString(): String {
        return "MemberInfo(userName=$userName, nickName=$nickName, account=$account, avatar=$avatar, mute=$mute, hostMute=$hostMute, state=$state, videoOpen=$videoOpen)"
    }
}