package com.jason.colorwheel.binding

import androidx.databinding.BindingAdapter
import com.jason.colorwheel.feature.MainView
import com.jason.colorwheel.gradientseekbar.GradientSeekBar
import com.jason.colorwheel.gradientseekbar.resetColor

@BindingAdapter("resetColor")
fun GradientSeekBar.bindResetColor(argb: Int) {
    this.resetColor(argb)
}

@BindingAdapter("brightnessChanged")
fun GradientSeekBar.bindBrightnessChangedListener(listener: MainView) {
    this.colorChangeListener = { offset, argb -> listener.brightnessChanged(offset, argb) }
}

