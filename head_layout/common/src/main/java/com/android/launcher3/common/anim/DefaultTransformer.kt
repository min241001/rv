package com.android.launcher3.common.anim

import android.view.View

open class DefaultTransformer : BaseTransformer() {

    public override val isPagingEnabled: Boolean
        get() = true

    override fun onTransform(page: View, position: Float) {}

}
