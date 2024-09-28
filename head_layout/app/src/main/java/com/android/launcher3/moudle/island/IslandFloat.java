package com.android.launcher3.moudle.island;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.android.launcher3.R;
import com.android.launcher3.common.base.BaseRecyclerAdapter;
import com.android.launcher3.common.utils.AppLauncherUtils;
import com.android.launcher3.common.utils.DensityUtils;
import com.android.launcher3.moudle.notification.bean.NotificationBean;
import com.android.launcher3.moudle.notification.queue.IslandNotificationLinkedQueue;
import com.lzf.easyfloat.EasyFloat;
import com.lzf.easyfloat.enums.SidePattern;
import com.lzf.easyfloat.interfaces.OnFloatCallbacks;
import com.lzf.easyfloat.interfaces.OnInvokeView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class IslandFloat {

    public final static String APP_ICON_FLOAT = "APP_ICON_FLOAT";
    public final static String MULTIPLE_MSG_FLOAT = "MULTIPLE_MSG_FLOAT";
    public final static String SINGLE_MSG_FLOAT = "SINGLE_MSG_FLOAT";
    public final static String AUDIO_FLOAT = "AUDIO_FLOAT";


    private Context context;
    private IslandMusicManager islandMusicManager;
    private List<IslandMsgBean> islandMsgBeans = new ArrayList<>();
    private LinkedList<NotificationBean> linkedList;


    public IslandFloat() {

    }

    public IslandFloat(Context context) {
        this.context = context;
        islandMusicManager = new IslandMusicManager(context);
    }


    /**
     * 音频弹窗展示浮窗
     */
    public void showAudioFloat() {

        EasyFloat.with(context)
                .setTag(AUDIO_FLOAT)
                .setLayout(R.layout.dialog_music, new OnInvokeView() {
                    @SuppressLint("ClickableViewAccessibility")
                    @Override
                    public void invoke(View view) {
                        ImageView iv_music_pre = view.findViewById(R.id.iv_music_pre);
                        ImageView iv_music_play = view.findViewById(R.id.iv_music_play);
                        ImageView iv_music_next = view.findViewById(R.id.iv_music_next);
                        LinearLayout ll_music = view.findViewById(R.id.ll_music);
                        TextView tv_music_name = view.findViewById(R.id.tv_music_name);
                        iv_music_pre.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.e("TAG", "上一首");
                                islandMusicManager.preMusic();
                            }
                        });

                        iv_music_play.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.e("TAG", "播放或暂停");
                                islandMusicManager.playOrPause();
                            }
                        });

                        iv_music_next.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.e("TAG", "下一首");
                                islandMusicManager.nexMusic();
                            }
                        });


                        ll_music.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                Intent intent = new Intent();
                                //com.ximalayaos.wearkid/.ui.play.PlayActivity
                                //播放页跳转异常 跳转home页
                                ComponentName comp = new ComponentName("com.ximalayaos.wearkid", "com.ximalayaos.wearkid.ui.home.HomeActivity");
                                intent.setComponent(comp);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                                return false;
                            }
                        });

                        ll_music.setOnTouchListener(new View.OnTouchListener() {
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
                                        Log.d("TouchTest", "distanceX: " + distanceX + ", distanceY: " + distanceY);
                                        if (Math.abs(distanceY) > 20 && Math.abs(distanceY) > Math.abs(distanceX) && distanceY > 0) {
                                            //下滑
                                        }
                                        //上滑显示应用图标浮窗
                                        if (Math.abs(distanceY) > 20 && Math.abs(distanceY) > Math.abs(distanceX) && distanceY < 0) {
                                            //上滑
                                            EasyFloat.dismiss(AUDIO_FLOAT);
                                            showAppIconFloat();
                                            return true; // 返回true表示消费了这个事件，不再传递给其他监听器
                                        }
                                        break;
                                    case MotionEvent.ACTION_UP:
                                        // 手指抬起时的逻辑处理
                                        break;
                                }


                                return false;
                            }
                        });

                        //TODO:当前音频播放曲目 待处理
                        tv_music_name.setText("喜马拉雅少儿");
                        int length = tv_music_name.getText().toString().toCharArray().length;
                        if (length >= 6) {
                            tv_music_name.setSingleLine(true);
                            tv_music_name.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                            tv_music_name.setMarqueeRepeatLimit(-1);
                            tv_music_name.setOverScrollMode(TextView.OVER_SCROLL_NEVER);
                            tv_music_name.setFocusable(true);
                            tv_music_name.setHorizontallyScrolling(true);
                            tv_music_name.setFreezesText(true);
                            tv_music_name.setSelected(true);
                            tv_music_name.requestFocus();
                        }

                    }
                })
                .setSidePattern(SidePattern.TOP)
                .setGravity(Gravity.CENTER_HORIZONTAL, 0, DensityUtils.dip2px(10, context))
                .setLayoutChangedGravity(Gravity.CENTER_HORIZONTAL)
                .setDragEnable(false)
                .show();
    }


    /**
     * 消息展示浮窗
     */
    public void showMultipleMsg() {

        if (linkedList == null || linkedList.size() == 0) {
            return;
        }

        EasyFloat.with(context)
                .setTag(MULTIPLE_MSG_FLOAT)
                .setLayout(R.layout.dialog_island_common, new OnInvokeView() {
                    @Override
                    public void invoke(View view) {

                        RecyclerView rv_island_list = view.findViewById(R.id.rv_island_list);
                        rv_island_list.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                        IslandAdapter islandAdapter = new IslandAdapter(context, islandMsgBeans, R.layout.dialog_island_common_item);
                        rv_island_list.setAdapter(islandAdapter);

//                        rv_island_list.setLoopEnabled(true);//设置无限滚动
//                        rv_island_list.openAutoScroll();//开启滚动
//                        rv_island_list.setCanTouch(true);//是否可手动滑动

                        islandAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(RecyclerView parent, View view, int position) {

                                switch (islandAdapter.getItem(position).getAppName()) {
                                    case "电话":
                                        //跳转到对应应用
                                        Log.e("TAG", "--电话未读点击了--");
                                        AppLauncherUtils.launchApp(context, "com.baehug.dialer");
                                        IslandNotificationLinkedQueue.getInstance().removeQueue(linkedList.get(position));
                                        islandMsgBeans.remove(position);
                                        EasyFloat.dismiss(MULTIPLE_MSG_FLOAT);
                                        break;
                                    case "微聊":
                                        Log.e("TAG", "--微聊未读点击了--");
                                        AppLauncherUtils.launchApp(context, "com.baehug.watch.wechat");
                                        IslandNotificationLinkedQueue.getInstance().removeQueue(linkedList.get(position));
                                        islandMsgBeans.remove(position);
                                        EasyFloat.dismiss(MULTIPLE_MSG_FLOAT);
                                        break;
                                    case "短信":
                                        Log.e("TAG", "--短信未读点击了--");
                                        AppLauncherUtils.launchApp(context, "com.baehug.sms");
                                        IslandNotificationLinkedQueue.getInstance().removeQueue(linkedList.get(position));
                                        islandMsgBeans.remove(position);
                                        EasyFloat.dismiss(MULTIPLE_MSG_FLOAT);
                                        break;
                                    case "喜马拉雅少儿":
                                        Intent intent = new Intent();
                                        //这里跳转的是淘宝的启动页
                                        ComponentName comp = new ComponentName("com.ximalayaos.wearkid", "com.ximalayaos.wearkid.ui.home.HomeActivity");
                                        intent.setComponent(comp);
                                        //为三方的activity新开任务栈
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        context.startActivity(intent);

                                        IslandNotificationLinkedQueue.getInstance().removeQueue(linkedList.get(position));
                                        islandMsgBeans.remove(position);
                                        EasyFloat.dismiss(MULTIPLE_MSG_FLOAT);
                                        break;

                                    case "懒人听书":
                                        Intent intent2 = new Intent();
                                        //这里跳转的是淘宝的启动页
                                        ComponentName comp2 = new ComponentName("bubei.tingshu.wear", "bubei.tingshu.wear.ui.home.HomeActivity");
                                        intent2.setComponent(comp2);
                                        //为三方的activity新开任务栈
                                        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        context.startActivity(intent2);

                                        IslandNotificationLinkedQueue.getInstance().removeQueue(linkedList.get(position));
                                        islandMsgBeans.remove(position);
                                        EasyFloat.dismiss(MULTIPLE_MSG_FLOAT);
                                        break;
                                }


                                //TODO:消息浮窗点击跳转后返回后 浮窗的展示逻辑

                                //跳转后存在消息 展示图标浮窗?
                                //后台有音频播放 展示音频浮窗?
                                //优先级 优先展示哪类浮窗

                                if (islandMsgBeans != null && islandMsgBeans.size() > 0) {
                                    if (EasyFloat.getFloatView(AUDIO_FLOAT) != null) {
                                        EasyFloat.show(AUDIO_FLOAT);
                                    } else {
                                        showAppIconFloat();
                                    }

                                } else {
                                    if (EasyFloat.getFloatView(AUDIO_FLOAT) != null) {
                                        EasyFloat.show(AUDIO_FLOAT);
                                    }
                                    EasyFloat.dismiss(APP_ICON_FLOAT);
                                }

                            }
                        });


                        islandAdapter.setOnTouchListener(new BaseRecyclerAdapter.OnTouchListener() {

                            private float startX;
                            private float startY;
                            private float distanceX;
                            private float distanceY;

                            @Override
                            public boolean onTouch(RecyclerView parent, View view, int position, MotionEvent motionEvent) {

                                Log.e("TAG", "--onTouch-------");

                                switch (motionEvent.getAction()) {
                                    case MotionEvent.ACTION_DOWN:
                                        startX = motionEvent.getX();
                                        startY = motionEvent.getY();
                                        break;
                                    case MotionEvent.ACTION_MOVE:
                                        distanceX = motionEvent.getX() - startX;
                                        distanceY = motionEvent.getY() - startY;
                                        // 处理滑动距离，可以根据需要进行逻辑处理
                                        Log.d("TouchTest--MSG", "distanceX: " + distanceX + ", distanceY: " + distanceY);
                                        if (Math.abs(distanceY) > 20 && Math.abs(distanceY) > Math.abs(distanceX) && distanceY > 0) {
                                            //下滑

                                        }

                                        //上滑显示应用图标浮窗
                                        if (Math.abs(distanceY) > 20 && Math.abs(distanceY) > Math.abs(distanceX) && distanceY < 0) {
                                            //上滑
                                            EasyFloat.dismiss(MULTIPLE_MSG_FLOAT);
                                            showAppIconFloat();

                                            return true; // 返回true表示消费了这个事件，不再传递给其他监听器
                                        }
                                        break;
                                    case MotionEvent.ACTION_UP:
                                        // 手指抬起时的逻辑处理
                                        view.performClick();
                                        break;
                                }


                                return false;

                            }
                        });

                    }
                })
                .setSidePattern(SidePattern.TOP)
                .setGravity(Gravity.CENTER_HORIZONTAL, 0, DensityUtils.dip2px(40, context))
                .setLayoutChangedGravity(Gravity.CENTER_HORIZONTAL)
                .setAnimator(null)
                .setDragEnable(false)
                .show();
    }


    /**
     * 应用图标浮窗
     */
    public void showAppIconFloat() {

        if (islandMsgBeans != null && islandMsgBeans.size() > 0) {
            islandMsgBeans.clear();
        }

        linkedList = IslandNotificationLinkedQueue.getInstance().getNoRepeatQueues();
        if (linkedList != null && linkedList.size() > 0) {
            for (int i = 0; i < linkedList.size(); i++) {
                islandMsgBeans.add(new IslandMsgBean(linkedList.get(i).getContent(), linkedList.get(i).getAppName(), linkedList.get(i).getPackageName(), linkedList.get(i).getAppIcon()));
            }
        }

        if (islandMsgBeans != null && islandMsgBeans.size() > 0) {
            EasyFloat.with(context)
                    .setTag(APP_ICON_FLOAT)
                    .setLayout(R.layout.dialog_island_app_icon, new OnInvokeView() {
                        @Override
                        public void invoke(View view) {

                            RecyclerView rv_island_icon_list = view.findViewById(R.id.rv_island_icon_list);
                            rv_island_icon_list.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                            IslandIconAdapter islandIconAdapter = new IslandIconAdapter(context, islandMsgBeans, R.layout.dialog_island_app_icon_item);
                            rv_island_icon_list.setAdapter(islandIconAdapter);
                            //每次滚动一个item条目
                            //PagerSnapHelper snapHelper = new PagerSnapHelper();
                            //snapHelper.attachToRecyclerView(rv_island_icon_list);
                            islandIconAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(RecyclerView parent, View view, int position) {
                                    EasyFloat.dismiss(APP_ICON_FLOAT);
                                    showMultipleMsg();
                                }
                            });

                        }
                    })
                    .setSidePattern(SidePattern.TOP)
                    .setGravity(Gravity.CENTER_HORIZONTAL, 0, DensityUtils.dip2px(60, context))
                    .setLayoutChangedGravity(Gravity.CENTER_HORIZONTAL)
                    .setAnimator(null)
                    .setDragEnable(false)
                    .show();
        }

    }


    /**
     * 对于同一应用只展示一条悬浮记录
     */
    public List<IslandMsgBean> removeDuplicationBy2For() {
        if (islandMsgBeans != null) {
            islandMsgBeans.clear();
        }

        linkedList = IslandNotificationLinkedQueue.getInstance().getQueues();
        if (linkedList != null && linkedList.size() > 0) {
            for (int i = 0; i < linkedList.size(); i++) {

                /*if (linkedList.get(i).getPackageName().equals("com.baehug.watch.wechat")
                        || linkedList.get(i).getPackageName().equals("com.baehug.videochat")
                        || linkedList.get(i).getPackageName().equals("com.baehug.sms")
                        || linkedList.get(i).getPackageName().equals("com.baehug.dialer"))
                    islandMsgBeans.add(new IslandMsgBean(linkedList.get(i).getContent(), linkedList.get(i).getAppIcon(), linkedList.get(i).getAppName()));*/

                islandMsgBeans.add(new IslandMsgBean(linkedList.get(i).getContent(), linkedList.get(i).getAppName(), linkedList.get(i).getPackageName(), linkedList.get(i).getAppIcon()));
            }
        }

        List<IslandMsgBean> noRepeatList = getNoRepeatList(islandMsgBeans);
        islandMsgBeans.clear();
        islandMsgBeans.addAll(noRepeatList);

        return islandMsgBeans;
    }


    private List<IslandMsgBean> getNoRepeatList(List<IslandMsgBean> oldList) {
        List<IslandMsgBean> list = new ArrayList<>();
        for (IslandMsgBean islandMsgBean : oldList) {
            if (!list.contains(islandMsgBean)) {
                list.add(islandMsgBean);
            }
        }
        return list;
    }


    public void disMissAllFloat() {
        Log.e("TAG", "隐藏所有浮窗");
        EasyFloat.dismiss(APP_ICON_FLOAT);
        EasyFloat.dismiss(MULTIPLE_MSG_FLOAT);
        EasyFloat.dismiss(SINGLE_MSG_FLOAT);
        EasyFloat.dismiss(AUDIO_FLOAT);

    }


}
