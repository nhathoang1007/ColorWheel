package com.jason.colorwheel.feature

import com.jason.colorwheel.feature.adapter.TemplateColorAdapter

interface MainView {
    val adapter: TemplateColorAdapter
    val viewModel: MainViewModel
    fun brightnessChanged(offset: Float, color: Int)
}