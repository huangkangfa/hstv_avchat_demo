package com.android.hsdemo.ui.rtc

import android.Manifest
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import com.android.hsdemo.KEY_ROOM_ID
import com.android.hsdemo.KEY_USER_ID
import com.android.baselib.base.BaseActivity
import com.android.baselib.utils.showShortToast
import com.android.hsdemo.BR
import com.android.hsdemo.R
import com.android.hsdemo.databinding.ActivityRtcBinding
import com.tbruyelle.rxpermissions2.RxPermissions
import com.uber.autodispose.android.lifecycle.autoDispose
import kotlinx.android.synthetic.main.activity_rtc.*

class ActivityRTC : BaseActivity<VMRTC, ActivityRtcBinding>() {

    companion object {
        fun start(context: Context, userId: String, roomId: String) {
            val intent = Intent(context, ActivityRTC().javaClass)
            intent.putExtra(KEY_USER_ID, userId)
            intent.putExtra(KEY_ROOM_ID, roomId)
            context.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_rtc

    override fun getVariableId(): Int = BR.vm

    override fun afterCreate() {
        handleIntent()
        mViewModel.mLocalPreviewView.value = trtc_tc_cloud_view_main
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
        if (!TextUtils.isEmpty(mViewModel.mRoomId.toString())) {
            trtc_tv_room_number.text = mViewModel.mRoomId.value.toString()
        }
        mViewModel.mRemoteViewList.value?.add(trtc_tc_cloud_view_1)
        mViewModel.mRemoteViewList.value?.add(trtc_tc_cloud_view_2)
        mViewModel.mRemoteViewList.value?.add(trtc_tc_cloud_view_3)
        mViewModel.mRemoteViewList.value?.add(trtc_tc_cloud_view_4)
        mViewModel.mRemoteViewList.value?.add(trtc_tc_cloud_view_5)
        mViewModel.mRemoteViewList.value?.add(trtc_tc_cloud_view_6)
    }

    private fun handleIntent() {
        val intent = intent
        if (null != intent) {
            val userId = intent.getStringExtra(KEY_USER_ID);
            val roomId = intent.getStringExtra(KEY_ROOM_ID);
            if (userId != null) {
                mViewModel.mUserId.value = userId.toString()
            }
            if (roomId != null) {
                mViewModel.mRoomId.value = roomId.toString()
            }
        }
    }

    override fun onDestroy() {
        mViewModel.exitRoom()
        super.onDestroy()
    }

}