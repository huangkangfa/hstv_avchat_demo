package com.android.avchat.impl

import com.android.avchat.interfaces.IIMInterface
import com.android.baselib.global.AppGlobal.context
import com.tencent.imsdk.v2.V2TIMCallback
import com.tencent.imsdk.v2.V2TIMManager
import com.tencent.imsdk.v2.V2TIMSDKConfig
import com.tencent.imsdk.v2.V2TIMSDKListener


class IMImpl : IIMInterface {

    /**
     * 初始化IM
     */
    override fun init(sdkAppID: Int, sdkConfig: V2TIMSDKConfig, listener: V2TIMSDKListener) {
        V2TIMManager.getInstance().initSDK(context, sdkAppID, sdkConfig, listener)
    }

    /**
     * 获取 V2TIMManager
     */
    override fun getClinet(): V2TIMManager {
        return V2TIMManager.getInstance()
    }

    /**
     * IM登录
     */
    override fun login(userID: String, userSig: String, callback: V2TIMCallback) {
        V2TIMManager.getInstance().login(userID, userSig, callback)
    }

    /**
     * IM退出登录
     */
    override fun logout(callback: V2TIMCallback) {
        V2TIMManager.getInstance().logout(callback)
    }

    /**
     * IM退出登录
     */
    override fun logout() {
        V2TIMManager.getInstance().logout(object : V2TIMCallback {
            override fun onSuccess() {
            }

            override fun onError(p0: Int, p1: String?) {
            }
        })
    }

}