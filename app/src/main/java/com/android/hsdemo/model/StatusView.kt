package com.android.hsdemo.model

import android.view.View

class StatusView<T : View>(
    var view: T,
    var index: Int,
    var selectedResId: Int,
    var unSelectedResId: Int
) {

    override fun toString(): String {
        return "[index=$index,selectedResId=$selectedResId,unSelectedResId=$unSelectedResId,view=${view.id}]"
    }

}