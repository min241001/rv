package com.android.launcher3.moudle.splash;

import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.launcher3.R;
import com.android.launcher3.common.constant.SettingsConstant;
import com.android.launcher3.common.data.AppLocalData;
import com.android.launcher3.common.dialog.AlertDialog;

public class SplashDialog {

    private Context mContext;
    private AlertDialog mAlertDialog = null;
    private AlertDialog.Builder mAlert = null;
    private int tag = 0;

    public SplashDialog(Context context) {
        this.mContext = context;
    }

    public void startPregress() {

        if (isShow()) {
            return;
        }

        mAlert = new AlertDialog.Builder(mContext);

        mAlert.setContentView(R.layout.dialog_splash)
                .fullHeight()
                .fullWidth();

        mAlertDialog = mAlert.show();

        mAlertDialog.setCancelable(false);

        RelativeLayout rlParent = mAlertDialog.findViewById(R.id.rl_parent);
        ImageView ivImg = mAlertDialog.findViewById(R.id.iv_img);
        TextView tvText = mAlertDialog.findViewById(R.id.tv_text);
        Button btnConfirm = mAlertDialog.findViewById(R.id.btn_confirm);
        Button btnBinding = mAlertDialog.findViewById(R.id.btn_binding);

        setTextColor(tvText, "【西萌守护】");

        mAlertDialog.findViewById(R.id.btn_binding).setOnClickListener(v -> {
            try {
                AppLocalData.getInstance().setSpSplash(false);
                Intent intent = new Intent();
                intent.putExtra("title","APP下载");
                intent.putExtra("state" , 1);
                intent.setAction(SettingsConstant.SPLASH_APP_DOWNLOAD);
                mContext.startActivity(intent);
                stopPregress();
            }catch (Exception e){
                e.printStackTrace();
            }
        });

        btnConfirm.setOnClickListener(v -> {
            switch (tag) {
                case 0: {
                    rlParent.setBackgroundResource(R.mipmap.splash_2_bg);
                    ivImg.setImageResource(R.mipmap.splash_2_text);
                    tvText.setText(mContext.getString(R.string.splash_text_2));
                    tvText.setGravity(Gravity.TOP);
                    btnBinding.setVisibility(View.GONE);

                    rlParent.removeView(btnConfirm);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            (int) mContext.getResources().getDimension(R.dimen._120dp),
                            (int) mContext.getResources().getDimension(R.dimen._50dp)
                    );
                    params.addRule(RelativeLayout.ALIGN_PARENT_END);
                    params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    params.topMargin = 10;
                    btnConfirm.setLayoutParams(params);
                    rlParent.addView(btnConfirm);

                    setTextColor(tvText, "【西萌主题】");

                }
                break;
                case 1: {
                    rlParent.setBackgroundResource(R.mipmap.splash_3_bg);
                    ivImg.setImageResource(R.mipmap.splash_3_text);
                    tvText.setText(mContext.getString(R.string.splash_text_3));
                    tvText.setGravity(Gravity.BOTTOM);
                    btnConfirm.setText(mContext.getString(R.string.splash_know));

                    rlParent.removeView(btnConfirm);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            (int) mContext.getResources().getDimension(R.dimen._120dp),
                            (int) mContext.getResources().getDimension(R.dimen._50dp)
                    );
                    params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    btnConfirm.setLayoutParams(params);
                    rlParent.addView(btnConfirm);

                    params = (RelativeLayout.LayoutParams) tvText.getLayoutParams();
                    params.bottomMargin = 7;
                    tvText.setLayoutParams(params);

                    setTextColor(tvText, "【西萌市场】");

                }
                break;
                case 2: {
                    AppLocalData.getInstance().setSpSplash(false);
                    stopPregress();
                }
                break;
            }
            tag++;
        });
    }

    public void stopPregress() {
        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
        }
    }

    private boolean isShow() {
        if (mAlertDialog != null) {
            return mAlertDialog.isShowing();
        }
        return false;
    }

    private void setTextColor(TextView tvText, String textStr) {
        try {
            String text = tvText.getText().toString().trim();
            int startIndex = text.indexOf(textStr);
            SpannableStringBuilder style = new SpannableStringBuilder(text);
            style.setSpan(new ForegroundColorSpan(mContext.getColor(R.color.color_ff7e00)),startIndex,startIndex + 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvText.setText(style);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
