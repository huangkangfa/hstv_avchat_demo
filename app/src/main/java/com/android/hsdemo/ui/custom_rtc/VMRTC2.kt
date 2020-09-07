package com.android.hsdemo.ui.custom_rtc

import android.app.Application
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.TextureView
import android.view.View
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.android.avchat.AVChatManager
import com.android.avchat.customcapture.TestRenderVideoFrame
import com.android.baselib.ActivityManager
import com.android.baselib.custom.eventbus.EventBus
import com.android.baselib.custom.recyleview.adapter.AbstractAdapter
import com.android.baselib.custom.recyleview.adapter.ListItem
import com.android.baselib.custom.recyleview.adapter.setUP
import com.android.baselib.utils.Preferences
import com.android.baselib.utils.showShortToast
import com.android.hsdemo.*
import com.android.hsdemo.model.*
import com.android.hsdemo.network.HttpCallback
import com.android.hsdemo.network.RemoteRepositoryImpl
import com.android.hsdemo.util.TimeUtil
import com.elvishew.xlog.XLog
import com.rxjava.rxlife.life
import com.tencent.liteav.TXLiteAVCode
import com.tencent.trtc.TRTCCloudDef
import com.tencent.trtc.TRTCCloudListener
import kotlinx.coroutines.*

class VMRTC2(application: Application) : AndroidViewModel(application) {

    /**
     * 自己音量变化gif自动隐藏的倒计时
     */
    var autoAudioDismissTime = 0

    /**
     * 主屏摄像头关闭的默认背景是否显示
     */
    var visibilityOfMuteVideoDefault: MutableLiveData<Int> = MutableLiveData(View.GONE)

    /**
     * 主屏自己的音量gif是否显示
     */
    var visibilityOfMuteAudio: MutableLiveData<Int> = MutableLiveData(View.GONE)

    /**
     * 默认摄像头前置
     */
    private var mIsFrontCamera: MutableLiveData<Boolean> = MutableLiveData(true)

    val mRoomName: MutableLiveData<String> = MutableLiveData("") // 房间名称
    val mRoomId: MutableLiveData<String> = MutableLiveData() // 房间Id
    val mUserId: MutableLiveData<String> = MutableLiveData() // 我的Id
    val mTimeDownStr: MutableLiveData<String> = MutableLiveData("00:00:00") // 会议时间文本
    var time: Long = 0L // 会议时间

    val mMeetingDetail: MutableLiveData<MeetingDetail> = MutableLiveData() // 会议详情

    private var mLocalPreviewView: TextureView? = null //主屏视图控件

    private lateinit var adapter: AbstractAdapter<ItemOfMemberInfo> //小窗口列表适配器

    var isScreen: Boolean = false //是否是全屏状态

    /**
     * 右侧小视频列表的用户数据
     */
    var data: ArrayList<ItemOfMemberInfo> = ArrayList()

    /**
     * 其他人的RenderVideoFrame
     */
    var othersRenderVideoFrame: HashMap<String, TestRenderVideoFrame> = HashMap()

    /**
     * 我自己的数据
     */
    var myself: ItemOfMemberInfo

    /**
     * 自己的RenderVideoFrame
     */
    var myselfRenderVideoFrame: TestRenderVideoFrame

    /**
     * 主屏的数据
     */
    var screenMember: ItemOfMemberInfo

    init {
        //初始化自己的默认数据,并设置主屏是自己
        val self = MemberInfo()
        self.account = Preferences.getString(KEY_ACCID)
        self.avatar = Preferences.getString(KEY_AVATAR)
        self.hostMute = false
        self.mute = false
        self.nickName = Preferences.getString(KEY_USER_NICK_NAME)
        self.userName = Preferences.getString(KEY_USER_NAME)
        self.videoOpen = true
        self.state = "1"
        myself = ItemOfMemberInfo(0, self, 2)
        myselfRenderVideoFrame =
            TestRenderVideoFrame(myself._data.account, TRTCCloudDef.TRTC_VIDEO_STREAM_TYPE_BIG)
        screenMember = myself
    }

