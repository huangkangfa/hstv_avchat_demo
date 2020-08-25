package com.android.hsdemo.ui.rtc

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.TextUtils
import android.view.View
import androidx.annotation.RequiresApi
import com.android.baselib.base.BaseActivity
import com.android.baselib.utils.showShortToast
import com.android.hsdemo.*
import com.android.hsdemo.KEY_ROOM_ID
import com.android.hsdemo.databinding.ActivityRtcBinding
import com.elvishew.xlog.XLog
import com.tbruyelle.rxpermissions2.RxPermissions
import com.uber.autodispose.android.lifecycle.autoDispose
import kotlinx.android.synthetic.main.activity_rtc.*

class ActivityRTC : BaseActivity<VMRTC, ActivityRtcBinding>(), View.OnFocusChangeListener {

    companion object {
        fun start(context: Context, userId: String, roomId: String, roomName: String) {
            val intent = Intent(context, ActivityRTC().javaClass)
            intent.putExtra(KEY_USER_ID, userId)
            intent.putExtra(KEY_ROOM_ID, roomId)
            intent.putExtra(KEY_ROOM_NAME, roomName)
            context.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_rtc

    override fun getVariableId(): Int = BR.vm

    override fun afterCreate() {
        handleIntent()
        mViewModel.mLocalPreviewView.value = vTRTCMain

        //设置放大动画
        btnMuteAudio.onFocusChangeListener = this
        btnMuteVideo.onFocusChangeListener = this
        btnScreen.onFocusChangeListener = this
        btnControl.onFocusChangeListener = this
        btnExit.onFocusChangeListener = this

        requestPermissions()
    }

    private fun requestPermissions() {
        RxPermissions(this@ActivityRTC).request(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ).autoDispose(this@ActivityRTC)
            .subscribe { granted ->
                if (granted) {
                    //权限允许的情况下
                    initView()
                    mViewModel.enterRoom()
                } else {
                    //没有权限的情况下
                    showShortToast("权限被拒绝")
                }
            }
    }

    private fun initView() {
        mViewModel.mRemoteViewList.value?.add(trtcRemoteView1)
        mViewModel.mRemoteViewList.value?.add(trtcRemoteView2)
        mViewModel.mRemoteViewList.value?.add(trtcRemoteView3)
    }

    private fun handleIntent() {
        val intent = intent
        if (null != intent) {
            val userId = intent.getStringExtra(KEY_USER_ID);
            val roomId = intent.getStringExtra(KEY_ROOM_ID);
            val roomName = intent.getStringExtra(KEY_ROOM_NAME);
            if (userId != null) {
                mViewModel.mUserId.value = userId.toString()
            }
            if (roomId != null) {
                mViewModel.mRoomId.value = roomId.toString()
            }
            if (roomName != null) {
                mViewModel.mRoomName.value = roomName.toString()
            }
        }
    }

    override fun onDestroy() {
        mViewModel.exitRoom()
        super.onDestroy()
    }

    override fun onFocusChange(view: View, focus: Boolean) {
        if(focus){
            view.animate().scaleX(1.1f).scaleY(1.1f).setDuration(500).start()
        }else{
            view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(500).start()
        }
    }

}