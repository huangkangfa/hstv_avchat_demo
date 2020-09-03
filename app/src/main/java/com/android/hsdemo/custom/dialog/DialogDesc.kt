package com.android.hsdemo.custom.dialog

import android.app.Activity
import android.os.Build
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import com.android.baselib.base.BaseDialog
import com.android.baselib.custom.recyleview.SpaceItem
import com.android.baselib.custom.recyleview.adapter.AbstractAdapter
import com.android.baselib.custom.recyleview.adapter.ListItem
import com.android.baselib.custom.recyleview.adapter.setUP
import com.android.baselib.utils.dp2px
import com.android.baselib.utils.showShortToast
import com.android.hsdemo.BTN_BACKGROUNDS
import com.android.hsdemo.BTN_TEXT_COLORS
import com.android.hsdemo.R
import com.android.hsdemo.model.ItemOfUser
import com.android.hsdemo.model.StatusView
import com.android.hsdemo.model.User
import com.android.hsdemo.network.RemoteRepositoryImpl
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.dialog_select_members.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DialogDesc(mActivity: Activity) : BaseDialog(mActivity) {

    init {
        setContentView(R.layout.dialog_desc, dp2px(580f), dp2px(400f), false)
        setGravity(Gravity.CENTER)
        setAnimations(R.anim.bottom_in)
    }

}