package com.android.hsdemo.ui.login.vm

import android.app.Application
import android.text.TextUtils
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.android.avchat.AVChatManager
import com.android.baselib.global.AppGlobal.context
import com.android.baselib.utils.Preferences
import com.android.baselib.utils.showShortToast
import com.android.hsdemo.KEY_USER_NAME
import com.android.hsdemo.KEY_USER_PASSWORD
import com.android.hsdemo.R
import com.android.hsdemo.network.HttpCallback
import com.android.hsdemo.network.RemoteRepositoryImpl
import com.android.hsdemo.model.User
import com.elvishew.xlog.XLog
import com.rxjava.rxlife.life
import com.tencent.imsdk.v2.V2TIMCallback


class VMLogin(application: Application) : AndroidViewModel(application) {

    /**
     * Fragment类型
     */
    enum class FragmentType {
        TYPE_REGISTER,
        TYPE_FORGET
    }

    /**
     * 用户名称
     */
    val userName = MutableLiveData<String>(Preferences.getString(KEY_USER_NAME))
    val userName2 = MutableLiveData<String>("")

    /**
     * 用户密码
     */
    val userPassword = MutableLiveData<String>(Preferences.getString(KEY_USER_PASSWORD))
    val userPassword2 = MutableLiveData<String>("")

    /**
     * 重复密码
     */
    val repeatPassword = MutableLiveData<String>("")

    /**
     * 验证码
     */
    val verificationCode = MutableLiveData<String>("")

    /**
     * 是否是注册
     * 用于注册部分跟忘记密码部分
     * TYPE_REGISTER:注册部分  TYPE_FORGET:忘记密码
     */
    val fragmentType = MutableLiveData<FragmentType>(
        FragmentType.TYPE_REGISTER
    )

    /**
     * 注册或者忘记密码的确定按钮文本
     */
    fun getSureButtonNameOfFragmentType(): String {
        return if (fragmentType.value == FragmentType.TYPE_REGISTER) {
            context.getString(R.string.registered)
        } else {
            context.getString(R.string.sure)
        }
    }

    /**
     * 注册或者忘记密码的密码文本
     */
    fun getDefaultTextOfUserPassword(): String {
        return if (fragmentType.value == FragmentType.TYPE_REGISTER) {
            context.getString(R.string.user_password)
        } else {
            context.getString(R.string.new_password)
        }
    }

    /**
     * 注册或者忘记密码的重复密码文本
     */
    fun getDefaultTextOfUserRepeatPassword(): String {
        return if (fragmentType.value == FragmentType.TYPE_REGISTER) {
            context.getString(R.string.repeat_password)
        } else {
            context.getString(R.string.repeat_new_password)
        }
    }

    /**
     * 登录
     */
    fun login(owner: LifecycleOwner, callback: HttpCallback<User>) {
        if (TextUtils.isEmpty(userName.value.toString())) {
            callback.failed("用户张账号不能为空")
            return
        }
        if (TextUtils.isEmpty(userPassword.value.toString())) {
            callback.failed("密码不能为空")
            return
        }
        RemoteRepositoryImpl.login(userName.value.toString(), userPassword.value.toString())
            .life(owner)
            .subscribe(
                { user: User ->
                    AVChatManager.login(user.accid.toString(),user.userSig.toString(),object :V2TIMCallback{
                        override fun onError(p0: Int, p1: String?) {
                            callback.failed("登录失败 IM ERROR $p0 $p1")
                        }

                        override fun onSuccess() {
                            callback.success(user)
                        }
                    })
                }
            ) { throwable: Throwable? -> callback.failed(throwable?.message.toString()) }
    }

    /**
     * 发送验证码
     */
    fun sendVerifyCode(owner: LifecycleOwner, callback: HttpCallback<String>) {
        if (TextUtils.isEmpty(userName2.value.toString())) {
            callback.failed("手机号码不能为空")
            return
        }
        if (userName2.value.toString().length != 11) {
            callback.failed("手机号码不正确")
            return
        }
        var type = 0
        if (fragmentType.value == FragmentType.TYPE_REGISTER) {
            type = 1
        }
        RemoteRepositoryImpl.getVerify(userName2.value.toString(), type)
            .life(owner)
            .subscribe(
                { result: String -> callback.success(result) }
            ) { throwable: Throwable? -> callback.failed(throwable?.message.toString()) }
    }

    /**
     * 修改密码
     */
    fun modifyPassword(owner: LifecycleOwner, callback: HttpCallback<String>) {
        if (TextUtils.isEmpty(userName2.value.toString())) {
            callback.failed("手机号码不能为空")
            return
        }
        if (TextUtils.isEmpty(verificationCode.value.toString())) {
            callback.failed("验证码不能为空")
            return
        }
        if (TextUtils.isEmpty(userPassword2.value.toString())) {
            callback.failed("密码不能为空")
            return
        }
        if (TextUtils.isEmpty(repeatPassword.value.toString())) {
            callback.failed("重复密码不能为空")
            return
        }
        if (!TextUtils.equals(repeatPassword.value.toString(),userPassword2.value.toString())) {
            callback.failed("2次密码不一致")
            return
        }
        RemoteRepositoryImpl.modifyPassword(
            userName2.value.toString(),
            verificationCode.value.toString(),
            userPassword2.value.toString(),
            repeatPassword.value.toString()
        ).life(owner)
            .subscribe(
                { result: String -> callback.success(result) }
            ) { throwable: Throwable? -> callback.failed(throwable?.message.toString()) }
    }

    /**
     * 注册
     */
    fun register(owner: LifecycleOwner, callback: HttpCallback<User>) {
        if (TextUtils.isEmpty(userName2.value.toString())) {
            callback.failed("手机号码不能为空")
            return
        }
        if (TextUtils.isEmpty(verificationCode.value.toString())) {
            callback.failed("验证码不能为空")
            return
        }
        if (TextUtils.isEmpty(userPassword2.value.toString())) {
            callback.failed("密码不能为空")
            return
        }
        if (TextUtils.isEmpty(repeatPassword.value.toString())) {
            callback.failed("重复密码不能为空")
            return
        }
        if (!TextUtils.equals(repeatPassword.value.toString(),userPassword2.value.toString())) {
            callback.failed("2次密码不一致")
            return
        }
        RemoteRepositoryImpl.register(
            userName2.value.toString(),
            verificationCode.value.toString(),
            userPassword2.value.toString(),
            repeatPassword.value.toString()
        ).life(owner)
            .subscribe(
                { result: User -> callback.success(result) }
            ) { throwable: Throwable? -> callback.failed(throwable?.message.toString()) }
    }

}