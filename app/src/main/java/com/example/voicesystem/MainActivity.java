package com.example.voicesystem;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;

import com.example.ChatInterface.SpeechSynthesisUtil;

/**
 * 主活动
 */
public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";
    //碎片ID
    private int fragmentID = R.id.mainactivity_fragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //得到许可
        getpermissions();
        setContentView(R.layout.mainactivity_layout);
        //碎片1
        MainActivityFragment1 mainActivityFragment1=new MainActivityFragment1();
        //初始化的时候就用碎片1
        replaceFragment(mainActivityFragment1);
    }

    @Override
    //啥时候调用这个方法的？？创建菜单，
    public boolean onCreateOptionsMenu(Menu menu) {
        //如果在界面中找到主碎片的Toolbar就设置菜单
        if(findViewById(R.id.mainactivity_fragment1_Toolbar)!=null){
            getMenuInflater().inflate(R.menu.activity_main_layout_main_fragment_toolbar_menu,menu);
        }
        return true;
    }
    //切换碎片（用fragment替换fragmentID这个取域）
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        transaction.replace(fragmentID,fragment);
        transaction.commit();
    }

    //得到许可
    private void getpermissions(){
        //如果安卓版本大于23
        if (Build.VERSION.SDK_INT >= 23) {
            //请求码？？？？？
            int REQUEST_CODE_CONTACT = 101;
            //要申请的权限
            String[] permissions = {Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.MODIFY_AUDIO_SETTINGS,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.WAKE_LOCK,
                    Manifest.permission.CHANGE_NETWORK_STATE,
                    Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.INTERNET};
            //验证是否许可权限
            for (String str : permissions) {
                if (ContextCompat.checkSelfPermission(MainActivity.this,str) != PackageManager.PERMISSION_GRANTED) {
                    Log.d("AudioRecordButton","没权限");
                    //一次把整个permissions的权限都申请了，直接return
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                    return;
                }
            }
        }
    }


}
