package com.android.launcher3.common.anim

import android.view.View
import kotlin.math.abs

open class StackTransformer : BaseTransformer() {

    private val MIN_SCALE = 0.85f
    private val MIN_ALPHA = 0.5f

    override fun onTransform(page: View, position: Float) {
        val absPosition = abs(position)
        val pageWidth = page.width.toFloat()

        when {
            position < -1 -> {
                page.alpha = 0f
            }
            position <= 0 -> {
                page.alpha = 1f
                page.translationX = 0f
                page.scaleX = 1f
                page.scaleY = 1f
            }
            position <= 1 -> {
                val scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - absPosition)
                page.alpha = 1 - absPosition
                page.translationX = -pageWidth * position
                page.scaleX = scaleFactor
                page.scaleY = scaleFactor
            }
            else -> {
                page.alpha = 0f
            }
        }

        if (absPosition < -1 || absPosition > 1) {
            page.alpha = MIN_ALPHA
        }
    }
}

