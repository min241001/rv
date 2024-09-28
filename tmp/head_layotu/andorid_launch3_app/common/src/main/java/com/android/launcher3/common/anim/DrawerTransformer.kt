package com.android.launcher3.common.anim

import android.view.View

open class DrawerTransformer : BaseTransformer() {

    override fun onTransform(page: View, position: Float) {
        if (position <= 0) page.translationX = 0f
        else if (position <= 1) page.translationX = -page.width / 2 * position
    }
}