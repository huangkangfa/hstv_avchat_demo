package com.android.hsdemo.model

import com.android.baselib.custom.recyleview.adapter.ListItemI
import com.android.hsdemo.R

open class ItemOfMemberInfo(var _type: Int, var _data: MemberInfo, var layoutType: Int = 1) :
    ListItemI {
    override fun getType(): Int {
        return if(layoutType == 1){
            R.layout.item_adapter_video_view
        }else{
            R.layout.item_adapter_video_view2
        }
    }

}