package com.android.hsdemo.model

import com.android.baselib.recyleview.adapter.ListItemI
import com.android.hsdemo.R

class ItemOfMeeting(private var _type: Int, var _data: Meeting?) : ListItemI {
    override fun getType(): Int {
        return if(_type==0){
            R.layout.item_adapter_join_meeting_0
        }else{
            R.layout.item_adapter_join_meeting_1
        }
    }

}