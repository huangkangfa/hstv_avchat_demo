package com.android.hsdemo.ui.rtc

import android.app.Application
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.android.avchat.AVChatManager
import com.android.avchat.AVChatManager.getTRTCClient
import com.android.baselib.ActivityManager
import com.android.baselib.custom.eventbus.EventBus
import com.android.baselib.custom.recyleview.adapter.AbstractAdapter
import com.android.baselib.custom.recyleview.adapter.ListItem
import com.android.baselib.custom.recyleview.adapter.setUP
import com.android.baselib.utils.Preferences
import com.android.baselib.utils.showShortToast
import com.android.hsdemo.*
import com.android.hsdemo.EventKey.MEETING_USER_VOICE_VOLUME
import com.android.hsdemo.model.*
import com.android.hsdemo.network.HttpCallback
import com.android.hsdemo.network.RemoteRepositoryImpl
import com.android.hsdemo.util.TimeUtil.secToTime
import com.elvishew.xlog.XLog
import com.rxjava.rxlife.life
import com.tencent.liteav.TXLiteAVCode
import com.tencent.rtmp.ui.TXCloudVideoView
import com.tencent.trtc.TRTCCloudDef
import com.tencent.trtc.TRTCCloudListener
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class VMRTC(application: Application) : AndroidViewModel(application) {

    var visibilityOfMuteVideoDefault: MutableLiveData<Int> = MutableLiveData(View.GONE)
    var visibilityOfMuteAudio: MutableLiveData<Int> = MutableLiveData(View.GONE)

    private var mIsFrontCamera: MutableLiveData<Boolean> = MutableLiveData(true) // 默认摄像头前置

    val mRoomName: MutableLiveData<String> = MutableLiveData("") // 房间名称
    val mRoomId: MutableLiveData<String> = MutableLiveData() // 房间Id
    val mUserId: MutableLiveData<String> = MutableLiveData() // 我的Id
    val mTimeDownStr: MutableLiveData<String> = MutableLiveData("00:00:00") // 倒计时
    var time: Long = 0L

    val mMeetingDetail: MutableLiveData<MeetingDetail> = MutableLiveData() // 会议详情

    lateinit var mLocalPreviewView: TXCloudVideoView

    var ids: ArrayList<String> = ArrayList() // 视频用户id数据
    var data: ArrayList<ItemOfMemberInfo> = ArrayList() // 视频用户详情数据

    private lateinit var adapter: AbstractAdapter<ItemOfMemberInfo>

    var isScreen: Boolean = false //是否是全屏状态

    var myself: ItemOfMemberInfo
    var screenMember: ItemOfMemberInfo

    init {
        val self = MemberInfo()
        self.account = Preferences.getString(KEY_ACCID)
        self.avatar = Preferences.getString(KEY_AVATAR)
        self.hostMute = "0"
        self.mute = "0"
        self.nickName = Preferences.getString(KEY_USER_NICK_NAME)
        self.userName = Preferences.getString(KEY_USER_NAME)
        self.videoOpen = true
        self.state = "1"
        myself = ItemOfMemberInfo(0, self)
        screenMember = myself
    }

    /**
     * 主屏用户交换
     */
    fun changeScreenMember(temp: ItemOfMemberInfo) {
        val lastScreenAccid = screenMember._data.account.toString()
        val lastScreenType = screenMember._type
        val position = data.indexOf(temp)
        if (position != -1) {
            data.remove(temp)
            data.add(position, screenMember)
            screenMember = temp
            if (screenMember._type == 0) {
                getTRTCClient().stopLocalPreview()
                XLog.i("【视频会议】主屏播放我自己的预览视频 ${screenMember._data.account}")
                getTRTCClient().startLocalPreview(true, mLocalPreviewView)
            } else {
                if (lastScreenType == 0) {
                    XLog.i("【视频会议】停止自己的主屏预览视频")
                    getTRTCClient().stopLocalPreview()
                }
                XLog.i("【视频会议】主屏播放远程用户的预览视频 ${screenMember._data.account}")
                getTRTCClient().startRemoteView(screenMember._data.account, mLocalPreviewView)
            }
            adapter.notifyItemRangeChanged(position, 1)
//            GlobalScope.launch(IO) {
//                delay(50)
//                XLog.i("【视频会议】更新切换窗口后的视频流数据 发送Event lastScreenAccid = $lastScreenAccid")
//                EventBus.with("${EventKey.VIDEO_CONTROL}_${lastScreenAccid}").postValue(true)
//            }
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
        GlobalScope.launch(Main) {
            mTimeDownStr.value = secToTime(time.toInt())
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
                getTRTCClient().stopLocalPreview()
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
                getTRTCClient().stopLocalPreview()
                XLog.i("【视频会议】自己是主屏 直接打开自己的摄像头")
                getTRTCClient().startLocalPreview(mIsFrontCamera.value ?: true, mLocalPreviewView)
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
            if (TextUtils.equals(myself._data.hostMute, "1")) {
                showShortToast("主持人已经强制静音，不可操作")
                return
            }
        }
        if (!v.isSelected) {
            getTRTCClient().stopLocalAudio()
            //上报自己静音
            RemoteRepositoryImpl.opReport(
                mMeetingDetail.value?.id.toString(),
                MeetingCmd.SET_AUDIO_MUTE_SELF,
                null,
                null
            ).subscribe()
            EventBus.with(
                "${MEETING_USER_VOICE_VOLUME}_${mUserId.value.toString()}",
                Int::class.java
            ).postValue(0)
            visibilityOfMuteAudio.value = View.GONE
        } else {
            getTRTCClient().startLocalAudio()
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
            mLocalPreviewView,
            object : TRTCCloudListener() {

                override fun onUserVoiceVolume(
                    userVolumes: ArrayList<TRTCCloudDef.TRTCVolumeInfo>?,
                    totalVolume: Int
                ) {
                    super.onUserVoiceVolume(userVolumes, totalVolume)
                    if (userVolumes != null && userVolumes.size > 0) {
                        userVolumes.forEach {
//                            XLog.i("【视频会议】音量变化 userId=${it.userId} volume=${it.volume}")
                            EventBus.with(
                                "${MEETING_USER_VOICE_VOLUME}_${it.userId}",
                                Int::class.java
                            ).postValue(it.volume)
                        }
                    }
                }

                override fun onRemoteUserEnterRoom(userId: String) {
                    super.onRemoteUserEnterRoom(userId)
                    XLog.i("【视频会议】加入成员 $userId")
                    addUserById(userId)
                }

                //
                override fun onRemoteUserLeaveRoom(userId: String, p1: Int) {
                    super.onRemoteUserLeaveRoom(userId, p1)
                    XLog.i("【视频会议】离开成员 $userId")
                    removeUserById(userId)
                }

                override fun onUserVideoAvailable(userId: String, available: Boolean) {
                    super.onUserVideoAvailable(userId, available)
                    XLog.i("【视频会议】成员$userId 视频流状态 $available")
                    EventBus.with("${EventKey.VIDEO_CONTROL}_${userId}").postValue(available)
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
        if (ids.indexOf(userId) == -1) {
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
                            data.add(ItemOfMemberInfo(1, mMemberInfo))
                            ids.add(userId)
                            adapter.notifyItemChanged(data.size - 1)
                            EventBus.with(EventKey.MEETING_USER_CHANGE, String::class.java).postValue("1")
                            //顺时针旋转270调整  因为机顶盒上小视频窗口偏转了这些角度
                            getTRTCClient().setRemoteViewRotation(mMemberInfo.account,TRTCCloudDef.TRTC_VIDEO_ROTATION_270)
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
                    ids.remove(userId)
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
                ids.remove(userId)
                adapter.notifyItemRangeRemoved(index, 1)
                EventBus.with("${EventKey.VIDEO_CONTROL}_${userId}")
                    .postValue(myself._data.videoOpen)
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

}