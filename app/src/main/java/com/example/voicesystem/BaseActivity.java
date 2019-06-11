package com.example.voicesystem;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * 统一活动类，用来管理活动进出活动管理器
 */
public class BaseActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //打印当前活动名
        //Log.d("BaseActivity",getClass().getSimpleName());

        ActivityCollector.addActivity(this);

    }
    protected void onDestroy(){
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
