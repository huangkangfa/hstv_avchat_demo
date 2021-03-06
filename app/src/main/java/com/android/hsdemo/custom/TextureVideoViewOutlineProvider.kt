package com.android.hsdemo.custom

import android.graphics.Outline
import android.graphics.Rect
import android.os.Build
import android.view.View
import android.view.ViewOutlineProvider
import androidx.annotation.RequiresApi

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
class TextureVideoViewOutlineProvider(private val mRadius: Float) : ViewOutlineProvider() {
    override fun getOutline(view: View, outline: Outline) {
        val rect = Rect()
        view.getGlobalVisibleRect(rect)
        val leftMargin = 0
        val topMargin = 0
        val selfRect = Rect(
            leftMargin, topMargin,
            rect.right - rect.left - leftMargin, rect.bottom - rect.top - topMargin
        )
        outline.setRoundRect(selfRect, mRadius)
    }
}