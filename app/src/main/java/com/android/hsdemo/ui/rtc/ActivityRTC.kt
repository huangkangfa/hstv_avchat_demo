package com.android.hsdemo.ui.rtc

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
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
import com.android.baselib.utils.Preferences
import com.android.baselib.utils.dp2px
import com.android.baselib.utils.showShortToast
import com.android.hsdemo.*
import com.android.hsdemo.EventKey.MEETING_USER_CHANGE
import com.android.hsdemo.custom.dialog.DialogHint
import com.android.hsdemo.custom.dialog.DialogPeopleList
import com.android.hsdemo.databinding.ActivityRtcBinding
import com.android.hsdemo.model.IMCmd
import com.android.hsdemo.model.ItemOfMemberInfo
import com.android.hsdemo.model.MeetingDetail
import com.android.hsdemo.network.HttpCallback
import com.android.hsdemo.util.getAnimation
import com.bumptech.glide.Glide
import com.elvishew.xlog.XLog
import com.google.gson.Gson
import com.tbruyelle.rxpermissions2.RxPermissions
import com.tencent.imsdk.v2.*
import com.tencent.rtmp.ui.TXCloudVideoView
import com.tencent.trtc.TRTCCloudDef
import com.uber.autodispose.android.lifecycle.autoDispose
import kotlinx.android.synthetic.main.activity_rtc.*
import okio.ByteString.Companion.toByteString
import java.util.*
import kotlin.concurrent.fixedRateTimer

