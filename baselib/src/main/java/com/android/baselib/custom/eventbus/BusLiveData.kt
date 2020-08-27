package com.android.baselib.custom.eventbus

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

class BusLiveData<T>(  // 比如BusLiveData 在添加 observe的时候，同一个界面对应的一个事件只能注册一次
    // 自己添加逻辑定制
    val mEventType: String
) : MutableLiveData<T>() {
    //首次注册的时候，是否需要当前LiveData 最新数据
    var mNeedCurrentDataWhenFirstObserve = false

    //主动触发数据更新事件才通知所有Observer
    var mIsStartChangeData = false
    override fun setValue(value: T) {
        mIsStartChangeData = true
        super.setValue(value)
    }

    override fun postValue(value: T) {
        mIsStartChangeData = true
        super.postValue(value)
    }

    //添加注册对应事件type的监听
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        super.observe(owner, ObserverWrapper(observer, this))
    }

    //数据更新一直通知刷新
    override fun observeForever(observer: Observer<in T>) {
        super.observeForever(ObserverWrapper(observer, this))
    }

    override fun removeObserver(observer: Observer<in T>) {
        super.removeObserver(observer)
    }
}