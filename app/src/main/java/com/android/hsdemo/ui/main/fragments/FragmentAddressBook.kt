package com.android.hsdemo.ui.main.fragments

import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import com.android.baselib.base.BaseFragment
import com.android.baselib.recyleview.adapter.ListItem
import com.android.hsdemo.BR
import com.android.hsdemo.R
import com.android.hsdemo.databinding.FragmentAddressBookBinding
import com.android.hsdemo.model.ItemOfUser
import com.android.hsdemo.ui.main.vm.VMFAddressBook
import kotlinx.android.synthetic.main.fragment_address_book.*

class FragmentAddressBook : BaseFragment<VMFAddressBook, FragmentAddressBookBinding>() {

    override fun getLayoutId(): Int = R.layout.fragment_address_book

    override fun getVariableId(): Int = BR.vm

    /**
     * 快速加入会议号item
     */
    private val itemOfUser = ListItem<ItemOfUser>(
        R.layout.item_adapter_user,
        { holder, item ->
            val userName =holder.getView<TextView>(R.id.userName)
            val userPhone =holder.getView<TextView>(R.id.userPhone)
            userName.text = item._data.nickName
            userPhone.text = item._data.userName
        }, {

        }
    )

    override fun afterCreate() {
        //初始化RecycleView
        mViewModel.initRecycleView(
            recyclerView,
            GridLayoutManager(context, 2, GridLayoutManager.HORIZONTAL, false),
            itemOfUser
        )
        //请求数据
        mViewModel.requestData(this)
    }
}