package com.android.launcher3.moudle.stopwatch.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.launcher3.R;
import com.android.launcher3.common.base.BaseMvpActivity;
import com.android.launcher3.moudle.stopwatch.adapter.CountTimeAdapter;
import com.android.launcher3.moudle.stopwatch.presenter.StopWatchPagesPresenter;

import java.util.ArrayList;

public class StopWatchPagesActivity extends BaseMvpActivity<StopWatchPagesView, StopWatchPagesPresenter> implements StopWatchPagesView {

    private LinearLayout ll_time;
    private LinearLayout ll_instruct;
    private ImageView iv_lap_or_reset;
    private ImageView iv_img1;
    private ImageView iv_img2;
    private ImageView iv_img3;
    private ImageView iv_star_or_stop;
    private CountTimeAdapter countTimeAdapter;
    private boolean isStart;//秒表是否开始
    private boolean isReset;//是否重置
    private RelativeLayout rl_style_first;
    private TextView tv_time;
    private RecyclerView recycler_view_stop_watch_pages;
    private TimeView tv_style_first;
    private TimeMilliSecondView tmsv_style_first;
    private TimeMinuteView tmv_style_first;
    private CoverTimeView ctv_style_first;
    private TimeSecondsView tsv_style_second;

    private long elapsedTime;

    private ArrayList<String> mData = new ArrayList<>();
    private RelativeLayout rl_layout;

    private int styleId;//三个样式状态 0：大表盘 1：小表盘 2：无表盘
    private float offset;
    private long duration;
    private ImageView iv_line;
    private int screenWidth;
    private int screenHeight;
    private boolean isRecover;//上滑下滑动画判断

