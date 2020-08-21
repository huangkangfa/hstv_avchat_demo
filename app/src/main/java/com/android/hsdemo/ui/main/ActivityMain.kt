package com.android.hsdemo.ui.main

import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.android.baselib.base.BaseFragmentActivity
import com.android.hsdemo.BTN_MAIN_BACKGROUNDS
import com.android.hsdemo.BTN_TEXT_COLORS
import com.android.hsdemo.R
import com.android.hsdemo.model.StatusView
import com.android.hsdemo.ui.main.fragments.FragmentAddressBook
import com.android.hsdemo.ui.main.fragments.FragmentCreateMeeting
import com.android.hsdemo.ui.main.fragments.FragmentJoinMeeting
import com.android.hsdemo.ui.main.fragments.FragmentPersionCenter
import com.android.hsdemo.util.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_register_forget.*

class ActivityMain : BaseFragmentActivity(), View.OnFocusChangeListener {

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, ActivityMain().javaClass))
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_main

    /**
     * 可控制按钮数组
     */
    private var btns = arrayOfNulls<StatusView<View>>(4)
    private var tvs = arrayOfNulls<StatusView<TextView>>(4)
    private var imgs = arrayOfNulls<StatusView<ImageView>>(4)

    override fun afterCreate() {

        btnPersionCenter.onFocusChangeListener = this
        btnJoinMeeting.onFocusChangeListener = this
        btnCreateMeeting.onFocusChangeListener = this
        btnAddressBook.onFocusChangeListener = this

        btns[0] =
            StatusView<View>(btnPersionCenter, 0, BTN_MAIN_BACKGROUNDS[0], BTN_MAIN_BACKGROUNDS[1])
        btns[1] =
            StatusView<View>(btnJoinMeeting, 1, BTN_MAIN_BACKGROUNDS[0], BTN_MAIN_BACKGROUNDS[1])
        btns[2] =
            StatusView<View>(btnCreateMeeting, 2, BTN_MAIN_BACKGROUNDS[0], BTN_MAIN_BACKGROUNDS[1])
        btns[3] =
            StatusView<View>(btnAddressBook, 3, BTN_MAIN_BACKGROUNDS[0], BTN_MAIN_BACKGROUNDS[1])

        tvs[0] = StatusView<TextView>(btnPersionCenterTv, 0, BTN_TEXT_COLORS[0], BTN_TEXT_COLORS[1])
        tvs[1] = StatusView<TextView>(btnJoinMeetingTv, 1, BTN_TEXT_COLORS[0], BTN_TEXT_COLORS[1])
        tvs[2] = StatusView<TextView>(btnCreateMeetingTv, 2, BTN_TEXT_COLORS[0], BTN_TEXT_COLORS[1])
        tvs[3] = StatusView<TextView>(btnAddressBookTv, 3, BTN_TEXT_COLORS[0], BTN_TEXT_COLORS[1])

        imgs[0] = StatusView<ImageView>(
            btnPersionCenterImg,
            0,
            R.mipmap.icon_main_persion_center_1,
            R.mipmap.icon_main_persion_center_0
        )
        imgs[1] =
            StatusView<ImageView>(
                btnJoinMeetingImg,
                1,
                R.mipmap.icon_main_join_meeting_1,
                R.mipmap.icon_main_join_meeting_0
            )
        imgs[2] =
            StatusView<ImageView>(
                btnCreateMeetingImg,
                2,
                R.mipmap.icon_main_create_meeting_1,
                R.mipmap.icon_main_create_meeting_0
            )
        imgs[3] =
            StatusView<ImageView>(
                btnAddressBookImg,
                3,
                R.mipmap.icon_main_contact_1,
                R.mipmap.icon_main_contact_0
            )

        controlFocusStatusOfView(btnPersionCenter, true)

    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onFocusChange(view: View, focus: Boolean) {
        if (focus) {
            when {
                btnPersionCenter == view -> {
                    //个人中心
                    changeViewBackground(0, btns)
                    changeTextColor(0, tvs)
                    changeImageSrc(0, imgs)
                    replaceFragment(
                        R.id.flMain,
                        FragmentPersionCenter(),
                        "persionCenter",
                        false
                    ).commit()
                }
                btnJoinMeeting == view -> {
                    //参加会议
                    changeViewBackground(1, btns)
                    changeTextColor(1, tvs)
                    changeImageSrc(1, imgs)
                    replaceFragment(
                        R.id.flMain,
                        FragmentJoinMeeting(),
                        "joinMeeting",
                        false
                    ).commit()
                }
                btnCreateMeeting == view -> {
                    //发起会议
                    changeViewBackground(2, btns)
                    changeTextColor(2, tvs)
                    changeImageSrc(2, imgs)
                    replaceFragment(
                        R.id.flMain,
                        FragmentCreateMeeting(),
                        "createMeeting",
                        false
                    ).commit()
                }
                else -> {
                    //通讯录
                    changeViewBackground(3, btns)
                    changeTextColor(3, tvs)
                    changeImageSrc(3, imgs)
                    replaceFragment(
                        R.id.flMain,
                        FragmentAddressBook(),
                        "addressBook",
                        false
                    ).commit()
                }
            }
        } else {
            if (isFocusClear(btns)) {
                changeViewBackground(-1, btns)
            }
        }
    }

}