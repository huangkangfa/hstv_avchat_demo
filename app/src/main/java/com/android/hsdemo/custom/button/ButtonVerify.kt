package com.android.hsdemo.custom.button

import android.content.Context
import android.graphics.Color
import android.os.CountDownTimer
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.android.hsdemo.R
import kotlinx.android.synthetic.main.view_button_verify.view.*

class ButtonVerify @JvmOverloads constructor(
    private val mContext: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(mContext, attrs, defStyleAttr), LifecycleObserver {

    //倒计时时间 默认60秒
    private val mTime = 60

    //倒计时器
    private var mTimer: CountDownTimer? = null
    private var mOnClickVerifyListener: OnClickVerifyListener? = null

    private fun init() {
        LayoutInflater.from(mContext).inflate(R.layout.view_button_verify, this)
        mTimer = object : CountDownTimer((mTime * 1000).toLong(), 1000) {

            override fun onTick(millisUntilFinished: Long) {
                btn.text = "已发送(${millisUntilFinished / 1000}s)"
            }

            override fun onFinish() {
                btn.isEnabled = true
                if(btn.isFocusableInTouchMode){
                    //处于聚焦状态
                    btn.setBackgroundResource(R.drawable.bg_color_edit_focus)
                    btn.setTextColor(Color.parseColor("#2F1609"))
                }else{
                    //处于未聚焦状态
                    if(TextUtils.equals(btn.text,resources.getString(R.string.send_verify_code))){
                        btn.setBackgroundResource(R.drawable.bg_color_edit)
                    }else{
                        btn.setBackgroundResource(R.drawable.bg_color_down)
                    }
                    btn.setTextColor(Color.parseColor("#B4FFFFFF"))
                }

                btn.text = resources.getString(R.string.send_verify_code)
                if (mOnClickVerifyListener != null) mOnClickVerifyListener!!.onFinish()
            }
        }
        btn.setOnClickListener(OnClickListener {
            if (mOnClickVerifyListener != null) {
                mOnClickVerifyListener!!.onClick(this@ButtonVerify)
            }
        })
        btn.onFocusChangeListener = OnFocusChangeListener {
                v: View, focus: Boolean ->
            if(focus){
                btn.setBackgroundResource(R.drawable.bg_color_edit_focus)
                btn.setTextColor(Color.parseColor("#2F1609"))
            }else{
                btn.setBackgroundResource(R.drawable.bg_color_edit)
                btn.setTextColor(Color.parseColor("#B4FFFFFF"))
            }
        }
    }

    fun start() {
        btn.isEnabled = false
        if(btn.isFocusableInTouchMode){
            btn.setBackgroundResource(R.drawable.bg_color_edit_focus)
        }else{
            btn.setBackgroundResource(R.drawable.bg_color_down)
        }
        btn.text = "已发送(${mTime}s)"
        btn.setTextColor(Color.parseColor("#FFCA28"))
        mTimer!!.start()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        btn.performClick()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestory() {
        //释放资源停止倒计时器
        if (mTimer != null) {
            mTimer!!.cancel()
            mTimer = null
        }
    }

    fun setOnClickVerifyListener(onClickVerifyListener: OnClickVerifyListener) {
        mOnClickVerifyListener = onClickVerifyListener
    }

    interface OnClickVerifyListener {
        fun onClick(view: View)
        fun onFinish()
    }

    init {
        init()
    }
}