package com.android.baselib.utils

import android.text.TextUtils
import android.widget.Toast
import com.android.baselib.global.AppGlobal.context

private var toast: Toast? = null

fun showShortToast(str: String) {
    if (TextUtils.isEmpty(str))
        return
    if (toast == null) {
        toast = Toast.makeText(context, "", Toast.LENGTH_SHORT)
        toast?.setText(str)
    } else {
        //如果当前Toast没有消失， 直接显示内容，不需要重新设置
        toast?.setText(str)
    }
    toast?.show()
}

fun showShortToast(textId: Int) {
    showShortToast(context.resources.getString(textId))
}

fun showLongToast(str: String) {
    if (TextUtils.isEmpty(str))
        return
    if (toast == null) {
        toast = Toast.makeText(context, "", Toast.LENGTH_LONG)
        toast?.setText(str)
    } else {
        //如果当前Toast没有消失， 直接显示内容，不需要重新设置
        toast?.setText(str)
    }
    toast?.show()
}

fun showLongToast(textId: Int) {
    showLongToast(context.resources.getString(textId))
}