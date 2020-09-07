package com.android.avchat.interfaces

import com.tencent.rtmp.ui.TXCloudVideoView
import com.tencent.trtc.TRTCCloud
import com.tencent.trtc.TRTCCloudListener

interface IRTCInterface {

    fun getTRTCClient(): TRTCCloud
    fun enterRoom(mUserId: String, mUserSig: String, mRoomId: Int,isFrontCamera: Boolean, localPreviewView: TXCloudVideoView?,mTRTCCloudListener: TRTCCloudListener)
    fun setBeautyParam(beautyStyle: Int, level: Int, whitenessLevel: Int)
    fun setVideoEncoderParam(resolution: Int, fps: Int, bitrate: Int, resolutionMode: Int)
    fun startLocalVedioAndAudio(isFront: Boolean, localPreviewView: TXCloudVideoView?)
    fun stopLocalVedioAndAudio()
    fun exitRoom()
}