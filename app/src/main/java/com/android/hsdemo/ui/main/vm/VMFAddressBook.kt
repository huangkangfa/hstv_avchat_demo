package com.android.hsdemo.ui.main.vm

import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.android.baselib.custom.recyleview.adapter.AbstractAdapter
import com.android.baselib.custom.recyleview.adapter.ListItem
import com.android.baselib.custom.recyleview.adapter.setUP
import com.android.hsdemo.model.ItemOfUser
import com.android.hsdemo.model.User
import com.android.hsdemo.network.HttpCallback
import com.android.hsdemo.network.RemoteRepositoryImpl
import com.rxjava.rxlife.life

class VMFAddressBook(application: Application) : AndroidViewModel(application) {
    /** O
     * 列表数据源
     */
    var data: ArrayList<ItemOfUser> = arrayListOf()

    lateinit var adapter: AbstractAdapter<ItemOfUser>

    var isInitOK = false

    /**
     * 初始化RecycleView列表
     */
    fun initRecycleView(
        recyclerView: RecyclerView,
        manager: RecyclerView.LayoutManager,
        itemOfUser: ListItem<ItemOfUser>
    ) {
        adapter = recyclerView.setUP(
            data,
            itemOfUser,
            manager = manager
        )
        isInitOK = true
    }

    /**
     * 获取用户列表
     */
    private fun getUserList(owner: LifecycleOwner, callback: HttpCallback<List<User>>) {
        RemoteRepositoryImpl.getUserList()
            .life(owner)
            .subscribe(
                { result: List<User> -> callback.success(result) }
            ) { throwable: Throwable? -> callback.failed(throwable?.message.toString()) }
    }

    /**
     * 获取列表数据
     */
    fun requestData(
        owner: LifecycleOwner,
        callback: HttpCallback<List<ItemOfUser>>? = null
    ) {
        getUserList(owner, object : HttpCallback<List<User>> {
            override fun success(t: List<User>) {
                val result: ArrayList<ItemOfUser> = ArrayList()
                for (user in t) {
                    result.add(ItemOfUser(user))
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










