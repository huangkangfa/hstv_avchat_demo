package com.android.avchat

import com.android.avchat.impl.IMImpl
import com.android.avchat.impl.RTCImpl
import com.android.avchat.interfaces.IIMInterface
import com.android.avchat.interfaces.IRTCInterface
import com.elvishew.xlog.XLog
import com.tencent.imsdk.v2.V2TIMCallback
import com.tencent.imsdk.v2.V2TIMManager
import com.tencent.imsdk.v2.V2TIMSDKConfig
import com.tencent.imsdk.v2.V2TIMSDKListener
import com.tencent.rtmp.TXLiveConstants
import com.tencent.rtmp.ui.TXCloudVideoView
import com.tencent.trtc.TRTCCloud
import com.tencent.trtc.TRTCCloudDef
import com.tencent.trtc.TRTCCloudListener

object AVChatManager {

    /**
     * IM   即时通讯
     */
    private val mIMImpl: IIMInterface = IMImpl()

    /**
     * TRTC  音视频
     */
    private val mRTCImpl: IRTCInterface = RTCImpl()

    /**
     * 腾讯云 SDKAppId
     */
    var mSdkAppId: Int = 0

    /**
     * 计算签名用的加密密钥
     */
    var liveSecretKey: String = ""

    /**
     * 初始化
     */
    fun init(
        sdkAppId: Int,
        sdkAppLiveSecretKey: String,
        sdkConfig: V2TIMSDKConfig,
        listener: V2TIMSDKListener
    ) {
        mSdkAppId = sdkAppId
        liveSecretKey = sdkAppLiveSecretKey
        mIMImpl.init(mSdkAppId, sdkConfig, listener)
    }

    /**
     * 获取IM client
     */
    fun getIMClient(): V2TIMManager {
        return mIMImpl.getClinet()
    }

    /**
     * 获取音视频 client
     */
    fun getTRTCClient(): TRTCCloud {
        return mRTCImpl.getTRTCClient()
    }


    /********************************** 通常部分 **********************************************/

    /**
     * 登录
     */
    fun login(userID: String, userSig: String, callback: V2TIMCallback) {
        mIMImpl.login(userID, userSig, callback)
    }

    /**
     * 登出
     */
    fun logout(callback: V2TIMCallback) {
        mIMImpl.logout(callback)
    }

    /**
     * 登出
     */
    fun logout() {
        mIMImpl.logout()
    }

    /********************************** 音视频部分 **********************************************/

    /**
     * 创建/加入 视频会议房间
     */
    fun enterRoom(
        mUserId: String,
        mUserSig: String,
        mRoomId: Int,
        isFrontCamera: Boolean,
        localPreviewView: TXCloudVideoView,
        mTRTCCloudListener: TRTCCloudListener
    ) {
        mRTCImpl.enterRoom(mUserId, mUserSig, mRoomId, isFrontCamera, localPreviewView,mTRTCCloudListener)
        mRTCImpl.setBeautyParam(TXLiveConstants.BEAUTY_STYLE_NATURE,5,1)
        mRTCImpl.setVideoEncoderParam(TRTCCloudDef.TRTC_VIDEO_RESOLUTION_640_360,15,550,TRTCCloudDef.TRTC_VIDEO_RESOLUTION_MODE_PORTRAIT)
    }

    /**
     * 离开视频会议房间
     */
    fun exitRoom() {
        mRTCImpl.exitRoom()
    }

    /**
     * 设置默认美颜效果
     * beautyStyle 美颜风格.三种美颜风格：0 ：光滑  1：自然  2：朦胧
     * level 美颜级别
     * whitenessLevel 美白级别
     */
    fun setBeautyParam(beautyStyle: Int, level: Int, whitenessLevel: Int) {
        mRTCImpl.setBeautyParam(beautyStyle, level, whitenessLevel)
    }

    /**
     * 视频基本设置
     * resolution 分辨率 TRTCCloudDef.TRTC_VIDEO_RESOLUTION_1280_720
     * fps
     * bitrate  带宽
     * resolutionMode 横竖屏  TRTCCloudDef.TRTC_VIDEO_RESOLUTION_MODE_PORTRAIT
     */
    fun setVideoEncoderParam(
        resolution: Int,
        fps: Int,
        bitrate: Int,
        resolutionMode: Int
    ) {
        mRTCImpl.setVideoEncoderParam(resolution, fps, bitrate, resolutionMode)
    }

}


















