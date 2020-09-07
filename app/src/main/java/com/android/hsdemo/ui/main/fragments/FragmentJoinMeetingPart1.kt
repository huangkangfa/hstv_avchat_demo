package com.android.hsdemo.ui.main.fragments

import android.text.TextUtils
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.baselib.base.BaseFragment
import com.android.baselib.custom.recyleview.adapter.ListItem
import com.android.baselib.utils.Preferences
import com.android.baselib.utils.showShortToast
import com.android.hsdemo.BR
import com.android.hsdemo.KEY_ACCID
import com.android.hsdemo.R
import com.android.hsdemo.custom.ClipLinearLayout
import com.android.hsdemo.databinding.FragmentJoinMeetingBinding
import com.android.hsdemo.model.ItemOfMeeting
import com.android.hsdemo.network.HttpCallback
import com.android.hsdemo.ui.custom_rtc.ActivityRTC2
import com.android.hsdemo.ui.main.vm.VMFJoinMeeting
import com.android.hsdemo.ui.rtc.ActivityRTC
import com.android.hsdemo.util.controlFocusStatusOfView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_join_meeting_part1.*

class FragmentJoinMeetingPart1 :
    BaseFragment<VMFJoinMeeting, FragmentJoinMeetingBinding>() {

    override fun getLayoutId(): Int = R.layout.fragment_join_meeting_part1

    override fun getVariableId(): Int = BR.vm

    /**
     * 快速加入会议号item
     */
    private val itemOfEmpty = ListItem<ItemOfMeeting>(
        R.layout.item_adapter_join_meeting_0,
        { holder, _ ->
            val listItem = holder.getView<ClipLinearLayout>(R.id.listItem)
            val btnP = holder.getView<RelativeLayout>(R.id.btnP)
            val btnC = holder.getView<ImageView>(R.id.btnC)
            listItem.setOnFocusChangeListener { _, focus ->
                if (focus) {
                    btnP.setBackgroundResource(R.drawable.border_1)
                    btnC.setImageResource(R.mipmap.icon_add_1)
                } else {
                    btnP.setBackgroundResource(R.drawable.border_0)
                    btnC.setImageResource(R.mipmap.icon_add_0)
                }
            }
            //左边焦点不越界
            holder.itemView.nextFocusLeftId = holder.itemView.id
            //右边焦点控制，不然跳到下方主界面的另一个选项
            holder.itemView.nextFocusRightId = holder.itemView.id
            if (mViewModel.data.size > 1) {
                holder.itemView.nextFocusRightId = View.NO_ID
            }
            //向下焦点控制
            holder.itemView.nextFocusDownId =
                requireActivity().findViewById<View>(R.id.btnJoinMeeting).id
        }, {
            //快速输入会议号
            (parentFragment as FragmentJoinMeeting).changeFragment(false)
        }
    )

    /**
     * 实际数据item
     */
    private val itemOfData = ListItem<ItemOfMeeting>(
        R.layout.item_adapter_join_meeting_1,
        { holder, item ->
            val listItem = holder.getView<ClipLinearLayout>(R.id.listItem)
            val btnP = holder.getView<RelativeLayout>(R.id.btnP)
            val flagC = holder.getView<FrameLayout>(R.id.flagC)
            val tvTitle = holder.getView<TextView>(R.id.tvTitle)
            val tvContent = holder.getView<TextView>(R.id.tvContent)
            tvTitle.text = item._data?.title ?: "未知"
            tvContent.text = "会议号：${item._data?.meetingNo ?: "未知"}"
            if (TextUtils.equals(
                    item._data?.creater.toString(),
                    Preferences.getString(KEY_ACCID)
                )
            ) {
                flagC.visibility = View.VISIBLE
            } else {
                flagC.visibility = View.GONE
            }
            listItem.setOnFocusChangeListener { _, focus ->
                if (focus) {
                    btnP.setBackgroundResource(R.drawable.border_1)
                } else {
                    btnP.setBackgroundResource(R.drawable.border_0)
                }
            }
            //最右边item焦点不越界
            if (mViewModel.isLastItem(item)) {
                holder.itemView.nextFocusRightId = holder.itemView.id
            } else {
                holder.itemView.nextFocusRightId = View.NO_ID
            }
            //向下焦点控制
            holder.itemView.nextFocusDownId =
                requireActivity().findViewById<View>(R.id.btnJoinMeeting).id
        }, {
            mViewModel.joinQuick(this, it._data, object : HttpCallback<String> {
                override fun success(t: String) {
                    //快速加入
                    ActivityRTC.start(
                        requireActivity(),
                        Preferences.getString(KEY_ACCID),
                        it._data?.meetingNo.toString(),
                        it._data?.title.toString()
                    )
                }

                override fun failed(msg: String) {
                    showShortToast(msg)
                }

            })
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

        mViewModel.requestData(this)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            focusThis()
        }
    }

    fun focusThis() {
        controlFocusStatusOfView(recyclerView, true)
    }

}