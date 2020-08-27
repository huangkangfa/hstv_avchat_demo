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
import com.android.hsdemo.EventKey
import com.android.hsdemo.KEY_USERSIG
import com.android.hsdemo.MeetingCmd
import com.android.hsdemo.model.ItemOfMemberInfo
import com.android.hsdemo.model.MeetingDetail
import com.android.hsdemo.model.MemberInfo
import com.android.hsdemo.model.User
import com.android.hsdemo.network.HttpCallback
import com.android.hsdemo.network.RemoteRepositoryImpl
import com.android.hsdemo.util.TimeUtil.getTimeString
import com.android.hsdemo.util.TimeUtil.secToTime
import com.elvishew.xlog.XLog
import com.rxjava.rxlife.life
import com.tencent.liteav.TXLiteAVCode
import com.tencent.rtmp.ui.TXCloudVideoView
import com.tencent.trtc.TRTCCloudListener
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class VMRTC(application: Application) : AndroidViewModel(application) {

    var visibilityOfMuteVideoDefault: MutableLiveData<Int> = MutableLiveData(View.GONE)

    private var mIsFrontCamera: MutableLiveData<Boolean> = MutableLiveData(true) // 默认摄像头前置

    val mRoomName: MutableLiveData<String> = MutableLiveData("") // 房间名称
    val mRoomId: MutableLiveData<String> = MutableLiveData() // 房间Id
    val mUserId: MutableLiveData<String> = MutableLiveData() // 用户Id
    val mTimeDownStr: MutableLiveData<String> = MutableLiveData("00:00:00") // 倒计时
    var time: Long = 0L

    private val mMeetingDetail: MutableLiveData<MeetingDetail> = MutableLiveData() // 会议详情

    var mLocalPreviewView: MutableLiveData<TXCloudVideoView> = MutableLiveData()

    var ids: ArrayList<String> = ArrayList() // 远端用户id数据
    var data: ArrayList<ItemOfMemberInfo> = ArrayList() // 远端用户详情数据
    private lateinit var adapter: AbstractAdapter<ItemOfMemberInfo>

    var isScreen: Boolean = false //是否是全屏状态

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
            getTRTCClient().stopLocalPreview()
            visibilityOfMuteVideoDefault.value = View.VISIBLE
            //上报自己关闭摄像头
            RemoteRepositoryImpl.opReport(
                mMeetingDetail.value?.id.toString(),
                MeetingCmd.CAMERA_CLOSE,
                null,
                null
            ).subscribe()
        } else {
            getTRTCClient().startLocalPreview(mIsFrontCamera.value ?: true, mLocalPreviewView.value)
            visibilityOfMuteVideoDefault.value = View.GONE
            //上报自己打开摄像头
            RemoteRepositoryImpl.opReport(
                mMeetingDetail.value?.id.toString(),
                MeetingCmd.CAMERA_OPEN,
                null,
                null
            ).subscribe()
        }
        v.isSelected = !v.isSelected
    }

    /**
     * 静音开关按钮点击事件
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun onMuteAudioClick(v: View) {
        if (!v.isSelected) {
            getTRTCClient().stopLocalAudio()
            //上报自己静音
            RemoteRepositoryImpl.opReport(
                mMeetingDetail.value?.id.toString(),
                MeetingCmd.SET_AUDIO_MUTE_SELF_CANCLE,
                null,
                null
            ).subscribe()
        } else {
            getTRTCClient().startLocalAudio()
            //上报自己取消静音
            RemoteRepositoryImpl.opReport(
                mMeetingDetail.value?.id.toString(),
                MeetingCmd.SET_AUDIO_MUTE_SELF,
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
            mLocalPreviewView.value!!,
            object : TRTCCloudListener() {

//                override fun onRemoteUserEnterRoom(userId: String) {
//                    super.onRemoteUserEnterRoom(userId)
//                    XLog.i("【视频会议】加入成员 $userId")
//                    addUserById(userId)
//                }
//
//                override fun onRemoteUserLeaveRoom(userId: String, p1: Int) {
//                    super.onRemoteUserLeaveRoom(userId, p1)
//                    XLog.i("【视频会议】离开成员 $userId")
//                    EventBus.with("${EventKey.VIDEO_CONTROL}_${userId}").postValue(false)
//                    removeUserById(userId)
//                }

                override fun onUserVideoAvailable(userId: String, available: Boolean) {
                    super.onUserVideoAvailable(userId, available)
                    XLog.i("【视频会议】成员$userId 视频流状态 $available")
                    if(available){
                        addUserById(userId)
                        EventBus.with("${EventKey.VIDEO_CONTROL}_${userId}").postValue(true)
                    }else{
                        EventBus.with("${EventKey.VIDEO_CONTROL}_${userId}").postValue(false)
                        removeUserById(userId)
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
                            data.add(ItemOfMemberInfo(mMemberInfo))
                            ids.add(userId)
                            adapter.notifyDataSetChanged()
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
        for (item in data) {
            if (TextUtils.equals(item._data.account, userId)) {
                data.remove(item)
                ids.remove(userId)
                adapter.notifyDataSetChanged()
                EventBus.with(EventKey.MEETING_USER_CHANGE, String::class.java).postValue("0")
                return
            }
        }
    }

    fun exitRoom() {
        AVChatManager.exitRoom()
    }

}