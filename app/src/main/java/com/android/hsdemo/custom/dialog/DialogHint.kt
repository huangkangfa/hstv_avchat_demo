package com.android.hsdemo.custom.dialog

import android.app.Activity
import android.os.Build
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.android.baselib.base.BaseDialog
import com.android.baselib.utils.dp2px
import com.android.hsdemo.BTN_BACKGROUNDS
import com.android.hsdemo.BTN_TEXT_COLORS
import com.android.hsdemo.R
import com.android.hsdemo.model.StatusView
import com.android.hsdemo.util.controlFocusStatusOfView
import kotlinx.android.synthetic.main.dialog_hint.*

/**
 * 提示通用弹窗
 */
class DialogHint @JvmOverloads constructor(activity: Activity, cancel: Boolean = true) :
    BaseDialog(activity), View.OnFocusChangeListener {

    /**
     * 可控制按钮数组
     */
    private var btns = arrayOfNulls<StatusView<View>>(2)
    private var tvs = arrayOfNulls<StatusView<TextView>>(2)
    private var imgs = arrayOfNulls<StatusView<ImageView>>(2)

    init {
        setContentView(R.layout.dialog_hint, dp2px(460f), WRAP_CONTENT, cancel)
        setGravity(Gravity.CENTER)
        setAnimations(R.anim.bottom_in)

        btnOK.onFocusChangeListener = this
        btnCancel.onFocusChangeListener = this

        btns[0] = StatusView<View>(btnOK, 0, BTN_BACKGROUNDS[0], BTN_BACKGROUNDS[1])
        btns[1] = StatusView<View>(btnCancel, 1, BTN_BACKGROUNDS[0], BTN_BACKGROUNDS[1])

        tvs[0] = StatusView<TextView>(btnOKTv, 0, BTN_TEXT_COLORS[0], BTN_TEXT_COLORS[1])
        tvs[1] = StatusView<TextView>(btnCancelTv, 1, BTN_TEXT_COLORS[0], BTN_TEXT_COLORS[1])

        imgs[0] = StatusView<ImageView>(
            btnOKImg,
            0,
            R.mipmap.icon_sure_1,
            R.mipmap.icon_sure_0
        )
        imgs[1] = StatusView<ImageView>(
            btnCancelImg,
            1,
            R.mipmap.icon_back_1,
            R.mipmap.icon_back_0
        )

        btnCancel.setOnClickListener { dismiss() }
        controlFocusStatusOfView(btnCancel, true)
    }

    /**
     * 设置弹框文本内容
     */
    fun setContent(str: String) {
        tvContent.text = str
    }

    /**
     * 确定按钮点击事件
     */
    fun setOnSureClickListener(listener: View.OnClickListener) {
        btnOK.setOnClickListener(listener)
    }

    /**
     * 取消按钮点击事件
     */
    fun setOnCancleClickListener(listener: View.OnClickListener) {
        btnCancel.setOnClickListener(listener)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onFocusChange(view: View, focus: Boolean) {
        if (focus) {
            when {
                btnOK == view -> {
                    btns[0]?.selectedResId?.let { btnOK.setBackgroundResource(it) }
                    tvs[0]?.selectedResId?.let { context?.getColor(it)?.let { btnOKTv.setTextColor(it) } }
                    imgs[0]?.selectedResId?.let { btnOKImg.setImageResource(it) }
                }
                btnCancel == view -> {
                    btns[1]?.selectedResId?.let { btnCancel.setBackgroundResource(it) }
                    tvs[1]?.selectedResId?.let { context?.getColor(it)?.let { btnCancelTv.setTextColor(it) } }
                    imgs[1]?.selectedResId?.let { btnCancelImg.setImageResource(it) }
                }
            }
        } else {
            when {
                btnOK == view -> {
                    btns[0]?.unSelectedResId?.let { btnOK.setBackgroundResource(it) }
                    tvs[0]?.unSelectedResId?.let { context?.getColor(it)?.let { btnOKTv.setTextColor(it) } }
                    imgs[0]?.unSelectedResId?.let { btnOKImg.setImageResource(it) }
                }
                btnCancel == view -> {
                    btns[1]?.unSelectedResId?.let { btnCancel.setBackgroundResource(it) }
                    tvs[1]?.unSelectedResId?.let { context?.getColor(it)?.let { btnCancelTv.setTextColor(it) } }
                    imgs[1]?.unSelectedResId?.let { btnCancelImg.setImageResource(it) }
                }
            }
        }
    }

}