class ActivityRTC : BaseActivity<VMRTC, ActivityRtcBinding>(),
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

            holder.itemView.tag = item._data.account.toString()

            val vv = holder.getView<TXCloudVideoView>(R.id.mTXCloudVideoView)
            val tv = holder.getView<TextView>(R.id.mNickName)
            val iv = holder.getView<ImageView>(R.id.mIcon)

            /**
             * 控制摄像头显示
             */
            if (item._data.videoOpen == true) {
                vv.visibility = View.VISIBLE
            } else {
                vv.visibility = View.GONE
            }

            /**
             * 判断当前item是否是自己
             */
            val isMySelf = TextUtils.equals(
                item._data.account.toString(),
                Preferences.getString(KEY_ACCID)
            )

            /**
             * 声音变化
             */
            EventBus.with(
                "${EventKey.MEETING_USER_VOICE_VOLUME}_${item._data.account}",
                Int::class.java
            ).observe(this,
                Observer {
                    if (TextUtils.equals(item._data.hostMute.toString(), "1")
                        || TextUtils.equals(item._data.mute.toString(), "1")
                    ) {
                        iv.visibility = View.VISIBLE
                        iv.setImageResource(R.mipmap.icon_remote_mute)
                    } else {
                        iv.setImageResource(R.mipmap.icon_remote_voice)
                        if (it > 10) {
                            iv.visibility = View.VISIBLE
                        } else {
                            iv.visibility = View.GONE
                        }
                    }
                })

            if (TextUtils.equals(item._data.hostMute.toString(), "1")
                || TextUtils.equals(item._data.mute.toString(), "1")
            ) {
                iv.visibility = View.VISIBLE
                iv.setImageResource(R.mipmap.icon_remote_mute)
            } else {
                iv.setImageResource(R.mipmap.icon_remote_voice)
                iv.visibility = View.GONE
            }

            /**
             * 1.如果类型是0，展示本地预览
             * 2.如果类型是1，展示远程视频
             * 3.点击事件
             *  1>判断当前是否是自己的item，如果是自己，就把当前item跟主屏幕item交换，切换播放流，刷新对应UI
             *  2>如果不是自己的item，也同上操作
             */
            if (item._type == 0) {
                AVChatManager.getTRTCClient().stopLocalPreview()
                XLog.i("【视频会议】尝试开启小窗口本地预览视频 ${item._data.account.toString()}")
                AVChatManager.getTRTCClient().startLocalPreview(true, vv)
                tv.text = "我自己"
            } else {
                XLog.i("【视频会议】尝试开启小窗口远端用户视频 ${item._data.account.toString()}")
                AVChatManager.getTRTCClient().startRemoteView(item._data.account, vv)
                tv.text = item._data.nickName
            }

            /**
             * 因为要用户确定开启流的时候再订阅流，所以以事件形式来处理播放与关闭
             */
            EventBus.with("${EventKey.VIDEO_CONTROL}_${item._data.account}", Boolean::class.java)
                .observe(this, Observer<Boolean> {
                    XLog.i("【视频会议】${holder.itemView.tag} 中接收视频状态 $it")
                    if (it) {
                        if (isMySelf) {
                            AVChatManager.getTRTCClient().stopLocalPreview()
                            XLog.i("【视频会议】小窗口播放我自己的预览视频 ${mViewModel.mUserId.value}")
                            AVChatManager.getTRTCClient().startLocalPreview(true, vv)
                        } else {
                            XLog.i("【视频会议】小窗口播放item的远程视频 ${item._data.account}")
                            AVChatManager.getTRTCClient().startRemoteView(item._data.account, vv)
                        }
                    } else {
                        if (isMySelf) {
                            XLog.i("【视频会议】小窗口停止我自己的预览视频 ${mViewModel.mUserId.value}")
                            AVChatManager.getTRTCClient().stopLocalPreview()
                        } else {
                            XLog.i("【视频会议】小窗口停止item的远程视频  ${item._data.account}")
                            AVChatManager.getTRTCClient().stopRemoteView(item._data.account)
                        }
                    }
                })

        }, {
            //切换视频源
            mViewModel.changeScreenMember(it)
        }
    )

    override fun afterCreate() {
        dismissKeyguard()
        initAnimotion()
        handleIntent()
        initDefault()
        requestPermissions()
    }

    // 设置窗口flag，亮屏并且解锁/覆盖在锁屏界面上
    private fun dismissKeyguard() {
        window.addFlags(
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                    or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )
    }

    /**
     * 默认设置初始化
     */
    private fun initDefault() {
        mViewModel.mLocalPreviewView = vTRTCMain
        dialogExit = DialogHint(this)
        dialogPeopleList = DialogPeopleList(this)
        dialogPeopleList.data = mViewModel.getPeopleList()
        initDialog(false)
        btnExit.setOnClickListener(this)
        btnScreen.setOnClickListener(this)
        btnControl.setOnClickListener(this)

        Glide.with(this).load(R.drawable.gif_meeting_voice).into(gifVoice)

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

        /**
         * 参会人员变动
         */
        EventBus.with(MEETING_USER_CHANGE, String::class.java).observe(this, Observer {
            dialogPeopleList.data = mViewModel.getPeopleList()
            dialogPeopleList.changeUI()
        })

        /**
         * 自己的音量发生变动
         */
        EventBus.with(
            "${EventKey.MEETING_USER_VOICE_VOLUME}_${Preferences.getString(KEY_ACCID)}",
            Int::class.java
        ).observe(this,
            Observer {
                if (it > 0) {
                    mViewModel.visibilityOfMuteAudio.value = View.VISIBLE
                } else {
                    mViewModel.visibilityOfMuteAudio.value = View.GONE
                }
            })

        V2TIMManager.getMessageManager()
            .addAdvancedMsgListener(object : V2TIMAdvancedMsgListener() {
                override fun onRecvNewMessage(msg: V2TIMMessage?) {
                    super.onRecvNewMessage(msg)
                    val data = msg?.customElem?.data
                    if (data != null) {
                        val result = Gson().fromJson(String(data), IMCmd::class.java)
                        if (result != null)
                            doWithCmd(result)
                        XLog.i("【IM】onRecvNewMessage ${String(data)}")
                    }
                }
            })
    }

    private fun doWithCmd(cmd: IMCmd) {
        //确定是该房间的指令
        if (TextUtils.equals(cmd.r.toString(), mViewModel.mRoomId.value.toString())) {
            val isMe = TextUtils.equals(cmd.a, mViewModel.mUserId.value.toString())
            val isMaster = TextUtils.equals(
                mViewModel.mUserId.value.toString(),
                mViewModel.mMeetingDetail.value?.masterId
            )
            when (cmd.t) {
                1 -> {
                    // （主持人）全员静音
                    for (item in mViewModel.data) {
                        if (item._type == 1 || !isMaster) {
                            item._data.hostMute = "1"
                            //更新小窗口
                            mViewModel.changeUI(item)
                        }
                    }
                    if (mViewModel.screenMember._type == 1 || !isMaster) {
                        mViewModel.screenMember._data.hostMute = "1"
                        //更新主屏
                        updateUI()
                    }
                    //功能变更自己
                    AVChatManager.getTRTCClient().stopLocalAudio()
                }
                2 -> {
                    // （主持人）取消全员静音
                    for (item in mViewModel.data) {
                        if (item._type == 1 || !isMaster) {
                            item._data.hostMute = "0"
                            //更新小窗口
                            mViewModel.changeUI(item)
                        }
                    }
                    if (mViewModel.screenMember._type == 1 || !isMaster) {
                        mViewModel.screenMember._data.hostMute = "0"
                        //更新主屏
                        updateUI()
                    }
                    //功能变更自己
                    if (TextUtils.equals(mViewModel.myself._data.mute.toString(), "1")) {
                        AVChatManager.getTRTCClient().stopLocalAudio()
                    } else {
                        AVChatManager.getTRTCClient().startLocalAudio()
                    }
                }
                3 -> {
                    // （主持人）设置他人静音
                    for (item in mViewModel.data) {
                        if (TextUtils.equals(item._data.account.toString(), cmd.a.toString())) {
                            item._data.hostMute = "1"
                            mViewModel.changeUI(item)
                        }
                    }
                    if (TextUtils.equals(
                            mViewModel.screenMember._data.account.toString(),
                            cmd.a.toString()
                        )
                    ) {
                        mViewModel.screenMember._data.hostMute = "1"
                        //更新主屏
                        updateUI()
                    }
                    //功能变更自己
                    if (isMe) {
                        AVChatManager.getTRTCClient().stopLocalAudio()
                    }
                }
                4 -> {
                    // （主持人）取消他人静音
                    for (item in mViewModel.data) {
                        if (TextUtils.equals(item._data.account.toString(), cmd.a.toString())) {
                            item._data.hostMute = "0"
                            mViewModel.changeUI(item)
                        }
                    }
                    if (TextUtils.equals(
                            mViewModel.screenMember._data.account.toString(),
                            cmd.a.toString()
                        )
                    ) {
                        mViewModel.screenMember._data.hostMute = "0"
                        //更新主屏
                        updateUI()
                    }
                    //功能变更自己
                    if (isMe) {
                        if (TextUtils.equals(mViewModel.myself._data.mute.toString(), "1")) {
                            AVChatManager.getTRTCClient().stopLocalAudio()
                        } else {
                            AVChatManager.getTRTCClient().startLocalAudio()
                        }
                    }
                }
                5 -> {
                    // 设置自己静音
                    if (!isMe) {
                        for (item in mViewModel.data) {
                            if (TextUtils.equals(item._data.account.toString(), cmd.a.toString())) {
                                item._data.mute = "1"
                                mViewModel.changeUI(item)
                            }
                        }
                        if (TextUtils.equals(
                                mViewModel.screenMember._data.account.toString(),
                                cmd.a.toString()
                            )
                        ) {
                            mViewModel.screenMember._data.mute = "1"
                            //更新主屏
                            updateUI()
                        }
                    }
                }
                6 -> {
                    // 取消自己静音
                    if (!isMe) {
                        for (item in mViewModel.data) {
                            if (TextUtils.equals(item._data.account.toString(), cmd.a.toString())) {
                                item._data.mute = "0"
                                mViewModel.changeUI(item)
                            }
                        }
                        if (TextUtils.equals(
                                mViewModel.screenMember._data.account.toString(),
                                cmd.a.toString()
                            )
                        ) {
                            mViewModel.screenMember._data.mute = "0"
                            //更新主屏
                            updateUI()
                        }
                    }
                }
                7 -> {
                    // 踢人
                    if (isMe) {
                        exit(isMaster)
                    }
                }
                8 -> {
                    // 结束会议
                    exit(isMaster)
                }
                9 -> {
                    // 自己摄像头打开
                    if (!isMe) {
                        for (item in mViewModel.data) {
                            if (TextUtils.equals(item._data.account.toString(), cmd.a.toString())) {
                                item._data.videoOpen = true
                                mViewModel.changeUI(item)
                            }
                        }
                        if (TextUtils.equals(
                                mViewModel.screenMember._data.account.toString(),
                                cmd.a.toString()
                            )
                        ) {
                            mViewModel.screenMember._data.videoOpen = true
                            //更新主屏
                            updateUI()
                        }
                    }
                }
                10 -> {
                    // 自己摄像头关闭
                    if (!isMe) {
                        for (item in mViewModel.data) {
                            if (TextUtils.equals(item._data.account.toString(), cmd.a.toString())) {
                                item._data.videoOpen = false
                                mViewModel.changeUI(item)
                            }
                        }
                        if (TextUtils.equals(
                                mViewModel.screenMember._data.account.toString(),
                                cmd.a.toString()
                            )
                        ) {
                            mViewModel.screenMember._data.videoOpen = false
                            //更新主屏
                            updateUI()
                        }
                    }
                }
            }
        }
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
                updateMeetingDetail()
            }

            override fun failed(msg: String) {
                showShortToast(msg)
                this@ActivityRTC.finish()
            }
        })
    }

    private fun updateMeetingDetail() {
        //参与成员弹框数据赋值
        dialogPeopleList.meetingDetail = mViewModel.mMeetingDetail.value
        dialogPeopleList.initMyself(mViewModel.myself)

        //更新自己的声音变化
        if (mViewModel.myself._data.hostMute == "1" || mViewModel.myself._data.mute == "1") {
            mViewModel.visibilityOfMuteAudio.value = View.GONE
        } else {
            mViewModel.visibilityOfMuteAudio.value = View.VISIBLE
        }
        //更新主屏视图
        updateUI()
    }

    /**
     * intent数据处理
     */
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

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_BACK -> {
                if (mViewModel.isScreen) {
                    changeScreen(false)
                    return true
                }
                dialogExit.show()
                return true
            }
        }
        if (mViewModel.isScreen
            && (keyCode == KeyEvent.KEYCODE_DPAD_LEFT
                    || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
                    || keyCode == KeyEvent.KEYCODE_DPAD_UP
                    || keyCode == KeyEvent.KEYCODE_DPAD_DOWN)
        ) {
            changeScreen(false)
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

    /**
     * 更新主屏幕
     */
    private fun updateUI() {
        //更新摄像头
        btnMuteVideo.isSelected = mViewModel.myself._data.videoOpen != true
        //更新静音
        btnMuteAudio.isSelected =
            mViewModel.myself._data.hostMute == "1" || mViewModel.myself._data.mute == "1"
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

}