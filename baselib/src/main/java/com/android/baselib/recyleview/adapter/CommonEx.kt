package com.android.baselib.recyleview.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

infix fun ViewGroup.inflate(layoutResId: Int): View = LayoutInflater.from(context).inflate(layoutResId, this, false)