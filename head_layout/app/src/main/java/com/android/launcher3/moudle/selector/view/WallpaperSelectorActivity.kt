package com.android.launcher3.moudle.selector.view

import androidx.lifecycle.lifecycleScope
import com.android.launcher3.common.base.WallpaperUtil
import com.android.launcher3.common.bean.SelectorBean
import com.android.launcher3.common.utils.BitmapUtils
import com.android.launcher3.common.utils.ThreadPoolUtils
import com.android.launcher3.moudle.selector.presenter.CommomSelectorPresenter
import com.android.launcher3.moudle.selector.presenter.WallpaperSelectorPresenter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WallpaperSelectorActivity : CommomSelectorActivity<WallpaperSelectorPresenter>() {

    override fun createPresenter(): CommomSelectorPresenter {
        return WallpaperSelectorPresenter()
    }

    override fun onItemClickToSetting(bean: SelectorBean, position: Int) {
        super.onItemClickToSetting(bean, position)
        ThreadPoolUtils.getExecutorService().execute {
            try {
                WallpaperUtil.setWallpaper(BitmapUtils.drawableToBitmap(bean.thumb))
                lifecycleScope.launch {
                    delay(250)
                    forward()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}