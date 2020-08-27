package com.android.hsdemo.ui.main.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.android.hsdemo.model.Meeting
import com.android.hsdemo.model.User
import com.android.hsdemo.network.HttpCallback
import com.android.hsdemo.network.RemoteRepositoryImpl
import com.rxjava.rxlife.life

class VMFPersionCenter(application: Application) : AndroidViewModel(application) {

    /**
     * 用户昵称
     */
    val userNickName: MutableLiveData<String> = MutableLiveData("")

    /**
     * 用户手机号
     */
    val userPhone: MutableLiveData<String> = MutableLiveData("")

    /**
     * 用户余额
     */
    val userBalance: MutableLiveData<String> = MutableLiveData("0")

    /**
     * 用户头像
     */
    val userAvatar: MutableLiveData<String> = MutableLiveData("")


    /**
     * 修改用户昵称
     */
    fun modifyUserInfo(owner: LifecycleOwner, callback: HttpCallback<String>) {
        RemoteRepositoryImpl.modifyUserInfo(null, userNickName.value.toString())
            .life(owner)
            .subscribe(
                { result: String -> callback.success(result) }
            ) { throwable: Throwable? -> callback.failed(throwable?.message.toString()) }
    }

}