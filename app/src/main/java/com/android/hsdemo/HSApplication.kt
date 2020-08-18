package com.android.hsdemo

import com.android.avchat.AVchatApplication
import com.android.hsdemo.http.NetConfig
import com.tencent.imsdk.v2.V2TIMSDKConfig
import com.tencent.imsdk.v2.V2TIMSDKListener
import com.tencent.imsdk.v2.V2TIMUserFullInfo

class HSApplication : AVchatApplication() {

    override fun onCreate() {
        super.onCreate()
        NetConfig.init()
    }

    override fun getV2TIMSDKConfig(): V2TIMSDKConfig {
        val config = V2TIMSDKConfig()
        config.setLogLevel(V2TIMSDKConfig.V2TIM_LOG_INFO)
        return config
    }

    override fun getV2TIMSDKListener(): V2TIMSDKListener = object : V2TIMSDKListener() {

        override fun onConnecting() {
            super.onConnecting()
            // 正在连接到腾讯云服务器
        }

        override fun onConnectSuccess() {
            super.onConnectSuccess()
            // 已经成功连接到腾讯云服务器
        }

        override fun onConnectFailed(code: Int, error: String) {
            super.onConnectFailed(code, error)
            // 连接腾讯云服务器失败
        }

        override fun onKickedOffline() {
            super.onKickedOffline()
            // 当前用户被踢下线
        }

        override fun onUserSigExpired() {
            super.onUserSigExpired()
            // 登录票据已经过期
        }

        override fun onSelfInfoUpdated(info: V2TIMUserFullInfo?) {
            super.onSelfInfoUpdated(info)
            // 当前用户的资料发生了更新
        }

    }

    override fun geTencentAppId(): Int {
        return BuildConfig.TENCENT_APP_ID
    }

    override fun getSecretKey(): String {
        return BuildConfig.TENCENT_LIVE_SECRETKEY
    }

}