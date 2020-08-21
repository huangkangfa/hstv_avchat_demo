package com.android.hsdemo.ui.login.fragments

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
import com.android.hsdemo.databinding.FragmentLoginBinding
import com.android.hsdemo.model.StatusView
import com.android.hsdemo.ui.login.vm.VMLogin
import com.android.hsdemo.util.*
import kotlinx.android.synthetic.main.fragment_login.*

class FragmentLogin : BaseFragment<VMLogin, FragmentLoginBinding>(), View.OnFocusChangeListener {

    override fun getLayoutId(): Int = R.layout.fragment_login

    override fun getVariableId(): Int = BR.vm

    /**
     * 可控制按钮数组
     */
    private var btns = arrayOfNulls<StatusView<View>>(3)
    private var tvs = arrayOfNulls<StatusView<TextView>>(3)
    private var imgs = arrayOfNulls<StatusView<ImageView>>(3)

    override fun afterCreate() {

        btnRegistered.onFocusChangeListener = this
        btnLogin.onFocusChangeListener = this
        btnForget.onFocusChangeListener = this

        btns[0] = StatusView<View>(btnRegistered, 0, BTN_BACKGROUNDS[0], BTN_BACKGROUNDS[1])
        btns[1] = StatusView<View>(btnLogin, 1, BTN_BACKGROUNDS[0], BTN_BACKGROUNDS[1])
        btns[2] = StatusView<View>(btnForget, 2, BTN_BACKGROUNDS[0], BTN_BACKGROUNDS[1])

        tvs[0] = StatusView<TextView>(btnRegisteredTv, 0, BTN_TEXT_COLORS[0], BTN_TEXT_COLORS[1])
        tvs[1] = StatusView<TextView>(btnLoginTv, 1, BTN_TEXT_COLORS[0], BTN_TEXT_COLORS[1])
        tvs[2] = StatusView<TextView>(btnForgetTv, 2, BTN_TEXT_COLORS[0], BTN_TEXT_COLORS[1])

        imgs[0] = StatusView<ImageView>(
            btnRegisteredImg,
            0,
            R.mipmap.icon_register_1,
            R.mipmap.icon_register_0
        )
        imgs[1] =
            StatusView<ImageView>(btnLoginImg, 1, R.mipmap.icon_login_1, R.mipmap.icon_login_0)
        imgs[2] =
            StatusView<ImageView>(btnForgetImg, 2, R.mipmap.icon_forget_1, R.mipmap.icon_forget_0)

        controlFocusStatusOfView(etUserName, true)

    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onFocusChange(view: View, focus: Boolean) {
        if (focus) {
            when {
                btnRegistered == view -> {
                    changeViewBackground(0, btns)
                    changeTextColor(0, tvs)
                    changeImageSrc(0, imgs)
                }
                btnLogin == view -> {
                    changeViewBackground(1, btns)
                    changeTextColor(1, tvs)
                    changeImageSrc(1, imgs)
                }
                else -> {
                    changeViewBackground(2, btns)
                    changeTextColor(2, tvs)
                    changeImageSrc(2, imgs)
                }
            }
        } else {
            if (isFocusClear(btns)) {
                changeViewBackground(-1, btns)
                changeTextColor(-1, tvs)
                changeImageSrc(-1, imgs)
            }
        }
    }

}