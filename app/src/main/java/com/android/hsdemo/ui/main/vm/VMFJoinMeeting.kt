package com.android.hsdemo.ui.main.vm

import android.app.Application
import android.text.TextUtils
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.android.baselib.custom.recyleview.adapter.AbstractAdapter
import com.android.baselib.custom.recyleview.adapter.ListItem
import com.android.baselib.custom.recyleview.adapter.setUP
import com.android.baselib.utils.MD5Utils
import com.android.hsdemo.model.ItemOfMeeting
import com.android.hsdemo.model.Meeting
import com.android.hsdemo.model.MeetingDetail
import com.android.hsdemo.network.HttpCallback
import com.android.hsdemo.network.RemoteRepositoryImpl
import com.rxjava.rxlife.life

class VMFJoinMeeting(application: Application) : AndroidViewModel(application) {

    /**
     * 列表数据源
     */
    var data: ArrayList<ItemOfMeeting> = arrayListOf(ItemOfMeeting(0, null))

    /**
     * 自己输入加入会议会议号
     */
    val meetingNo: MutableLiveData<String> = MutableLiveData("")

    /**
     * 自己输入加入会议会议密码
     */
    val meetingPassword: MutableLiveData<String> = MutableLiveData("")

    /**
     * 会议室名称
     */
    val meetingTitle: MutableLiveData<String> = MutableLiveData("")

    private lateinit var adapter: AbstractAdapter<ItemOfMeeting>

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
    }

    /**
     * 是最后一项
     */
    fun isLastItem(item: ItemOfMeeting): Boolean {
        if (TextUtils.equals(data[data.size - 1]._data?.id.toString(), item._data?.id.toString())) {
            return true
        }
        return false
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
     * 参加会议
     */
    fun joinMeeting(owner: LifecycleOwner, callback: HttpCallback<MeetingDetail>) {
        meetingTitle.value = ""
        if (TextUtils.isEmpty(meetingNo.value.toString())) {
            callback.failed("会议号不能为空")
            return
        }
        getMeetingDetail(meetingNo.value.toString(), owner, object : HttpCallback<MeetingDetail> {
            override fun success(t: MeetingDetail) {
                if (!TextUtils.isEmpty(t.pwd.toString()) && !TextUtils.equals(
                        MD5Utils.toMD5(meetingPassword.value.toString()),
                        t.pwd.toString()
                    )
                ) {
                    callback.failed("密码不正确")
                } else {
                    RemoteRepositoryImpl.joinOrLeaveMeeting(t.id.toString(), 1)
                        .life(owner)
                        .subscribe(
                            {
                                meetingTitle.value = t.title.toString()
                                callback.success(t)
                            }
                        ) { throwable: Throwable? -> callback.failed(throwable?.message.toString()) }
                }
            }

            override fun failed(msg: String) {
                callback.failed(msg)
            }
        })
    }

    /**
     * 快速加入会议
     */
    fun joinQuick(owner: LifecycleOwner, t: Meeting?, callback: HttpCallback<String>) {
        RemoteRepositoryImpl.joinOrLeaveMeeting(t?.id.toString(), 1)
            .life(owner)
            .subscribe(
                {
                    meetingTitle.value = t?.title.toString()
                    callback.success("加入成功")
                }
            ) { throwable: Throwable? -> callback.failed(throwable?.message.toString()) }
    }

    /**
     * 获取会议详情
     */
    private fun getMeetingDetail(
        meetingNo: String,
        owner: LifecycleOwner,
        callback: HttpCallback<MeetingDetail>
    ) {
        RemoteRepositoryImpl.getMeetingDetail(meetingNo)
            .life(owner)
            .subscribe(
                { result: MeetingDetail ->
                    callback.success(result)
                }
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