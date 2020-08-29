package com.android.hsdemo.ui.welcome

import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import com.android.avchat.AVChatManager
import com.android.baselib.utils.Preferences
import com.android.baselib.utils.showShortToast
import com.android.hsdemo.*
import com.android.hsdemo.model.User
import com.android.hsdemo.network.HttpCallback
import com.android.hsdemo.network.RemoteRepositoryImpl
import com.android.hsdemo.ui.login.ActivityLogin
import com.android.hsdemo.ui.main.ActivityMain
import com.android.hsdemo.ui.rtc.ActivityRTC
import com.rxjava.rxlife.life
import com.tencent.imsdk.v2.V2TIMCallback
import kotlinx.coroutines.*

class ActivityWelcome : AppCompatActivity() {

    private val userName = Preferences.getString(KEY_USER_NAME)
    private val userPassword = Preferences.getString(KEY_USER_PASSWORD)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    private fun init() {
        if (TextUtils.isEmpty(userName)) {
            goLogin()
        } else {
            //再次登录
            autoLogin(this@ActivityWelcome, object : HttpCallback<User> {
                override fun success(t: User) {
                    goMain(t)
                }

                override fun failed(msg: String) {
                    showShortToast("自动登录失败，请手动登录")
                    goLogin()
                }
            })
        }
    }

    private fun goMain(t: User) {
        saveUser(t)
        //跳转主界面
        ActivityMain.start(this@ActivityWelcome)
        this@ActivityWelcome.finish()
    }

    private fun goLogin() {
        GlobalScope.launch(Dispatchers.IO) {
            delay(1000)
            withContext(Dispatchers.Main) {
                ActivityLogin.start(this@ActivityWelcome)
                this@ActivityWelcome.finish()
            }
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
                { user: User ->
                    AVChatManager.login(user.accid.toString(),user.userSig.toString(),object :
                        V2TIMCallback {
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

}