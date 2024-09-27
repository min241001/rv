package com.renny.contractgridview;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.launcher3.desktop1.Desktop1Fragment;
import com.android.launcher3.desktop2.Desktop2Fragment;
import com.android.launcher3.desktop3.Desktop3Fragment;
import com.android.launcher3.desktop4.Desktop4Fragment;
import com.android.launcher3.desktop6.Desktop6Fragment;
import com.example.desktop7.view.Desktop7Fragment;

public class FragmentActivity extends AppCompatActivity {
    FragmentManager fragmentManager;
    FragmentTransaction transaction;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
//Fragment启动方法封装
        replaceFragment(new Desktop1Fragment());

    }
    private void replaceFragment(Fragment fragment) {
// 1.获取FragmentManager，在活动中可以直接通过调用getFragmentManager()方法得到
        fragmentManager =getSupportFragmentManager();
// 2.开启一个事务，通过调用beginTransaction()方法开启
        transaction = fragmentManager.beginTransaction();
// 3.向容器内添加或替换碎片，一般使用replace()方法实现，需要传入容器的id和待添加的碎片实例
        transaction.replace(R.id.fr_container, fragment);  //fr_container不能为fragment布局，可使用线性布局相对布局等。
// 4.使用addToBackStack()方法，将事务添加到返回栈中，填入的是用于描述返回栈的一个名字
        transaction.addToBackStack(null);
// 5.提交事物,调用commit()方法来完成
        transaction.commit();
    }
}
