package com.android.hsdemo.model

import com.android.baselib.custom.recyleview.adapter.ListItemI
import com.android.hsdemo.R

class ItemOfPeople(var _data: MemberInfo) : ListItemI {
    override fun getType(): Int {
        return R.layout.item_adapter_memberlist
    }
}