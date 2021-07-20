package com.jason.colorwheel.feature

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jason.colorwheel.model.ColorTemplate
import com.jason.colorwheel.utils.ColorSelectState
import com.jason.colorwheel.utils.DefaultColor

class MainViewModel : ViewModel() {

    private val _colorWheelColor = MutableLiveData<Int>()
    val colorWheelColor: LiveData<Int>
        get() = _colorWheelColor

    private val _colorsTemplate = MutableLiveData<MutableList<ColorTemplate>>()
    val colorsTemplate: LiveData<MutableList<ColorTemplate>>
        get() = _colorsTemplate

    init {
        val list = mutableListOf(
            ColorTemplate(DefaultColor.TEAL, ColorSelectState.SELECTED),
            ColorTemplate(DefaultColor.GREEN, ColorSelectState.UNSELECTED),
            ColorTemplate(DefaultColor.ORANGE, ColorSelectState.UNSELECTED)
        )
        _colorsTemplate.postValue(list)
    }

    fun onColorWheelChanged(color: Int) {
        _colorWheelColor.postValue(color)
    }

    fun onDefaultColorSelected(color: ColorTemplate) {
        val list = _colorsTemplate.value ?: mutableListOf()
        list.forEach {
            it.state = if (it == color) ColorSelectState.SELECTED else ColorSelectState.UNSELECTED
        }
        _colorsTemplate.postValue(list)
    }
}
