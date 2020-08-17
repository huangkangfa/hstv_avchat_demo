package com.android.baselib.utils

import android.util.TypedValue
import com.android.baselib.global.AppGlobal.context

/**
 * dp转px
 *
 * @param dpVal
 * @return
 */
fun dp2px(dpVal: Float): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dpVal, context.resources.displayMetrics
    ).toInt()
}

/**
 * sp转px
 *
 * @param spVal
 * @return
 */
fun sp2px(spVal: Float): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        spVal, context.resources.displayMetrics
    ).toInt()
}

/**
 * px转dp
 *
 * @param pxVal
 * @return
 */
fun px2dp(pxVal: Float): Float {
    val scale: Float =
        context.resources.displayMetrics.density
    return pxVal / scale
}

/**
 * px转sp
 *
 * @param pxVal
 * @return
 */
fun px2sp(pxVal: Float): Float {
    return pxVal / context.resources.displayMetrics.scaledDensity
}