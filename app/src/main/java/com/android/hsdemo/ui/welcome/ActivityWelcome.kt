package com.android.hsdemo.ui.welcome

import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import com.android.baselib.utils.Preferences
import com.android.baselib.utils.showShortToast
import com.android.hsdemo.*
import com.android.hsdemo.model.User
import com.android.hsdemo.network.HttpCallback
import com.android.hsdemo.network.RemoteRepositoryImpl
import com.android.hsdemo.ui.login.ActivityLogin
import com.android.hsdemo.ui.main.ActivityMain
import com.rxjava.rxlife.life

class ActivityWelcome : AppCompatActivity() {

    private val userName = Preferences.getString(KEY_USER_NAME)
    private val userPassword = Preferences.getString(KEY_USER_PASSWORD)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (TextUtils.isEmpty(userName)) {
            ActivityLogin.start(this@ActivityWelcome)
            this@ActivityWelcome.finish()
        } else {
            //再次登录
            autoLogin(this@ActivityWelcome, object : HttpCallback<User> {
                override fun success(t: User) {
                    Preferences.saveValue(KEY_TOKEN, t.token.toString())
                    Preferences.saveValue(KEY_USERSIG, t.userSig.toString())
                    Preferences.saveValue(KEY_ACCID, t.accid.toString())
                    Preferences.saveValue(
                        KEY_MEETINGNOTICEACCOUNT,
                        t.meetingNoticeAccount.toString()
                    )
                    //跳转主界面
                    ActivityMain.start(this@ActivityWelcome)
                    this@ActivityWelcome.finish()
                }

                override fun failed(msg: String) {
                    showShortToast("自动登录失败，请手动登录")
                    ActivityLogin.start(this@ActivityWelcome)
                    this@ActivityWelcome.finish()
                }
            })
        }
    }

    private fun autoLogin(owner: LifecycleOwner, callback: HttpCallback<User>) {
        if (TextUtils.isEmpty(userName)) {
            showShortToast("用户张账号不能为空")
            return
        }
        if (TextUtils.isEmpty(userPassword)) {
            showShortToast("密码不能为空")
            return
        }
        RemoteRepositoryImpl.login(userName, userPassword)
            .life(owner)
            .subscribe(
                { user: User -> callback.success(user) }
            ) { throwable: Throwable? -> callback.failed(throwable?.message.toString()) }
    }

}