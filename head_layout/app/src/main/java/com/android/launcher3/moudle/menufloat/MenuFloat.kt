package com.android.launcher3.moudle.menufloat

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.android.launcher3.R
import com.android.launcher3.common.constant.AppPackageConstant.CAMERA_ACTION
import com.android.launcher3.common.constant.AppPackageConstant.MB_ACTION
import com.android.launcher3.common.utils.AppLauncherUtils
import com.android.launcher3.common.utils.DensityUtils
import com.android.launcher3.common.utils.ToastUtils
import com.android.launcher3.moudle.light.LightActivity
import com.android.launcher3.moudle.shortcut.presenter.PullDownPresenter
import com.android.launcher3.moudle.touchup.bean.AppInfoBean
import com.android.launcher3.receiver.FloatReceiver
import com.android.launcher3.utils.KeyCodeUtil
import com.lzf.easyfloat.EasyFloat
import com.lzf.easyfloat.enums.ShowPattern
import com.lzf.easyfloat.enums.SidePattern


class MenuFloat {
    var pullDownPresenter: PullDownPresenter? = null
    var context: Activity? = null
    var menuData: MutableList<AppInfoBean>? = null

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: MenuFloat? = null

        const val MENU_FLOAT: String = "MENU_FLOAT"
        const val MENU_FLOAT_LEFT: String = "MENU_FLOAT_LEFT"
        const val MENU_FLOAT_RIGHT: String = "MENU_FLOAT_RIGHT"

        const val FLOAT_0: String = "FLOAT_0"
        const val FLOAT_1: String = "FLOAT_1"
        const val FLOAT_2: String = "FLOAT_2"

        @JvmStatic
        @Synchronized
        fun getInstance(): MenuFloat {
            if (instance == null) {
                instance = MenuFloat()
            }
            return instance!!
        }

