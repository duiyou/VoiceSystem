package com.example.voicesystem;



import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.ChatInterface.ChatActivity;
import com.example.SpeechSetInterface.SpeechSetActivity;

/**
 * 主活动第一个碎片
 */
public class MainActivityFragment1 extends Fragment implements View.OnClickListener{
    private View view;
    private Toolbar toolbar;
    private MainActivity mainActivity;
    private RelativeLayout relativeLayout1;
    private RelativeLayout relativeLayout6;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //这个碎片加载的布局
        view=inflater.inflate(R.layout.mainactivity_fragment1,container,false);
        //碎片所在的活动
        mainActivity=(MainActivity)getActivity();
        //标题栏
        toolbar=(Toolbar)view.findViewById(R.id.mainactivity_fragment1_Toolbar);
        //智能聊天按钮
        relativeLayout1=view.findViewById(R.id.mainactivity_fragment1_bottom_relativelayout1);
        relativeLayout1.setOnClickListener(this);//设置监听
        //语音设置按钮
        relativeLayout6=view.findViewById(R.id.mainactivity_fragment1_bottom_relativelayout6);
        relativeLayout6.setOnClickListener(this);//设置监听
        //创建碎片时设置活动中的ActionBar
        setActionBar();
        return view;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mainactivity_fragment1_bottom_relativelayout1:
                //开启ChatActivity
                ChatActivity.actionStart((Context)mainActivity);
                break;
            case R.id.mainactivity_fragment1_bottom_relativelayout6:
                //开启ChatActivity
                SpeechSetActivity.actionStart((Context)mainActivity);
                break;
        }
    }
    //设置标题栏
    private void setActionBar(){
        toolbar.setTitle("APP名称xxx");
        //设置标题栏为Toolbar
        mainActivity.setSupportActionBar(toolbar);
        ActionBar actionBar=mainActivity.getSupportActionBar();
        //有导航键
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
}
