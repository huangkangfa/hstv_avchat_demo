package com.android.avchat.impl

import com.android.avchat.AVChatManager
import com.android.avchat.interfaces.IRTCInterface
import com.android.baselib.global.AppGlobal.context
import com.elvishew.xlog.XLog
import com.tencent.rtmp.TXLiveConstants
import com.tencent.rtmp.TXLiveConstants.BEAUTY_STYLE_NATURE
import com.tencent.rtmp.ui.TXCloudVideoView
import com.tencent.trtc.TRTCCloud
import com.tencent.trtc.TRTCCloudDef
import com.tencent.trtc.TRTCCloudDef.TRTCParams
import com.tencent.trtc.TRTCCloudDef.TRTCVideoEncParam
import com.tencent.trtc.TRTCCloudListener

class RTCImpl : IRTCInterface {

    private lateinit var mTRTCCloud: TRTCCloud

    /**
     * 获取音视频sdk单例
     */
    override fun getTRTCClient(): TRTCCloud {
        return mTRTCCloud
    }

    /**
     * 加入房间
     */
    override fun enterRoom(
        mUserId: String,
        mUserSig: String,
        mRoomId: Int,
        isFrontCamera: Boolean,
        localPreviewView: TXCloudVideoView,
        mTRTCCloudListener: TRTCCloudListener
    ) {
        mTRTCCloud = TRTCCloud.sharedInstance(context)
        mTRTCCloud.setListener(mTRTCCloudListener)
        val trtcParams = TRTCParams()
        trtcParams.sdkAppId = AVChatManager.mSdkAppId
        trtcParams.userId = mUserId
        trtcParams.roomId = mRoomId
        trtcParams.userSig = mUserSig
        trtcParams.role = TRTCCloudDef.TRTCRoleAnchor

        setBeautyParam(BEAUTY_STYLE_NATURE,5,1)
        setVideoEncoderParam(TRTCCloudDef.TRTC_VIDEO_RESOLUTION_640_360,15,550,TRTCCloudDef.TRTC_VIDEO_RESOLUTION_MODE_LANDSCAPE)

        mTRTCCloud.enterRoom(trtcParams, TRTCCloudDef.TRTC_APP_SCENE_VIDEOCALL)

        startLocalVedioAndAudio(isFrontCamera, localPreviewView)
    }

    /**
     * 退出房间
     */
    override fun exitRoom() {
        stopLocalVedioAndAudio()
        mTRTCCloud.exitRoom()
        mTRTCCloud.setListener(null)
        TRTCCloud.destroySharedInstance()
    }

    /**
     * 开启音视频本地采集与上行
     */
    override fun startLocalVedioAndAudio(
        isFront: Boolean,
        localPreviewView: TXCloudVideoView
    ) {
        // 开启本地声音采集并上行
        mTRTCCloud.startLocalAudio()
        // 开启本地画面采集并上行
        mTRTCCloud.startLocalPreview(isFront, localPreviewView)
    }

    /**
     * 关闭音视频本地采集与上行
     */
    override fun stopLocalVedioAndAudio() {
        // 关闭本地声音采集并上行
        mTRTCCloud.stopLocalAudio()
        // 关闭本地画面采集并上行
        mTRTCCloud.stopLocalPreview()
    }

    /**
     * 设置默认美颜效果
     * beautyStyle 美颜风格.三种美颜风格：0 ：光滑  1：自然  2：朦胧
     * level 美颜级别
     * whitenessLevel 美白级别
     */
    override fun setBeautyParam(beautyStyle: Int, level: Int, whitenessLevel: Int) {
        val beautyManager = mTRTCCloud.beautyManager
        beautyManager.setBeautyStyle(beautyStyle)
        beautyManager.setBeautyLevel(level)
        beautyManager.setWhitenessLevel(whitenessLevel)
    }

    /**
     * 视频基本设置
     * resolution 分辨率 TRTCCloudDef.TRTC_VIDEO_RESOLUTION_1280_720
     * fps
     * bitrate  带宽
     * resolutionMode 横竖屏  TRTCCloudDef.TRTC_VIDEO_RESOLUTION_MODE_PORTRAIT
     */
    override fun setVideoEncoderParam(
        resolution: Int,
        fps: Int,
        bitrate: Int,
        resolutionMode: Int
    ) {
        val encParam = TRTCVideoEncParam()
        encParam.videoResolution = resolution
        encParam.videoFps = fps
        encParam.videoBitrate = bitrate
        encParam.videoResolutionMode = resolutionMode
        mTRTCCloud.setVideoEncoderParam(encParam)
    }

}