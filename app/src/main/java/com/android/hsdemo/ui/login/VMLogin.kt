package com.android.hsdemo.ui.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.android.baselib.global.AppGlobal.context
import com.android.hsdemo.R

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
    val userName = MutableLiveData<String>("")

    /**
     * 用户密码
     */
    val userPassword = MutableLiveData<String>("")

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
    val fragmentType = MutableLiveData<FragmentType>(FragmentType.TYPE_REGISTER)

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

}