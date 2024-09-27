
package com.android.launcher3.common.anim

import android.view.View
import kotlin.math.abs
import kotlin.math.pow

open class ZoomTransformer : BaseTransformer() {

    override fun onTransform(page: View, position: Float) {
        val scale = if (position < 0) position + 1f else abs(1f - position)
        page.scaleX = scale
        page.scaleY = scale
        page.pivotX = page.width * 0.5f
        page.pivotY = page.height * 0.5f
        val alpha = if (position < -1f || position > 1f) {
            0f
        } else {
            1f - (scale - 1f).pow(2)
        }
        page.alpha = alpha
    }
}
