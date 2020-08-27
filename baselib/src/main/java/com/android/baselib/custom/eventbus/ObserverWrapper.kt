package com.android.baselib.custom.eventbus

import androidx.lifecycle.Observer

class ObserverWrapper<T>(private var observer: Observer<in T>, var liveData: BusLiveData<T>) :
    Observer<T> {
    override fun onChanged(t: T) {
        if (liveData.mIsStartChangeData) {
            observer.onChanged(t)
        }
    }

    init {
        //mIsStartChangeData 可过滤掉liveData首次创建监听，之前的遗留的值
        liveData.mIsStartChangeData = liveData.mNeedCurrentDataWhenFirstObserve
    }
}