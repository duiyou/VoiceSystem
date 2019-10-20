package com.example.AllUtil;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;

/**
 * 语音合成工具类，单例模式
 * 参考百度语音http://ai.baidu.com/docs#/TTS-Android-SDK/14b0ed4d
 * 参考博客https://blog.csdn.net/qq_37217804/article/details/82621862
 * 解决Android9.0无法播放https://blog.csdn.net/qq_35317752/article/details/88618403！！！！！！！！！！！！！！！！！！！！！！
 */
public class SpeechSynthesisManager {
    //语音合成类
    private SpeechSynthesizer mSpeechSynthesizer;
    private static final String TAG = "SpeechSynthesisUtil";
    //全局静态类，单例模式
    public static SpeechSynthesisManager mSpeechSynthesisUtil;
    private Context mContext;
    private static final String appId="16265600";
    private static final String apiKey="HiqW9i6DFZWB0aUNU4O0dBlG";
    private static final String secretKey="DTTH6pGU87RxV4F1n9wfKHdMsH6yACsh";

    private SpeechSynthesisManager(Context context) {
        this.mContext=context;
    }
    public static SpeechSynthesisManager getInstance(Context context){
        if(mSpeechSynthesisUtil==null){
            mSpeechSynthesisUtil=new SpeechSynthesisManager(context);
        }
        return mSpeechSynthesisUtil;
    }
    //初始化语音合成类，发音人，音量，语速，音调
    public void init(String speechSpeaker,String speechVolume,String speechSpeed,String speechPitch){
        if (mSpeechSynthesisUtil == null){
            Log.e(TAG, "init初始化时尚未初始化语音合成类");
            return;
        }
        mSpeechSynthesizer = SpeechSynthesizer.getInstance();
        mSpeechSynthesizer.setContext(mContext);
        int result;
        result=mSpeechSynthesizer.setAppId(appId);
        checkResult(result,"setAppId");
        result=mSpeechSynthesizer.setApiKey(apiKey,secretKey);
        checkResult(result,"setApiKey");
        // 纯在线模式
        mSpeechSynthesizer.auth(TtsMode.ONLINE);
        // 设置在线发声音人： 0 普通女声（默认） 1 普通男声 2 特别男声 3 情感男声<度逍遥> 4 情感儿童声<度丫丫>
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, speechSpeaker);
        // 设置合成的音量，0-9 ，默认 5
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOLUME, speechVolume);
        // 设置合成的语速，0-9 ，默认 5
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED, speechSpeed);
        // 设置合成的语调，0-9 ，默认 5
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_PITCH, speechPitch);
        //设置音频流出口为手机音乐，参考https://blog.csdn.net/weixin_37730482/article/details/80567141
        mSpeechSynthesizer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        //设置合成的参数后，需要调用此方法初始化
        result=mSpeechSynthesizer.initTts(TtsMode.ONLINE);
        checkResult(result,"initTts");
    }
    //设置监听
    public void setSpeechSynthesizerListener(SpeechSynthesizerListener mListener){
        if(mSpeechSynthesizer==null){
            Log.d(TAG, "设置监听有问题");
            return;
        }
        mSpeechSynthesizer.setSpeechSynthesizerListener(mListener);
    }
    //播放语音
    public void speak(String str){
        if(mSpeechSynthesizer==null){
            Log.e(TAG, "speak播放时尚未初始化语音合成类");
            return;
        }
        int result=mSpeechSynthesizer.speak(str);
        checkResult(result,"speak");
    }
    //播放语音停止
    public void stop(){
        if(mSpeechSynthesizer==null){
            Log.e(TAG, "stop停止播放时尚未初始化语音合成类");
            return;
        }
        int result=mSpeechSynthesizer.stop();
        checkResult(result,"stop");
    }
    //销毁语音合成
    public void Destpry(){
        if(mSpeechSynthesisUtil!=null){
            int result;
            result=mSpeechSynthesizer.stop();
            checkResult(result,"stop");
            result=mSpeechSynthesizer.release();
            checkResult(result,"release");
            mSpeechSynthesizer=null;
        }
    }

    //检查返回值是否不为0，如果是则有问题
    private void checkResult(int result, String method) {
        if (result != 0) {
            Log.e(TAG, "checkResult检查返回值时出错，error code :" + result + "   method:" + method + ", 错误码文档:http://yuyin.baidu.com/docs/tts/122");
        }
    }


}
