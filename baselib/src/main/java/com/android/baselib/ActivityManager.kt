package com.android.baselib

import android.app.Activity
import java.util.*

/**
 * Activity管理
 * Created by huangkangfa on 2017/10/11.
 *
 */
object ActivityManager {

    private var activities: Stack<Activity> = Stack()

    /**
     * activity集合
     * @return
     */
    private fun activityStack(): Stack<Activity> {
        return activities
    }

    /**
     * 获取下一个activity
     * @return
     */
    private fun activityStackLastElement(): Activity? {
        try {
            return activityStack().lastElement()
        } catch (e: Exception) {
        }
        return null
    }

    /**
     * 添加
     * @param activity
     */
    fun add(activity: Activity) {
        activityStack().add(activity)
    }

    /**
     * 获取当前activity
     * @return
     */
    fun get(): Activity? {
        return activityStackLastElement()
    }

    /**
     * 获取指定activity
     * @return
     */
    operator fun get(cls: Class<*>): Activity? {
        for (activity in activityStack()) {
            if (activity.javaClass == cls) {
                return activity
            }
        }
        return null
    }

    /**
     * 移除当前Activity
     */
    fun remove() {
        val activity = activityStackLastElement()
        remove(activity)
    }

    /**
     * 移除Activity
     */
    fun remove(activity: Activity?) {
        if (activity != null) {
            activityStack().remove(activity)
        }
    }

    /**
     * 关闭当前界面
     */
    fun finish() {
        val activity = activityStackLastElement()
        finish(activity)
    }

    /**
     * 关闭指定界面
     * @param activity
     */
    fun finish(activity: Activity?) {
        if (activity != null) {
            activityStack().remove(activity)
            activity.finish()
        }
    }

    /**
     * 关闭指定界面
     * @param cls
     */
    fun finish(cls: Class<*>) {
        for (activity in activityStack()) {
            if (activity.javaClass == cls) {
                finish(activity)
                break
            }
        }
    }

    /**
     * 逐层关闭到指定界面
     * @param cls
     */
    fun finishExceptOne(cls: Class<*>) {
        while (true) {
            val activity = activityStackLastElement() ?: break
            if (activity.javaClass == cls) {
                break
            }
            finish(activity)
        }
    }

    /**
     * 关闭所有页面s
     */
    fun finishAll() {
        while (true) {
            val activity = activityStackLastElement() ?: break
            finish(activity)
        }
    }

}