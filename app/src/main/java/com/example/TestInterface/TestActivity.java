package com.example.TestInterface;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AndroidRuntimeException;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.baidu.speech.EventListener;
import com.baidu.speech.asr.SpeechConstant;
import com.example.AllUtil.SpeechRecognitionSDKManager;
import com.example.AllUtil.SpeechWakeUpManager;
import com.example.voicesystem.BaseActivity;
import com.example.voicesystem.R;
import com.tuyenmonkey.mkloader.MKLoader;

import org.json.JSONException;
import org.json.JSONObject;


public class TestActivity extends BaseActivity implements EventListener {
    private static final String TAG = "TestActivity";

    private boolean isPress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testactivity_layout);
        /*
        final SpeechRecognitionSDKManager speechRecognitionSDKManager = SpeechRecognitionSDKManager.getInstance(this,this);
        Button button = findViewById(R.id.test_activity_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isPress){
                    isPress = true;
                    speechRecognitionSDKManager.startSpeech(null,SpeechRecognitionSDKManager.RECOGNITION_TYPE_CHINESE);

                }else{
                    isPress = false;
                    speechRecognitionSDKManager.stop();
                }
            }
        });
        */
        final SpeechWakeUpManager speechWakeUpManager = SpeechWakeUpManager.getInstance(this,this);
        speechWakeUpManager.startWakeUp();
    }
    //
    @Override
    public void onEvent(String name, String params, byte[] data, int offset, int length) {
        //唤醒时间
        if("wp.data".equals(name)){
            try {
                JSONObject json = new JSONObject(params);
                int errorCode = json.getInt("errorCode");
                if(errorCode == 0){
                    //唤醒成功
                    Log.d(TAG, "onEvent: 唤醒成功了");

                    String wakeUpWord = json.getString("word");
                    Log.d(TAG, "onEvent: 唤醒词："+wakeUpWord);
                }else{
                    //唤醒失败
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if("wp.exit".equals(name)){
            //停止唤醒
        }

    }


    //开始这个活动
    public static void actionStart(Context context){
        Intent intent = new Intent(context, TestActivity.class);
        context.startActivity(intent);
    }


}
