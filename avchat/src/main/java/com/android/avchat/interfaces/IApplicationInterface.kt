package com.android.avchat.interfaces

import com.tencent.imsdk.v2.V2TIMSDKConfig
import com.tencent.imsdk.v2.V2TIMSDKListener

interface IApplicationInterface {

    /**
     * IM相关参数
     */
    fun getV2TIMSDKConfig(): V2TIMSDKConfig
    fun getV2TIMSDKListener(): V2TIMSDKListener

    /**
     * 音视频相关参数
     */
    fun geTencentAppId(): Int
    fun getSecretKey(): String
}