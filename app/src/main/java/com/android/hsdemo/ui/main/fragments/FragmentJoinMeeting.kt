package com.android.hsdemo.ui.main.fragments

import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.android.baselib.base.BaseFragment
import com.android.baselib.custom.eventbus.EventBus
import com.android.hsdemo.BR
import com.android.hsdemo.EventKey
import com.android.hsdemo.R
import com.android.hsdemo.databinding.FragmentJoinMeetingBinding
import com.android.hsdemo.ui.main.vm.VMFJoinMeeting
import kotlinx.android.synthetic.main.fragment_join_meeting.*

class FragmentJoinMeeting : BaseFragment<VMFJoinMeeting, FragmentJoinMeetingBinding>() {

    var fType = true

    private var currentFragment: Fragment = Fragment()

    var fragments: ArrayList<Fragment> = arrayListOf(
        FragmentJoinMeetingPart1(),
        FragmentJoinMeetingPart2()
    )

    override fun getLayoutId(): Int = R.layout.fragment_join_meeting

    override fun getVariableId(): Int = BR.vm

    override fun afterCreate() {
        initFragments()
        changeFragment(fType)
    }

    private fun initFragments() {
        addFragment(
            R.id.flBody,
            fragments[0],
            "part1",
            false
        ).hide(fragments[0]).commitAllowingStateLoss()
        addFragment(
            R.id.flBody,
            fragments[1],
            "part2",
            false
        ).hide(fragments[1]).commitAllowingStateLoss()

        //离开视频会议，刷新此界面
        EventBus.with(EventKey.MEETING_STATUS_END,String::class.java).observe(this, Observer {
            if(fType){
                (fragments[0] as FragmentJoinMeetingPart1).mViewModel.requestData(this)
            }
        })

    }

    fun changeFragment(type: Boolean) {
        fType = type
        if (fType) {
            showFragment(fragments[0])
            tvTitle.visibility = View.VISIBLE
        } else {
            showFragment(fragments[1])
            tvTitle.visibility = View.GONE
        }
    }

    private fun showFragment(fragment: Fragment) {
        if (currentFragment == fragment)
            return
        val transaction = childFragmentManager.beginTransaction()
        transaction.hide(currentFragment)
        currentFragment = fragment
        transaction.show(fragment).commitAllowingStateLoss()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            if(fType){
                (fragments[0] as FragmentJoinMeetingPart1).mViewModel.requestData(this)
            }else{
                (fragments[1] as FragmentJoinMeetingPart2).focusThis()
            }
        }
    }

}