package com.android.baselib.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.reflect.ParameterizedType

abstract class BaseFragment<VM : AndroidViewModel, VDB : ViewDataBinding> : Fragment() {

    lateinit var mViewModel: VM
    lateinit var mViewDataBinding: VDB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mViewDataBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        val vmClass: Class<VM> =
            (this.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<VM>
        mViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        ).get(vmClass)
        mViewDataBinding.setVariable(getVariableId(), mViewModel)
        mViewDataBinding.lifecycleOwner = this
        return mViewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        afterCreate()
    }

    /**
     * 布局文件id
     */
    @LayoutRes
    protected abstract fun getLayoutId(): Int

    /**
     * VM绑定数据id
     */
    protected abstract fun getVariableId(): Int

    /**
     * create初始化后的操作
     */
    protected abstract fun afterCreate()

    /**
     * 获取当前显示的fragment
     */
    fun getVisibleFragment(): Fragment? {
        val fragments: List<Fragment> = childFragmentManager.fragments
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
        listener: BaseFragmentActivity.OnCustomAnimationsLister? = null
    ): FragmentTransaction {
        val mTransaction = childFragmentManager.beginTransaction()
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
        listener: BaseFragmentActivity.OnCustomAnimationsLister? = null
    ): FragmentTransaction {
        val mTransaction = childFragmentManager.beginTransaction()
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

}