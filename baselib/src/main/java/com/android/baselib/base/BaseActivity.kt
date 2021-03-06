package com.android.baselib.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.baselib.ActivityManager
import java.lang.reflect.ParameterizedType

abstract class BaseActivity<VM : AndroidViewModel, VDB : ViewDataBinding> : AppCompatActivity() {

    protected lateinit var mViewModel: VM
    protected lateinit var mViewDataBinding: VDB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityManager.add(this)
        setContentView(getLayoutId())
        mViewDataBinding = DataBindingUtil.setContentView(this, getLayoutId())
        val vmClass: Class<VM> =
            (this.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<VM>
        mViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(vmClass)
        mViewDataBinding.setVariable(getVariableId(), mViewModel)
        mViewDataBinding.lifecycleOwner = this
        afterCreate()
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

    /**
     * VM绑定数据id
     */
    protected abstract fun getVariableId(): Int

    /**
     * create初始化后的操作
     */
    protected abstract fun afterCreate()

}