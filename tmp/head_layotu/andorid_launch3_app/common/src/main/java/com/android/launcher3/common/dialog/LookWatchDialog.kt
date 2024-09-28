package com.android.launcher3.common.dialog
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.android.launcher3.common.R
import com.android.launcher3.common.mode.LookWatchModel
import com.android.launcher3.common.utils.AudioFocusManager
import com.android.launcher3.common.utils.ScreenUtils
import com.bumptech.glide.Glide

class LookWatchDialog private constructor() {

    private var mContext:Context? = null
    private var mAlert: AlertDialog.Builder? = null
    private var mAlertDialog: AlertDialog? = null

    private val handler = Handler(Looper.getMainLooper())

    private val model by lazy { mContext?.let { LookWatchModel(it) } }

    private constructor(mContext:Context) : this() {
        this.mContext = mContext
        init()
    }

    companion object {

        fun init(mContext:Context): LookWatchDialog {
            return LookWatchDialog(mContext)
        }
    }

    private fun init(){

    }

    fun startPregress(){
        AudioFocusManager.getInstance().getFlag()
        AudioFocusManager.getInstance().requestAudioFocus()
        mAlert = AlertDialog.Builder(mContext)
        mAlert?.setContentView(R.layout.dialog_look_watch)
            ?.fullWidth()
            ?.fullHeight()
        mAlertDialog = mAlert?.show()
        val view = mAlertDialog?.getView<ImageView>(R.id.iv_gif)
        mContext?.let { view?.let { view -> Glide.with(it).load(R.drawable.ic_gif).into(view) } }
        model?.play()
        model?.setLight(true)
        handler.postDelayed(runnable, 15000)
        ScreenUtils(mContext).acquireScreenLock()
        mAlertDialog?.setOnCancelListener {
            stopPregress()
        }

        mAlertDialog?.getView<ConstraintLayout>(R.id.layout)?.setOnClickListener {
            stopPregress()
        }
    }

    private var runnable = Runnable {
        stopPregress()
    }

    fun isShow() : Boolean?{
       return mAlertDialog?.isShowing
    }

    private fun stopPregress(){
        AudioFocusManager.getInstance().freedAudioFocusNoDelay()
        handler.removeCallbacks(runnable)
        mAlertDialog?.dismiss()
        model?.stop()
        model?.setLight(false)
        ScreenUtils(mContext).releaseScreenLock()
    }
}