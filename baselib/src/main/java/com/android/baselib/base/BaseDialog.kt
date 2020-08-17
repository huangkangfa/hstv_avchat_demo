package com.android.baselib.base

import android.app.Activity
import android.app.Dialog
import android.view.LayoutInflater
import com.android.baselib.R

/**
 * APP基础Dialog
 * 提供Dialog快捷功能
 * Created by huangkangfa on 16/3/23.
 */
open class BaseDialog(var mActivity: Activity) :
    Dialog(mActivity, R.style.FullScreenDialog) {

    /**
     * 添加Viwe 宽高度参数(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
     * @param layoutResID
     * @param layoutWidth
     * @param layoutHeight
     */
    fun setContentView(layoutResID: Int, layoutWidth: Int, layoutHeight: Int) {
        setContentView(layoutResID, layoutWidth, layoutHeight, true)
    }

    /**
     * 添加Viwe 宽高度参数(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
     * @param layoutResID viewID
     * @param layoutWidth 宽度
     * @param layoutHeight 高度
     * @param cancel true is click框外 dismiss
     */
    fun setContentView(
        layoutResID: Int,
        layoutWidth: Int,
        layoutHeight: Int,
        cancel: Boolean
    ) {
        setCanceledOnTouchOutside(cancel)
        val viewRoot = LayoutInflater.from(mActivity).inflate(layoutResID, null)
        setContentView(viewRoot)
        window!!.setLayout(layoutWidth, layoutHeight)
    }

    /**
     * 添加进出场动画
     * @param anumStyleResID
     */
    protected fun setAnimations(anumStyleResID: Int) {
        window!!.setWindowAnimations(anumStyleResID)
    }

    /**
     * 添加相对位置
     * @param gravity
     */
    protected fun setGravity(gravity: Int) {
        window!!.setGravity(gravity)
    }

    companion object {
        /**
         * Special value for the height or width requested by a View.
         * MATCH_PARENT means that the view wants to be as big as its parent,
         * minus the parent's padding, if any. Introduced in API Level 8.
         */
        const val MATCH_PARENT = -1

        /**
         * Special value for the height or width requested by a View.
         * WRAP_CONTENT means that the view wants to be just large enough to fit
         * its own internal content, taking its own padding into account.
         */
        const val WRAP_CONTENT = -2
    }

}