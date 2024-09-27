package com.android.launcher3.common.utils

import android.app.Activity
import android.app.ActivityManager
import android.app.KeyguardManager
import android.content.ComponentName
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.android.launcher3.common.CommonApp
import com.android.launcher3.common.constant.AppPackageConstant
import com.android.launcher3.common.constant.SettingsConstant.*
import kotlin.math.roundToInt

object ShortCutUtils {

    private val contentResolver: ContentResolver
        get() = CommonApp.getInstance().contentResolver

    //获取亮度
    fun getSystemBrightness(): Int {
        try {
            return Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS) * 100 / 255
        } catch (e: Settings.SettingNotFoundException) {
            e.printStackTrace();
        }
        return 1
    }

    //设置亮度
    fun setSystemBrightness(brightness: Int) = Settings.System.putInt(
        contentResolver,
        Settings.System.SCREEN_BRIGHTNESS,
        brightness * 255 / 100
    )

    //获取音大小
    fun getSound(): Int {
        val mAudioManager =
            CommonApp.getInstance().getSystemService(AppCompatActivity.AUDIO_SERVICE) as AudioManager
        val max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_RING)
        val current = mAudioManager.getStreamVolume(AudioManager.STREAM_RING)
        return (current.toFloat() / max.toFloat() * 100).roundToInt()
    }

    //设置声音大小
    fun setSound(context : Context, mNowVolume:Int){
        val mAudioManager = context.getSystemService(AppCompatActivity.AUDIO_SERVICE) as AudioManager
        val max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_RING)
        val volume = if (mNowVolume < 5){
            0
        }else{
            if ((max * mNowVolume / 100).toDouble().roundToInt() == 0){
                1
            }else{
                (max * mNowVolume / 100).toDouble().roundToInt()
            }
        }
        mAudioManager.setStreamVolume(AudioManager.STREAM_RING, volume , AudioManager.FLAG_PLAY_SOUND)
    }

    //获取夜间模式 1夜间模式
    fun getNightMode() : Int =
        Settings.System.getInt(contentResolver, NIGHT_MODE,0)


    //设置夜间模式
    fun setNightMode( mode : Int){
        startService(CommonApp.getInstance())
        Settings.System.putInt(contentResolver, NIGHT_MODE , mode)
    }


    //获取是否设置闹钟 1设置
    fun getAlarmClock() =
        Settings.System.getInt(contentResolver, ALARM_CLOCK , 0)


    //是否设置锁屏密码
    fun getKeyguard() : Boolean{
        val keyguardManager = CommonApp.getInstance().getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        return keyguardManager.isKeyguardSecure
    }

    //关机
    fun shutDown(){
        ShutdownUtils.shutdown()
    }

    //跳转风格
    fun startWorkspaces(){
        AppLauncherUtils.launchAppAction(CommonApp.getInstance(),SPACES_ACTION)
    }

    //跳转锁屏
    fun startChooseLock(activity : Activity){
        val intent = Intent(Intent.ACTION_VIEW)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            intent.component =
                ComponentName("com.android.settings", "com.android.settings.ChooseLockGeneric")
            LauncherAppManager.startActivity(activity, intent)
        } else {
            AppLauncherUtils.launchAppAction(activity, AppPackageConstant.SECURITY_ACTION)
        }
    }

     private fun isServiceRunning(context: Context): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val services = manager.getRunningServices(Int.MAX_VALUE)
        for (service in services) {
            if (SERVICE == service.service.className) {
                return true
            }
        }
        return false
    }

    private fun startService(context: Context) {
        if (!isServiceRunning(context)) {
            val intent = Intent()
            intent.action = SERVICE_ACTION
            intent.setPackage(context.packageName)
            context.startService(intent)
        }
    }
}