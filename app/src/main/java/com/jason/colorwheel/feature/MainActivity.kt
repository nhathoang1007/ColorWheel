package com.jason.colorwheel.feature

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.jason.colorwheel.R
import com.jason.colorwheel.databinding.ActivityMainBinding
import com.jason.colorwheel.feature.adapter.TemplateColorAdapter
import com.jason.colorwheel.utils.ColorSelectState

class MainActivity : AppCompatActivity(), MainView {

    override val adapter: TemplateColorAdapter by lazy {
        TemplateColorAdapter {
            viewModel.onDefaultColorSelected(it)
        }
    }

    override val viewModel: MainViewModel by lazy {
        MainViewModel()
    }

    private lateinit var dataBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        dataBinding.lifecycleOwner = this
        initView()
        initViewModel()
    }

    private fun initView() {
        dataBinding.apply {
            iView = this@MainActivity
            colorWheel.colorChangeListener = {
                viewModel.onColorWheelChanged(it)
            }
        }
    }

    private fun initViewModel() {
        viewModel.apply {
            colorsTemplate.observe(this@MainActivity, {
                adapter.updateData(it)
                it.find { f -> f.state == ColorSelectState.SELECTED }?.let { selected ->
                    dataBinding.colorWheel.setColor(selected.color.color)
                }
            })
        }
    }

    override fun brightnessChanged(offset: Float, color: Int) {
        dataBinding.colorWheel.onBrightnessChanged(offset)
    }
}