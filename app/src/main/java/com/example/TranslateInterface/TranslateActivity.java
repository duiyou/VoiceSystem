package com.example.TranslateInterface;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;


import com.baidu.speech.EventListener;
import com.baidu.speech.asr.SpeechConstant;
import com.example.AllUtil.SpeechRecognitionSDKManager;
import com.example.voicesystem.BaseActivity;
import com.example.voicesystem.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class TranslateActivity  extends BaseActivity implements EventListener {

    private static final String TAG = "TranslateActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.translateactivity_layout);
        InterfaceUtil util = new InterfaceUtil(TranslateActivity.this);
    }

    @Override
    public void onEvent(String name, String params, byte[] data, int offset, int length) {
        String result = "name：" + name;

        if (length > 0 && data.length > 0) {
            result += ", 语义解析结果：" + new String(data, offset, length);
            //Log.d(TAG, "识别到了："+ result);
        }

        if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_READY)) {
            // 引擎准备就绪，可以开始说话
            Log.d(TAG, "准备开始说话：");

        } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_BEGIN)) {
            // 检测到用户的已经开始说话
            Log.d(TAG, "检测到开始说话：");

        } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_END)) {
            // 检测到用户的已经停止说话
            Log.d(TAG, "检测到已经停止说话了：");
        } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL)) {
             //临时识别结果, 长语音模式需要从此消息中取出结果
            result += "识别临时识别结果";
            if (params != null && !params.isEmpty()) {
                result += "params :" + params + "\n";
            }
            //解析返回的JSON数据
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(params);
                //通过key（text）获取value，即获得机器人回复的结果
                String text = jsonObject.getString("results_recognition");
                Log.d(TAG, "临时的real结果是："+ text);
            } catch (JSONException e) {
                //e.printStackTrace();
                Log.d("RobotUtil", "得到机器人回复结果解析有问题");
            }
            //Log.d(TAG, "临时得到的信息："+result);

        } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_FINISH)) {
            // 识别结束， 最终识别结果或可能的错误
            Log.d(TAG, "识别结束了，结果："+result);
        }

    }

    //开始这个活动
    public static void actionStart(Context context){
        Intent intent=new Intent(context, TranslateActivity.class);
        context.startActivity(intent);
    }


}
