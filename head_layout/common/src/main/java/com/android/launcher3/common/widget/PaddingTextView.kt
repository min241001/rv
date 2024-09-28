package com.android.launcher3.common.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet

class PaddingTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : androidx.appcompat.widget.AppCompatTextView(context, attrs, defStyleAttr) {

    private var minRect: Rect? = null

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (minRect == null) {
            minRect = Rect()
        }
        paint.getTextBounds(text.toString(), 0, text.length, minRect)
        val width = minRect!!.width()
        val height = minRect!!.height()
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        val text = text.toString()
        val left = minRect?.left ?: 0
        val top = minRect?.top ?: 0
        val paint = paint
        paint.color = currentTextColor
        canvas.drawText(text, (-left).toFloat(), (-top).toFloat(), paint)
    }
}
