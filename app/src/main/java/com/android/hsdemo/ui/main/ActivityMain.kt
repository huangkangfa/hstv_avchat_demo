package com.android.hsdemo.ui.main

import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.KeyEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.android.baselib.base.BaseFragmentActivity
import com.android.hsdemo.BTN_MAIN_BACKGROUNDS
import com.android.hsdemo.BTN_TEXT_COLORS
import com.android.hsdemo.R
import com.android.hsdemo.custom.dialog.DialogHint
import com.android.hsdemo.custom.dialog.DialogWait
import com.android.hsdemo.model.StatusView
import com.android.hsdemo.ui.main.fragments.*
import com.android.hsdemo.util.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlin.system.exitProcess

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

    private lateinit var dialogHint: DialogHint
    private lateinit var dialogWait: DialogWait

    private lateinit var fragmentJoinMeeting: FragmentJoinMeeting
    private lateinit var fragmentCreateMeeting: FragmentCreateMeeting
    private lateinit var fragmentAddressBook: FragmentAddressBook
    private lateinit var fragmentPersionCenter: FragmentPersionCenter
    private var currentFragment: Fragment = Fragment()

    override fun afterCreate() {
        initDialog()
        initFragments()
        initFocus()
    }

    private fun initFocus() {
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

        GlobalScope.launch(Dispatchers.IO) {
            delay(200)
            withContext(Dispatchers.Main) {
                controlFocusStatusOfView(btnJoinMeeting, true)
            }
        }
    }

    private fun initDialog() {
        dialogWait = DialogWait(this@ActivityMain)
        dialogHint = DialogHint(this@ActivityMain)
        dialogHint.setContent("确定要退出应用吗？")
        dialogHint.setOnSureClickListener(View.OnClickListener {
            exitProcess(0)
        })
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
                    showFragment(fragmentPersionCenter)
                }
                btnJoinMeeting == view -> {
                    //参加会议
                    changeViewBackground(1, btns)
                    changeTextColor(1, tvs)
                    changeImageSrc(1, imgs)
                    showFragment(fragmentJoinMeeting)
                }
                btnCreateMeeting == view -> {
                    //发起会议
                    changeViewBackground(2, btns)
                    changeTextColor(2, tvs)
                    changeImageSrc(2, imgs)
                    showFragment(fragmentCreateMeeting)
                }
                else -> {
                    //通讯录
                    changeViewBackground(3, btns)
                    changeTextColor(3, tvs)
                    changeImageSrc(3, imgs)
                    showFragment(fragmentAddressBook)
                }
            }
        } else {
            if (isFocusClear(btns)) {
                changeViewBackground(-1, btns)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val currentFragment = getVisibleFragment()
        if (currentFragment == fragmentJoinMeeting && fragmentJoinMeeting.fType) {
            (fragmentJoinMeeting.fragments[0] as FragmentJoinMeetingPart1).mViewModel.requestData(this)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            val currentFragment = getVisibleFragment()
            if (currentFragment == fragmentCreateMeeting) {
                //处理创建会议的二级界面返回
                if (fragmentCreateMeeting.btnCreateMeeting.visibility == View.GONE) {
                    fragmentCreateMeeting.changeTypeUI(true)
                    return true
                }
            }
            if (currentFragment == fragmentJoinMeeting) {
                //处理参加会议的二级界面返回
                if (!fragmentJoinMeeting.fType) {
                    fragmentJoinMeeting.changeFragment(true)
                    return true
                }
            }
            //退出应用时
            dialogHint.show()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    fun showLoading(str: String = "") {
        dialogWait.show(str)
    }

    fun dismissLoading() {
        dialogWait.dismiss()
    }

    private fun initFragments() {

        fragmentJoinMeeting = FragmentJoinMeeting()
        fragmentCreateMeeting = FragmentCreateMeeting()
        fragmentAddressBook = FragmentAddressBook()
        fragmentPersionCenter = FragmentPersionCenter()

        addFragment(
            R.id.flMain,
            fragmentCreateMeeting,
            "createMeeting",
            false
        ).hide(fragmentCreateMeeting).commitAllowingStateLoss()
        addFragment(
            R.id.flMain,
            fragmentAddressBook,
            "addressBook",
            false
        ).hide(fragmentAddressBook).commitAllowingStateLoss()
        addFragment(
            R.id.flMain,
            fragmentPersionCenter,
            "persionCenter",
            false
        ).hide(fragmentPersionCenter).commitAllowingStateLoss()
        addFragment(
            R.id.flMain,
            fragmentJoinMeeting,
            "joinMeeting",
            false
        ).show(fragmentJoinMeeting).commitAllowingStateLoss()
    }

    private fun showFragment(fragment: Fragment) {
        if (currentFragment == fragment)
            return
        val transaction = supportFragmentManager.beginTransaction()
        transaction.hide(currentFragment)
        currentFragment = fragment
        transaction.show(fragment).commitAllowingStateLoss()
    }

}