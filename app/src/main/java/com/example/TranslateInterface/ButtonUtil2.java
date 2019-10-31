package com.example.TranslateInterface;

import android.content.Context;

import com.baidu.speech.EventListener;
import com.example.AllUtil.SpeechRecognitionSDKManager;
import com.example.AllUtil.SpeechSynthesisManager;
import com.example.AllUtil.TranslateManager;



public class ButtonUtil2
    implements EventListener ,
        TranslateManager.TranslationListener{
    private static final String TAG = "ButtonUtil2";

    //本活动所在的Context
    Context context;

    //语音识别类
    SpeechRecognitionSDKManager speechRecognitionSDKManager;

    //翻译管理器类
    private TranslateManager mTranslateManager;

    //语音合成管理类
    private SpeechSynthesisManager mSpeechSynthesisManager;

    //翻译方式
    private int translateWay = 0;



    //构造器
    ButtonUtil2(Context context){
        this.context = context;
        //初始化语音识别类
        initRecognitionManager();
        //初始化翻译类
        initTranslateManager();
        //初始化语音合成类
        initSynthesisManager();
    }

    //初始化语音识别类
    private void initRecognitionManager(){
        speechRecognitionSDKManager = SpeechRecognitionSDKManager.getInstance(this.context,this);
    }

    //初始化翻译类
    private void initTranslateManager(){
        mTranslateManager = new TranslateManager();
        mTranslateManager.setTranslationListener(this);
    }

    //初始化语音合成类
    private void initSynthesisManager(){
        mSpeechSynthesisManager = SpeechSynthesisManager.getInstance(context);
        mSpeechSynthesisManager.init("0","5","5","5");
    }




    /*********************以下是关于语音识别的的方法****************************/

    //中文识别
    public static final int SPEECH_CHINESE = SpeechRecognitionSDKManager.RECOGNITION_TYPE_CHINESE;
    //英文识别
    public static final int SPEECH_ENGLISH = SpeechRecognitionSDKManager.RECOGNITION_TYPE_ENGLISH;
    //粤语识别
    public static final int SPEECH_CANTONESE = SpeechRecognitionSDKManager.RECOGNITION_TYPE_CANTONESE;

    public void speechStart(int speechType){
        speechRecognitionSDKManager.startSpeech(null,speechType);
    }

    //语音识别回调的方法
    @Override
    public void onEvent(String s, String s1, byte[] bytes, int i, int i1) {

    }




    /*********************以下是关于文本翻译的的方法****************************/
    @Override
    public void wellTranslation(String realResult) {

    }
}
