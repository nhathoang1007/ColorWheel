package com.jason.colorwheel.binding

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


@BindingAdapter("initLinearLayoutManager")
fun RecyclerView.initLinearLayoutManager(orientation: Int) {
    this.layoutManager = LinearLayoutManager(context, orientation, false)
}
@BindingAdapter("initAdapter")
fun RecyclerView.initAdapter(adapter: RecyclerView.Adapter<*>) {
    this.adapter = adapter
}