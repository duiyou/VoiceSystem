package com.example.TranslateInterface;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;

import com.example.AllUtil.SpeechRecognitionSDKManager;
import com.example.AllUtil.SpeechSynthesisManager;
import com.example.AllUtil.SpeechWakeUpManager;
import com.example.voicesystem.BaseActivity;
import com.example.voicesystem.R;



public class TranslateActivity  extends BaseActivity {

    private static final String TAG = "TranslateActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.translateactivity_layout);
        //开启活动以后直接开启界面工具类
        InterfaceUtil util = new InterfaceUtil(TranslateActivity.this);
    }

    //销毁时记得销毁语音识别工具和语音合成工具
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(SpeechRecognitionSDKManager.instance != null){
            SpeechRecognitionSDKManager.instance.release();
            Log.d(TAG, "onDestroy: 始放语音识别");
        }
        if(SpeechSynthesisManager.mSpeechSynthesisUtil != null){
            SpeechSynthesisManager.mSpeechSynthesisUtil.Destpry();
            Log.d(TAG, "onDestroy: 销毁语音合成");
        }
        if(SpeechWakeUpManager.instance != null){
            SpeechWakeUpManager.instance.release();
            Log.d(TAG, "onDestroy: 始放语音唤醒");
        }
        //销毁活动
        finish();
    }

    //开始这个活动
    public static void actionStart(Context context){
        Intent intent=new Intent(context, TranslateActivity.class);
        context.startActivity(intent);
    }


}
