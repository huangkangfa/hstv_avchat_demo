package com.android.hsdemo.ui.main.fragments

import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import com.android.baselib.base.BaseFragment
import com.android.baselib.custom.recyleview.SpaceItem
import com.android.baselib.custom.recyleview.adapter.ListItem
import com.android.baselib.utils.dp2px
import com.android.hsdemo.BR
import com.android.hsdemo.R
import com.android.hsdemo.databinding.FragmentAddressBookBinding
import com.android.hsdemo.model.ItemOfUser
import com.android.hsdemo.ui.main.vm.VMFAddressBook
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_address_book.recyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FragmentAddressBook : BaseFragment<VMFAddressBook, FragmentAddressBookBinding>() {

    override fun getLayoutId(): Int = R.layout.fragment_address_book

    override fun getVariableId(): Int = BR.vm

    private val itemOfUser = ListItem<ItemOfUser>(
        R.layout.item_adapter_user,
        { holder, item ->
            val userName = holder.getView<TextView>(R.id.userName)
            val userPhone = holder.getView<TextView>(R.id.userPhone)
            val userImg = holder.getView<ImageView>(R.id.userImg)
            userName.text = item._data.nickName
            userPhone.text = item._data.userName
            Glide.with(holder.itemView)
                .load(item._data.userAvatar)
                .placeholder(R.mipmap.icon_default)
                .error(R.mipmap.icon_default)
                .into(userImg)
        }, {

        }
    )

    override fun afterCreate() {
        //初始化RecycleView
        recyclerView.addItemDecoration(
            SpaceItem(
                left = dp2px(2f),
                right = dp2px(2f),
                bottom = dp2px(5f)
            )
        )
        mViewModel.initRecycleView(
            recyclerView,
            GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false),
            itemOfUser
        )
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            if (mViewModel.isInitOK) {
                //请求数据
                mViewModel.requestData(this)
            } else {
                GlobalScope.launch(Dispatchers.IO) {
                    delay(50)
                    onHiddenChanged(hidden)
                }
            }
        }
    }


}