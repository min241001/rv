package com.android.launcher3.moudle.timer.view;


import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.launcher3.R;
import com.android.launcher3.common.base.BaseActivity;
import com.android.launcher3.moudle.timer.adpater.TimerAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * * Author : fangzheng
 * Date : 2024/9/24
 * Details : 所有计时器列表界面
 */


public class TimerAllActivity extends BaseActivity {
    private ImageButton  Imgbtn_rencent_time;
    private TextView tx_recently_used;
    private RelativeLayout layout_recently_used;
    private List<String> dataList;
    private RecyclerView recyclerView;
    private TimerAdapter adapter;
    private static final int REQUEST_CODE=1;
    private ImageButton btn_add;
    private ImageButton btn_cancle;
    private TextView current_tx;

    @Override
    protected int getResourceId() {
        return R.layout.activity_timer_alltimer;
    }

    @Override
    protected void initView() {
        super.initView();
        btn_add=findViewById(R.id.btn_add);
        btn_cancle=findViewById(R.id.Image_Back);
        current_tx=findViewById(R.id.tx_current_time);
        tx_recently_used=findViewById(R.id.tx_recently_used);
        layout_recently_used=findViewById(R.id.layout_recently_used);
        Imgbtn_rencent_time=findViewById(R.id.Imgbtn_rencent_time);

        btn_add.setOnClickListener(v -> {  //进入时间选择器界面
            Intent intent=new Intent(TimerAllActivity.this,TimerAddActivity.class);
            startActivityForResult(intent,REQUEST_CODE);
        });


        btn_cancle.setOnClickListener(v -> {  //返回
               onBackPressed();
        });


        Imgbtn_rencent_time.setOnClickListener(v -> { //进入计时器暂停界面
              Intent intent1=new Intent(TimerAllActivity.this,TimerPauseActivity.class);
            //  Log.e(TAG,currentTime);
            //  intent1.putExtra("time",currentTime);
              startActivity(intent1);
        });




        recyclerView = findViewById(R.id.recyclerView); //添加适配器
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // 设置为2列
        dataList = new ArrayList<>();

    }

    @Override
    protected void initData() {
        super.initData();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { //获取上一个页面返回的数据
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    int resultData1 = data.getIntExtra("hour",0);
                    int resultData2 = data.getIntExtra("minute",0);
                    int resultData3 = data.getIntExtra("second",0);

                    // 格式化输出
                    String currentTime = String.format("%02d:%02d:%02d", resultData1,resultData2,resultData3);
                    current_tx.setText(currentTime);
                    tx_recently_used.setVisibility(View.VISIBLE);//最近使用可见
                    layout_recently_used.setVisibility(View.VISIBLE);


                    dataList.add(currentTime);
                    adapter = new TimerAdapter(dataList);
                    recyclerView.setAdapter(adapter);

                }
            }
        }
    }





}
