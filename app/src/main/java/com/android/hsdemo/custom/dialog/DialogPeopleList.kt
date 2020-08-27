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
import com.android.hsdemo.BTN_BACKGROUNDS
import com.android.hsdemo.BTN_TEXT_COLORS
import com.android.hsdemo.R
import com.android.hsdemo.model.ItemOfMemberInfo
import com.android.hsdemo.model.StatusView
import com.bumptech.glide.Glide
import com.elvishew.xlog.XLog
import kotlinx.android.synthetic.main.dialog_select_people.*

class DialogPeopleList(mActivity: Activity) : BaseDialog(mActivity), View.OnFocusChangeListener,
    View.OnClickListener {

    private var btns = arrayOfNulls<StatusView<View>>(1)
    private var tvs = arrayOfNulls<StatusView<TextView>>(1)
    private var imgs = arrayOfNulls<StatusView<ImageView>>(1)

    val adapter: AbstractAdapter<ItemOfMemberInfo>
    var data = arrayListOf<ItemOfMemberInfo>()
    var isMaster: Boolean = false

    /**
     * 列表子项
     */
    private val itemOfData = ListItem<ItemOfMemberInfo>(
        R.layout.item_adapter_memberlist,
        { holder, item ->

            val userAvatar = holder.getView<ImageView>(R.id.userAvatar)
            val userNickName = holder.getView<TextView>(R.id.userNickName)
            val userMute = holder.getView<ImageView>(R.id.userMute)
            val userDel = holder.getView<ImageView>(R.id.userDel)

            Glide.with(context).load(item._data.avatar)
                .placeholder(R.mipmap.icon_default_circle)
                .error(R.mipmap.icon_default_circle).into(userAvatar)
            userNickName.text = item._data.nickName

            if(isMaster){
                userNickName.visibility = View.VISIBLE
                userDel.visibility = View.VISIBLE
            }else{
                userNickName.visibility = View.GONE
                userDel.visibility = View.GONE
            }

            userMute.setOnClickListener {
                //静音与否操作

            }

            userDel.setOnClickListener {
                //踢人操作

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
        progressBar.visibility = View.VISIBLE
        adapter = recyclerView.setUP(
            data,
            itemOfData,
            manager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        )
        progressBar.visibility = View.GONE
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

            btnMuteAllTv.text = "取消全员静音"
        } else {
            //取消全员静音操作

            btnMuteAllTv.text = "全员静音"
        }
        view.isSelected = !view.isSelected
    }

    fun changeUI() {
        progressBar.visibility = View.VISIBLE
        XLog.i("【视频会议】人员列表视图更新 size=${data.size}")
        title.text = "参会人员列表(${data.size})"
        adapter.notifyDataSetChanged()
        progressBar.visibility = View.GONE
    }

}