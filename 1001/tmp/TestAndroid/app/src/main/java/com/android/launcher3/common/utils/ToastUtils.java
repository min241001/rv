package com.android.launcher3.common.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.android.launcher3.R;

public class ToastUtils {

    //使用主线程looper初始化handler,保证handler发送的消息运行在主线程
    private static Handler handler = new Handler(Looper.getMainLooper());

    private static Toast mToast;

    public static void show(Context context, int text) {
        //判断当前是主线程还是子线程
        if (Looper.myLooper() == Looper.getMainLooper()) {
            //当前looper是否等于主线程looper, 如果是, 说明当前是在主线程
            if (mToast == null){
                mToast =  Toast.makeText(context, text, Toast.LENGTH_SHORT);
            }else {
                mToast.setText(text);
                mToast.setDuration(Toast.LENGTH_SHORT);
            }
            mToast.show();
        } else {
            //子线程
            int finalText = text;
            handler.post(() -> {
                //当Looper轮询到此任务时, 会在主线程运行此方法
                if (mToast == null){
                    mToast = Toast.makeText(context, finalText, Toast.LENGTH_SHORT);
                }else {
                    mToast.setText(finalText);
                    mToast.setDuration(Toast.LENGTH_SHORT);
                }
                mToast.show();
            });
        }
    }
    public static void show(Context context,String text) {
        if (text.contains("Unable to resolve host")){
            text = context.getString(R.string.no_network);
        }
        //判断当前是主线程还是子线程
        if (Looper.myLooper() == Looper.getMainLooper()) {
            //当前looper是否等于主线程looper, 如果是, 说明当前是在主线程
            if (mToast == null){
                mToast =  Toast.makeText(context, text, Toast.LENGTH_SHORT);
            }else {
                mToast.setText(text);
                mToast.setDuration(Toast.LENGTH_SHORT);
            }
            mToast.show();
        } else {
            //子线程
            String finalText = text;
            handler.post(() -> {
                //当Looper轮询到此任务时, 会在主线程运行此方法
                if (mToast == null){
                    mToast = Toast.makeText(context, finalText, Toast.LENGTH_SHORT);
                }else {
                    mToast.setText(finalText);
                    mToast.setDuration(Toast.LENGTH_SHORT);
                }
                mToast.show();
            });
        }
    }

    public static void show4Debug(Context context,final String text) {
        if (true) {
            return;
        }
        //判断当前是主线程还是子线程
        if (Looper.myLooper() == Looper.getMainLooper()) {
            //当前looper是否等于主线程looper, 如果是, 说明当前是在主线程
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            //System.out.println("主线程吐司....");
        } else {
            //子线程
            //handler.sendEmptyMessage(0);//handler发送一个消息给队列
            //handler发送一个任务给队列
            handler.post(new Runnable() {
                @Override
                public void run() {
                    //当Looper轮询到此任务时, 会在主线程运行此方法
                    Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
                }
            });
            //System.out.println("子线程吐司....");
        }
    }

}