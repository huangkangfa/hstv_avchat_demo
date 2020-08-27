package com.android.baselib.custom.eventbus

import androidx.collection.ArrayMap
import androidx.lifecycle.MutableLiveData

/**
 * Created by wb.zhuzichu18 on 2018/9/7.
 */
class EventBus private constructor() {
    private val mCacheBus: MutableMap<String, BusLiveData<Any>>

    private object SingletonHolder {
        val DEFAULT_BUS = EventBus()
    }

    private fun <T> withInfo(key: String, type: Class<T>, needData: Boolean): MutableLiveData<T> {
        if (!mCacheBus.containsKey(key)) {
            mCacheBus[key] = BusLiveData(key)
        }
        val data = mCacheBus[key]
        data!!.mNeedCurrentDataWhenFirstObserve = needData
        return mCacheBus[key] as MutableLiveData<T>
    }

    companion object {
        private fun get(): EventBus {
            return SingletonHolder.DEFAULT_BUS
        }

        fun with(key: String): MutableLiveData<Any> {
            return get().withInfo(key, Any::class.java, false)
        }

        fun <T> with(key: String, type: Class<T>): MutableLiveData<T> {
            return get().withInfo(key, type, false)
        }

        fun <T> with(
            key: String,
            type: Class<T>,
            needCurrentDataWhenNewObserve: Boolean
        ): MutableLiveData<T> {
            return get().withInfo(key, type, needCurrentDataWhenNewObserve)
        }
    }

    init {
        mCacheBus = ArrayMap()
    }
}