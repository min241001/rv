package com.android.launcher3.common.dialog

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.android.launcher3.common.R
import com.android.launcher3.common.widget.ProgressBarView


class ChargeDialog private constructor() {

    private var mContext: Context? = null
    private var mAlert: AlertDialog.Builder? = null
    private var mAlertDialog: AlertDialog? = null

    private var tvCharge: TextView? = null
    private var progressBarVie : ProgressBarView ? = null
    private var progress : ProgressBarView ? = null
    private var tcColon : TextView ?= null
    private var animatorSet: AnimatorSet? = null
    private var scaleSet: AnimatorSet? = null

    private var rlImage : RelativeLayout ? = null
    private var rlProgress : RelativeLayout ? = null
    private var ivOalBattery : ImageView ? = null
    private var ivBigBattery : ImageView ? = null
    private var ivProgress : ImageView ? = null

    private var viewItemCharge : View ?= null
    private var viewLayoutCharge : View ?= null

    private val mLoading: Loading by lazy { Loading.init(mContext) }

    private constructor(mContext: Context) : this() {
        this.mContext = mContext
        init()
    }

    companion object {
        fun init(mContext: Context): ChargeDialog {
            return ChargeDialog(mContext)
        }
    }

    private fun init() {

    }

    fun startProgress(battery: Int) {
        if (isShow() == true) {
            return
        }
        mAlert = AlertDialog.Builder(mContext)
        mAlert?.setContentView(R.layout.dialog_charge)
            ?.fullWidth()
            ?.fullHeight()
        mAlertDialog = mAlert?.show()
        mAlertDialog?.setCancelable(false)

        tvCharge = mAlertDialog?.getView<TextView>(R.id.tv_charge)
        progressBarVie = mAlertDialog?.getView<ProgressBarView>(R.id.progressBarVie)
        tcColon = mAlertDialog?.getView<TextView>(R.id.tc_colon)

        viewItemCharge = mAlertDialog?.getView(R.id.view_item_charge)
        viewLayoutCharge = mAlertDialog?.getView(R.id.view_layout_charge)

        rlImage = mAlertDialog?.getView(R.id.rl_image)
        rlProgress = mAlertDialog?.getView(R.id.rl_progress)
        ivOalBattery = mAlertDialog?.getView(R.id.iv_oval_battery)
        ivBigBattery = mAlertDialog?.getView(R.id.iv_big_battery)
        ivProgress = mAlertDialog?.getView(R.id.iv_progress)
        progress = mAlertDialog?.getView(R.id.progress)

       viewLayoutCharge?.setOnClickListener {
           mLoading.startPregress(R.layout.layout_charge)
       }

        setBattery(battery)
        rlImage?.let { scaleAnim(it, start = {
            viewItemCharge?.visibility = View.VISIBLE
            viewLayoutCharge?.visibility = View.GONE
        }, end = {
            rlImage?.visibility = View.GONE
            rlProgress?.visibility = View.VISIBLE
            reduce(battery)
        }) }
        colonAnim()
    }

    fun stopProgress() {
        mAlertDialog?.dismiss()
        animatorSet?.cancel()
        scaleSet?.cancel()
    }

    private fun isShow(): Boolean? {
        return mAlertDialog?.isShowing
    }

    @SuppressLint("SetTextI18n")
    fun setBattery(battery: Int) {
        tvCharge?.text = "电量 ${battery}%"
        progressBarVie?.progress = battery

        if (battery > 20){
            progressBarVie?.setUnReachedBarColor(Color.parseColor("#062F05"))
            progressBarVie?.setReachedBarColor(Color.parseColor("#00FE18"))
        }else{
            progressBarVie?.setUnReachedBarColor(Color.parseColor("#2F0505"))
            progressBarVie?.setReachedBarColor(Color.parseColor("#D90808"))
        }
    }

    @SuppressLint("ObjectAnimatorBinding")
    private fun colonAnim(){
        val alphaAnimator = ObjectAnimator.ofFloat(tcColon, "alpha", 1.0f, 0.0f)
        alphaAnimator.duration = 3000
        alphaAnimator.repeatCount = ObjectAnimator.INFINITE

        animatorSet = AnimatorSet()
        animatorSet?.play(alphaAnimator)
        animatorSet?.start()
    }


    private fun scaleAnim(view: View, sValue :Float = 0f , eValue :Float = 1f, start: () -> Unit , end: () -> Unit){
        val scaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, sValue, eValue)
        val scaleY = ObjectAnimator.ofFloat(view, View.SCALE_Y, sValue, eValue)

        scaleX.duration = 2500
        scaleY.duration = 2500

        scaleSet = AnimatorSet()

        if (sValue == 1f){
            val alpha = ObjectAnimator.ofFloat(view, View.ALPHA, 1f, 0f)
            alpha.duration = 2500
            scaleSet?.playTogether(scaleX, scaleY,alpha)
        }else{
            scaleSet?.playTogether(scaleX, scaleY)
        }

        scaleSet?.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {
                start.invoke()

            }

            override fun onAnimationEnd(animation: Animator) {
               end.invoke()
            }

            override fun onAnimationCancel(animation: Animator?) {

            }

            override fun onAnimationRepeat(animation: Animator?) {

            }
        })

        scaleSet?.start()
    }

    private fun reduce(battery: Int){

        if (battery > 20){
            progress?.setUnReachedBarColor(Color.parseColor("#062F05"))
            progress?.setReachedBarColor(Color.parseColor("#00FE18"))
        }else{
            progress?.setUnReachedBarColor(Color.parseColor("#2F0505"))
            progress?.setReachedBarColor(Color.parseColor("#D90808"))
        }

        progress?.setProgressAnimation(battery) {
            rlProgress?.let {
                scaleAnim(it, 1f, 0.1f, {

                }, {
                    viewItemCharge?.visibility = View.GONE
                    viewLayoutCharge?.visibility = View.VISIBLE
                })
            }
        }
    }
}