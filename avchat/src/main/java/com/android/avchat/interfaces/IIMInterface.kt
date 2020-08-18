package com.android.avchat.interfaces

import com.tencent.imsdk.v2.V2TIMCallback
import com.tencent.imsdk.v2.V2TIMManager
import com.tencent.imsdk.v2.V2TIMSDKConfig
import com.tencent.imsdk.v2.V2TIMSDKListener

interface IIMInterface {
    fun init(sdkAppID: Int, sdkConfig: V2TIMSDKConfig, listener: V2TIMSDKListener)
    fun getClinet(): V2TIMManager
    fun login(userID:String,userSig:String,callback:V2TIMCallback)
    fun logout(callback: V2TIMCallback)
    fun logout()
}