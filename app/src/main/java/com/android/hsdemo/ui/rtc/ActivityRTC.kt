package com.android.hsdemo.ui.rtc

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.view.animation.Animation
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.avchat.AVChatManager
import com.android.baselib.base.BaseActivity
import com.android.baselib.custom.eventbus.EventBus
import com.android.baselib.custom.recyleview.SpaceItem
import com.android.baselib.custom.recyleview.adapter.ListItem
import com.android.baselib.utils.dp2px
import com.android.baselib.utils.showShortToast
import com.android.hsdemo.*
import com.android.hsdemo.EventKey.MEETING_USER_CHANGE
import com.android.hsdemo.KEY_ROOM_ID
import com.android.hsdemo.custom.TextureVideoViewOutlineProvider
import com.android.hsdemo.custom.dialog.DialogHint
import com.android.hsdemo.custom.dialog.DialogPeopleList
import com.android.hsdemo.databinding.ActivityRtcBinding
import com.android.hsdemo.model.ItemOfMemberInfo
import com.android.hsdemo.model.MeetingDetail
import com.android.hsdemo.network.HttpCallback
import com.android.hsdemo.util.getAnimation
import com.elvishew.xlog.XLog
import com.tbruyelle.rxpermissions2.RxPermissions
import com.tencent.rtmp.ui.TXCloudVideoView
import com.uber.autodispose.android.lifecycle.autoDispose
import kotlinx.android.synthetic.main.activity_rtc.*
import kotlinx.android.synthetic.main.fragment_address_book.*
import java.util.*
import kotlin.concurrent.fixedRateTimer