    /**
     * 设置主屏view
     * 只用设置一次
     */
    fun setLocalPreviewViewByFirst(localView: TextureView) {
        mLocalPreviewView = localView
        AVChatManager.getTRTCClient().setLocalVideoRenderListener(
            TRTCCloudDef.TRTC_VIDEO_PIXEL_FORMAT_Texture_2D,
            TRTCCloudDef.TRTC_VIDEO_BUFFER_TYPE_TEXTURE,
            myselfRenderVideoFrame
        )
        myselfRenderVideoFrame.start(mLocalPreviewView)
        AVChatManager.getTRTCClient().startLocalPreview(true, null)
    }

    /**
     * 获取主屏视图
     */
    fun getLocalPreviewView(): TextureView? {
        return mLocalPreviewView
    }

    /**
     * 主屏用户交换
     */
    fun changeScreenMember(currentScreenMember: ItemOfMemberInfo) {
        val lastScreenMember = screenMember
        val position = data.indexOf(currentScreenMember)
        if (position != -1) {

            //大小屏数据交换
            data.remove(currentScreenMember)
            data.add(position, lastScreenMember)
            screenMember = currentScreenMember

            //主屏视频更新
            XLog.i("【视频会议】主屏用户摄像头的开关状态：${screenMember._data.videoOpen}")
            if (screenMember._type == 0) {
                if (screenMember._data.videoOpen == true) {
                    XLog.i("【视频会议】主屏播放我自己的预览视频 ${screenMember._data.account}")
                    visibilityOfMuteVideoDefault.value = View.GONE
                    AVChatManager.getTRTCClient().setLocalVideoRenderListener(
                        TRTCCloudDef.TRTC_VIDEO_PIXEL_FORMAT_Texture_2D,
                        TRTCCloudDef.TRTC_VIDEO_BUFFER_TYPE_TEXTURE,
                        myselfRenderVideoFrame
                    )
                    myselfRenderVideoFrame.start(mLocalPreviewView)
                } else {
                    XLog.i("【视频会议】主屏关闭我自己的预览视频 ${screenMember._data.account}")
                    visibilityOfMuteVideoDefault.value = View.VISIBLE
                    myselfRenderVideoFrame.stop()
                }
            } else {
                if (screenMember._data.videoOpen == true) {
                    XLog.i("【视频会议】主屏播放远程用户的预览视频 ${screenMember._data.account}")
                    visibilityOfMuteVideoDefault.value = View.GONE
                    AVChatManager.getTRTCClient().setRemoteVideoRenderListener(screenMember._data.account, TRTCCloudDef.TRTC_VIDEO_PIXEL_FORMAT_I420, TRTCCloudDef.TRTC_VIDEO_BUFFER_TYPE_BYTE_ARRAY,othersRenderVideoFrame[screenMember._data.account])
                    othersRenderVideoFrame[screenMember._data.account]?.start(mLocalPreviewView)
                } else {
                    XLog.i("【视频会议】主屏关闭远程用户的预览视频 ${screenMember._data.account}")
                    visibilityOfMuteVideoDefault.value = View.VISIBLE
                    othersRenderVideoFrame[screenMember._data.account]?.stop()
                }
            }
            //小窗口视频更新
            adapter.notifyItemRangeChanged(position, 1)
        }
    }

    /**
     * 初始化RecycleView列表
     */
    fun initRecycleView(
        recyclerView: RecyclerView,
        manager: RecyclerView.LayoutManager,
        itemOfData: ListItem<ItemOfMemberInfo>
    ) {
        adapter = recyclerView.setUP(
            data,
            itemOfData,
            manager = manager
        )
    }

    /**
     * 获取参会人员
     */
    fun getPeopleList(): ArrayList<ItemOfPeople> {
        val result = ArrayList<ItemOfPeople>()
        for (item in data) {
            result.add(ItemOfPeople(item._data))
        }
        return result
    }

    /**
     * 获取倒计时文本
     */
    fun updateTimeDown() {
        GlobalScope.launch(Dispatchers.Main) {
            mTimeDownStr.value = TimeUtil.secToTime(time.toInt())
        }
    }


