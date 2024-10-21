package com.xbx.balldemo.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xbx.balldemo.R;
import com.xbx.balldemo.util.Constants;

/**
 * Create by pengmin on 2024/10/18 .
 */
public class MessageWindow implements View.OnClickListener {

    private Context context;
    WindowManager windowManager = null;
    View popup_v = null;
    WindowManager.LayoutParams layoutParams = null;
    private int absolutely_x, absolutely_y;

    public MessageWindow(Context context, int abs_x, int abs_y) {
        this.context = context;
        this.absolutely_x = abs_x;
        this.absolutely_y = abs_y;
        InitPopupWindows();
    }

    public void InitPopupWindows() {
        popup_v = LayoutInflater.from(context).inflate(R.layout.layout_tips, null);
        // popup_v = LayoutInflater.from(context).inflate(R.layout.layout_tips, (ViewGroup) root_v, false);

        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            layoutParams = new WindowManager.LayoutParams();
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            //layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |    //不拦截页面点击事件
            //        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
            //        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            }
            layoutParams.format = PixelFormat.TRANSLUCENT;
            //layoutParams.x = absolutely_x;
            layoutParams.y = absolutely_y - (int) context.getResources().getDimension(R.dimen._8dp);
            layoutParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
            layoutParams.width = (int) context.getResources().getDimension(R.dimen.w_width);
            layoutParams.height = (int) context.getResources().getDimension(R.dimen.w_height);
            layoutParams.windowAnimations = R.style.popwindowAnimStyle;
            InitPopupView(popup_v);
            //popup_v.setFocusable(true);
            //popup_v.setFocusableInTouchMode(true);

            //setOnLayoutTouchListener(popup_v);
            //setOnViewTouchListener(popup_v);

            /*popup_v.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View arg0, MotionEvent arg1) {
                    // TODO Auto-generated method stub
                    Log.i(Constants.mwTAG, "Sample x" + arg1.getX());
                    Log.i(Constants.mwTAG, "Sample x" + arg1.getY());
                    return true;
                }
            });*/
            //popup_v.setOnTouchListener(new ItemViewTouchListener2(layoutParams, windowManager));//这个十可移动功能


        }

    }

    private void InitPopupView(View v) {
        RelativeLayout rl = v.findViewById(R.id.ball_popup_rl);
        rl.setFocusable(true);
        rl.setFocusableInTouchMode(true);
        setGestureListener(rl);
        ImageView iv = v.findViewById(R.id.ball_popup_iv);
        iv.setImageResource(R.drawable.ic_launcher_background);
        TextView tv1 = v.findViewById(R.id.ball_popup_tv1);
        TextView tv2 = v.findViewById(R.id.ball_popup_tv2);
        tv1.setText("微信消息");
        tv2.setText("new message");
        iv.setOnClickListener(this);

    }

    public void ShowWindow() {
        if (windowManager != null) {
            RemoveView();
            AddView();
        }
    }
    public void DismissWindow(){
        RemoveView();
    }

    public void CloseWindow() {
        if (windowManager != null) {
            RemoveView();
            windowManager = null;
        }
    }

    private void AddView() {
        layoutParams.windowAnimations = R.style.popwindowAnimStyle;
        windowManager.addView(popup_v, layoutParams);
    }


    private void RemoveView() {
        if (popup_v.getWindowToken() != null) {
            windowManager.removeView(popup_v);
        }
    }

    private void RemoveView2() {
        if (popup_v.getWindowToken() != null) {
            layoutParams.windowAnimations = R.style.popwindowAnimStyle2;
            windowManager.updateViewLayout(popup_v, layoutParams);
            windowManager.removeView(popup_v);
            //windowManager.removeViewImmediate(popup_v);
        }
    }


    private float mPosX, mPosY, mCurPosX, mCurPosY;

    private void setGestureListener(View v) {
        v.setOnTouchListener(new View.OnTouchListener() {

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mPosX = event.getX();
                        mPosY = event.getY();
                        //// Log.i(Constants.mwTAG, "mPosX:" + mPosX + "mPosY:" + mPosY);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mCurPosX = event.getX();
                        mCurPosY = event.getY();
                        //// Log.i(Constants.mwTAG, "mCurPosX:" + mCurPosX + "mCurPosY:" + mCurPosY);
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.i(Constants.mwTAG, "mCurPosY - mPosY:" + (mCurPosY - mPosY) + "Math.abs(mCurPosY - mPosY):" + Math.abs(mCurPosY - mPosY));
                        if (mCurPosY - mPosY > 0
                                && (Math.abs(mCurPosY - mPosY) > 25)) {
                            //向下滑動


                        } else if (mCurPosY - mPosY < 0
                                && (Math.abs(mCurPosY - mPosY) > 150)) {
                            //向上滑动
                            Log.i(Constants.mwTAG, "move up");

                            RemoveView2();
                            mPosX = 0.0f;
                            mPosY = 0.0f;
                            mCurPosX = 0.0f;
                            mCurPosY = 0.0f;

                        }
                        break;
                }
                return true;
            }

        });
    }

    //end
    private float posX, posY, curPosX, curPosY;
    private void setOnLayoutTouchListener(View v) {
        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        posX = event.getX();
                        posY = event.getY();
                        Log.i(Constants.mwTAG, "posX:" + posX + "posY:" + posY);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        curPosX = event.getX();
                        curPosY = event.getY();
                        Log.i(Constants.mwTAG, "curPosX:" + curPosX + "curPosY:" + curPosY);
                        break;
                    case MotionEvent.ACTION_UP:
                        if ((curPosX - posX > 0) && (Math.abs(curPosX - posX) > 25)) {
                            Log.v(Constants.mwTAG, "向左滑动");
                        } else if ((curPosX - posX < 0) && (Math.abs(curPosX - posX) > 25)) {
                            Log.v(Constants.mwTAG, "向右滑动");
                        }
                        if ((curPosY - posY > 0) && (Math.abs(curPosY - posY) > 25)) {
                            Log.v(Constants.mwTAG, "向下滑动");
                        } else if ((curPosY - posY < 0) && (Math.abs(curPosY - posY) > 25)) {

                            Log.v(Constants.mwTAG, "向上滑动");
                        }
                        break;
                }
                return true;
            }
        });
    }

    private void setOnViewTouchListener(View v) {
        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        posX = event.getX();
                        posY = event.getY();
                        Log.i(Constants.mwTAG, "posX:" + posX + "posY:" + posY);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        curPosX = event.getX();
                        curPosY = event.getY();
                        Log.i(Constants.mwTAG, "curPosX:" + curPosX + "curPosY:" + curPosY);
                        break;
                    case MotionEvent.ACTION_UP:
                        if ((curPosX - posX > 0) && (Math.abs(curPosX - posX) > 25)) {
                            Log.v(Constants.mwTAG, "向左滑动");
                        } else if ((curPosX - posX < 0) && (Math.abs(curPosX - posX) > 25)) {
                            Log.v(Constants.mwTAG, "向右滑动");
                        }
                        if ((curPosY - posY > 0) && (Math.abs(curPosY - posY) > 25)) {
                            Log.v(Constants.mwTAG, "向下滑动");
                        } else if ((curPosY - posY < 0) && (Math.abs(curPosY - posY) > 25)) {

                            Log.v(Constants.mwTAG, "向上滑动");
                        }
                        break;
                }
                return true;
            }
        });


    }

    private void setOnViewTouchListener3(View v) {
        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {


                return true;
            }

        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ball_popup_iv:
                Toast.makeText(context, "iv click", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }
}





