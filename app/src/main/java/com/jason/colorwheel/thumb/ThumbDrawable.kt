package com.jason.colorwheel.thumb

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.jason.colorwheel.utils.ensureWithinRange

internal class ThumbDrawable {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { strokeWidth = 1f }
    private var x = 0f
    private var y = 0f

    var indicatorColor = 0
    var strokeColor = 0
    var thumbColor = 0
    var radius = 0
    var isShowShadow = false

    var colorCircleScale = 0f
        set(value) { field = ensureWithinRange(value, 0f, 1f) }

    fun setCoordinates(x: Float, y: Float) {
        this.x = x
        this.y = y
    }

    fun draw(canvas: Canvas) {
        drawThumb(canvas)
        drawStroke(canvas)
        drawColorIndicator(canvas)

    }

    private fun drawThumb(canvas: Canvas) {
        paint.color = thumbColor
        paint.style = Paint.Style.FILL
        val clone = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = thumbColor
            style = Paint.Style.FILL
            strokeWidth = 1f
            if (isShowShadow) {
                setShadowLayer(20f, -0f, -0f, Color.GRAY)
            }
        }
        canvas.drawCircle(x, y, radius.toFloat(), clone)
    }

    private fun drawStroke(canvas: Canvas) {
        val strokeCircleRadius = radius - paint.strokeWidth / 2f

        paint.color = strokeColor
        paint.style = Paint.Style.STROKE
        canvas.drawCircle(x, y, strokeCircleRadius, paint)
    }

    private fun drawColorIndicator(canvas: Canvas) {
        val colorIndicatorCircleRadius = radius * colorCircleScale

        paint.color = indicatorColor
        paint.style = Paint.Style.FILL
        canvas.drawCircle(x, y, colorIndicatorCircleRadius, paint)
    }

    fun restoreState(state: ThumbDrawableState) {
        radius = state.radius
        thumbColor = state.thumbColor
        strokeColor = state.strokeColor
        colorCircleScale = state.colorCircleScale
    }

    fun saveState() = ThumbDrawableState(this)
}