    /**
     * 获取会议详情
     */
    fun getMeetingDetail(owner: LifecycleOwner, callback: HttpCallback<MeetingDetail>) {
        RemoteRepositoryImpl.getMeetingDetail(mRoomId.value.toString())
            .life(owner)
            .subscribe(
                { result: MeetingDetail ->
                    mMeetingDetail.value = result
                    if (result.memberInfos != null && result.memberInfos!!.size > 0) {
                        for (item in result.memberInfos!!) {
                            if (TextUtils.equals(item.account, myself._data.account)) {
                                myself._data.avatar = item.avatar
                                myself._data.hostMute = item.hostMute
                                myself._data.mute = item.mute
                                myself._data.nickName = item.nickName
                                myself._data.userName = item.userName
                                myself._data.videoOpen = item.videoOpen
                                myself._data.state = item.state
                            }
                        }
                    }

                    callback.success(result)
                }
            ) { throwable: Throwable? -> callback.failed(throwable?.message.toString()) }
    }

    /**
     * 摄像头开关按钮点击事件
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun onMuteVideoClick(v: View) {
        if (!v.isSelected) {
            //上报自己关闭摄像头
            RemoteRepositoryImpl.opReport(
                mMeetingDetail.value?.id.toString(),
                MeetingCmd.CAMERA_CLOSE,
                null,
                null
            ).subscribe()
            if (screenMember._type == 0) {
                XLog.i("【视频会议】自己是主屏 直接关闭自己的摄像头")
                myselfRenderVideoFrame.stop()
                myself._data.videoOpen = false
                changeScreenDefaultUI()
            } else {
                XLog.i("【视频会议】自己是附屏 关闭${mUserId.value.toString()}的摄像头")
                EventBus.with("${EventKey.VIDEO_CONTROL}_${mUserId.value.toString()}")
                    .postValue(false)
            }
        } else {
            //上报自己打开摄像头
            RemoteRepositoryImpl.opReport(
                mMeetingDetail.value?.id.toString(),
                MeetingCmd.CAMERA_OPEN,
                null,
                null
            ).subscribe()
            if (screenMember._type == 0) {
                XLog.i("【视频会议】自己是主屏 直接打开自己的摄像头")
                AVChatManager.getTRTCClient().setLocalVideoRenderListener(
                    TRTCCloudDef.TRTC_VIDEO_PIXEL_FORMAT_Texture_2D,
                    TRTCCloudDef.TRTC_VIDEO_BUFFER_TYPE_TEXTURE,
                    myselfRenderVideoFrame
                )
                myselfRenderVideoFrame.start(mLocalPreviewView)
                myself._data.videoOpen = true
                changeScreenDefaultUI()
            } else {
                XLog.i("【视频会议】自己是附屏 打开${mUserId.value.toString()}的摄像头")
                EventBus.with("${EventKey.VIDEO_CONTROL}_${mUserId.value.toString()}")
                    .postValue(true)
            }
        }
        v.isSelected = !v.isSelected
    }

    /**
     * 更新主屏的屏幕开关状态
     */
    private fun changeScreenDefaultUI() {
        if (screenMember._data.videoOpen == true) {
            visibilityOfMuteVideoDefault.value = View.GONE
        } else {
            visibilityOfMuteVideoDefault.value = View.VISIBLE
        }
    }

