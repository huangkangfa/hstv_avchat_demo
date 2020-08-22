package com.android.hsdemo.ui.main.vm

import android.app.Application
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.android.hsdemo.model.Meeting
import com.android.hsdemo.network.HttpCallback
import com.android.hsdemo.network.RemoteRepositoryImpl
import com.rxjava.rxlife.life

class VMFCreateMeeting(application: Application) : AndroidViewModel(application) {

    /**
     * 会议名称
     */
    val meetingName = MutableLiveData<String>("")

    /**
     * 参会人员
     */
    val meetingPeopleStr = MutableLiveData<String>("请选择会议人员")

    /**
     * 参会密码
     */
    val meetingPassword = MutableLiveData<String>("")

    /**
     * 用户
     */
    val userBalance = MutableLiveData<String>("0元")

    /**
     * 会议编号
     */
    val meetingNo = MutableLiveData<String>("")

    /**
     * 会议id
     */
    val meetingId = MutableLiveData<Long>()

    fun clearStatus() {
        meetingName.value = ""
        meetingPeopleStr.value = ""
        meetingPassword.value = ""
        meetingNo.value = ""
        meetingId.value = 0
    }


    /**
     * 发起会议
     */
    fun createMeeting(owner: LifecycleOwner, callback: HttpCallback<Meeting>) {
        if (TextUtils.isEmpty(meetingName.value.toString())) {
            callback.failed("会议名称不能为空")
            return
        }
        RemoteRepositoryImpl.createMeeting(
            meetingName.value.toString(),
            meetingPassword.value.toString(),
            ""
        )
            .life(owner)
            .subscribe(
                { result: Meeting -> callback.success(result) }
            ) { throwable: Throwable? -> callback.failed(throwable?.message.toString()) }
    }

    /**
     * 加入会议
     */
    fun joinMeeting(owner: LifecycleOwner, callback: HttpCallback<String>) {
        RemoteRepositoryImpl.joinOrLeaveMeeting(meetingId.value.toString(), 1)
            .life(owner)
            .subscribe(
                { result: String -> callback.success(result) }
            ) { throwable: Throwable? -> callback.failed(throwable?.message.toString()) }
    }

}