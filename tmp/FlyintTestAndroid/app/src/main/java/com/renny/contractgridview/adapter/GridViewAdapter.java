package com.renny.contractgridview.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.renny.contractgridview.R;
import com.renny.contractgridview.bean.AppInfoBean;

import java.util.List;

/**
 * Created by pengmin on 2024/8/30.
 */
public class GridViewAdapter extends BaseAdapter
{

    /**
     * listview中的数据集
     */
    private List<AppInfoBean> mDataList;

    private Context               mContext;
    private GridView mgv;

    public GridViewAdapter(List<AppInfoBean> list, Context context)
    {
        this.mDataList = list;
        this.mContext = context;
    }

    /**
     * 设置listview对象
     *
     * @param lisv
     */
    public void setListView(GridView gv)
    {
        this.mgv = gv;
    }


    @Override
    public int getCount()
    {
        // TODO Auto-generated method stub
        return mDataList.size();
    }

    @Override
    public Object getItem(int position)
    {
        // TODO Auto-generated method stub
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        // TODO Auto-generated method stub
        if (convertView == null)
        {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.vertical_overlay_fragment_gv_item, null);
        }

        ImageView iv = (ImageView) convertView.findViewById(R.id.item_card_gv_iv);
        iv.setImageDrawable(mDataList.get(position).getApp_icon());
        ImageView iv_del = (ImageView)convertView.findViewById(R.id.item_card_gv_item_del);
        iv_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return convertView;
    }

}
