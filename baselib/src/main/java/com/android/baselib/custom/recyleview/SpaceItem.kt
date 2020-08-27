package com.android.baselib.custom.recyleview

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpaceItem(
    var left: Int = 0, var top: Int = 0, var right: Int = 0, var bottom: Int = 0,
    var left0: Int = left, var top0: Int = top, var right0: Int = right, var bottom0: Int = bottom
) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if (parent.getChildAdapterPosition(view) != 0) {
            outRect.top = top
            outRect.bottom = bottom
            outRect.left = left
            outRect.right = right
        } else {
            outRect.top = top0
            outRect.bottom = bottom0
            outRect.left = left0
            outRect.right = right0
        }
    }
}