class ActivityRTC : BaseActivity<VMRTC, ActivityRtcBinding>(), View.OnFocusChangeListener,
    View.OnClickListener {

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

    private lateinit var dialogExit: DialogHint
    private lateinit var dialogPeopleList: DialogPeopleList

    private var animScreenTopGone: Animation? = null
    private var animScreenRightGone: Animation? = null
    private var animScreenBottomGone: Animation? = null
    private var animScreenTopVisible: Animation? = null
    private var animScreenRightVisible: Animation? = null
    private var animScreenBottomVisible: Animation? = null

    private lateinit var timer: Timer

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    val itemOfData = ListItem<ItemOfMemberInfo>(
        R.layout.item_adapter_video_view,
        { holder, item ->

            val vv = holder.getView<TXCloudVideoView>(R.id.mTXCloudVideoView)
            val tv = holder.getView<TextView>(R.id.mNickName)
            val iv = holder.getView<ImageView>(R.id.mIcon)

            EventBus.with("${EventKey.VIDEO_CONTROL}_${item._data.account}", Boolean::class.java)
                .observe(this, Observer<Boolean> {
                    XLog.i("【视频会议】EventBus ${item._data.account} 接收 $it")
                    if (it) {
                        AVChatManager.getTRTCClient().startRemoteView(item._data.account, vv)
                    } else {
                        AVChatManager.getTRTCClient().stopRemoteView(item._data.account)
                    }
                })

            // 开始显示用户userId的视频画面
            AVChatManager.getTRTCClient().startRemoteView(item._data.account, vv)

            tv.text = item._data.nickName
            if (TextUtils.equals(
                    item._data.hostMute.toString(),
                    "1"
                ) || TextUtils.equals(item._data.mute.toString(), "1")
            ) {
                iv.setImageResource(R.mipmap.icon_remote_mute)
            } else {
                iv.setImageResource(R.mipmap.icon_remote_voice)
            }

        }, {
            //切换视频源

        }
    )

    override fun afterCreate() {
        initAnimotion()
        handleIntent()
        initDefault()

        //设置放大动画
//        btnMuteAudio.onFocusChangeListener = this
//        btnMuteVideo.onFocusChangeListener = this
//        btnScreen.onFocusChangeListener = this
//        btnControl.onFocusChangeListener = this
//        btnExit.onFocusChangeListener = this

        requestPermissions()
    }

    /**
     * 初始化动画
     */
    private fun initAnimotion() {
        animScreenTopGone =
            getAnimation(this@ActivityRTC, R.anim.top_out, object : Animation.AnimationListener {
                override fun onAnimationStart(p0: Animation?) {
                }

                override fun onAnimationEnd(p0: Animation?) {
                    screenTop.visibility = View.GONE
                }

                override fun onAnimationRepeat(p0: Animation?) {
                }
            })
        animScreenRightGone =
            getAnimation(this@ActivityRTC, R.anim.right_out, object : Animation.AnimationListener {
                override fun onAnimationStart(p0: Animation?) {
                }

                override fun onAnimationEnd(p0: Animation?) {
                    screenRight.visibility = View.GONE
                }

                override fun onAnimationRepeat(p0: Animation?) {
                }
            })
        animScreenBottomGone =
            getAnimation(this@ActivityRTC, R.anim.bottom_out, object : Animation.AnimationListener {
                override fun onAnimationStart(p0: Animation?) {
                }

                override fun onAnimationEnd(p0: Animation?) {
                    screenBottom.visibility = View.GONE
                }

                override fun onAnimationRepeat(p0: Animation?) {
                }
            })
        animScreenTopVisible =
            getAnimation(this@ActivityRTC, R.anim.top_in, object : Animation.AnimationListener {
                override fun onAnimationStart(p0: Animation?) {
                    screenTop.visibility = View.VISIBLE
                }

                override fun onAnimationEnd(p0: Animation?) {
                }

                override fun onAnimationRepeat(p0: Animation?) {
                }
            })
        animScreenRightVisible =
            getAnimation(this@ActivityRTC, R.anim.right_in, object : Animation.AnimationListener {
                override fun onAnimationStart(p0: Animation?) {
                    screenRight.visibility = View.VISIBLE
                }

                override fun onAnimationEnd(p0: Animation?) {
                }

                override fun onAnimationRepeat(p0: Animation?) {
                }
            })
        animScreenBottomVisible =
            getAnimation(this@ActivityRTC, R.anim.bottom_in, object : Animation.AnimationListener {
                override fun onAnimationStart(p0: Animation?) {
                    screenBottom.visibility = View.VISIBLE
                }

                override fun onAnimationEnd(p0: Animation?) {
                }

                override fun onAnimationRepeat(p0: Animation?) {
                }
            })
    }

    /**
     * 默认设置初始化
     */
    private fun initDefault() {
        mViewModel.mLocalPreviewView.value = vTRTCMain
        dialogExit = DialogHint(this)
        dialogPeopleList = DialogPeopleList(this)
        dialogPeopleList.data = mViewModel.data
        initDialog(false)
        btnExit.setOnClickListener(this)
        btnScreen.setOnClickListener(this)
        btnControl.setOnClickListener(this)

        screenRight.addItemDecoration(
            SpaceItem(
                bottom = dp2px(18f)
            )
        )
        mViewModel.initRecycleView(
            screenRight,
            LinearLayoutManager(this@ActivityRTC, LinearLayoutManager.VERTICAL, false),
            itemOfData
        )

        EventBus.with(MEETING_USER_CHANGE, String::class.java).observe(this, Observer {
            dialogPeopleList.data = mViewModel.data
            dialogPeopleList.changeUI()
        })
    }

    /**
     * 设置退出弹框
     */
    private fun initDialog(isMaster: Boolean) {
        if (isMaster) {
            // 我是主持人
            dialogExit.setContent("确定要结束会议？")
            dialogExit.setOnSureClickListener(View.OnClickListener {
                exit(true)
            })
        } else {
            // 我是成员
            dialogExit.setContent("确定要退出会议？")
            dialogExit.setOnSureClickListener(View.OnClickListener {
                exit(false)
            })
        }
        dialogPeopleList.isMaster = isMaster
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
                    initStart()
                } else {
                    //没有权限的情况下
                    showShortToast("权限被拒绝")
                }
            }
    }

    /**
     * 触发视频会议初始化
     */
    private fun initStart() {
        mViewModel.enterRoom()
        startTimer()
        mViewModel.getMeetingDetail(this, object : HttpCallback<MeetingDetail> {
            override fun success(t: MeetingDetail) {
                //设置退出弹框
                if (t.masterId != null && TextUtils.equals(
                        t.masterId.toString(),
                        mViewModel.mUserId.value.toString()
                    )
                ) {
                    initDialog(true)
                }
                //同步会议信息

            }

            override fun failed(msg: String) {
                showShortToast(msg)
                this@ActivityRTC.finish()
            }
        })
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
                changeScreen(false)
            }
        }
    }

    override fun onDestroy() {
        stopTimer()
        mViewModel.exitRoom()
        super.onDestroy()
    }

    override fun onFocusChange(view: View, focus: Boolean) {
        if (focus) {
            view.animate().scaleX(1.1f).scaleY(1.1f).setDuration(500).start()
        } else {
            view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(500).start()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mViewModel.isScreen) {
                changeScreen(false)
                return true
            }
            dialogExit.show()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    /**
     * 退出
     */
    private fun exit(isMaster: Boolean) {
        if (isMaster) {
            mViewModel.setMeetingEnd()
        }
        this@ActivityRTC.finish()
    }

    override fun onClick(view: View) {
        when (view) {
            btnExit -> { //退出
                dialogExit.show()
            }
            btnScreen -> { //全屏
                changeScreen(true)
            }
            btnControl -> { //控制列表
                dialogPeopleList.show()
            }
        }
    }

    private fun changeScreen(flag: Boolean) {
        mViewModel.isScreen = flag
        if (flag) {
            screenTop.startAnimation(animScreenTopGone)
            screenRight.startAnimation(animScreenRightGone)
            screenBottom.startAnimation(animScreenBottomGone)
        } else {
            screenTop.startAnimation(animScreenTopVisible)
            screenRight.startAnimation(animScreenRightVisible)
            screenBottom.startAnimation(animScreenBottomVisible)
        }
    }

    private fun startTimer() {
        mViewModel.time = 0L
        timer = fixedRateTimer("", false, 0, 1000) {
            mViewModel.time = mViewModel.time + 1
            mViewModel.updateTimeDown()
        }
    }

    private fun stopTimer() {
        mViewModel.time = 0L
        timer.cancel()
    }


}