    /**
     * 静音开关按钮点击事件
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun onMuteAudioClick(v: View) {
        if (!TextUtils.equals(
                mMeetingDetail.value?.masterId.toString(),
                mUserId.value.toString()
            )
        ) {
            //观众身份
            if (myself._data.hostMute == true) {
                showShortToast("主持人已经强制静音，不可操作")
                return
            }
        }
        if (!v.isSelected) {
            AVChatManager.getTRTCClient().stopLocalAudio()
            //上报自己静音
            RemoteRepositoryImpl.opReport(
                mMeetingDetail.value?.id.toString(),
                MeetingCmd.SET_AUDIO_MUTE_SELF,
                null,
                null
            ).subscribe()
            EventBus.with(
                "${EventKey.MEETING_USER_VOICE_VOLUME}_${mUserId.value.toString()}",
                Int::class.java
            ).postValue(0)
        } else {
            AVChatManager.getTRTCClient().startLocalAudio()
            //上报自己取消静音
            RemoteRepositoryImpl.opReport(
                mMeetingDetail.value?.id.toString(),
                MeetingCmd.SET_AUDIO_MUTE_SELF_CANCLE,
                null,
                null
            ).subscribe()
        }
        v.isSelected = !v.isSelected
    }

    /**
     * 主持人结束会议上报
     */
    fun setMeetingEnd() {
        RemoteRepositoryImpl.opReport(
            mMeetingDetail.value?.id.toString(),
            MeetingCmd.DESTROY_MEETING,
            null,
            null
        ).subscribe(
            {
                EventBus.with(EventKey.MEETING_STATUS_END).postValue("")
            }
        ) { throwable: Throwable? -> XLog.i("【视频会议】上报结束会议 error = ${throwable?.message.toString()}") }
    }

    fun enterRoom() {
        AVChatManager.enterRoom(
            mUserId.value.toString(),
            Preferences.getString(KEY_USERSIG, ""),
            mRoomId.value.toString().toInt(),
            mIsFrontCamera.value ?: true,
            null,
            object : TRTCCloudListener() {

                override fun onConnectionLost() {
                    //SDK 跟服务器的连接断开。
                    super.onConnectionLost()
                    XLog.i("【视频会议】跟服务器的连接断开")
                }

                override fun onTryToReconnect() {
                    //SDK 尝试重新连接到服务器。
                    super.onTryToReconnect()
                    XLog.i("【视频会议】尝试重新连接到服务器")
                }

                override fun onConnectionRecovery() {
                    //SDK 跟服务器的连接恢复。
                    super.onConnectionRecovery()
                    XLog.i("【视频会议】跟服务器的连接恢复")
                }

                override fun onUserVoiceVolume(
                    userVolumes: ArrayList<TRTCCloudDef.TRTCVolumeInfo>?,
                    totalVolume: Int
                ) {
                    super.onUserVoiceVolume(userVolumes, totalVolume)
                    if (userVolumes != null && userVolumes.size > 0) {
                        userVolumes.forEach {
//                            XLog.i("【视频会议】音量变化 userId=${it.userId} volume=${it.volume}")
                            EventBus.with(
                                "${EventKey.MEETING_USER_VOICE_VOLUME}_${it.userId}",
                                Int::class.java
                            ).postValue(it.volume)
                        }
                    }
                }

                override fun onRemoteUserLeaveRoom(userId: String, p1: Int) {
                    super.onRemoteUserLeaveRoom(userId, p1)
                    XLog.i("【视频会议】离开成员 $userId")
                    removeUserById(userId)
                }

                override fun onUserVideoAvailable(userId: String, available: Boolean) {
                    super.onUserVideoAvailable(userId, available)
                    if (available) {
                        XLog.i("【视频会议】加入成员 $userId")
                        addUserById(userId)
                    }
                    XLog.i("【视频会议】成员$userId 视频流状态 $available")
                    if (othersRenderVideoFrame[userId] != null) {
                        EventBus.with("${EventKey.VIDEO_CONTROL}_${userId}").postValue(available)
                        AVChatManager.getTRTCClient().startRemoteView(userId, null)
                    }
                }

                override fun onError(errCode: Int, errMsg: String, extraInfo: Bundle) {
                    super.onError(errCode, errMsg, extraInfo)
                    XLog.i("【视频会议】出现错误 code=$errCode msg=$errMsg")
                    showShortToast("出现错误 code：$errCode")
                    if (errCode == TXLiteAVCode.ERR_ROOM_ENTER_FAIL) {
                        ActivityManager.finish()
                    }
                }

            }
        )
    }

