package com.android.hsdemo.util

import android.content.Context
import android.os.Build
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.android.baselib.global.AppGlobal.context
import com.android.hsdemo.R
import com.android.hsdemo.model.StatusView
import com.elvishew.xlog.XLog

/**
 * 控制控件的聚焦状态
 */
fun controlFocusStatusOfView(view: View, focus: Boolean) {
    view.isFocusable = focus
    view.isFocusableInTouchMode = focus
    view.requestFocus()
    view.requestFocusFromTouch()
}

/**
 * 视图是否全部清除聚焦了
 */
fun isFocusClear(views: Array<View?>): Boolean {
    for (view in views) {
        if (view != null && view.isFocusableInTouchMode) {
            return false
        }
    }
    return true
}

fun isFocusClear(views: Array<StatusView<View>?>): Boolean {
    for (view in views) {
        if (view?.view != null && view.view.isFocusableInTouchMode) {
            return false
        }
    }
    return true
}


/**
 * 变更指定view背景，其他都是默认背景
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun changeViewBackground(
    index: Int,
    views: Array<StatusView<View>?>
) {
    for (statusView in views) {
        if (statusView?.view != null) {
            if (statusView.index == index) {
                statusView.view.background =
                    statusView.selectedResId.let { context.getDrawable(it) }
                statusView.view.animate().scaleX(1.1f).scaleY(1.1f).setDuration(500).start()
            } else {
                statusView.view.background =
                    statusView.unSelectedResId.let { context.getDrawable(it) }
                statusView.view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(500).start()
            }
        }
    }
}

/**
 * 变更指定TextView字体颜色,其他都是默认
 */
@RequiresApi(Build.VERSION_CODES.M)
fun changeTextColor(
    index: Int,
    views: Array<StatusView<TextView>?>
) {
    for (statusView in views) {
        if (statusView?.view != null) {
            if (statusView.index == index) {
                statusView.selectedResId.let { statusView.view.setTextColor(context.getColor(it)) }
            } else {
                statusView.unSelectedResId.let { statusView.view.setTextColor(context.getColor(it)) }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun changeImageSrc(
    index: Int,
    views: Array<StatusView<ImageView>?>
) {
    for (statusView in views) {
        if (statusView?.view != null) {
            if (statusView.index == index) {
                statusView.selectedResId.let {
                    statusView.view.setImageDrawable(
                        context.getDrawable(
                            it
                        )
                    )
                }
            } else {
                statusView.unSelectedResId.let {
                    statusView.view.setImageDrawable(
                        context.getDrawable(
                            it
                        )
                    )
                }
            }
        }
    }
}

/**
 * 获取动画对象
 */
fun getAnimation(context: Context, res: Int, listener: Animation.AnimationListener): Animation {
    val anim = AnimationUtils.loadAnimation(context, res)
    anim.setAnimationListener(listener)
    return anim
}