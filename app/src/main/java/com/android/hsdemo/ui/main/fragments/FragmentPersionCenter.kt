package com.android.hsdemo.ui.main.fragments

import com.android.baselib.base.BaseFragment
import com.android.hsdemo.BR
import com.android.hsdemo.R
import com.android.hsdemo.databinding.FragmentPersionCenterBinding
import com.android.hsdemo.ui.main.vm.VMFPersionCenter

class FragmentPersionCenter : BaseFragment<VMFPersionCenter, FragmentPersionCenterBinding>() {

    override fun getLayoutId(): Int = R.layout.fragment_persion_center

    override fun getVariableId(): Int = BR.vm

    override fun afterCreate() {

    }
}