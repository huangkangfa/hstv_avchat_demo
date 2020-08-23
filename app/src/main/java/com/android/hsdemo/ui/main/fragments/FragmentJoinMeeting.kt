package com.android.hsdemo.ui.main.fragments

import android.os.Build
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.baselib.base.BaseFragment
import com.android.baselib.recyleview.adapter.ListItem
import com.android.baselib.utils.Preferences
import com.android.hsdemo.*
import com.android.hsdemo.databinding.FragmentJoinMeetingBinding
import com.android.hsdemo.model.ItemOfMeeting
import com.android.hsdemo.model.StatusView
import com.android.hsdemo.ui.main.vm.VMFJoinMeeting
import com.android.hsdemo.ui.rtc.ActivityRTC
import kotlinx.android.synthetic.main.fragment_join_meeting.*
import kotlinx.coroutines.*

class FragmentJoinMeeting : BaseFragment<VMFJoinMeeting, FragmentJoinMeetingBinding>(),
    View.OnFocusChangeListener {

    override fun getLayoutId(): Int = R.layout.fragment_join_meeting

    override fun getVariableId(): Int = BR.vm

    private var btns = arrayOfNulls<StatusView<View>>(1)
    private var tvs = arrayOfNulls<StatusView<TextView>>(1)
    private var imgs = arrayOfNulls<StatusView<ImageView>>(1)

    /**
     * 快速加入会议号item
     */
    private val itemOfEmpty = ListItem<ItemOfMeeting>(
        R.layout.item_adapter_join_meeting_0,
        { holder, _ ->
            val btnP = holder.getView<RelativeLayout>(R.id.btnP)
            val btnC = holder.getView<ImageView>(R.id.btnC)
            btnP.setOnFocusChangeListener { _, focus ->
                if (focus) {
                    btnP.setBackgroundResource(R.drawable.border_1)
                    btnC.setImageResource(R.mipmap.icon_add_1)
                } else {
                    btnP.setBackgroundResource(R.drawable.border_0)
                    btnC.setImageResource(R.mipmap.icon_add_0)
                }
            }
        }, {
            //快速输入会议号
            changeTypeUI(false)
        }
    )

    /**
     * 实际数据item
     */
    private val itemOfData = ListItem<ItemOfMeeting>(
        R.layout.item_adapter_join_meeting_1,
        { holder, item ->
            val btnP = holder.getView<RelativeLayout>(R.id.btnP)
            val tvTitle = holder.getView<TextView>(R.id.tvTitle)
            val tvContent = holder.getView<TextView>(R.id.tvContent)
            tvTitle.text = item._data?.title ?: "未知"
            tvContent.text = "会议号：${item._data?.meetingNo ?: "未知"}"
            btnP.setOnFocusChangeListener { _, focus ->
                if (focus) {
                    btnP.setBackgroundResource(R.drawable.border_1)
                } else {
                    btnP.setBackgroundResource(R.drawable.border_0)
                }
            }
        }, {
            //快速加入
            ActivityRTC.start(
                requireActivity(),
                Preferences.getString(KEY_ACCID),
                it._data?.meetingNo.toString()
            )
        }
    )

    override fun afterCreate() {

        btnOK.onFocusChangeListener = this
        btns[0] = StatusView<View>(btnOK, 0, BTN_BACKGROUNDS[0], BTN_BACKGROUNDS[1])
        tvs[0] = StatusView<TextView>(btnOKTv, 0, BTN_TEXT_COLORS[0], BTN_TEXT_COLORS[1])
        imgs[0] = StatusView<ImageView>(
            btnOKImg,
            0,
            R.mipmap.icon_sure_1,
            R.mipmap.icon_sure_0
        )

        //初始化RecycleView
        mViewModel.initRecycleView(
            recyclerView,
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false),
            itemOfEmpty,
            itemOfData
        )

        //第一个fragment 需要请求数据
        mViewModel.requestData(this)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden && tvTitle.visibility == View.VISIBLE) {
            if (mViewModel.isInitOK) {
                //请求数据
                mViewModel.requestData(this)
            } else {
                GlobalScope.launch(Dispatchers.IO) {
                    delay(50)
                    onHiddenChanged(hidden)
                }
            }
        }
    }

    fun changeTypeUI(isFirst: Boolean) {
        if (isFirst) {
            tvTitle.visibility = View.VISIBLE
            recyclerView.visibility = View.VISIBLE
            llInput.visibility = View.GONE
            btnOK.visibility = View.GONE
        } else {
            tvTitle.visibility = View.GONE
            recyclerView.visibility = View.GONE
            llInput.visibility = View.VISIBLE
            btnOK.visibility = View.VISIBLE
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onFocusChange(view: View, focus: Boolean) {
        if (focus) {
            if (view == btnOK) {
                view.animate().scaleX(1.1f).scaleY(1.1f).setDuration(500).start()
                btns[0]?.selectedResId?.let { btnOK.setBackgroundResource(it) }
                tvs[0]?.selectedResId?.let {
                    context?.getColor(it)?.let { btnOKTv.setTextColor(it) }
                }
                imgs[0]?.selectedResId?.let { btnOKImg.setImageResource(it) }
            }
        } else {
            if (view == btnOK) {
                view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(500).start()
                btns[0]?.unSelectedResId?.let { btnOK.setBackgroundResource(it) }
                tvs[0]?.unSelectedResId?.let {
                    context?.getColor(it)?.let { btnOKTv.setTextColor(it) }
                }
                imgs[0]?.unSelectedResId?.let { btnOKImg.setImageResource(it) }
            }
        }
    }

}