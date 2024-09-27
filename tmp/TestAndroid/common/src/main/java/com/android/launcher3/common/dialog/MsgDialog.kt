package com.android.launcher3.common.dialog

import android.content.Context
import android.graphics.drawable.Drawable
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.android.launcher3.common.R
import com.android.launcher3.common.constant.SettingsConstant
import com.android.launcher3.common.utils.*
import java.text.SimpleDateFormat
import java.util.*

class MsgDialog private constructor() {

    private var mContext: Context? = null
    private var mAlert: AlertDialog.Builder? = null
    private var mAlertDialog: AlertDialog? = null

    private val mediaPlayer  by lazy { mContext?.let { AudioPlayer.getInstance(it) } }

    private constructor(mContext: Context) : this() {
        this.mContext = mContext
        init()
    }

    companion object {
        fun init(mContext: Context): MsgDialog {
            return MsgDialog(mContext)
        }

    }

    private fun init() {

    }

    fun startPregress(packageName: String, img: Drawable, appName: String, text: String, time: Long) {
        if (isShow() == true){
            mAlertDialog?.dismiss()
            ScreenUtils(mContext).releaseScreenLock()
            stop()
        }
        play()
        mAlert = AlertDialog.Builder(mContext)
        mAlert?.setContentView(R.layout.dialog_msg)
            ?.fullWidth()
            ?.fullHeight()
        mAlertDialog = mAlert?.show()
        mAlertDialog?.setCancelable(false)
        ScreenUtils(mContext).acquireScreenLock()
        resetUI(packageName, img, appName, text, time)
    }

    fun stopPregress() {
        mAlertDialog?.dismiss()
    }

    private fun isShow(): Boolean? {
        return mAlertDialog?.isShowing
    }

    private fun resetUI(packageName: String,img: Drawable, appName: String, text: String, time: Long){
        val cl_view = mAlertDialog?.getView<ConstraintLayout>(R.id.cl_view)
        val iv_img = mAlertDialog?.getView<ImageView>(R.id.iv_img)
        val tv_name = mAlertDialog?.getView<TextView>(R.id.tv_name)
        val ib_close = mAlertDialog?.getView<ImageButton>(R.id.ib_close)
        val tv_text = mAlertDialog?.getView<TextView>(R.id.tv_text)
        val tv_time = mAlertDialog?.getView<TextView>(R.id.tv_time)

        iv_img?.setImageDrawable(img)
        tv_name?.text = appName
        tv_text?.text = text

        val date = Date(time)
        val sdf = SimpleDateFormat("MM.dd HH:mm", Locale.getDefault())
        val formattedDate: String = sdf.format(date)

        tv_time?.setText(formattedDate)
        ib_close?.setOnClickListener{
            mAlertDialog?.dismiss()
        }

        cl_view?.setOnClickListener {
            stopPregress()
            if (LauncherAppManager.isForbiddenInClass(mContext,packageName)){
                ToastUtils.show(R.string.app_forbidden_in_class)
            } else if (LauncherAppManager.isAppForbidden(mContext,packageName)) {
                ToastUtils.show(R.string.app_forbidden)
            } else {
                if (packageName == "com.baehug.dialer"){
                    AppLauncherUtils.launchAppAction(mContext,"com.baehug.dialer.call")
                }else{
                    AppLauncherUtils.launchApp(mContext,packageName)
                }
            }
        }
    }

    private fun play(){
        try {
            val audioManager: AudioManager = mContext?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            if (audioManager.ringerMode == AudioManager.RINGER_MODE_SILENT){
                return
            }

            if (mediaPlayer!!.isPlaying()){
                return
            }

            mediaPlayer!!.play(R.raw.fluorine)
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun stop(){
        mediaPlayer?.stop()
    }
}