package com.android.hsdemo.ui.main.fragments

import com.android.baselib.base.BaseFragment
import com.android.hsdemo.BR
import com.android.hsdemo.R
import com.android.hsdemo.databinding.FragmentCreateMeetingBinding
import com.android.hsdemo.ui.main.vm.VMFCreateMeeting

class FragmentCreateMeeting : BaseFragment<VMFCreateMeeting, FragmentCreateMeetingBinding>() {

    override fun getLayoutId(): Int = R.layout.fragment_create_meeting

    override fun getVariableId(): Int = BR.vm

    override fun afterCreate() {

    }
}