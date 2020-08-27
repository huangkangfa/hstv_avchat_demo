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

class DialogSelectPeople(mActivity: Activity) : BaseDialog(mActivity),
    View.OnFocusChangeListener, View.OnClickListener {

    private var btns = arrayOfNulls<StatusView<View>>(2)
    private var tvs = arrayOfNulls<StatusView<TextView>>(2)
    private var imgs = arrayOfNulls<StatusView<ImageView>>(2)

    private var adapter: AbstractAdapter<ItemOfUser>? = null
    private var data: ArrayList<ItemOfUser> = arrayListOf()
    private var selectedData: HashMap<String, User> = HashMap()

    interface OnStatusClickListener {
        fun onSureClick(data: HashMap<String, User>)
        fun onCancleClick()
    }

    var listener: OnStatusClickListener? = null

    private val itemOfUser = ListItem<ItemOfUser>(
        R.layout.item_adapter_user,
        { holder, item ->
            val userName = holder.getView<TextView>(R.id.userName)
            val userPhone = holder.getView<TextView>(R.id.userPhone)
            val userImg = holder.getView<ImageView>(R.id.userImg)
            val statusSelect = holder.getView<ImageView>(R.id.statusSelect)

            statusSelect.visibility = View.VISIBLE
            if (selectedData[item._data.accid] == null) {
                Glide.with(holder.itemView)
                    .load(R.mipmap.icon_item_select_0)
                    .into(statusSelect)
            } else {
                Glide.with(holder.itemView)
                    .load(R.mipmap.icon_item_select_1)
                    .into(statusSelect)
            }

            userName.text = item._data.nickName
            userPhone.text = item._data.userName
            Glide.with(holder.itemView)
                .load(item._data.userAvatar)
                .placeholder(R.mipmap.icon_default)
                .error(R.mipmap.icon_default)
                .into(userImg)

            holder.itemView.setOnClickListener {
                if (selectedData[item._data.accid.toString()] == null) {
                    //添加
                    selectedData[item._data.accid.toString()] = item._data
                    Glide.with(holder.itemView)
                        .load(R.mipmap.icon_item_select_1)
                        .into(statusSelect)
                } else {
                    //移除
                    selectedData.remove(item._data.accid.toString())
                    Glide.with(holder.itemView)
                        .load(R.mipmap.icon_item_select_0)
                        .into(statusSelect)
                }
                tvNum.text = selectedData.size.toString()
            }

        }, {

        }
    )

    init {
        setContentView(R.layout.dialog_select_members, dp2px(580f), dp2px(480f), false)
        setGravity(Gravity.CENTER)
        setAnimations(R.anim.bottom_in)

        btns[0] = StatusView<View>(btnOK, 0, BTN_BACKGROUNDS[0], BTN_BACKGROUNDS[1])
        btns[1] = StatusView<View>(btnCancel, 1, BTN_BACKGROUNDS[0], BTN_BACKGROUNDS[1])
        tvs[0] = StatusView<TextView>(btnOKTv, 0, BTN_TEXT_COLORS[0], BTN_TEXT_COLORS[1])
        tvs[1] = StatusView<TextView>(btnCancelTv, 1, BTN_TEXT_COLORS[0], BTN_TEXT_COLORS[1])
        imgs[0] = StatusView<ImageView>(
            btnOKImg,
            0,
            R.mipmap.icon_sure_1,
            R.mipmap.icon_sure_0
        )
        imgs[1] = StatusView<ImageView>(
            btnCancelImg,
            0,
            R.mipmap.icon_back_1,
            R.mipmap.icon_back_0
        )
        btnOK.onFocusChangeListener = this
        btnCancel.onFocusChangeListener = this
        btnOK.setOnClickListener(this)
        btnCancel.setOnClickListener(this)

        //初始化RecycleView
        recyclerView.addItemDecoration(
            SpaceItem(
                left = dp2px(2f),
                right = dp2px(2f),
                bottom = dp2px(5f)
            )
        )
        adapter = recyclerView.setUP(
            data,
            itemOfUser,
            manager = GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false)
        )
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onFocusChange(view: View, focus: Boolean) {
        when (view) {
            btnOK -> {
                if (focus) {
                    btns[0]?.selectedResId?.let { btnOK.setBackgroundResource(it) }
                    tvs[0]?.selectedResId?.let {
                        context?.getColor(it)?.let { btnOKTv.setTextColor(it) }
                    }
                    imgs[0]?.selectedResId?.let { btnOKImg.setImageResource(it) }
                } else {
                    btns[0]?.unSelectedResId?.let { btnOK.setBackgroundResource(it) }
                    tvs[0]?.unSelectedResId?.let {
                        context?.getColor(it)?.let { btnOKTv.setTextColor(it) }
                    }
                    imgs[0]?.unSelectedResId?.let { btnOKImg.setImageResource(it) }
                }
            }
            btnCancel -> {
                if (focus) {
                    btns[0]?.selectedResId?.let { btnCancel.setBackgroundResource(it) }
                    tvs[0]?.selectedResId?.let {
                        context?.getColor(it)?.let { btnCancelTv.setTextColor(it) }
                    }
                    imgs[0]?.selectedResId?.let { btnCancelImg.setImageResource(it) }
                } else {
                    btns[0]?.unSelectedResId?.let { btnCancel.setBackgroundResource(it) }
                    tvs[0]?.unSelectedResId?.let {
                        context?.getColor(it)?.let { btnCancelTv.setTextColor(it) }
                    }
                    imgs[0]?.unSelectedResId?.let { btnCancelImg.setImageResource(it) }
                }
            }
        }
    }

    override fun onClick(view: View) {
        when (view) {
            btnOK -> {
                dismiss()
                listener?.onSureClick(selectedData)
            }
            btnCancel -> {
                dismiss()
                listener?.onCancleClick()
            }
        }
    }

    private fun changeUI(index: Int = -1) {
        GlobalScope.launch(Main) {
            tvNum.text = selectedData.size.toString()
            if (index == -1) {
                adapter?.notifyDataSetChanged()
            } else {
                adapter?.notifyItemRangeChanged(index, 1)
            }
        }
    }

    override fun show() {
        super.show()
        progressBar.visibility = View.VISIBLE
        RemoteRepositoryImpl.getUserList()
            .subscribe(
                { t: List<User> ->
                    progressBar.visibility = View.GONE
                    val result: ArrayList<ItemOfUser> = ArrayList()
                    for (user in t) {
                        result.add(ItemOfUser(user))
                    }
                    data.removeAll(data)
                    data.addAll(result)
                    changeUI()
                }
            ) { throwable: Throwable? ->
                progressBar.visibility = View.GONE
                showShortToast(throwable?.message.toString())
            }
    }

    fun clear(){
        selectedData.clear()
        tvNum.text = selectedData.size.toString()
    }

}