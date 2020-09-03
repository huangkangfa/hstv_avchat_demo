package com.android.hsdemo.ui.main.fragments

import android.os.Build
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import com.android.baselib.base.BaseFragment
import com.android.baselib.custom.eventbus.EventBus
import com.android.baselib.utils.Preferences
import com.android.baselib.utils.showShortToast
import com.android.hsdemo.*
import com.android.hsdemo.EventKey.LOGOUT
import com.android.hsdemo.databinding.FragmentPersionCenterBinding
import com.android.hsdemo.model.StatusView
import com.android.hsdemo.network.HttpCallback
import com.android.hsdemo.ui.login.ActivityLogin
import com.android.hsdemo.ui.main.ActivityMain
import com.android.hsdemo.ui.main.vm.VMFPersionCenter
import com.bumptech.glide.Glide
import com.elvishew.xlog.XLog
import kotlinx.android.synthetic.main.fragment_join_meeting_part2.*
import kotlinx.android.synthetic.main.fragment_persion_center.*

class FragmentPersionCenter : BaseFragment<VMFPersionCenter, FragmentPersionCenterBinding>(),
    View.OnFocusChangeListener {

    override fun getLayoutId(): Int = R.layout.fragment_persion_center

    override fun getVariableId(): Int = BR.vm

    private var lastNickName: String = ""

    private var btns = arrayOfNulls<StatusView<View>>(1)
    private var tvs = arrayOfNulls<StatusView<TextView>>(1)
    private var imgs = arrayOfNulls<StatusView<ImageView>>(1)

    override fun afterCreate() {
        initData()
        initFocusThings()
        initHeadImg()

        EventBus.with(LOGOUT,String::class.java).observe(this, Observer {
            clearUser()
            ActivityLogin.start(requireActivity())
            requireActivity().finish()
        })
    }

    private fun initHeadImg() {
        Glide.with(this)
            .load(mViewModel.userAvatar.value.toString())
            .placeholder(R.mipmap.icon_default)
            .error(R.mipmap.icon_default)
            .into(ivHead)
    }

    private fun initFocusThings() {
        btns[0] = StatusView<View>(btnExit, 0, BTN_BACKGROUNDS[0], BTN_BACKGROUNDS[1])
        tvs[0] = StatusView<TextView>(btnExitTv, 0, BTN_TEXT_COLORS[0], BTN_TEXT_COLORS[1])
        imgs[0] = StatusView<ImageView>(
            btnExitImg,
            0,
            R.mipmap.icon_btn_exit_1,
            R.mipmap.icon_btn_exit_0
        )
        //退出登录
        btnExit.onFocusChangeListener = this
        btnExit.setOnClickListener {
            EventBus.with(LOGOUT,String::class.java).postValue("")
        }

        //昵称
        etUserName.onFocusChangeListener = this

        btnExit.nextFocusDownId = requireActivity().findViewById<View>(R.id.btnPersionCenter).id
    }

    private fun initData() {
        mViewModel.userNickName.value = Preferences.getString(KEY_USER_NICK_NAME)
        mViewModel.userPhone.value = Preferences.getString(KEY_USER_NAME)
        mViewModel.userAvatar.value = Preferences.getString(KEY_AVATAR)
        mViewModel.userBalance.value = Preferences.getString(KEY_USER_BALANCE)
        if (mViewModel.userBalance.value == null) {
            mViewModel.userBalance.value = "0"
        }
        lastNickName = mViewModel.userNickName.value.toString()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onFocusChange(view: View, focus: Boolean) {
        when (view) {
            btnExit -> {
                if (focus) {
                    btns[0]?.selectedResId?.let { btnExit.setBackgroundResource(it) }
                    tvs[0]?.selectedResId?.let {
                        context?.getColor(it)?.let { btnExitTv.setTextColor(it) }
                    }
                    imgs[0]?.selectedResId?.let { btnExitImg.setImageResource(it) }
                } else {
                    btns[0]?.unSelectedResId?.let { btnExit.setBackgroundResource(it) }
                    tvs[0]?.unSelectedResId?.let {
                        context?.getColor(it)?.let { btnExitTv.setTextColor(it) }
                    }
                    imgs[0]?.unSelectedResId?.let { btnExitImg.setImageResource(it) }
                }
            }
            etUserName -> {
                if (!focus) {
                    if (TextUtils.isEmpty(mViewModel.userNickName.value.toString())) {
                        mViewModel.userNickName.value = lastNickName
                    } else if (!TextUtils.equals(
                            lastNickName,
                            mViewModel.userNickName.value.toString()
                        )
                    ) {
                        (requireActivity() as ActivityMain).showLoading()
                        //变更昵称
                        mViewModel.modifyUserInfo(this, object : HttpCallback<String> {
                            override fun success(t: String) {
                                (requireActivity() as ActivityMain).dismissLoading()
                                lastNickName = mViewModel.userNickName.value.toString()
                                Preferences.saveValue(KEY_USER_NICK_NAME, lastNickName)
                                showShortToast("昵称修改成功")
                            }

                            override fun failed(msg: String) {
                                (requireActivity() as ActivityMain).dismissLoading()
                                showShortToast(msg)
                            }
                        })
                    }
                }
            }
        }
    }

}