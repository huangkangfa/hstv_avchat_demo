package com.android.hsdemo.ui.login.fragments

import android.os.Build
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.android.baselib.base.BaseFragment
import com.android.baselib.utils.showShortToast
import com.android.hsdemo.BR
import com.android.hsdemo.BTN_BACKGROUNDS
import com.android.hsdemo.BTN_TEXT_COLORS
import com.android.hsdemo.R
import com.android.hsdemo.custom.button.ButtonVerify
import com.android.hsdemo.custom.dialog.DialogWait
import com.android.hsdemo.databinding.FragmentRegisterForgetBinding
import com.android.hsdemo.model.StatusView
import com.android.hsdemo.network.HttpCallback
import com.android.hsdemo.ui.login.vm.VMLogin
import com.android.hsdemo.util.*
import kotlinx.android.synthetic.main.fragment_login.etUserName
import kotlinx.android.synthetic.main.fragment_register_forget.*
import kotlinx.android.synthetic.main.view_button_verify.view.*

class FragmentRegisterAndForget(fragmentType: VMLogin.FragmentType) :
    BaseFragment<VMLogin, FragmentRegisterForgetBinding>(), View.OnFocusChangeListener {

    override fun getLayoutId(): Int = R.layout.fragment_register_forget

    override fun getVariableId(): Int = BR.vm

    private val mFragmentType: VMLogin.FragmentType = fragmentType

    /**
     * 可控制按钮数组
     */
    private var btns = arrayOfNulls<StatusView<View>>(2)
    private var tvs = arrayOfNulls<StatusView<TextView>>(2)
    private var imgs = arrayOfNulls<StatusView<ImageView>>(2)

    private lateinit var waitDialog : DialogWait

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun afterCreate() {
        mViewModel.fragmentType.value = mFragmentType
        initButton()
        initVerify()
        controlFocusStatusOfView(etUserName, true)
    }

    private fun initVerify() {
        waitDialog = DialogWait(requireActivity())
        btnVerify.setOnClickVerifyListener(object : ButtonVerify.OnClickVerifyListener {
            override fun onClick(view: View) {
                waitDialog.show()
                mViewModel.sendVerifyCode(this@FragmentRegisterAndForget,object : HttpCallback<String>{
                    override fun success(t: String) {
                        waitDialog.dismiss()
                        btnVerify.start()
                    }

                    override fun failed(msg: String) {
                        waitDialog.dismiss()
                    }

                })
            }
            override fun onFinish() {
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun initButton() {
        btnOK.onFocusChangeListener = this
        btnCancel.onFocusChangeListener = this

        btns[0] = StatusView<View>(btnOK, 0, BTN_BACKGROUNDS[0], BTN_BACKGROUNDS[1])
        btns[1] = StatusView<View>(btnCancel, 1, BTN_BACKGROUNDS[0], BTN_BACKGROUNDS[1])

        tvs[0] = StatusView<TextView>(btnOKTv, 0, BTN_TEXT_COLORS[0], BTN_TEXT_COLORS[1])
        tvs[1] = StatusView<TextView>(btnCancelTv, 1, BTN_TEXT_COLORS[0], BTN_TEXT_COLORS[1])

        if (mViewModel.fragmentType.value == VMLogin.FragmentType.TYPE_REGISTER) {
            btnOKImg.setImageDrawable(activity?.getDrawable(R.mipmap.icon_register_0))
            imgs[0] = StatusView<ImageView>(
                btnOKImg,
                0,
                R.mipmap.icon_register_1,
                R.mipmap.icon_register_0
            )
        } else {
            btnOKImg.setImageDrawable(activity?.getDrawable(R.mipmap.icon_sure_0))
            imgs[0] = StatusView<ImageView>(
                btnOKImg,
                0,
                R.mipmap.icon_sure_1,
                R.mipmap.icon_sure_0
            )
        }
        imgs[1] =
            StatusView<ImageView>(btnCancelImg, 1, R.mipmap.icon_back_1, R.mipmap.icon_back_0)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onFocusChange(view: View, focus: Boolean) {
        if (focus) {
            if (btnOK == view) {
                changeViewBackground(0, btns)
                changeTextColor(0, tvs)
                changeImageSrc(0, imgs)
            } else {
                changeViewBackground(1, btns)
                changeTextColor(1, tvs)
                changeImageSrc(1, imgs)
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