package com.android.launcher3.moudle.timer.view;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.launcher3.R;
import com.android.launcher3.common.base.BaseActivity;
import com.android.launcher3.moudle.timer.adpater.MyAdapter;
import com.android.launcher3.moudle.timer.util.ItemUtil;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Author : FangZheng
 * Date : 2024/9/20
 * Details : 计时器主界面
 */
public class TimerMainActivity extends BaseActivity {

    private TextView tx_timer,center_timer;
    private ImageButton btn_1,btn_2;
    private RecyclerView recyclerView;
    private MyAdapter myAdapter;
    private List<ItemUtil> itemList;
    @Override
    protected int getResourceId() {
        return R.layout.activity_timer_main;
    }


    @SuppressLint("WrongViewCast")
    @Override
    protected void initView() {
        super.initView();
        tx_timer=findViewById(R.id.textview1);
        center_timer=findViewById(R.id.center_timer);
        btn_1=findViewById(R.id.ImageButton1);
        btn_2=findViewById(R.id.ImageButton2);


        btn_1.setOnClickListener(v -> {
            onBackPressed();
        });

        btn_2.setOnClickListener(v -> { //跳转到所有计时器界面
            Intent intent=new Intent(TimerMainActivity.this, TimerAllActivity.class);
            startActivity(intent);
        });

    }

    @Override
    protected void initData() {
        super.initData();
        tx_timer.setText("09:30");

        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY); // 获取24小时制的小时
        int minute = calendar.get(Calendar.MINUTE); // 获取分钟
        // 格式化输出
        String currentTime = String.format("%02d:%02d", hour, minute);
        // 设置到 TextView 中显示
        tx_timer.setText(currentTime);


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // 设置布局管理器

        itemList = new ArrayList<>();

        myAdapter = new MyAdapter(itemList); // 创建适配器
        recyclerView.setAdapter(myAdapter); // 设置适配器

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}