        @JvmStatic
        @DrawableRes
        fun getResourceId(tag: String?): Int {
            return when (tag) {
                "administrator" -> R.drawable.manager
                "screenshot" -> R.drawable.screenshot
                "cleanup" -> R.drawable.cleanup
                "camera" -> R.drawable.camera
                "brightness" -> R.drawable.brightness
                "sms" -> R.drawable.message
                "sound" -> R.drawable.voice
                else -> R.drawable.stopwatch
            }
        }
    }

    fun initData(): MutableList<AppInfoBean> {
        context ?: return mutableListOf()
        val sharedPreferences =
            context!!.getSharedPreferences(FloatReceiver.APP_FLOAT, Context.MODE_PRIVATE)
        val appIdOne = sharedPreferences.getString(FLOAT_0, "")
        val appIdTwo = sharedPreferences.getString(FLOAT_1, "")
        val appIdThree = sharedPreferences.getString(FLOAT_2, "")

        if (appIdOne.isNullOrEmpty() || appIdTwo.isNullOrEmpty() || appIdThree.isNullOrEmpty()) {
            return mutableListOf<AppInfoBean>().apply {
                val bean0 = AppInfoBean()
                bean0.resourceId = R.drawable.manager
                bean0.app_name = "administrator"
                val bean1 = AppInfoBean()
                bean1.resourceId = R.drawable.screenshot
                bean1.app_name = "screenshot"
                val bean2 = AppInfoBean()
                bean2.resourceId = R.drawable.cleanup
                bean2.app_name = "cleanup"
                add(bean0)
                add(bean1)
                add(bean2)
            }
        } else {
            return mutableListOf<AppInfoBean>().apply {
                val bean0 = AppInfoBean()
                bean0.resourceId = getResourceId(appIdOne)
                bean0.app_name = appIdOne
                val bean1 = AppInfoBean()
                bean1.resourceId = getResourceId(appIdTwo)
                bean1.app_name = appIdTwo
                val bean2 = AppInfoBean()
                bean2.resourceId = getResourceId(appIdThree)
                bean2.app_name = appIdThree
                add(bean0)
                add(bean1)
                add(bean2)
            }
        }
    }

    fun disMissAllFloat() {
        EasyFloat.dismiss(MENU_FLOAT)
        EasyFloat.dismiss(MENU_FLOAT_LEFT)
        EasyFloat.dismiss(MENU_FLOAT_RIGHT)
    }

    fun showMenuFloat(context: Context) {
        menuData?.isEmpty() ?: return
        EasyFloat.with(context.applicationContext)
            .setTag(MENU_FLOAT)
            .setShowPattern(ShowPattern.ALL_TIME)
            .setSidePattern(SidePattern.RESULT_HORIZONTAL)
            .setImmersionStatusBar(true)
            .setGravity(Gravity.START, 0, 100)
            .setLayout(R.layout.float_view) { rootView ->
                rootView.findViewById<ImageView>(R.id.iv_menu).run {
                    setOnClickListener {
                        val location = IntArray(2)
                        rootView.getLocationOnScreen(location)
                        val pointX = location[0]
                        val pointY = location[1]
                        if (location[0] > 10) {
                            EasyFloat.hide(MENU_FLOAT)
                            showRightMenu(pointX, pointY, context)
                        } else {
                            EasyFloat.hide(MENU_FLOAT)
                            showLeftMenu(pointX, pointY, context)
                        }
                    }
                }
            }.registerCallback {
                touchEvent { view, motionEvent ->
                }
                dragEnd { action ->
                }
            }
            .show()
    }

    fun updateMenuFloat(beans: MutableList<AppInfoBean>) {
        if (EasyFloat.isShow(MENU_FLOAT_LEFT)) {
            EasyFloat.dismiss(MENU_FLOAT_LEFT)
        } else if (EasyFloat.isShow(MENU_FLOAT_RIGHT)) {
            EasyFloat.dismiss(MENU_FLOAT_RIGHT)
        }

        menuData?.let {
            it.clear()
            it.addAll(beans)
        } ?: run {
            menuData = beans.toMutableList()
        }
    }


    private fun showLeftMenu(pointX: Int, pointY: Int, context: Context) {
        val radius = context.resources.getDimension(R.dimen._50dp)
        val menu_height_half = context.resources.getDimension(R.dimen._18dp)
        val center_height_half = context.resources.getDimension(R.dimen._14dp)
        EasyFloat.with(context.applicationContext)
            .setTag(MENU_FLOAT_LEFT)
            .setShowPattern(ShowPattern.ALL_TIME)
            .setSidePattern(SidePattern.DEFAULT)
            .setImmersionStatusBar(true)
            .setLocation(
                pointX,
                pointY - DensityUtils.dip2px(
                    (radius + menu_height_half - center_height_half),
                    context
                )
            )
            .setDragEnable(false)
            .setLayout(R.layout.float_menu_left) {
                menuData?.forEachIndexed { index, item ->
                    when (index) {
                        0 -> {
                            it.findViewById<ImageView>(R.id.left_iv_menu_one).run {
                                tag = item.app_name
                                setImageResource(item.resourceId)
                                setOnClickListener {
                                    clickMenu(this, context)
                                    EasyFloat.dismiss(MENU_FLOAT_LEFT)
                                }
                            }
                        }

                        1 -> {
                            it.findViewById<ImageView>(R.id.left_iv_menu_two).run {
                                tag = item.app_name
                                setImageResource(item.resourceId)
                                setOnClickListener {
                                    clickMenu(this, context)
                                    EasyFloat.dismiss(MENU_FLOAT_LEFT)
                                }
                            }
                        }

                        2 -> {
                            it.findViewById<ImageView>(R.id.left_iv_menu_three).run {
                                tag = item.app_name
                                setImageResource(item.resourceId)
                                setOnClickListener {
                                    clickMenu(this, context)
                                    EasyFloat.dismiss(MENU_FLOAT_LEFT)
                                }
                            }

                        }
                    }
                }
                it.findViewById<ImageView>(R.id.left_iv_menu_center).run {
                    setOnClickListener {
                        EasyFloat.dismiss(MENU_FLOAT_LEFT)
                    }
                }
            }
            .registerCallback {
                dismiss {
                    EasyFloat.show(MENU_FLOAT)
                }
            }
            .show()
    }

    private fun showRightMenu(pointX: Int, pointY: Int, context: Context) {
        val radius = context.resources.getDimension(R.dimen._50dp)
        val menu_height_half = context.resources.getDimension(R.dimen._20dp)
        val center_height_half = context.resources.getDimension(R.dimen._14dp)
        EasyFloat.with(context.applicationContext)
            .setTag(MENU_FLOAT_RIGHT)
            .setShowPattern(ShowPattern.ALL_TIME)
            .setSidePattern(SidePattern.DEFAULT)
            .setImmersionStatusBar(true)
            .setLocation(
                pointX - DensityUtils.dip2px(
                    radius + menu_height_half - center_height_half,
                    context
                ),
                pointY - DensityUtils.dip2px(
                    radius + menu_height_half - center_height_half,
                    context
                )
            )
            .setDragEnable(false)
            .setLayout(R.layout.float_menu_right) {
                menuData?.forEachIndexed { index, item ->
                    when (index) {
                        0 -> {
                            it.findViewById<ImageView>(R.id.right_iv_menu_one).run {
                                tag = item.app_name
                                setImageResource(item.resourceId)
                                setOnClickListener {
                                    clickMenu(this, context)
                                    EasyFloat.dismiss(MENU_FLOAT_RIGHT)
                                }
                            }
                        }

                        1 -> {
                            it.findViewById<ImageView>(R.id.right_iv_menu_two).run {
                                tag = item.app_name
                                setImageResource(item.resourceId)
                                setOnClickListener {
                                    clickMenu(this, context)
                                    EasyFloat.dismiss(MENU_FLOAT_RIGHT)
                                }
                            }
                        }

                        2 -> {
                            it.findViewById<ImageView>(R.id.right_iv_menu_three).run {
                                tag = item.app_name
                                setImageResource(item.resourceId)
                                setOnClickListener {
                                    clickMenu(this, context)
                                    EasyFloat.dismiss(MENU_FLOAT_RIGHT)
                                }
                            }
                        }
                    }
                }
                it.findViewById<ImageView>(R.id.right_iv_menu_center).run {
                    setOnClickListener {
                        EasyFloat.dismiss(MENU_FLOAT_RIGHT)
                    }
                }
            }
            .registerCallback {
                dismiss {
                    EasyFloat.show(MENU_FLOAT)
                }
            }
            .show()
    }

    private fun clickMenu(view: View, mContext: Context) {
//        if (!BaseUtil.continuousClicks()) {
//            return
//        }
        when (view.tag) {
            "administrator" -> {
                ToastUtils.show("管理员")
            }

            "screenshot" -> {
                KeyCodeUtil.sendKeyCode(KeyEvent.KEYCODE_SYSRQ)
            }

            "cleanup" -> {
                if (pullDownPresenter == null) {
                    pullDownPresenter = PullDownPresenter(context as Activity)
                }
                pullDownPresenter!!.clearCache()
            }

            "camera" -> {
                val intent = Intent()
                intent.setAction(CAMERA_ACTION)
                context?.startActivity(intent)
            }

            "flashlight" -> {}
            "brightness" -> {
                val intent = Intent(context, LightActivity::class.java)
                context?.startActivity(intent)
            }

            "sms" -> {
//                val intent = Intent()
//                intent.setAction("com.baehug.sms")
//                context.startActivity(intent)
                AppLauncherUtils.launchApp(context, "com.baehug.sms")
            }

            "sound" -> {
                val intent = Intent()
                intent.setAction("com.baehug.settings.sound")
                context?.startActivity(intent)
            }

            "stopwatch" -> {
                val intent = Intent()
                intent.setAction(MB_ACTION)
                context?.startActivity(intent)
            }
        }
    }
}
