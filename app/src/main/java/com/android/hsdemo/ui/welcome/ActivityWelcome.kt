package com.android.hsdemo.ui.welcome

import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import com.android.avchat.AVChatManager
import com.android.baselib.custom.eventbus.EventBus
import com.android.baselib.utils.Preferences
import com.android.baselib.utils.showShortToast
import com.android.hsdemo.*
import com.android.hsdemo.model.ItemOfMemberInfo
import com.android.hsdemo.model.MemberInfo
import com.android.hsdemo.model.User
import com.android.hsdemo.network.HttpCallback
import com.android.hsdemo.network.RemoteRepositoryImpl
import com.android.hsdemo.ui.login.ActivityLogin
import com.android.hsdemo.ui.main.ActivityMain
import com.android.hsdemo.ui.rtc.ActivityRTC
import com.elvishew.xlog.XLog
import com.rxjava.rxlife.life
import com.tencent.imsdk.v2.V2TIMCallback
import kotlinx.coroutines.*

class ActivityWelcome : AppCompatActivity() {

    private val userName = Preferences.getString(KEY_USER_NAME)
    private val userAccid = Preferences.getString(KEY_ACCID)
    private val userSig = Preferences.getString(KEY_USERSIG)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    private fun init() {
        if (TextUtils.isEmpty(userName)) {
            goLogin()
        } else {
            //外面套一个接口是为了鉴别是否登录
            RemoteRepositoryImpl.getUserListByAccids(userAccid)
                .subscribe(
                    {
                        //再次登录IM
                        AVChatManager.login(userAccid, userSig, object :
                            V2TIMCallback {
                            override fun onError(p0: Int, p1: String?) {
                                showShortToast("自动登录失败，请手动登录")
                            }

                            override fun onSuccess() {
                                goMain(null)
                            }
                        })
                    }
                ) { throwable: Throwable? ->
                    showShortToast("网络异常 ${throwable?.message.toString()}")
                    goLogin()
                }
        }
    }

    private fun goMain(t: User?) {
        if (t != null)
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

}