    /**
     * 添加用户
     */
    private fun addUserById(userId: String) {
        if (othersRenderVideoFrame[userId] == null) {
            RemoteRepositoryImpl.getUserListByAccids(userId)
                .subscribe(
                    { result: List<User> ->
                        val list = result as ArrayList<User>
                        if (list.size > 0) {
                            val mMemberInfo = MemberInfo()
                            mMemberInfo.account = list[0].accid.toString()
                            mMemberInfo.nickName = list[0].nickName.toString()
                            mMemberInfo.avatar = list[0].userAvatar.toString()
                            mMemberInfo.userName = list[0].userName.toString()
                            data.add(ItemOfMemberInfo(1, mMemberInfo, 2))
                            othersRenderVideoFrame[userId] = TestRenderVideoFrame(
                                userId,
                                TRTCCloudDef.TRTC_VIDEO_STREAM_TYPE_BIG
                            )
                            AVChatManager.getTRTCClient().setRemoteVideoRenderListener(userId, TRTCCloudDef.TRTC_VIDEO_PIXEL_FORMAT_I420, TRTCCloudDef.TRTC_VIDEO_BUFFER_TYPE_BYTE_ARRAY,othersRenderVideoFrame[userId])
                            adapter.notifyItemChanged(data.size - 1)
                            EventBus.with(EventKey.MEETING_USER_CHANGE, String::class.java)
                                .postValue("1")
                        }
                    }
                ) { throwable: Throwable? ->
                    XLog.e("【视频会议】getUserListByAccids error = ${throwable?.message.toString()}")
                }
        }
    }

    /**
     * 移除用户
     */
    private fun removeUserById(userId: String) {
        //移除的时候  如果移除用户是主屏的话，还要额外处理
        var isScreenUserBeRemoved = true
        for (item in data) {
            if (TextUtils.equals(item._data.account, userId)) {
                isScreenUserBeRemoved = false
                val index = data.indexOf(item)
                if (index != -1) {
                    data.removeAt(index)
                    othersRenderVideoFrame[userId]?.stop()
                    othersRenderVideoFrame.remove(userId)
                    adapter.notifyItemRangeRemoved(index, 1)
                    EventBus.with(EventKey.MEETING_USER_CHANGE, String::class.java).postValue("0")
                }
                return
            }
        }
        if (isScreenUserBeRemoved && TextUtils.equals(screenMember._data.account, userId)) {
            val index = data.indexOf(myself)
            if (index != -1) {
                screenMember = myself
                data.removeAt(index)
                othersRenderVideoFrame[userId]?.stop()
                othersRenderVideoFrame.remove(userId)
                adapter.notifyItemRangeChanged(index, 1)
                EventBus.with("${EventKey.VIDEO_CONTROL}_${userId}")
                    .postValue(myself._data.videoOpen)
                EventBus.with(EventKey.MEETING_USER_CHANGE, String::class.java).postValue("0")
            }
        }
    }

    /**
     * 退出房间
     */
    fun exitRoom() {
        RemoteRepositoryImpl.joinOrLeaveMeeting(mMeetingDetail.value?.id.toString(), 0).subscribe()
        AVChatManager.exitRoom()
    }

    /**
     * 数据变化更新视图
     */
    fun changeUI(member: ItemOfMemberInfo? = null) {
        //更新副屏
        if (member == null) {
            adapter.notifyDataSetChanged()
        } else {
            val index = data.indexOf(member)
            if (index != -1) {
                adapter.notifyItemRangeChanged(index, 1)
            }
        }
    }

    /**
     * 设置播放语音大小的显隐
     */
    fun setVisibilityOfMuteAudio(isVisibility: Boolean) {
        if (isVisibility) {
            visibilityOfMuteAudio.value = View.VISIBLE
            if (autoAudioDismissTime == 0) {
                autoAudioDismissTime = 2
                GlobalScope.launch(Dispatchers.IO) {
                    while (autoAudioDismissTime > 0) {
                        delay(1000)
                        autoAudioDismissTime--
                    }
                    withContext(Dispatchers.Main) {
                        setVisibilityOfMuteAudio(false)
                    }
                }
            } else {
                autoAudioDismissTime = 2
            }
        } else {
            visibilityOfMuteAudio.value = View.GONE
            autoAudioDismissTime = 0
        }
    }

}