package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes

private enum class ButtonState(val label: Int) {
    NORMAL(R.string.normal_state),
    LOADING(R.string.loading_state),
    COMPLETED(R.string.completed_state);
}

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var buttonState = ButtonState.NORMAL

    // Button Custom attribute
    private var buttonNormal = 0
    private var buttonLoading = 0
    private var buttonCompleted = 0

    // Progress
    private var progress = 0.0

    private val paint = Paint().apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create("", Typeface.BOLD)
    }

    init {
        isClickable = true

        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            buttonNormal = getColor(R.styleable.LoadingButton_button_normal, 0)
            buttonLoading = getColor(R.styleable.LoadingButton_button_loading, 0)
            buttonCompleted = getColor(R.styleable.LoadingButton_button_completed, 0)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        drawButton(canvas)
        drawText(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    override fun performClick(): Boolean {
        super.performClick()
        buttonState = ButtonState.LOADING

        if (buttonState == ButtonState.LOADING) {
            animation()
        }

        invalidate()
        return true
    }

    private fun animation() {
        val valueAnimator = ValueAnimator.ofFloat(0f, measuredWidth.toFloat())

        val updateListener = ValueAnimator.AnimatorUpdateListener { animated ->
            progress = (animated.animatedValue as Float).toDouble()
            invalidate()
        }

        valueAnimator.duration = 3000
        valueAnimator.addUpdateListener(updateListener)
        valueAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                buttonState = ButtonState.NORMAL
            }
        })
        valueAnimator.start()
    }

    private fun drawButton(canvas: Canvas?) {
        paint.color = buttonNormal
        canvas?.drawRect(
            0f, 0f,
            width.toFloat(), height.toFloat(), paint
        )

        if (buttonState == ButtonState.LOADING) {
            paint.color = buttonLoading
            canvas?.drawRect(
                0f, 0f,
                (width * (progress / 100)).toFloat(), height.toFloat(), paint
            )

            val rect = RectF(0f, 0f, 80f, 80f)
            canvas?.save()
            canvas?.translate((width / 2 + 220).toFloat(), 40f)
            paint.color = buttonCompleted
            canvas?.drawArc(rect, 0f, (360 * (progress / 100)).toFloat(), true, paint)
            canvas?.restore()
        }
    }

    private fun drawText(canvas: Canvas?) {
        paint.color = Color.WHITE
        val label = when (buttonState) {
            ButtonState.NORMAL -> context.getString(R.string.download)
            ButtonState.LOADING -> context.getString(R.string.loading)
            ButtonState.COMPLETED -> context.getString(R.string.download)
        }

        canvas?.drawText(label, (width / 2).toFloat(), ((height + 30) / 2).toFloat(), paint)
    }
}