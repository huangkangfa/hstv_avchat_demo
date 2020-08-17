package com.android.hsdemo.entrance

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.android.baselib.base.BaseActivity
import com.android.baselib.global.AppGlobal.context
import com.android.hsdemo.R
import com.android.hsdemo.databinding.ActivityEntranceBinding
import kotlinx.android.synthetic.main.activity_entrance.*

class ActivityEntrance : BaseActivity() {

    private lateinit var binding: ActivityEntranceBinding
    private lateinit var viewModel: VMEntrance

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, ActivityEntrance().javaClass))
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@ActivityEntrance, R.layout.activity_entrance)
        viewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(
                context as Application
            )
        ).get(VMEntrance::class.java)
        binding.vm = viewModel
        binding.lifecycleOwner = this

        //设置按钮聚焦变化监听
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val tempList = ArrayList<View>()
            tempList.add(bt_enter_room)
            tempList.add(bt_back)
            viewModel.initButtonFocusChangeListener(tempList)
        }
    }

}