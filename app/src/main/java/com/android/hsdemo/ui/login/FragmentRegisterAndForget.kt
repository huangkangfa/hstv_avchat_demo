package com.android.hsdemo.ui.login

import com.android.baselib.base.BaseFragment
import com.android.hsdemo.BR
import com.android.hsdemo.R
import com.android.hsdemo.databinding.FragmentRegisterForgetBinding

class FragmentRegisterAndForget(fragmentType: VMLogin.FragmentType) :
    BaseFragment<VMLogin, FragmentRegisterForgetBinding>() {

    override fun getLayoutId(): Int = R.layout.fragment_register_forget

    override fun getVariableId(): Int = BR.vm

    private val mFragmentType: VMLogin.FragmentType = fragmentType

    override fun afterCreate() {
        //设置fragment类型
        mViewModel.fragmentType.value = mFragmentType
    }

}