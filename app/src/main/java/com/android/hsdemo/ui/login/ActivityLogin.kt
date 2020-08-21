package com.android.hsdemo.ui.login

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.fragment.app.Fragment
import com.android.baselib.base.BaseFragmentActivity
import com.android.baselib.utils.Preferences
import com.android.baselib.utils.showShortToast
import com.android.hsdemo.*
import com.android.hsdemo.custom.dialog.DialogWait
import com.android.hsdemo.model.User
import com.android.hsdemo.network.HttpCallback
import com.android.hsdemo.ui.login.fragments.FragmentLogin
import com.android.hsdemo.ui.login.fragments.FragmentRegisterAndForget
import com.android.hsdemo.ui.login.vm.VMLogin
import com.android.hsdemo.ui.main.ActivityMain

class ActivityLogin : BaseFragmentActivity() {

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, ActivityLogin().javaClass))
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_login

    private lateinit var fragmentLogin: FragmentLogin
    private lateinit var fragmentRegister: FragmentRegisterAndForget
    private lateinit var fragmentForget: FragmentRegisterAndForget

    private lateinit var dialogWait: DialogWait

    override fun afterCreate() {
        fragmentLogin = FragmentLogin()
        fragmentRegister = FragmentRegisterAndForget(VMLogin.FragmentType.TYPE_REGISTER)
        fragmentForget = FragmentRegisterAndForget(VMLogin.FragmentType.TYPE_FORGET)
        dialogWait = DialogWait(this@ActivityLogin)

        replaceFragment(
            R.id.flBody,
            fragmentLogin,
            "login",
            false
        ).commit()
    }

    /**
     * 获取当前显示的fragment
     */
    private fun getVisibleFragment(): Fragment? {
        val fragments: List<Fragment> = this@ActivityLogin.supportFragmentManager.fragments
        for (fragment in fragments) {
            if (fragment.isVisible) return fragment
        }
        return null
    }

    /**
     * 注册或者忘记密码 确认点击
     */
    fun onRegisteredOrForgetClick(v: View) {
        val currentFragment = getVisibleFragment()
        if (currentFragment != null) {
            when (currentFragment) {
                fragmentRegister -> {
                    dialogWait.show()
                    fragmentRegister.mViewModel.register(this@ActivityLogin,
                        object : HttpCallback<User> {
                            override fun success(t: User) {
                                dialogWait.dismiss()
                                showShortToast("注册成功")
                                goMain(t,fragmentRegister.mViewModel.userPassword2.value.toString())
                            }

                            override fun failed(msg: String) {
                                dialogWait.dismiss()
                                showShortToast(msg)
                            }

                        })
                }
                fragmentForget -> {
                    dialogWait.show()
                    fragmentForget.mViewModel.modifyPassword(this@ActivityLogin,
                        object : HttpCallback<String> {
                            override fun success(t: String) {
                                dialogWait.dismiss()
                                showShortToast("修改成功，请重新登录")
                                //需要重新登录
                                replaceFragment(
                                    R.id.flBody,
                                    fragmentLogin,
                                    "login",
                                    false
                                ).commit()
                            }

                            override fun failed(msg: String) {
                                dialogWait.dismiss()
                                showShortToast(msg)
                            }

                        })
                }
            }
        }
    }

    /**
     * 注册
     */
    fun onRegisteredClick(v: View) {
        replaceFragment(
            R.id.flBody,
            fragmentRegister,
            "register",
            true
        ).commit()
    }

    /**
     * 登录
     */
    fun onLoginClick(v: View) {
        dialogWait.show()
        fragmentLogin.mViewModel.login(this, object : HttpCallback<User> {
            override fun success(t: User) {
                dialogWait.dismiss()
                goMain(t,fragmentLogin.mViewModel.userPassword.value.toString())
            }

            override fun failed(msg: String) {
                dialogWait.dismiss()
                showShortToast(msg)
            }
        })
    }

    /**
     * 忘记密码
     */
    fun onForgetPasswordClick(v: View) {
        replaceFragment(
            R.id.flBody,
            fragmentForget,
            "forget",
            true
        ).commit()
    }

    /**
     * 返回
     */
    fun onBackClick(v: View) {
        this@ActivityLogin.onBackPressed()
    }

    /**
     * 跳转至首页
     */
    fun goMain(t: User,pwd:String){
        //存储关键信息
        Preferences.saveValue(KEY_USER_NAME, t.userName.toString())
        Preferences.saveValue(KEY_USER_PASSWORD, pwd)
        Preferences.saveValue(KEY_TOKEN, t.token.toString())
        Preferences.saveValue(KEY_USERSIG, t.userSig.toString())
        Preferences.saveValue(KEY_ACCID, t.accid.toString())
        Preferences.saveValue(
            KEY_MEETINGNOTICEACCOUNT,
            t.meetingNoticeAccount.toString()
        )
        //跳转主界面
        ActivityMain.start(this@ActivityLogin)
    }

}