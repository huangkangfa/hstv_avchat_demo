package com.android.hsdemo.model

import com.google.gson.annotations.SerializedName

open class IMCmd {
    /**
     * 会控类型
     * 1-（主持人）全员静音，2-（主持人）取消全员静音，
     * 3-（主持人）设置他人静音，4-（主持人）取消他人静音，
     * 5-设置自己静音，6-取消自己静音，
     * 7-（主持人）踢人，
     * 8-结束会议（没有消息）,
     * 9-自己摄像头打开，10-自己摄像头关闭
     */
    @SerializedName("t")
    var t: Int? = null

    /**
     * 房间号
     */
    @SerializedName("r")
    var r: String? = null

    /**
     * IM账号
     */
    @SerializedName("a")
    var a: String? = null

    /**
     * 扩展字段
     */
    @SerializedName("e")
    var e: String? = null

}