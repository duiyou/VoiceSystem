package com.example.TranslateInterface;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.voicesystem.BaseActivity;
import com.example.voicesystem.R;

public class TranslateActivity  extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.translateactivity_layout);
        //初始化部件
        initComponent();
    }
    //初始化部件
    private void initComponent(){

    }

    //开始这个活动
    public static void actionStart(Context context){
        Intent intent=new Intent(context, TranslateActivity.class);
        context.startActivity(intent);
    }
}
