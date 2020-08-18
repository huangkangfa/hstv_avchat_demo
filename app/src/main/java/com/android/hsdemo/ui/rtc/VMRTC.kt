package com.android.hsdemo.ui.rtc

import android.app.Application
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.android.avchat.AVChatManager
import com.android.avchat.AVChatManager.getTRTCClient
import com.android.avchat.GenerateTestUserSig
import com.android.baselib.ActivityManager
import com.android.baselib.global.AppGlobal.context
import com.android.baselib.utils.showShortToast
import com.android.hsdemo.R
import com.tencent.liteav.TXLiteAVCode
import com.tencent.rtmp.ui.TXCloudVideoView
import com.tencent.trtc.TRTCCloudListener

class VMRTC(application: Application) : AndroidViewModel(application) {

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    var drawableOfMuteVideo: MutableLiveData<Drawable?> =
        MutableLiveData(application.getDrawable(R.drawable.rtc_mute_video))

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    var drawableOfMuteAudio: MutableLiveData<Drawable?> =
        MutableLiveData(application.getDrawable(R.drawable.rtc_mute_audio))

    var visibilityOfMuteVideoDefault: MutableLiveData<Int> = MutableLiveData(View.GONE)

    private var mIsFrontCamera: MutableLiveData<Boolean> = MutableLiveData(true) // 默认摄像头前置
    private var mLogLevel: MutableLiveData<Int> = MutableLiveData(0) // 日志等级

    var mRoomId: MutableLiveData<String> = MutableLiveData() // 房间Id
    var mUserId: MutableLiveData<String> = MutableLiveData() // 用户Id

    var mLocalPreviewView: MutableLiveData<TXCloudVideoView> = MutableLiveData()
    var mRemoteUidList: MutableLiveData<ArrayList<String>> =
        MutableLiveData(ArrayList()) // 远端用户Id列表
    var mRemoteViewList: MutableLiveData<ArrayList<TXCloudVideoView>> =
        MutableLiveData(ArrayList()) // 远端画面列表

    /**
     * 返回按钮点击事件
     */
    fun onBackClick(v: View) {
        ActivityManager.finish()
    }

    /**
     * 摄像头开关按钮点击事件
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun onMuteVideoClick(v: View) {
        val isSelected = v.isSelected
        if (!isSelected) {
            getTRTCClient().stopLocalPreview()
            drawableOfMuteVideo.value = context.getDrawable(R.mipmap.rtc_camera_off)
            visibilityOfMuteVideoDefault.value = View.VISIBLE
        } else {
            getTRTCClient().startLocalPreview(mIsFrontCamera.value ?: true, mLocalPreviewView.value)
            drawableOfMuteVideo.value = context.getDrawable(R.mipmap.rtc_camera_on)
            visibilityOfMuteVideoDefault.value = View.GONE
        }
        v.isSelected = !isSelected
    }

    /**
     * 静音开关按钮点击事件
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun onMuteAudioClick(v: View) {
        val isSelected = v.isSelected
        if (!isSelected) {
            getTRTCClient().stopLocalAudio()
            drawableOfMuteAudio.value = context.getDrawable(R.mipmap.rtc_mic_off)
        } else {
            getTRTCClient().startLocalAudio()
            drawableOfMuteAudio.value = context.getDrawable(R.mipmap.rtc_mic_on)
        }
        v.isSelected = !isSelected
    }

    /**
     * 前置摄像头点击事件
     */
    fun onSwitchCameraClick(v: View) {
        getTRTCClient().switchCamera()
        val isSelected = v.isSelected
        mIsFrontCamera.value = !isSelected
        v.isSelected = !isSelected
    }

    /**
     * 是否展示日志点击事件
     */
    fun onShowDebugViewClick(v: View) {
        mLogLevel.value = (mLogLevel.value?.plus(1))?.rem(3)
        getTRTCClient().showDebugView(mLogLevel.value?:1)
    }

    fun enterRoom() {
        AVChatManager.enterRoom(
            mUserId.value.toString(),
            GenerateTestUserSig.genTestUserSig(mUserId.value.toString()),
            mRoomId.value.toString().toInt(),
            mIsFrontCamera.value ?: true,
            mLocalPreviewView.value!!,
            object : TRTCCloudListener() {
                override fun onUserVideoAvailable(userId: String, available: Boolean) {
                    super.onUserVideoAvailable(userId, available)
                    val uids = mRemoteUidList.value
                    if (uids != null) {
                        val index: Int = uids.indexOf(userId)
                        if (available) {
                            if (index != -1) { //如果mRemoteUidList有，就不重复添加
                                return
                            }
                            uids.add(userId)
                            refreshRemoteVideoViews()
                        } else {
                            if (index == -1) { //如果mRemoteUidList没有，说明已关闭画面
                                return
                            }
                            /// 关闭用户userId的视频画面
                            getTRTCClient().stopRemoteView(userId)
                            uids.removeAt(index)
                            refreshRemoteVideoViews()
                        }
                    }
                }

                private fun refreshRemoteVideoViews() {
                    val views = mRemoteViewList.value
                    if (views != null) {
                        for (i in views.indices) {
                            if (i < views.size) {
                                val remoteUid: TXCloudVideoView = views[i]
                                views[i].visibility = View.VISIBLE
                                // 开始显示用户userId的视频画面
                                getTRTCClient().startRemoteView(remoteUid.toString(), views[i])
                            } else {
                                views[i].visibility = View.GONE
                            }
                        }
                    }
                }

                override fun onError(errCode: Int, errMsg: String, extraInfo: Bundle) {
                    super.onError(errCode, errMsg, extraInfo)
                    showShortToast("出现错误 code：$errCode")
                    if (errCode == TXLiteAVCode.ERR_ROOM_ENTER_FAIL) {
                        ActivityManager.finish()
                    }
                }
            }
        )
    }

    fun exitRoom() {
        AVChatManager.exitRoom()
    }

}