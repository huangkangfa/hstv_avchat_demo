package com.android.hsdemo.custom.dialog

import android.app.Activity
import android.text.TextUtils
import android.view.View
import com.android.baselib.base.BaseDialog
import com.android.hsdemo.R
import kotlinx.android.synthetic.main.dialog_wait.*

/**
 * 加载弹框
 */
class DialogWait(mActivity: Activity) : BaseDialog(mActivity) {

    init {
        setContentView(R.layout.dialog_wait, WRAP_CONTENT, WRAP_CONTENT, false)
    }

    /**
     * 展示弹框
     */
    fun show(str: String?) {
        mActivity.runOnUiThread {
            if (TextUtils.isEmpty(str)) {
                content.visibility = View.GONE
                content.text = ""
            } else {
                content.visibility = View.VISIBLE
                content.text = str
            }
        }
    }

}