package com.android.hsdemo.ui.main.fragments

import android.os.Build
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.android.baselib.base.BaseFragment
import com.android.hsdemo.BR
import com.android.hsdemo.BTN_BACKGROUNDS
import com.android.hsdemo.BTN_TEXT_COLORS
import com.android.hsdemo.R
import com.android.hsdemo.databinding.FragmentCreateMeetingBinding
import com.android.hsdemo.model.StatusView
import com.android.hsdemo.ui.main.vm.VMFCreateMeeting
import kotlinx.android.synthetic.main.fragment_create_meeting.*

class FragmentCreateMeeting : BaseFragment<VMFCreateMeeting, FragmentCreateMeetingBinding>(), View.OnFocusChangeListener {

    override fun getLayoutId(): Int = R.layout.fragment_create_meeting

    override fun getVariableId(): Int = BR.vm

    private var btns = arrayOfNulls<StatusView<View>>(1)
    private var tvs = arrayOfNulls<StatusView<TextView>>(1)
    private var imgs = arrayOfNulls<StatusView<ImageView>>(1)

    override fun afterCreate() {
        btnCreateMeeting.onFocusChangeListener = this

        btns[0] = StatusView<View>(btnCreateMeeting, 0, BTN_BACKGROUNDS[0], BTN_BACKGROUNDS[1])

        tvs[0] = StatusView<TextView>(btnCreateMeetingTv, 0, BTN_TEXT_COLORS[0], BTN_TEXT_COLORS[1])

        imgs[0] = StatusView<ImageView>(
            btnCreateMeetingImg,
            0,
            R.mipmap.icon_f_create2_meeting_1,
            R.mipmap.icon_f_create2_meeting_0
        )
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onFocusChange(view: View, focus: Boolean) {
        if(view == btnCreateMeeting){
            if (focus) {
                btns[0]?.selectedResId?.let { btnCreateMeeting.setBackgroundResource(it) }
                tvs[0]?.selectedResId?.let { context?.getColor(it)?.let { btnCreateMeetingTv.setTextColor(it) } }
                imgs[0]?.selectedResId?.let { btnCreateMeetingImg.setImageResource(it) }
            }else{
                btns[0]?.unSelectedResId?.let { btnCreateMeeting.setBackgroundResource(it) }
                tvs[0]?.unSelectedResId?.let { context?.getColor(it)?.let { btnCreateMeetingTv.setTextColor(it) } }
                imgs[0]?.unSelectedResId?.let { btnCreateMeetingImg.setImageResource(it) }
            }
        }
    }



}