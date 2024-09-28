package com.android.launcher3.desktop6;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.launcher3.common.base.BaseRecyclerAdapter;
import com.android.launcher3.common.base.WallpaperFragment;
import com.android.launcher3.common.utils.LogUtil;
import com.android.launcher3.desktop6.cutomlayoutmanager.PathLayoutManager;

/**
 * Author : liwenlin
 * Date : 2024/5/10
 * Details : 齿轮风格
 */
public class Desktop6Fragment extends WallpaperFragment {

    private static final String TAG = "Desktop6Fragment";
    private RecyclerView recyclerView;
    private ImageView imageView;
    private RelativeLayout rl;
    private Path path;
    private PathLayoutManager mPathLayoutManager;
    private Desktop6Adapter2 adapter;
    private DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();

    @Override
    protected int getResourceId() {
        return R.layout.fragment_desktop6;
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        recyclerView = mRootView.findViewById(R.id.recycler_view);
        imageView = mRootView.findViewById(R.id.iv_gear);
        imageView.setClickable(true);
        rl = mRootView.findViewById(R.id.rl);

    }

    private int measuredHeight;//齿轮图高度
    private int measuredWidth;//齿轮图宽度
    private int iconHeight;//item 图标高度


    @Override
    protected void initData() {
        super.initData();

        //获取屏幕宽高
        WindowManager manager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;

        //获取齿轮图片宽高 设置滑动轨迹
        imageView.post(new Runnable() {
            @Override
            public void run() {

                measuredWidth = imageView.getMeasuredWidth();
                measuredHeight = imageView.getMeasuredHeight();
                LogUtil.i(TAG, "post: 齿轮宽度：" + measuredWidth + " 齿轮高度：" + measuredHeight + "left" + imageView.getLeft(), LogUtil.TYPE_RELEASE);

                //30为滑动条目图标半径
                //4、8、10目视调节显示最合适位置
                //圆弧轨迹位置：4个浮动矩形坐标
                int top = screenHeight / 2 - measuredHeight / 2 - 50 - 0;
                int bottom = measuredHeight + top + 50 * 2 + 20;
                int left = measuredWidth / 2 + 50 + 14;
                int right = measuredWidth / 2 + 50 + 14;

                LogUtil.i(TAG, "post: screenWidth " + screenWidth + " screenHeight " + screenHeight + " Top "
                        + top + " bottom " + bottom + " left " + left + " right " + right, LogUtil.TYPE_RELEASE);

                path = new Path();
                RectF rectF = new RectF(-left, top, right, bottom);

                //测试能做到圆弧贴近齿轮的数值
                //RectF rectF = new RectF(-120, 25, 120, 260);
                //-90到180圆弧
                path.addArc(rectF, -90, 180);

                mPathLayoutManager = new PathLayoutManager(path, 95);//偏移值后期处理
                //PathLayoutManager 相关配置
                mPathLayoutManager.setItemDirectionFixed(true);//保持垂直
                mPathLayoutManager.setScrollMode(PathLayoutManager.SCROLL_MODE_LOOP);//无限循环
                mPathLayoutManager.setOrientation(RecyclerView.VERTICAL);//垂直滚动
                mPathLayoutManager.setAutoSelect(true);//自动选择
                mPathLayoutManager.setAutoSelectFraction(0.5f);//自动选择位置偏移
                mPathLayoutManager.setFlingEnable(true);

                recyclerView.setLayoutManager(mPathLayoutManager);

            }
        });

        adapter = new Desktop6Adapter2(getContext(), getLists(), R.layout.list_item_desktop6);
        recyclerView.setAdapter(adapter);

        /*recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }

                adapter = new Desktop6Adapter2(getContext(), getLists(), R.layout.list_item_desktop6);
                recyclerView.setAdapter(adapter);

                adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(RecyclerView parent, View view, int position) {
                        lunchApp(getLists().get(position));//打开position对应应用
                    }
                });

            }
        });*/


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {


                    /*int childCount = recyclerView.getChildCount();

                    //firstView
                    View firstChildAt = recyclerView.getChildAt(0);
                    Log.e("TAG---->firstChildAt", firstChildAt + "");
                    RecyclerView.LayoutParams layoutParamsFirst = (RecyclerView.LayoutParams) firstChildAt.getLayoutParams();
                    int firstPosition = layoutParamsFirst.getViewLayoutPosition();
                    Log.e("TAG---->firstPosition", firstPosition + "");

                    //lastView
                    View lastChildAt = recyclerView.getChildAt(childCount - 1);
                    Log.e("TAG---->lastChildAt", lastChildAt + "");
                    RecyclerView.LayoutParams layoutParamsLast = (RecyclerView.LayoutParams) lastChildAt.getLayoutParams();
                    int lastPosition = layoutParamsLast.getViewLayoutPosition();
                    Log.e("TAG---->lastPosition", lastPosition + "");*/

                }

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                for (int i = 0; i < recyclerView.getChildCount(); i++) {
                    recyclerView.getChildAt(i).invalidate();
                }
                float dyy = dy * -1;
                setRotate(imageView, dyy / 4);
            }
        });


    }

    /**
     * 齿轮跟随转动动画
     */
    private float values = 0f;
    private float newValues = 0f;
    private ObjectAnimator rotationAnimator;

    private void setRotate(ImageView imageView, float dy) {
        if (rotationAnimator == null)
            rotationAnimator = ObjectAnimator.ofFloat(imageView, "rotation", 0f, 10f);
        LogUtil.i(TAG, "setRotate: " + dy + "", LogUtil.TYPE_RELEASE);
        newValues = values + dy;
        rotationAnimator.setDuration(10L);
        rotationAnimator.setFloatValues(values, newValues);
        rotationAnimator.start();
        values = newValues;
    }


    @Override
    protected void initEvent() {
        super.initEvent();
        adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position) {
                lunchApp(getLists().get(position));//打开position对应应用
            }
        });
    }


    @Override
    protected void updateView() {
        if (!lists.isEmpty()) {
            adapter.setList(lists);
            adapter.notifyDataSetChanged();
        }
    }
}