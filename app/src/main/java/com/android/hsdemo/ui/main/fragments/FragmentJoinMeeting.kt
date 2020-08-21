package com.android.hsdemo.ui.main.fragments

import com.android.baselib.base.BaseFragment
import com.android.hsdemo.BR
import com.android.hsdemo.R
import com.android.hsdemo.databinding.FragmentJoinMeetingBinding
import com.android.hsdemo.ui.main.vm.VMFJoinMeeting

class FragmentJoinMeeting : BaseFragment<VMFJoinMeeting, FragmentJoinMeetingBinding>() {

    override fun getLayoutId(): Int = R.layout.fragment_join_meeting

    override fun getVariableId(): Int = BR.vm

    override fun afterCreate() {

    }
}