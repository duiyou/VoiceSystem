package com.example.voicesystem;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.support.v7.widget.Toolbar;


public class MainActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);
        MainActivityMainFragment mainActivityMainFragment=new MainActivityMainFragment();
        replaceFragment(mainActivityMainFragment);
    }

    @Override
    //啥时候调用这个方法的？？
    public boolean onCreateOptionsMenu(Menu menu) {
        //如果在界面中找到主碎片的Toolbar就设置菜单
        if(findViewById(R.id.main_activity_main_fragment_top_Toolbar)!=null){
            getMenuInflater().inflate(R.menu.activity_main_layout_main_fragment_toolbar_menu,menu);
        }
        return true;
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        transaction.replace(R.id.main_activity_fragment,fragment);
        transaction.commit();
    }
}
