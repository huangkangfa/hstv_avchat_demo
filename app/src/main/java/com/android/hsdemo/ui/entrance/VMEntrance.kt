package com.android.hsdemo.ui.entrance

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Build
import android.text.TextUtils
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.android.baselib.ActivityManager
import com.android.baselib.utils.showShortToast
import com.android.hsdemo.R
import com.android.hsdemo.ui.rtc.ActivityRTC

class VMEntrance(application: Application) : AndroidViewModel(application) {
    /**
     * 房间号
     */
    var roomId: MutableLiveData<String> = MutableLiveData()

    /**
     * 房间号
     */
    var userName: MutableLiveData<String> = MutableLiveData()

    init {
        roomId.value = "1256732"
        userName.value = "77777777"
    }

    /**
     * 点击空白的地方隐藏软键盘
     */
    fun onClickEmpty(v: View) {
        hideInput()
    }

    /**
     * 进入房间
     */
    fun enterRoom(v: View) {
        hideInput()
        if (TextUtils.isEmpty(roomId.value.toString())) {
            showShortToast("房间号不能为空")
            return
        }
        if (TextUtils.isEmpty(userName.value.toString())) {
            showShortToast("用户名不能为空")
            return
        }
        val activity: Activity? = ActivityManager.get()
        if (activity != null) {
            ActivityRTC.start(activity,userName.value.toString(),roomId.value.toString())
//            val intent = Intent(activity, RTCActivity::class.java)
//            intent.putExtra(KEY_ROOM_ID, roomId.value)
//            intent.putExtra(KEY_USER_ID, userName.value)
//            activity.startActivity(intent)
        } else {
            showShortToast("发生错误")
        }
    }

    /**
     * 返回
     */
    fun goBack(v: View) {
        ActivityManager.finish()
    }

    /**
     * 隐藏键盘
     */
    private fun hideInput() {
        val activity: Activity? = ActivityManager.get()
        if (activity != null) {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            val v: View = activity.window.peekDecorView()
            imm.hideSoftInputFromWindow(v.windowToken, 0)
        }
    }

    /**
     * 变更按钮背景
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun initButtonFocusChangeListener(views: ArrayList<View>) {
        for (view in views) {
            view.onFocusChangeListener = OnFocusChangeListener { v, focus ->
                val activity: Activity? = ActivityManager.get()
                if (activity != null) {
                    if (focus) {
                        v.background = activity.getDrawable(R.drawable.rtc_button_bg_focus)
                    } else {
                        v.background = activity.getDrawable(R.drawable.rtc_button_bg)
                    }
                }
            }
        }
    }

}