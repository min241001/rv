package com.antiphon.recyclerviewdemo.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.antiphon.recyclerviewdemo.R;
import com.antiphon.recyclerviewdemo.adapter.HeadFootViewAdapter;
import com.antiphon.recyclerviewdemo.view.BottomScaleLayoutManager;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

/**
 * 头布局尾部局
 */
public class HeadFootViewActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerview);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        //创建布局管理器-线性布局
        List<String> stringList = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            stringList.add("第 " + i + " 个item");
        }
        //StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        GridLayoutManager layoutManager = new GridLayoutManager(this,3);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position>6) {
                    return 3;
                }
                return 1;
            }
        });
        //LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        //数据适配器
        HeadFootViewAdapter adapter = new HeadFootViewAdapter(this, stringList,recyclerView);
        recyclerView.setAdapter(adapter);
        //设置数据






        adapter.setHeadView();//添加头布局
        adapter.setFootView();//添加尾布局
    }
}
