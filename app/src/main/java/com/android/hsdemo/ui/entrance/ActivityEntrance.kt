package com.android.hsdemo.ui.entrance

import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.View
import com.android.baselib.base.BaseActivity
import com.android.hsdemo.BR
import com.android.hsdemo.R
import com.android.hsdemo.databinding.ActivityEntranceBinding
import com.android.hsdemo.ui.entrance.vm.VMEntrance
import kotlinx.android.synthetic.main.activity_entrance.*

class ActivityEntrance : BaseActivity<VMEntrance, ActivityEntranceBinding>() {

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, ActivityEntrance().javaClass))
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_entrance

    override fun getVariableId(): Int = BR.vm

    override fun afterCreate() {
        //设置按钮聚焦变化监听
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val tempList = ArrayList<View>()
            tempList.add(bt_enter_room)
            tempList.add(bt_back)
            mViewModel.initButtonFocusChangeListener(tempList)
        }
    }

}