package com.android.hsdemo.ui.main.fragments

import android.os.Build
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.android.baselib.ActivityManager
import com.android.baselib.base.BaseFragment
import com.android.baselib.utils.Preferences
import com.android.baselib.utils.showShortToast
import com.android.hsdemo.*
import com.android.hsdemo.databinding.FragmentCreateMeetingBinding
import com.android.hsdemo.model.Meeting
import com.android.hsdemo.model.StatusView
import com.android.hsdemo.network.HttpCallback
import com.android.hsdemo.ui.main.ActivityMain
import com.android.hsdemo.ui.main.vm.VMFCreateMeeting
import com.android.hsdemo.ui.rtc.ActivityRTC
import com.android.hsdemo.util.controlFocusStatusOfView
import kotlinx.android.synthetic.main.fragment_create_meeting.*

class FragmentCreateMeeting : BaseFragment<VMFCreateMeeting, FragmentCreateMeetingBinding>(),
    View.OnFocusChangeListener {

    override fun getLayoutId(): Int = R.layout.fragment_create_meeting

    override fun getVariableId(): Int = BR.vm

    private var btns = arrayOfNulls<StatusView<View>>(3)
    private var tvs = arrayOfNulls<StatusView<TextView>>(3)
    private var imgs = arrayOfNulls<StatusView<ImageView>>(3)
    private lateinit var currentActivity: ActivityMain

    override fun afterCreate() {
        currentActivity = requireActivity() as ActivityMain

        btns[0] = StatusView<View>(btnCreateMeeting, 0, BTN_BACKGROUNDS[0], BTN_BACKGROUNDS[1])
        btns[1] = StatusView<View>(btnJoin, 1, BTN_BACKGROUNDS[0], BTN_BACKGROUNDS[1])
        btns[2] = StatusView<View>(btnCancel, 2, BTN_BACKGROUNDS[0], BTN_BACKGROUNDS[1])

        tvs[0] = StatusView<TextView>(btnCreateMeetingTv, 0, BTN_TEXT_COLORS[0], BTN_TEXT_COLORS[1])
        tvs[1] = StatusView<TextView>(btnJoinTv, 1, BTN_TEXT_COLORS[0], BTN_TEXT_COLORS[1])
        tvs[2] = StatusView<TextView>(btnCancelTv, 2, BTN_TEXT_COLORS[0], BTN_TEXT_COLORS[1])

        imgs[0] = StatusView<ImageView>(
            btnCreateMeetingImg,
            0,
            R.mipmap.icon_f_create2_meeting_1,
            R.mipmap.icon_f_create2_meeting_0
        )
        imgs[1] = StatusView<ImageView>(
            btnJoinImg,
            0,
            R.mipmap.icon_join_1,
            R.mipmap.icon_join_0
        )
        imgs[2] = StatusView<ImageView>(
            btnCancelImg,
            0,
            R.mipmap.icon_back_1,
            R.mipmap.icon_back_0
        )

        initListener()
        changeTypeUI(true)
    }

    private fun initListener() {
        btnCreateMeeting.onFocusChangeListener = this
        btnSelectPeople.onFocusChangeListener = this
        btnJoin.onFocusChangeListener = this
        btnCancel.onFocusChangeListener = this

        //发起会议
        btnCreateMeeting.setOnClickListener {
            currentActivity.showLoading()
            mViewModel.createMeeting(this, object : HttpCallback<Meeting> {
                override fun success(t: Meeting) {
                    currentActivity.dismissLoading()
                    mViewModel.meetingNo.value = t.meetingNo
                    mViewModel.meetingId.value = t.id
                    changeTypeUI(false)
                }

                override fun failed(msg: String) {
                    currentActivity.dismissLoading()
                    showShortToast(msg)
                }
            })
        }

        //加入会议
        btnJoin.setOnClickListener {
            currentActivity.showLoading()
            mViewModel.joinMeeting(this,object :HttpCallback<String>{
                override fun success(t: String) {
                    currentActivity.dismissLoading()
                    ActivityRTC.start(requireActivity(),Preferences.getString(KEY_ACCID),mViewModel.meetingNo.value.toString(),mViewModel.meetingName.value.toString())
                }

                override fun failed(msg: String) {
                    currentActivity.dismissLoading()
                    showShortToast(msg)
                }

            })
        }

        //返回
        btnCancel.setOnClickListener {
            changeTypeUI(true)
        }

    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onFocusChange(view: View, focus: Boolean) {
        when (view) {
            btnCreateMeeting -> {
                if (focus) {
                    btns[0]?.selectedResId?.let { btnCreateMeeting.setBackgroundResource(it) }
                    tvs[0]?.selectedResId?.let {
                        context?.getColor(it)?.let { btnCreateMeetingTv.setTextColor(it) }
                    }
                    imgs[0]?.selectedResId?.let { btnCreateMeetingImg.setImageResource(it) }
                } else {
                    btns[0]?.unSelectedResId?.let { btnCreateMeeting.setBackgroundResource(it) }
                    tvs[0]?.unSelectedResId?.let {
                        context?.getColor(it)?.let { btnCreateMeetingTv.setTextColor(it) }
                    }
                    imgs[0]?.unSelectedResId?.let { btnCreateMeetingImg.setImageResource(it) }
                }
            }
            btnSelectPeople -> {
                if (focus) {
                    btnSelectPeople.setImageResource(R.mipmap.icon_select_people_1)
                } else {
                    btnSelectPeople.setImageResource(R.mipmap.icon_select_people_0)
                }
            }
            btnJoin -> {
                if (focus) {
                    btns[1]?.selectedResId?.let { btnJoin.setBackgroundResource(it) }
                    tvs[1]?.selectedResId?.let {
                        context?.getColor(it)?.let { btnJoinTv.setTextColor(it) }
                    }
                    imgs[1]?.selectedResId?.let { btnJoinImg.setImageResource(it) }
                } else {
                    btns[1]?.unSelectedResId?.let { btnJoin.setBackgroundResource(it) }
                    tvs[1]?.unSelectedResId?.let {
                        context?.getColor(it)?.let { btnJoinTv.setTextColor(it) }
                    }
                    imgs[1]?.unSelectedResId?.let { btnJoinImg.setImageResource(it) }
                }
            }
            btnCancel -> {
                if (focus) {
                    btns[2]?.selectedResId?.let { btnCancel.setBackgroundResource(it) }
                    tvs[2]?.selectedResId?.let {
                        context?.getColor(it)?.let { btnCancelTv.setTextColor(it) }
                    }
                    imgs[2]?.selectedResId?.let { btnCancelImg.setImageResource(it) }
                } else {
                    btns[2]?.unSelectedResId?.let { btnCancel.setBackgroundResource(it) }
                    tvs[2]?.unSelectedResId?.let {
                        context?.getColor(it)?.let { btnCancelTv.setTextColor(it) }
                    }
                    imgs[2]?.unSelectedResId?.let { btnCancelImg.setImageResource(it) }
                }
            }
        }
    }

    /**
     * 创建会议跟加入会议的UI切换方法
     */
    fun changeTypeUI(isCreateMeeting: Boolean) {
        if (isCreateMeeting) {
            mViewModel.clearStatus()
            etMeetingName.isEnabled = true
            btnCreateMeeting.visibility = View.VISIBLE
            llMeetingPwd.visibility = View.VISIBLE
            llUserBalance.visibility = View.VISIBLE
            btnSelectPeople.visibility = View.VISIBLE
            llJoin.visibility = View.GONE
            llMeetingNo.visibility = View.GONE
        } else {
            etMeetingName.isEnabled = false
            btnCreateMeeting.visibility = View.GONE
            llMeetingPwd.visibility = View.GONE
            llUserBalance.visibility = View.GONE
            btnSelectPeople.visibility = View.INVISIBLE
            llJoin.visibility = View.VISIBLE
            llMeetingNo.visibility = View.VISIBLE
            controlFocusStatusOfView(btnJoin,true)
        }
    }

}