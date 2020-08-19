package com.android.hsdemo.ui.login

import com.android.baselib.base.BaseFragment
import com.android.hsdemo.BR
import com.android.hsdemo.R
import com.android.hsdemo.databinding.FragmentLoginBinding

class FragmentLogin : BaseFragment<VMLogin, FragmentLoginBinding>() {

    override fun getLayoutId(): Int = R.layout.fragment_login

    override fun getVariableId(): Int = BR.vm

    override fun afterCreate() {

    }

}