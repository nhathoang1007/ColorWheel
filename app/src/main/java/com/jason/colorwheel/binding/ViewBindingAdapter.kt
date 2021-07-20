package com.jason.colorwheel.binding

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter


@BindingAdapter("backgroundColor")
fun View.backgroundColor(color: Int) {
    this.setBackgroundColor(color)
}

@BindingAdapter("backgroundColor")
fun View.backgroundColor(color: String) {
    this.setBackgroundColor(Color.parseColor(color))
}

@BindingAdapter("backgroundTintColor")
fun AppCompatImageView.backgroundTintColor(color: String) {
    this.imageTintList = ColorStateList.valueOf(Color.parseColor(color))
}