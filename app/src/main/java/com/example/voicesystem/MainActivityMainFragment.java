package com.example.voicesystem;



import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainActivityMainFragment extends Fragment {

    private View view;
    private Toolbar toolbar;
    private MainActivity mainActivity;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.main_activity_main_fragment,container,false);
        toolbar=(Toolbar)view.findViewById(R.id.main_activity_main_fragment_top_Toolbar);
        setMainFragmentActionBar();
        return view;
    }
    //主活动主碎片设置标题栏
    private void setMainFragmentActionBar(){
        mainActivity=(MainActivity)getActivity();
        mainActivity.setSupportActionBar(toolbar);
        initializeToolbar();
    }
    //初始化标题栏
    private void initializeToolbar(){
        toolbar.setTitle("APP名称xxx");
        ActionBar actionBar=mainActivity.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
}
