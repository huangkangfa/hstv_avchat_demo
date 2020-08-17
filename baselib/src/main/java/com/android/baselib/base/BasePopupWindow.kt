package com.android.baselib.base

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow

/**
 * PopupWindow基类
 *
 * @author huangkangfa 2015年8月18日 下午4:57:38
 */
open class BasePopupWindow(var mActivity: Activity) : PopupWindow(mActivity) {
    fun setContentView(resource: Int) {
        setContentView(
            resource,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )
    }

    /**
     * 设置contentView
     *
     * @param resource  viewID
     * @param width
     * @param height
     * @param focusable 如果是false的话PopUpWindow只是一个浮现在当前界面上的view而已，不影响当前界面的任何操作。一般情况下设置true
     */
    fun setContentView(
        resource: Int,
        width: Int,
        height: Int,
        focusable: Boolean
    ) {
        contentView = LayoutInflater.from(mActivity).inflate(resource, null)
        setWidth(width)
        setHeight(height)
        isFocusable = focusable
        isTouchable = true
        isOutsideTouchable = true
        setBackgroundDrawable(
            BitmapDrawable(
                mActivity.resources,
                null as Bitmap?
            )
        )
    }

    fun findViewById(id: Int): View {
        return contentView.findViewById(id)
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