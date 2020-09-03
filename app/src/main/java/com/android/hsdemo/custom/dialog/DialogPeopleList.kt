package com.android.hsdemo.custom.dialog

import android.app.Activity
import android.os.Build
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.baselib.base.BaseDialog
import com.android.baselib.custom.recyleview.adapter.AbstractAdapter
import com.android.baselib.custom.recyleview.adapter.ListItem
import com.android.baselib.custom.recyleview.adapter.setUP
import com.android.baselib.utils.dp2px
import com.android.baselib.utils.showShortToast
import com.android.hsdemo.BTN_BACKGROUNDS
import com.android.hsdemo.BTN_TEXT_COLORS
import com.android.hsdemo.MeetingCmd
import com.android.hsdemo.R
import com.android.hsdemo.model.ItemOfMemberInfo
import com.android.hsdemo.model.ItemOfPeople
import com.android.hsdemo.model.MeetingDetail
import com.android.hsdemo.model.StatusView
import com.android.hsdemo.network.RemoteRepositoryImpl
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.dialog_select_people.*

class DialogPeopleList(mActivity: Activity) : BaseDialog(mActivity), View.OnFocusChangeListener,
    View.OnClickListener {

    private var btns = arrayOfNulls<StatusView<View>>(1)
    private var tvs = arrayOfNulls<StatusView<TextView>>(1)
    private var imgs = arrayOfNulls<StatusView<ImageView>>(1)

    var adapter: AbstractAdapter<ItemOfPeople>? = null
    var data = arrayListOf<ItemOfPeople>()
    var isMaster: Boolean = false
    var meetingDetail: MeetingDetail? = null
    var dialogWait: DialogWait = DialogWait(mActivity)
    var mySelf: ItemOfMemberInfo? = null

    /**
     * 列表子项
     */
    private val itemOfData = ListItem<ItemOfPeople>(
        R.layout.item_adapter_memberlist,
        { holder, item ->
            val userAvatar = holder.getView<ImageView>(R.id.userAvatar)
            val userNickName = holder.getView<TextView>(R.id.userNickName)
            val userMute = holder.getView<ImageView>(R.id.userMute)
            val userDel = holder.getView<ImageView>(R.id.userDel)

            Glide.with(context).load(item._data.avatar.toString())
                .placeholder(R.mipmap.icon_default_circle)
                .error(R.mipmap.icon_default_circle).into(userAvatar)

            userNickName.text = item._data.nickName.toString()

            if (isMaster) {
                userMute.visibility = View.VISIBLE
                userDel.visibility = View.VISIBLE
            } else {
                userMute.visibility = View.GONE
                userDel.visibility = View.GONE
            }

            if (item._data.hostMute == true) {
                Glide.with(context).load(R.mipmap.icon_people_mute_audio).into(userMute)
            } else {
                Glide.with(context).load(R.mipmap.icon_people_mute_audio_cancle).into(userMute)
            }

            userMute.setOnClickListener {
                //静音与否操作
                if (item._data.hostMute == true) {
                    dialogWait.show()
                    //取消静音
                    RemoteRepositoryImpl.opReport(
                        meetingDetail?.id.toString(),
                        MeetingCmd.SET_AUDIO_MUTE_OTHERS_CANCLE,
                        item._data.account,
                        null
                    ).subscribe(
                        {
                            dialogWait.dismiss()
                            item._data.hostMute = false
                            Glide.with(context).load(R.mipmap.icon_people_mute_audio_cancle)
                                .into(userMute)
                        }
                    ) { throwable: Throwable? ->
                        dialogWait.dismiss()
                        showShortToast("操作失败:${throwable?.message.toString()}")
                    }
                }
                if (item._data.hostMute == null || item._data.hostMute == false) {
                    dialogWait.show()
                    //静音
                    RemoteRepositoryImpl.opReport(
                        meetingDetail?.id.toString(),
                        MeetingCmd.SET_AUDIO_MUTE_OTHERS,
                        item._data.account,
                        null
                    ).subscribe(
                        {
                            dialogWait.dismiss()
                            item._data.hostMute = true
                            Glide.with(context).load(R.mipmap.icon_people_mute_audio)
                                .into(userMute)
                        }
                    ) { throwable: Throwable? ->
                        dialogWait.dismiss()
                        showShortToast("操作失败:${throwable?.message.toString()}")
                    }
                }
            }

            userDel.setOnClickListener {
                dialogWait.show()
                //踢人操作
                RemoteRepositoryImpl.opReport(
                    meetingDetail?.id.toString(),
                    MeetingCmd.KICK_OTHERS,
                    item._data.account,
                    null
                ).subscribe(
                    {
                        dialogWait.dismiss()
                        data.remove(item)
                        adapter?.notifyDataSetChanged()
                    }
                ) { throwable: Throwable? ->
                    dialogWait.dismiss()
                    showShortToast("操作失败:${throwable?.message.toString()}")
                }
            }

        }, {

        }
    )

    init {
        setContentView(R.layout.dialog_select_people, dp2px(260f), MATCH_PARENT, false)
        setGravity(Gravity.END)
        setAnimations(R.anim.right_in)

        btnMuteAll.onFocusChangeListener = this
        btnMuteAll.setOnClickListener(this)

        btns[0] = StatusView<View>(btnMuteAll, 0, BTN_BACKGROUNDS[0], BTN_BACKGROUNDS[1])
        tvs[0] = StatusView<TextView>(btnMuteAllTv, 0, BTN_TEXT_COLORS[0], BTN_TEXT_COLORS[1])
        imgs[0] = StatusView<ImageView>(
            btnMuteAllImg,
            0,
            R.mipmap.icon_audio_mute_all_1,
            R.mipmap.icon_audio_mute_all_0
        )

        progressBar.visibility = View.GONE
    }

    fun initMyself(temp: ItemOfMemberInfo) {
        itemMyself.visibility = View.VISIBLE
        mySelf = temp
        //自己要隐藏操作按钮
        itemMyself.findViewById<ImageView>(R.id.userMute).visibility = View.GONE
        itemMyself.findViewById<ImageView>(R.id.userDel).visibility = View.GONE
        //设置昵称
        itemMyself.findViewById<TextView>(R.id.userNickName).text =
            "${mySelf?._data?.nickName.toString()}(自己)"
        //设置头像
        Glide.with(context).load(mySelf?._data?.avatar.toString())
            .placeholder(R.mipmap.icon_default_circle)
            .error(R.mipmap.icon_default_circle)
            .into(itemMyself.findViewById(R.id.userAvatar))
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onFocusChange(view: View, focus: Boolean) {
        when (view) {
            btnMuteAll -> {
                if (focus) {
                    if (view == btnMuteAll) {
                        btns[0]?.selectedResId?.let { btnMuteAll.setBackgroundResource(it) }
                        tvs[0]?.selectedResId?.let {
                            context.getColor(it).let { btnMuteAllTv.setTextColor(it) }
                        }
                        imgs[0]?.selectedResId?.let { btnMuteAllImg.setImageResource(it) }
                    }
                } else {
                    if (view == btnMuteAll) {
                        btns[0]?.unSelectedResId?.let { btnMuteAll.setBackgroundResource(it) }
                        tvs[0]?.unSelectedResId?.let {
                            context.getColor(it).let { btnMuteAllTv.setTextColor(it) }
                        }
                        imgs[0]?.unSelectedResId?.let { btnMuteAllImg.setImageResource(it) }
                    }
                }
            }
        }
    }

    override fun onClick(view: View) {
        if (!view.isSelected) {
            //全员静音操作
            dialogWait.show()
            RemoteRepositoryImpl.opReport(
                meetingDetail?.id.toString(),
                MeetingCmd.AUDIO_MUTE_ALL,
                null,
                null
            ).subscribe(
                {
                    dialogWait.dismiss()
                    btnMuteAllTv.text = "取消全员静音"
                    view.isSelected = !view.isSelected
                    for (item in data) {
                        item._data.hostMute = true
                    }
                    adapter?.notifyDataSetChanged()
                }
            ) { throwable: Throwable? ->
                dialogWait.dismiss()
                showShortToast("操作失败:${throwable?.message.toString()}")
            }
        } else {
            //取消全员静音操作
            dialogWait.show()
            RemoteRepositoryImpl.opReport(
                meetingDetail?.id.toString(),
                MeetingCmd.AUDIO_MUTE_ALL_CANCLE,
                null,
                null
            ).subscribe(
                {
                    dialogWait.dismiss()
                    btnMuteAllTv.text = "全员静音"
                    view.isSelected = !view.isSelected
                    for (item in data) {
                        item._data.hostMute = false
                    }
                    adapter?.notifyDataSetChanged()
                }
            ) { throwable: Throwable? ->
                dialogWait.dismiss()
                showShortToast("操作失败:${throwable?.message.toString()}")
            }
        }
    }

    fun changeUI() {
        adapter = dialogRecyclerView.setUP(
            data,
            itemOfData,
            manager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        )
        title.text = "参会人员列表(${data.size + 1})"
        if (isMaster) {
            btnMuteAll.visibility = View.VISIBLE
        } else {
            btnMuteAll.visibility = View.GONE
        }
    }

}