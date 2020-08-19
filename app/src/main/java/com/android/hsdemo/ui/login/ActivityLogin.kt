package com.android.hsdemo.ui.login

import android.view.View
import com.android.baselib.base.BaseFragmentActivity
import com.android.hsdemo.R
import com.android.hsdemo.ui.entrance.ActivityEntrance

class ActivityLogin : BaseFragmentActivity() {

    override fun getLayoutId(): Int = R.layout.activity_login

    override fun afterCreate() {
        replaceFragment(
            R.id.flBody,
            FragmentLogin(),
            "login",
            false
        ).commit()
    }

    fun onRegisteredClick(v: View) {
        replaceFragment(
            R.id.flBody,
            FragmentRegisterAndForget(VMLogin.FragmentType.TYPE_REGISTER),
            "register",
            true
        ).commit()
    }

    fun onLoginClick(v: View) {
        ActivityEntrance.start(this@ActivityLogin)
    }

    fun onForgetPasswordClick(v: View) {
        replaceFragment(
            R.id.flBody,
            FragmentRegisterAndForget(VMLogin.FragmentType.TYPE_FORGET),
            "forget",
            true
        ).commit()
    }

    fun onBackClick(v : View){
        this@ActivityLogin.onBackPressed()
    }

}