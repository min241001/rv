package com.android.launcher3.moudle.island;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.launcher3.R;
import com.android.launcher3.common.base.BaseRecyclerAdapter;
import com.android.launcher3.common.base.BaseRecyclerHolder;
import com.android.launcher3.common.utils.AppUtils;
import com.lzf.easyfloat.EasyFloat;

import java.util.List;

public class IslandAdapter extends BaseRecyclerAdapter<IslandMsgBean> {
    private Context context;

    public IslandAdapter(Context context, List<IslandMsgBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
        this.context = context;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void convert(BaseRecyclerHolder holder, IslandMsgBean item, int position, boolean isScrolling) {
        ImageView iv_icon = holder.getView(R.id.iv_icon);
        TextView tv_text = holder.getView(R.id.tv_text);

        int length = tv_text.getText().toString().toCharArray().length;
        if (length >= 6) {
            tv_text.setSingleLine(true);
            tv_text.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            tv_text.setMarqueeRepeatLimit(-1);
            tv_text.setOverScrollMode(TextView.OVER_SCROLL_NEVER);
            tv_text.setFocusable(true);
            tv_text.setHorizontallyScrolling(true);
            tv_text.setFreezesText(true);
            tv_text.setSelected(true);
            tv_text.requestFocus();
        }

        iv_icon.setImageDrawable(item.getAppIcon());
        tv_text.setText(item.getMsg());
    }
}
