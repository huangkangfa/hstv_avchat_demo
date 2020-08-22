package com.android.hsdemo.ui.main.fragments

import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.baselib.base.BaseFragment
import com.android.baselib.recyleview.adapter.AbstractAdapter
import com.android.baselib.recyleview.adapter.ListItem
import com.android.baselib.recyleview.adapter.MultiAdapter
import com.android.baselib.recyleview.adapter.setUP
import com.android.baselib.utils.Preferences
import com.android.baselib.utils.showShortToast
import com.android.hsdemo.BR
import com.android.hsdemo.KEY_ACCID
import com.android.hsdemo.R
import com.android.hsdemo.databinding.FragmentJoinMeetingBinding
import com.android.hsdemo.model.ItemOfMeeting
import com.android.hsdemo.model.Meeting
import com.android.hsdemo.network.HttpCallback
import com.android.hsdemo.ui.main.ActivityMain
import com.android.hsdemo.ui.main.vm.VMFJoinMeeting
import com.android.hsdemo.ui.rtc.ActivityRTC
import com.android.hsdemo.util.controlFocusStatusOfView
import com.elvishew.xlog.XLog
import kotlinx.android.synthetic.main.fragment_join_meeting.*

class FragmentJoinMeeting : BaseFragment<VMFJoinMeeting, FragmentJoinMeetingBinding>() {

    override fun getLayoutId(): Int = R.layout.fragment_join_meeting

    override fun getVariableId(): Int = BR.vm

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
        //初始化RecycleView
        mViewModel.initRecycleView(
            recyclerView,
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false),
            itemOfEmpty,
            itemOfData
        )
        //请求数据
        mViewModel.requestData(this)
    }

}