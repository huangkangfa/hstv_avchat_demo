package com.android.hsdemo.ui.main.fragments

import android.view.View
import androidx.fragment.app.Fragment
import com.android.baselib.base.BaseFragment
import com.android.hsdemo.BR
import com.android.hsdemo.R
import com.android.hsdemo.databinding.FragmentJoinMeetingBinding
import com.android.hsdemo.ui.main.vm.VMFJoinMeeting
import kotlinx.android.synthetic.main.fragment_join_meeting.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
        ).hide(fragments[0]).commit()
        addFragment(
            R.id.flBody,
            fragments[1],
            "part2",
            false
        ).hide(fragments[1]).commit()
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
        transaction.show(fragment).commit()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden && fType) {
            if (mViewModel.isInitOK) {
                //请求数据
                mViewModel.requestData(this)
            } else {
                GlobalScope.launch(Dispatchers.IO) {
                    delay(50)
                    onHiddenChanged(hidden)
                }
            }
        }
    }

}