    @Override
    public void updateTimerText(String data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_time.setText(data);
            }
        });
    }


    @Override
    public void updateLapText(ArrayList<String> list) {
        mData.clear();
        mData.addAll(list);
        countTimeAdapter.notifyDataSetChanged();

    }

    @Override
    public void resetTimer() {
        isReset = false;
        elapsedTime = 0;
        iv_lap_or_reset.setImageResource(R.mipmap.blank_timing);
        tv_time.setText("00:00.00");
        updateTimer(elapsedTime);
        mData.clear();
        countTimeAdapter.notifyDataSetChanged();
    }

    @Override
    public void lapTimer() {
        isReset = true;
        iv_lap_or_reset.setImageResource(R.mipmap.reset_timing);

    }

    @Override
    public void pauseTimer() {
        iv_star_or_stop.setImageResource(R.mipmap.start_timing);
        iv_lap_or_reset.setImageResource(R.mipmap.reset_timing);
    }

    @Override
    public void startTimer() {
        iv_star_or_stop.setImageResource(R.mipmap.pause_timing);
        iv_lap_or_reset.setImageResource(R.mipmap.blank_timing);
    }

    @Override
    public void updateTimer(long l) {
        elapsedTime = l;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_style_first.setTotalSecond(elapsedTime / 1000 % 60);
                tmv_style_first.setTotalSecond(elapsedTime / 1000);
                tmsv_style_first.setTotalMillSecond(elapsedTime % 1000);
                ctv_style_first.setTotalSecond(elapsedTime / 1000 % 60);
                tsv_style_second.setTotalSecond(elapsedTime / 1000 % 60);
            }
        });
    }


    @Override
    protected StopWatchPagesPresenter createPresenter() {
        return new StopWatchPagesPresenter(this);
    }


    @Override
    protected int getResourceId() {
        return R.layout.activity_stop_watch_pages;
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initView() {
        super.initView();
        rl_layout = findViewById(R.id.rl_layout);
        ll_time = findViewById(R.id.ll_time);
        ll_instruct = findViewById(R.id.ll_instruct);

        iv_lap_or_reset = findViewById(R.id.iv_reset);
        iv_img1 = findViewById(R.id.iv_img1);
        iv_img2 = findViewById(R.id.iv_img2);
        iv_img3 = findViewById(R.id.iv_img3);
        iv_star_or_stop = findViewById(R.id.iv_star_or_stop);

        rl_style_first = findViewById(R.id.rl_style_first);
        //秒针大表盘
        tv_style_first = findViewById(R.id.tv_style_first);
        //毫秒表盘
        tmsv_style_first = findViewById(R.id.tmsv_style_first);
        //分钟表盘
        tmv_style_first = findViewById(R.id.tmv_style_first);
        //覆盖大秒针表盘
        ctv_style_first = findViewById(R.id.ctv_style_first);
        tv_time = findViewById(R.id.tv_time);
        //秒针小表盘
        tsv_style_second = findViewById(R.id.tsv_style_second);
        recycler_view_stop_watch_pages = findViewById(R.id.recycler_view_stop_watch_pages);
        iv_line = findViewById(R.id.iv_line);
        //设置右侧指示
        setIndicator(styleId);
        //设置动画时长
        setAnimatorDuration(500);
        recycler_view_stop_watch_pages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true));
        countTimeAdapter = new CountTimeAdapter(this, mData, R.layout.item_count_time);
        recycler_view_stop_watch_pages.setAdapter(countTimeAdapter);

        rl_layout.setOnTouchListener(new View.OnTouchListener() {
            private float startX;
            private float startY;
            private float distanceX;
            private float distanceY;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = motionEvent.getX();
                        startY = motionEvent.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        distanceX = motionEvent.getX() - startX;
                        distanceY = motionEvent.getY() - startY;
                        // 处理滑动距离，可以根据需要进行逻辑处理
                        /*if (Math.abs(distanceY) > 100 && Math.abs(distanceY) > Math.abs(distanceX) && distanceY > 0) {
                            //下滑

                            return true;
                        }
                        if (Math.abs(distanceY) > 100 && Math.abs(distanceY) > Math.abs(distanceX) && distanceY < 0) {
                            //上滑
                            if (styleId == 0) {
                                firstAnimation();
                            } else if (styleId == 1) {
                                secondAnimation();
                            }

                        }*/


                        if (Math.abs(distanceY) > Math.abs(distanceX) && distanceY < 0) {
                            //上滑 显示下一个UI
                            if (tsv_style_second.getVisibility() == View.GONE) {
                                isRecover = false;
                                //秒表小号表盘为隐藏状态 当前页面为大表盘页面
                                //执行动画 分钟表盘左移 毫秒表盘右移 大秒钟表盘缩放到最后隐藏 显示小号秒钟 文本缩放 覆盖表盘缩放到隐藏
                                firstAnimation();
                            } else {
                                //分割横条隐藏状态 当前页面为三并排小表盘页面
                                //执行动画 三小表盘向上横移渐变隐藏 文本平移 横条平移 列表平移
                                if (iv_line.getVisibility() == View.GONE) {
                                    isRecover = false;
                                    secondAnimation();
                                } else {
                                    Log.e(TAG, "当前为最后一个页面");
                                }

                            }

                            return true;
                        } else {
                            //下滑 显示上一个页面
                            if (iv_line.getVisibility() == View.VISIBLE) {
                                isRecover = true;
                                secondAnimation();
                            } else {
                                if (tsv_style_second.getVisibility() == View.VISIBLE) {
                                    isRecover = true;
                                    firstAnimation();
                                } else {
                                    Log.e(TAG, "当前为首页");
                                }
                            }
                        }


                        break;
                    case MotionEvent.ACTION_UP:
                        // 手指抬起时的逻辑处理
                        break;
                }


                return false;
            }
        });
    }


    private void getScreenSize() {
        WindowManager manager = this.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        screenWidth = outMetrics.widthPixels;
        screenHeight = outMetrics.heightPixels;
    }

    private void firstAnimation() {

        up_tmv_style_first();//第一页分钟表盘
        up_tmsv_style_first();//第一个毫秒表盘
        up_tsv_style_second();//第一个秒针表盘 隐藏后显示的第二个秒针表盘
        up_tv_style_first();//第一个秒针表盘(大表盘)
        up_ctv_style_first();//秒针覆盖空白秒表表盘
        up_tv_time();//时间文本显示

    }

    private void secondAnimation() {
        //三个小表盘渐变消失 平移一段位移
        alpha_tmv_style_first();
        alpha_tsv_style_second();
        alpha_tmsv_style_first();
        translation_tv_time();
        translation_iv_line();
        translation_recyclerView();

    }

    private void setAnimatorDuration(long milliSecond) {
        duration = milliSecond;
    }

    private void translation_tv_time() {
        ObjectAnimator translationY;
        if (!isRecover) {
            translationY = ObjectAnimator.ofFloat(tv_time, "translationY", -60);
        } else {
            translationY = ObjectAnimator.ofFloat(tv_time, "translationY", 0);
        }
        translationY.setDuration(duration);
        translationY.start();

    }

    private void translation_iv_line() {
        ObjectAnimator translationY;
        if (!isRecover) {
            translationY = ObjectAnimator.ofFloat(iv_line, "translationY", -60);
        } else {
            translationY = ObjectAnimator.ofFloat(iv_line, "translationY", 0);
        }
        translationY.setDuration(duration);
        translationY.start();

    }

    private void translation_recyclerView() {
        ObjectAnimator translationY;
        if (!isRecover) {
            translationY = ObjectAnimator.ofFloat(recycler_view_stop_watch_pages, "translationY", -60);
        } else {
            translationY = ObjectAnimator.ofFloat(recycler_view_stop_watch_pages, "translationY", 0);
        }
        translationY.setDuration(duration);
        translationY.start();

    }

    private void alpha_tmv_style_first() {
        if (!isRecover) {
            AnimatorSet animatorSet = new AnimatorSet();
            ObjectAnimator translationY = ObjectAnimator.ofFloat(tmv_style_first, "translationY", -100);
            ObjectAnimator alpha = ObjectAnimator.ofFloat(tmv_style_first, "alpha", 1f, 0f);
            animatorSet.play(translationY).with(alpha);
            animatorSet.setDuration(duration);
            animatorSet.start();
            animatorSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
//                    tmv_style_first.setVisibility(View.GONE);
                    iv_line.setVisibility(View.VISIBLE);
                    styleId=2;
                    setIndicator(styleId);
                    recycler_view_stop_watch_pages.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
        } else {
            AnimatorSet animatorSet = new AnimatorSet();
            ObjectAnimator translationY = ObjectAnimator.ofFloat(tmv_style_first, "translationY", 0);
            ObjectAnimator alpha = ObjectAnimator.ofFloat(tmv_style_first, "alpha", 1f);
            animatorSet.play(translationY).with(alpha);
            animatorSet.setDuration(duration);
            animatorSet.start();
            animatorSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    tmv_style_first.setVisibility(View.VISIBLE);
                    iv_line.setVisibility(View.GONE);
                    styleId=1;
                    setIndicator(styleId);
                    recycler_view_stop_watch_pages.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
        }

    }

    private void alpha_tsv_style_second() {

        if (!isRecover) {
            AnimatorSet animatorSet = new AnimatorSet();
            ObjectAnimator translationY = ObjectAnimator.ofFloat(tsv_style_second, "translationY", -100);
            ObjectAnimator alpha = ObjectAnimator.ofFloat(tsv_style_second, "alpha", 1f, 0f);
            animatorSet.play(translationY).with(alpha);
            animatorSet.setDuration(duration);
            animatorSet.start();
            animatorSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
//                    tsv_style_second.setVisibility(View.GONE);
                    iv_line.setVisibility(View.VISIBLE);
                    recycler_view_stop_watch_pages.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
        } else {
            AnimatorSet animatorSet = new AnimatorSet();
            ObjectAnimator translationY = ObjectAnimator.ofFloat(tsv_style_second, "translationY", 0);
            ObjectAnimator alpha = ObjectAnimator.ofFloat(tsv_style_second, "alpha", 1f);
            animatorSet.play(translationY).with(alpha);
            animatorSet.setDuration(duration);
            animatorSet.start();
            animatorSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    tsv_style_second.setVisibility(View.VISIBLE);
                    iv_line.setVisibility(View.GONE);
                    recycler_view_stop_watch_pages.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
        }


    }

    private void alpha_tmsv_style_first() {
        if (!isRecover) {
            AnimatorSet animatorSet = new AnimatorSet();
            ObjectAnimator translationY = ObjectAnimator.ofFloat(tmsv_style_first, "translationY", -100);
            ObjectAnimator alpha = ObjectAnimator.ofFloat(tmsv_style_first, "alpha", 1f, 0f);
            animatorSet.play(translationY).with(alpha);
            animatorSet.setDuration(duration);
            animatorSet.start();
            animatorSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
//                    tmsv_style_first.setVisibility(View.GONE);
                    iv_line.setVisibility(View.VISIBLE);
                    recycler_view_stop_watch_pages.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
        } else {
            AnimatorSet animatorSet = new AnimatorSet();
            ObjectAnimator translationY = ObjectAnimator.ofFloat(tmsv_style_first, "translationY", 0);
            ObjectAnimator alpha = ObjectAnimator.ofFloat(tmsv_style_first, "alpha", 1f);
            animatorSet.play(translationY).with(alpha);
            animatorSet.setDuration(duration);
            animatorSet.start();
            animatorSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    tmsv_style_first.setVisibility(View.VISIBLE);
                    iv_line.setVisibility(View.GONE);
                    recycler_view_stop_watch_pages.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
        }

    }

    private void up_tmv_style_first() {
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator objectAnimator;
        ObjectAnimator objectAnimator2;
        if (!isRecover) {
            objectAnimator = ObjectAnimator.ofFloat(tmv_style_first, "translationX", -tmv_style_first.getWidth() - 24);
            objectAnimator2 = ObjectAnimator.ofFloat(tmv_style_first, "translationY", -40);
        } else {
            objectAnimator = ObjectAnimator.ofFloat(tmv_style_first, "translationX", 0f);
            objectAnimator2 = ObjectAnimator.ofFloat(tmv_style_first, "translationY", 0f);
        }
        animatorSet.play(objectAnimator).with(objectAnimator2);
        animatorSet.setDuration(duration);
        animatorSet.start();

    }

    private void up_tmsv_style_first() {

        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator objectAnimator;
        ObjectAnimator objectAnimator2;
        if (!isRecover) {
            objectAnimator = ObjectAnimator.ofFloat(tmsv_style_first, "translationX", tmsv_style_first.getWidth() + 24);
            objectAnimator2 = ObjectAnimator.ofFloat(tmsv_style_first, "translationY", -40);
        } else {
            objectAnimator = ObjectAnimator.ofFloat(tmsv_style_first, "translationX", 0f);
            objectAnimator2 = ObjectAnimator.ofFloat(tmsv_style_first, "translationY", 0f);
        }
        animatorSet.play(objectAnimator).with(objectAnimator2);
        animatorSet.setDuration(duration);
        animatorSet.start();

    }

    private void up_tsv_style_second() {
        if (!isRecover) {
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(tsv_style_second, "translationY", -40);
            objectAnimator.setDuration(duration);
            objectAnimator.start();
            objectAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    tsv_style_second.setVisibility(View.VISIBLE);
                    styleId=1;
                    setIndicator(styleId);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
        } else {
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(tsv_style_second, "translationY", 0f);
            objectAnimator.setDuration(duration);
            objectAnimator.start();
            objectAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    tsv_style_second.setVisibility(View.GONE);
                    styleId=0;
                    setIndicator(styleId);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
        }

    }

    private void up_tv_style_first() {
        if (!isRecover) {
            AnimatorSet animatorSet = new AnimatorSet();
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(tv_style_first, "scaleX", 1f, 0.32f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(tv_style_first, "scaleY", 1f, 0.32f);
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(tv_style_first, "translationY", -82);
            animatorSet.play(objectAnimator).with(scaleY).with(scaleX);
            animatorSet.setDuration(duration);
            animatorSet.start();
            animatorSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    tv_style_first.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
        } else {
            AnimatorSet animatorSet = new AnimatorSet();
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(tv_style_first, "scaleX", 1f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(tv_style_first, "scaleY", 1f);
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(tv_style_first, "translationY", 0);
            animatorSet.play(objectAnimator).with(scaleY).with(scaleX);
            animatorSet.setDuration(duration);
            animatorSet.start();
            animatorSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    tv_style_first.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
        }

    }

    private void up_ctv_style_first() {
        if (!isRecover) {
            AnimatorSet animatorSet = new AnimatorSet();
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(ctv_style_first, "scaleX", 1f, 0.32f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(ctv_style_first, "scaleY", 1f, 0.32f);
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(ctv_style_first, "translationY", -82);
            animatorSet.play(objectAnimator).with(scaleY).with(scaleX);
            animatorSet.setDuration(duration);
            animatorSet.start();
            animatorSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    ctv_style_first.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
        } else {
            AnimatorSet animatorSet = new AnimatorSet();
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(ctv_style_first, "scaleX", 1f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(ctv_style_first, "scaleY", 1f);
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(ctv_style_first, "translationY", 0);
            animatorSet.play(objectAnimator).with(scaleY).with(scaleX);
            animatorSet.setDuration(duration);
            animatorSet.start();
            animatorSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    ctv_style_first.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
        }


    }

    private void up_tv_time() {
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator scaleX;
        ObjectAnimator scaleY;
        if (!isRecover) {
            scaleX = ObjectAnimator.ofFloat(tv_time, "scaleX", 1f, 2f);
            scaleY = ObjectAnimator.ofFloat(tv_time, "scaleY", 1f, 2f);
        } else {
            scaleX = ObjectAnimator.ofFloat(tv_time, "scaleX", 1f);
            scaleY = ObjectAnimator.ofFloat(tv_time, "scaleY", 1f);
        }
        animatorSet.play(scaleX).with(scaleY);
        animatorSet.setDuration(duration);
        animatorSet.start();

    }

    /**
     * 设置指示器
     *
     * @param position
     */
    private void setIndicator(int position) {
        iv_img1.setImageResource(R.drawable.bg_shape_stopwatch_dots_grey);
        iv_img2.setImageResource(R.drawable.bg_shape_stopwatch_dots_grey);
        iv_img3.setImageResource(R.drawable.bg_shape_stopwatch_dots_grey);
        switch (position) {
            case 0:
                iv_img1.setImageResource(R.drawable.bg_shape_stopwatch_dots);
                break;
            case 1:
                iv_img2.setImageResource(R.drawable.bg_shape_stopwatch_dots);
                break;
            case 2:
                iv_img3.setImageResource(R.drawable.bg_shape_stopwatch_dots);
                break;
        }

    }

    @Override
    protected void initEvent() {
        super.initEvent();
        iv_star_or_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mPresenter.startPauseTimer();
            }
        });

        iv_lap_or_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.resetLapTimer();
            }
        });
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy: ");
        super.onDestroy();
        //超过30分钟自动结束
        if (elapsedTime - 30 * 60 * 1000 > 0) {
            elapsedTime = 0;
            updateTimer(elapsedTime);
            mPresenter.removeTimer();
        }
    }
}
