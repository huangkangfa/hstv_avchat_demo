package com.android.hsdemo.ui.main.fragments

import com.android.baselib.base.BaseFragment
import com.android.hsdemo.BR
import com.android.hsdemo.R
import com.android.hsdemo.databinding.FragmentAddressBookBinding
import com.android.hsdemo.ui.main.vm.VMFAddressBook

class FragmentAddressBook : BaseFragment<VMFAddressBook, FragmentAddressBookBinding>() {

    override fun getLayoutId(): Int = R.layout.fragment_address_book

    override fun getVariableId(): Int = BR.vm

    override fun afterCreate() {

    }
}