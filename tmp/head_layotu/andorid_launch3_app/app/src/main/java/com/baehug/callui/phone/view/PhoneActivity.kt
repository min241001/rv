package com.baehug.callui.phone.view

import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.android.launcher3.R
import com.baehug.callui.dialog.RingPermissionDialog
import com.baehug.callui.phone.manager.CallerShowManager
import com.baehug.callui.phone.manager.PhoneCallManager
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.runtime.Permission
import kotlinx.android.synthetic.main.activity_phone.*

/**
 * Author : yanyong
 * Date : 2024/7/30
 * Details : 电话功能权限管理类
 */
@RequiresApi(Build.VERSION_CODES.M)
class PhoneActivity : AppCompatActivity() {
    var dialog: RingPermissionDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone)
        requestPermission()
        initView()
        initPersimmon()
    }

    private fun initView() {
    }

    private fun initPersimmon() {
        tv_set_ring.setOnClickListener {
            dialog = RingPermissionDialog(this)
            dialog?.getRingVideoPermission(this, object : CallerShowManager.OnPerManagerListener {
                override fun onGranted() {
                    val content: String = dialog!!.updateRingPerContent()
                    if (TextUtils.isEmpty(content)) {
                        Toast.makeText(applicationContext, "权限全部同意，正在设置视频铃声", Toast.LENGTH_SHORT).show()
                        return
                    }
                    dialog?.show()
                }

                override fun onDenied() {
                    Toast.makeText(applicationContext, "请至权限管理同意权限，才能设置视频铃声", Toast.LENGTH_SHORT).show()
                }

            })
        }
        bt_set_sys_caller.setOnClickListener {
            if (PhoneCallManager.instance.isDefaultPhoneCallApp()) {
                Toast.makeText(applicationContext, "已经是默认电话应用...", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            PhoneCallManager.instance.setDefaultPhoneCallApp()
        }
    }

    override fun onResume() {
        super.onResume()
        updateRingPerContent(dialog)
    }

    private fun updateRingPerContent(ringPerDialog: RingPermissionDialog?) {
        ringPerDialog?.let {
            if (it.isShowing) {
                val content: String = it.updateRingPerContent()
                if (TextUtils.isEmpty(content)) {
                    it.dismiss()
                    Toast.makeText(applicationContext,"设置来电秀成功",Toast.LENGTH_SHORT).show()
                    // 暂时关闭 保存到本地功能，统一走ijk Video cache逻辑
//                    downloadFile(FloatingWindowManager.instance.mp4Url)
                }
            }
        }
    }

    /**
     * 请求权限
     */
    private fun requestPermission() {
        AndPermission.with(this)
                .runtime()
                .permission(
                        Permission.Group.PHONE,
                        Permission.Group.LOCATION
                )
                .onGranted {
                    Toast.makeText(applicationContext, "权限同意", Toast.LENGTH_SHORT).show()
                }.onDenied {
                    Toast.makeText(applicationContext, "权限拒绝", Toast.LENGTH_SHORT).show()
                }.start()

    }

}
