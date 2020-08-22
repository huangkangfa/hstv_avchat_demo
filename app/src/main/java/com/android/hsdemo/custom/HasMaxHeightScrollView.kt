package com.android.hsdemo.custom

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.widget.ScrollView

/**
 * 有最大高度值的ScrollView
 */
class HasMaxHeightScrollView @JvmOverloads constructor(
    private val mContext: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ScrollView(
    mContext, attrs, defStyleAttr
) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var hMS = heightMeasureSpec
        try {
            val display = (mContext as Activity).windowManager.defaultDisplay
            val d = DisplayMetrics()
            display.getMetrics(d)
            // 设置控件最大高度不能超过屏幕高度的三分之一
            hMS = MeasureSpec.makeMeasureSpec(d.heightPixels / 3, MeasureSpec.AT_MOST)
        } catch (e: Exception) {
            Log.e("catch error", "" + e.message)
        }
        // 重新计算控件的宽高
        super.onMeasure(widthMeasureSpec, hMS)
    }
}