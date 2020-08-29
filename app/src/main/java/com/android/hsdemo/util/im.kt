package com.android.hsdemo.util

import android.text.TextUtils
import com.android.hsdemo.model.IMCmd
import com.google.gson.Gson
import com.tencent.imsdk.v2.V2TIMManager
import com.tencent.imsdk.v2.V2TIMMessage
import com.tencent.imsdk.v2.V2TIMSendCallback


/**
 * 发送消息
 */
fun sendMessage(
    msg: V2TIMMessage,
    accid: String,
    listener: V2TIMSendCallback<V2TIMMessage>? = null
) {
    V2TIMManager.getMessageManager().sendMessage(
        msg,
        accid,
        null,
        V2TIMMessage.V2TIM_PRIORITY_NORMAL,
        false,
        null,
        object : V2TIMSendCallback<V2TIMMessage> {
            override fun onError(p0: Int, p1: String?) {
                listener?.onError(p0, p1)
            }

            override fun onSuccess(p0: V2TIMMessage?) {
                listener?.onSuccess(p0)
            }

            override fun onProgress(p0: Int) {
                listener?.onProgress(p0)
            }

        })
}

/**
 * 获取会控自定义消息
 */
fun createIMCmdMessage(
    type: Int,
    accid: String,
    roomId: String,
    extra: String? = null
): V2TIMMessage {
    val cmd = IMCmd()
    cmd.t = type
    cmd.a = accid
    cmd.r = roomId
    if(!TextUtils.isEmpty(extra)){
        cmd.e = extra
    }
    return V2TIMManager.getMessageManager().createCustomMessage(Gson().toJson(cmd).toByteArray())
}