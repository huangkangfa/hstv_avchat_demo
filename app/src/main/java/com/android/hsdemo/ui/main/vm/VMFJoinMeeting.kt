package com.android.hsdemo.ui.main.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.android.baselib.recyleview.adapter.AbstractAdapter
import com.android.baselib.recyleview.adapter.ListItem
import com.android.baselib.recyleview.adapter.setUP
import com.android.hsdemo.model.ItemOfMeeting
import com.android.hsdemo.model.Meeting
import com.android.hsdemo.network.HttpCallback
import com.android.hsdemo.network.RemoteRepositoryImpl
import com.rxjava.rxlife.life
import kotlinx.android.synthetic.main.fragment_join_meeting.*
import kotlinx.coroutines.CoroutineScope

class VMFJoinMeeting(application: Application) : AndroidViewModel(application) {

    /**
     * 列表数据源
     */
    private var data: ArrayList<ItemOfMeeting> = arrayListOf(ItemOfMeeting(0, null))

    private lateinit var adapter: AbstractAdapter<ItemOfMeeting>

    var isInitOK = false

    /**
     * 初始化RecycleView列表
     */
    fun initRecycleView(
        recyclerView: RecyclerView,
        manager: RecyclerView.LayoutManager,
        itemOfEmpty: ListItem<ItemOfMeeting>,
        itemOfData: ListItem<ItemOfMeeting>
    ) {
        adapter = recyclerView.setUP(
            data,
            itemOfEmpty,
            itemOfData,
            manager = manager
        )
        isInitOK = true
    }

    /**
     * 获取我的会议数据
     */
    private fun getMyMeetingList(owner: LifecycleOwner, callback: HttpCallback<List<Meeting>>) {
        RemoteRepositoryImpl.getMyMeetingList()
            .life(owner)
            .subscribe(
                { result: List<Meeting> -> callback.success(result) }
            ) { throwable: Throwable? -> callback.failed(throwable?.message.toString()) }
    }

    /**
     * 获取列表数据
     */
    fun requestData(
        owner: LifecycleOwner,
        callback: HttpCallback<List<ItemOfMeeting>>? = null
    ) {
        getMyMeetingList(owner, object : HttpCallback<List<Meeting>> {
            override fun success(t: List<Meeting>) {
                val result: ArrayList<ItemOfMeeting> = ArrayList()
                result.add(ItemOfMeeting(0, null))
                for (meeting in t) {
                    result.add(ItemOfMeeting(1, meeting))
                }
                data.removeAll(data)
                data.addAll(result)
                adapter.notifyDataSetChanged()
                callback?.success(result)
            }

            override fun failed(msg: String) {
                callback?.failed(msg)
            }

        })
    }

}