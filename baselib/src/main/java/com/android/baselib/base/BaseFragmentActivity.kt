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
     * 添加Fragment
     * @param isAddBackStack 是否添加回退栈
     */
    fun addFragment(
        contentId: Int,
        fragment: Fragment,
        tag: String,
        isAddBackStack: Boolean,
        listener: OnCustomAnimationsLister?
    ): FragmentTransaction {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        if (listener != null) {
            transaction.setCustomAnimations(
                listener.setEnterAnimations(),
                listener.setExitAnimations(),
                listener.setPopEnterAnimations(),
                listener.setPopExitAnimations()
            )
        }
        transaction.add(contentId, fragment, tag)
        if (isAddBackStack) {
            transaction.addToBackStack(tag)
        }
        return transaction
    }

    fun addFragment(
        contentId: Int,
        fragment: Fragment,
        tag: String,
        isAddBackStack: Boolean
    ): FragmentTransaction {
        return addFragment(contentId, fragment, tag, isAddBackStack, null)
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
        listener: OnCustomAnimationsLister?
    ): FragmentTransaction {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        if (listener != null) {
            transaction.setCustomAnimations(
                listener.setEnterAnimations(),
                listener.setExitAnimations(),
                listener.setPopEnterAnimations(),
                listener.setPopExitAnimations()
            )
        }
        transaction.replace(contentId, fragment, tag)
        if (isAddBackStack) {
            transaction.addToBackStack(tag)
        }
        return transaction
    }

    fun replaceFragment(
        contentId: Int,
        fragment: Fragment,
        tag: String,
        isAddBackStack: Boolean
    ): FragmentTransaction {
        return replaceFragment(contentId, fragment, tag, isAddBackStack, null)
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