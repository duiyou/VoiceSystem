package com.example.AllUtil;

import android.content.Context;
import android.util.Log;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;

import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class SpeechRecognitionSDKManager {

    private static final String TAG = "SpeechRecognitionSDKMan";

    public static final int RECOGNITION_TYPE_CHINESE = 15372;//中文
    public static final int RECOGNITION_TYPE_ENGLISH = 17372;//英文
    public static final int RECOGNITION_TYPE_CANTONESE = 16372;//粤语

    //识别的功能类
    private EventManager asr;
    //识别参数集合
    private Map<String, Object> params;
    //监听识别的监听器
    private EventListener e;

    //构造器，传入Context和监听器
    private SpeechRecognitionSDKManager(Context context, EventListener e){
        init(context,e);
    }
    //单例
    public static SpeechRecognitionSDKManager instance;
    public static synchronized SpeechRecognitionSDKManager getInstance(Context context, EventListener e){
        if(instance == null)instance = new SpeechRecognitionSDKManager(context,e);
        return instance;
    }
    //初始化EventManager管理器
    private void init(Context context,EventListener e){
        asr = EventManagerFactory.create(context, "asr");
        this.e = e;
        asr.registerListener(e);
        params = new LinkedHashMap<String, Object>();


        //自己的
        params.put(SpeechConstant.VAD_ENDPOINT_TIMEOUT,1500);//不开启长语音（1.5s则为超时，表示停止说话）
        //params.put(SpeechConstant.VAD, SpeechConstant.VAD_TOUCH);//不开启长语音时，超时则关闭语音活动检测，要手动关闭语音识别
        params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false);//不需要音量回调
        params.put(SpeechConstant.ACCEPT_AUDIO_DATA, true);//需要音频数据回调
        params.put(SpeechConstant.DISABLE_PUNCTUATION,false);//不禁用标点符号

    }

    public void startSpeech(String outFilePath, int recognitionType){
        //识别输出到的文件
        if(outFilePath != null && outFilePath.trim().length() != 0){
            params.put(SpeechConstant.OUT_FILE, outFilePath);
        }
        //识别类型
        params.put(SpeechConstant.PID, recognitionType);

        //把参数都封装成json数据
        String json = null;
        json = new JSONObject(params).toString();
        //开始识别
        asr.send(SpeechConstant.ASR_START, json, null, 0, 0);
    }

    //发送停止录音事件，提前结束录音等待识别结果
    public void stop() {
        asr.send(SpeechConstant.ASR_STOP, null, null, 0, 0);
    }

    //取消本次识别，取消后将立即停止不会返回识别结果
    public void cancel(){
        asr.send(SpeechConstant.ASR_CANCEL, "{}", null, 0, 0);
    }

    //始放资源
    public void release(){
        //先取消
        cancel();
        //再始放资源
        asr.unregisterListener(e);
    }

}
