package com.android.hsdemo.custom.button

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.android.hsdemo.R
import com.android.hsdemo.model.StatusView

/**
 * 登录、注册、忘记密码 那块的按钮
 */
open class ButtonFocus(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    lateinit var svMain: StatusView<View>
    lateinit var svImg: StatusView<ImageView>
    lateinit var svTv: StatusView<TextView>

    init {
        initView(context)
    }

    private fun initView(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.view_button_focus, null)
//        initAttrs(attrs)
    }

    private fun initAttrs(attrs: AttributeSet?){
        val a = context.obtainStyledAttributes(
            attrs,
            R.styleable.ButtonFocus
        )
    }

}