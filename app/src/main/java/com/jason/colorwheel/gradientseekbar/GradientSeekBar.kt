package com.jason.colorwheel.gradientseekbar

import android.animation.ArgbEvaluator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.drawable.GradientDrawable
import android.os.Parcelable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.jason.colorwheel.R
import com.jason.colorwheel.extensions.dp
import com.jason.colorwheel.extensions.sp
import com.jason.colorwheel.thumb.ThumbDrawable
import com.jason.colorwheel.utils.ensureWithinRange
import com.jason.colorwheel.utils.isTap
import kotlin.math.roundToInt

open class GradientSeekBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val gradientColors = IntArray(2)
    private val thumbDrawable = ThumbDrawable()
    private val gradientDrawable = GradientDrawable()
    private val argbEvaluator = ArgbEvaluator()

    private lateinit var orientationStrategy: OrientationStrategy
    private var downX = 0f
    private var downY = 0f
    private var isDrawText = false

    var startColor
        get() = gradientColors[0]
        set(color) {
            setColors(start = color)
        }

    var endColor
        get() = gradientColors[1]
        set(color) {
            setColors(end = color)
        }

    var offset = 0f
        set(offset) {
            field = ensureOffsetWithinRange(offset)
            calculateArgb()
        }

    var barSize = 0
        set(width) {
            field = width
            requestLayout()
        }

    var cornersRadius = 0f
        set(radius) {
            field = radius
            invalidate()
        }

    var orientation = Orientation.VERTICAL
        set(orientation) {
            field = orientation
            orientationStrategy = createOrientationStrategy()
            requestLayout()
        }

    var thumbColor
        get() = thumbDrawable.thumbColor
        set(value) {
            thumbDrawable.thumbColor = value
            invalidate()
        }

    var thumbStrokeColor
        get() = thumbDrawable.strokeColor
        set(value) {
            thumbDrawable.strokeColor = value
            invalidate()
        }

    var thumbColorCircleScale
        get() = thumbDrawable.colorCircleScale
        set(value) {
            thumbDrawable.colorCircleScale = value
            invalidate()
        }

    var thumbRadius
        get() = thumbDrawable.radius
        set(radius) {
            thumbDrawable.radius = radius
            requestLayout()
        }

    var argb = 0
        private set

    var colorChangeListener: ((Float, Int) -> Unit)? = null

    var interceptTouchEvent = true

    init {
        parseAttributes(context, attrs)
    }

    private fun parseAttributes(context: Context, attrs: AttributeSet?) {
        val array = context.obtainStyledAttributes(
            attrs,
            R.styleable.GradientSeekBar,
            0,
            R.style.GradientSeekBarDefaultStyle
        )
        readThumbColor(array)
        readThumbStrokeColor(array)
        readColorCircleScale(array)
        readThumbRadius(array)
        readBarSize(array)
        readCornerRadius(array)
        readOffset(array)
        readOrientation(array)
        readGradientColors(array)
        readDrawText(array)
        array.recycle()
    }

    private fun readThumbColor(array: TypedArray) {
        thumbColor = array.getColor(R.styleable.GradientSeekBar_tb_thumbColor, 0)
    }

    private fun readThumbStrokeColor(array: TypedArray) {
        thumbStrokeColor = array.getColor(R.styleable.GradientSeekBar_tb_thumbStrokeColor, 0)
    }

    private fun readColorCircleScale(array: TypedArray) {
        thumbColorCircleScale =
            array.getFloat(R.styleable.GradientSeekBar_tb_thumbColorCircleScale, 0f)
    }

    private fun readThumbRadius(array: TypedArray) {
        thumbRadius = array.getDimensionPixelSize(R.styleable.GradientSeekBar_tb_thumbRadius, 0)
    }

    private fun readBarSize(array: TypedArray) {
        barSize = array.getDimensionPixelSize(R.styleable.GradientSeekBar_asb_barSize, 0)
    }

    private fun readCornerRadius(array: TypedArray) {
        cornersRadius = array.getDimension(R.styleable.GradientSeekBar_gsb_barCornersRadius, 0f)
    }

    private fun readOffset(array: TypedArray) {
        offset = array.getFloat(R.styleable.GradientSeekBar_gsb_offset, 0f)
    }

    private fun readOrientation(array: TypedArray) {
        orientation =
            Orientation.values()[array.getInt(R.styleable.GradientSeekBar_gsb_orientation, 0)]
    }

    private fun readDrawText(array: TypedArray) {
        isDrawText = array.getBoolean(R.styleable.GradientSeekBar_gsb_draw_text, false)
    }

    private fun readGradientColors(array: TypedArray) {
        setColors(
            array.getColor(R.styleable.GradientSeekBar_gsb_startColor, Color.TRANSPARENT),
            array.getColor(R.styleable.GradientSeekBar_gsb_endColor, Color.BLACK)
        )
    }

    private fun createOrientationStrategy(): OrientationStrategy {
        return when (orientation) {
            Orientation.VERTICAL -> VerticalStrategy()
            Orientation.HORIZONTAL -> HorizontalStrategy()
        }
    }

    fun setColors(start: Int = startColor, end: Int = endColor) {
        updateGradientColors(start, end)
        calculateArgb()
    }

    private fun updateGradientColors(start: Int, end: Int) {
        gradientColors[0] = start
        gradientColors[1] = end
        gradientDrawable.colors = gradientColors
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val dimens = orientationStrategy.measure(this, widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(dimens.width(), dimens.height())
    }

    override fun onDraw(canvas: Canvas) {
        drawGradientRect(canvas)
        drawThumb(canvas)
    }

    private fun drawGradientRect(canvas: Canvas) {
        gradientDrawable.orientation = orientationStrategy.gradientOrientation
        gradientDrawable.bounds = orientationStrategy.getGradientBounds(this)
        gradientDrawable.cornerRadius = cornersRadius
        gradientDrawable.draw(canvas)
    }

    private fun drawThumb(canvas: Canvas) {
        val coordinates = orientationStrategy.getThumbPosition(this, gradientDrawable.bounds)
        thumbDrawable.indicatorColor = argb
        thumbDrawable.setCoordinates(coordinates.x, coordinates.y)
        thumbDrawable.draw(canvas)
        drawText(canvas, coordinates)

    }

    private fun drawText(canvas: Canvas, coordinates: PointF) {
        if (isDrawText) {
            canvas.drawText(
                "${(offset * 100).roundToInt()}%",
                coordinates.x,
                coordinates.y - 24.dp,
                Paint().apply {
                    color = Color.WHITE
                    textSize = 12.sp.toFloat()
                    textAlign = Paint.Align.CENTER
                })
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> onActionDown(event)
            MotionEvent.ACTION_MOVE -> calculateOffsetOnMotionEvent(event)
            MotionEvent.ACTION_UP -> {
                calculateOffsetOnMotionEvent(event)
                if (isTap(event, downX, downY)) performClick()
            }
        }

        return true
    }

    private fun onActionDown(event: MotionEvent) {
        parent.requestDisallowInterceptTouchEvent(interceptTouchEvent)
        calculateOffsetOnMotionEvent(event)
        downX = event.x
        downY = event.y
    }

    override fun performClick() = super.performClick()

    private fun calculateOffsetOnMotionEvent(event: MotionEvent) {
        offset = orientationStrategy.getOffset(this, event, gradientDrawable.bounds)
    }

    private fun calculateArgb() {
        argb = argbEvaluator.evaluate(offset, startColor, endColor) as Int
        fireListener()
        invalidate()
    }

    private fun fireListener() {
        colorChangeListener?.invoke(offset, argb)
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        val thumbState = thumbDrawable.saveState()
        return GradientSeekBarState(superState, this, thumbState)
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state is GradientSeekBarState) {
            super.onRestoreInstanceState(state.superState)
            readGradientSeekBarState(state)
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    private fun readGradientSeekBarState(state: GradientSeekBarState) {
        updateGradientColors(state.startColor, state.endColor)
        offset = state.offset
        barSize = state.barSize
        cornersRadius = state.cornerRadius
        orientation = Orientation.values()[state.orientation]
        interceptTouchEvent = state.interceptTouchEvent
        thumbDrawable.restoreState(state.thumbState)
    }

    private fun ensureOffsetWithinRange(offset: Float) = ensureWithinRange(offset, 0f, 1f)

    enum class Orientation { VERTICAL, HORIZONTAL }
}

fun GradientSeekBar.resetColor(color: Int) {
    this.setColors(color, Color.BLACK)
}
