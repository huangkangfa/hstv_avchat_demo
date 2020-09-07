package com.android.hsdemo.ui.main.fragments

import android.os.Build
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.android.baselib.base.BaseFragment
import com.android.baselib.utils.Preferences
import com.android.baselib.utils.showShortToast
import com.android.hsdemo.*
import com.android.hsdemo.databinding.FragmentJoinMeetingBinding
import com.android.hsdemo.model.MeetingDetail
import com.android.hsdemo.model.StatusView
import com.android.hsdemo.network.HttpCallback
import com.android.hsdemo.ui.custom_rtc.ActivityRTC2
import com.android.hsdemo.ui.main.ActivityMain
import com.android.hsdemo.ui.main.vm.VMFJoinMeeting
import com.android.hsdemo.ui.rtc.ActivityRTC
import com.android.hsdemo.util.controlFocusStatusOfView
import kotlinx.android.synthetic.main.fragment_join_meeting_part2.*

class FragmentJoinMeetingPart2 : BaseFragment<VMFJoinMeeting, FragmentJoinMeetingBinding>(),
    View.OnFocusChangeListener {

    override fun getLayoutId(): Int = R.layout.fragment_join_meeting_part2

    override fun getVariableId(): Int = BR.vm

    private var btns = arrayOfNulls<StatusView<View>>(1)
    private var tvs = arrayOfNulls<StatusView<TextView>>(1)
    private var imgs = arrayOfNulls<StatusView<ImageView>>(1)
    private var parentActivity: ActivityMain? = null

    override fun afterCreate() {

        parentActivity = requireActivity() as ActivityMain

        btnOK.onFocusChangeListener = this
        btns[0] = StatusView<View>(btnOK, 0, BTN_BACKGROUNDS[0], BTN_BACKGROUNDS[1])
        tvs[0] = StatusView<TextView>(btnOKTv, 0, BTN_TEXT_COLORS[0], BTN_TEXT_COLORS[1])
        imgs[0] = StatusView<ImageView>(
            btnOKImg,
            0,
            R.mipmap.icon_sure_1,
            R.mipmap.icon_sure_0
        )
        btnOK.setOnClickListener {
            parentActivity?.showLoading()
            mViewModel.joinMeeting(parentActivity!!, object : HttpCallback<MeetingDetail> {
                override fun success(t: MeetingDetail) {
                    parentActivity?.dismissLoading()
                    ActivityRTC.start(
                        parentActivity!!,
                        Preferences.getString(KEY_ACCID),
                        mViewModel.meetingNo.value.toString(),
                        mViewModel.meetingTitle.value.toString()
                    )
                }

                override fun failed(msg: String) {
                    parentActivity?.dismissLoading()
                    showShortToast(msg)
                }
            })
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onFocusChange(view: View, focus: Boolean) {
        if (focus) {
            if (view == btnOK) {
                btns[0]?.selectedResId?.let { btnOK.setBackgroundResource(it) }
                tvs[0]?.selectedResId?.let {
                    context?.getColor(it)?.let { btnOKTv.setTextColor(it) }
                }
                imgs[0]?.selectedResId?.let { btnOKImg.setImageResource(it) }
            }
        } else {
            if (view == btnOK) {
                btns[0]?.unSelectedResId?.let { btnOK.setBackgroundResource(it) }
                tvs[0]?.unSelectedResId?.let {
                    context?.getColor(it)?.let { btnOKTv.setTextColor(it) }
                }
                imgs[0]?.unSelectedResId?.let { btnOKImg.setImageResource(it) }
            }
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            mViewModel.meetingNo.value = ""
            mViewModel.meetingPassword.value = ""
            focusThis()
        }
    }

    fun focusThis() {
        controlFocusStatusOfView(etMeetingNo, true)
    }

}