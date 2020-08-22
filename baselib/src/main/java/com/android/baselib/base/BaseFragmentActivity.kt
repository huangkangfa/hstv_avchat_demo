package com.android.baselib.base

import android.os.Bundle
import androidx.annotation.AnimRes
import androidx.annotation.AnimatorRes
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import com.android.baselib.ActivityManager
import com.android.baselib.R

abstract class BaseFragmentActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityManager.add(this)
        setContentView(getLayoutId())
        afterCreate()
    }

    abstract fun afterCreate()

    /**
     * 获取当前显示的fragment
     */
    fun getVisibleFragment(): Fragment? {
        val fragments: List<Fragment> = supportFragmentManager.fragments
        for (fragment in fragments) {
            if (fragment.isVisible) return fragment
        }
        return null
    }

    /**
     * 添加Fragment
     * @param isAddBackStack 是否添加回退栈
     */
    fun addFragment(
        contentId: Int,
        fragment: Fragment,
        tag: String,
        isAddBackStack: Boolean,
        listener: OnCustomAnimationsLister? = null
    ): FragmentTransaction {
        val mTransaction = supportFragmentManager.beginTransaction()
        if (listener != null) {
            mTransaction.setCustomAnimations(
                listener.setEnterAnimations(),
                listener.setExitAnimations(),
                listener.setPopEnterAnimations(),
                listener.setPopExitAnimations()
            )
        }
        mTransaction.add(contentId, fragment, tag)
        if (isAddBackStack) {
            mTransaction.addToBackStack(tag)
        }
        return mTransaction
    }

    /**
     * 覆盖Fragment
     * @param isAddBackStack 是否添加回退栈
     */
    fun replaceFragment(
        contentId: Int,
        fragment: Fragment,
        tag: String,
        isAddBackStack: Boolean,
        listener: OnCustomAnimationsLister? = null
    ): FragmentTransaction {
        val mTransaction = supportFragmentManager.beginTransaction()
        if (listener != null) {
            mTransaction.setCustomAnimations(
                listener.setEnterAnimations(),
                listener.setExitAnimations(),
                listener.setPopEnterAnimations(),
                listener.setPopExitAnimations()
            )
        }
        mTransaction.replace(contentId, fragment, tag)
        if (isAddBackStack) {
            mTransaction.addToBackStack(tag)
        }
        return mTransaction
    }

    interface OnCustomAnimationsLister {
        @AnimatorRes
        @AnimRes
        fun setEnterAnimations(): Int

        @AnimatorRes
        @AnimRes
        fun setExitAnimations(): Int

        @AnimatorRes
        @AnimRes
        fun setPopEnterAnimations(): Int

        @AnimatorRes
        @AnimRes
        fun setPopExitAnimations(): Int
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityManager.remove(this)
    }

    /**
     * 布局文件id
     */
    @LayoutRes
    protected abstract fun getLayoutId(): Int

}