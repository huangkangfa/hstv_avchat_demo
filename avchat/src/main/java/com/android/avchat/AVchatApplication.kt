package com.android.avchat

import com.android.avchat.interfaces.IApplicationInterface
import com.android.baselib.BaseApplication

abstract class AVchatApplication : BaseApplication(), IApplicationInterface {

    override fun onCreate() {
        super.onCreate()
        AVChatManager.init(geTencentAppId(),getSecretKey(),getV2TIMSDKConfig(), getV2TIMSDKListener())
    }

}