package com.example.TranslateInterface;

import android.content.Context;
import android.util.Log;

import com.baidu.speech.EventListener;
import com.baidu.speech.asr.SpeechConstant;
import com.example.AllUtil.SpeechRecognitionSDKManager;
import com.example.AllUtil.SpeechSynthesisManager;
import com.example.AllUtil.TranslateManager;

import org.json.JSONException;
import org.json.JSONObject;

public class BackgroundUtil {
    private static final String TAG = "BackgroundUtil";
    //监听接口
    public interface BackgroundUtilListener{
        void prepareSpeech();
        void wellRecognize(String result);
        void wellTranslate(String result);
    }
    private BackgroundUtilListener mListener;
    public void setBackgroundUtilListener(BackgroundUtilListener mListener){
        this.mListener = mListener;
    }

    private Context context;

    BackgroundUtil(Context context){
        this.context = context;
        //初始化语音识别类
        initRecognitionManager();
        //初始化翻译类
        initTranslateManager();
        //初始化语音合成类
        initSynthesisManager();
    }

    //初始化语音识别类
    private SpeechRecognitionSDKManager mSpeechRecognitionSDKManager;
    private void initRecognitionManager(){
        mSpeechRecognitionSDKManager = SpeechRecognitionSDKManager.getInstance(this.context,new MySpeechRecognitionListener());
    }

    //初始化翻译类
    private TranslateManager mTranslateManager;
    private void initTranslateManager(){
        mTranslateManager = new TranslateManager();
        mTranslateManager.setTranslationListener(new MyTranslationListener());
    }

    //初始化语音合成类
    private SpeechSynthesisManager mSpeechSynthesisManager;
    private void initSynthesisManager(){
        mSpeechSynthesisManager = SpeechSynthesisManager.getInstance(context);
        mSpeechSynthesisManager.init("0","5","5","5");
    }

    /****************************************下面是语音识别的操作*****************************/


    public static final int SPEECH_CHINESE = SpeechRecognitionSDKManager.RECOGNITION_TYPE_CHINESE;
    public static final int SPEECH_ENGLISH = SpeechRecognitionSDKManager.RECOGNITION_TYPE_ENGLISH;
    //开始语音识别
    public void startSpeech(String outFilePath, int recognitionType){
        mSpeechRecognitionSDKManager.startSpeech(outFilePath, recognitionType);
    }

    //停止语音识别
    public void stopRecognize(){
        mSpeechRecognitionSDKManager.stop();
    }

    //取消语音识别
    public void cancelRecognize(){
        mSpeechRecognitionSDKManager.cancel();
    }

    //语音识别监听
    class MySpeechRecognitionListener implements EventListener{
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
                //回调给界面，开始进度条和改变按钮图片表示开始说话
                if(mListener != null){
                    mListener.prepareSpeech();
                }

            } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_BEGIN)) {
                // 检测到用户的已经开始说话
                Log.d(TAG, "检测到开始说话：");

            } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_END)) {
                // 检测到用户的已经停止说话
                Log.d(TAG, "检测到已经停止说话了：");
            } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL)) {
                //临时识别结果, 长语音模式需要从此消息中取出结果
                String realResult = getRealResult(params);
                Log.d(TAG, "onEvent: 临时得到的结果："+realResult);

            } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_FINISH)) {
                // 识别结束， 最终识别结果或可能的错误
                ///////////***********这里有问题！！！！！！********///////////////////////////////
                Log.d(TAG, "onEvent: 识别结束了"+params);
//                String realResult = getRealResult(params);
//                Log.d(TAG, "onEvent: 结束得到的结果："+realResult);
                //结束了就告诉界面工具
                if(mListener != null){
                    mListener.wellRecognize("真实结果："+"?????");
                }
            }
        }
    }

    //从json数据解析识别结果
    private String key = "results_recognition";
    private String getRealResult(String str){
        //解析返回的JSON数据
        JSONObject jsonObject = null;
        String realResult = null;
        if(str != null && !str.isEmpty()){
            try {
                jsonObject = new JSONObject(str);
                //通过key（text）获取value，即获得机器人回复的结果
                realResult = jsonObject.getString(key);
            } catch (JSONException e) {
                //e.printStackTrace();
                Log.d(TAG, "getRealResult：解析识别结果json数据有问题");
            }
        }
        return realResult;
    }

    //翻译监听
    class MyTranslationListener implements TranslateManager.TranslationListener {
        @Override
        public void wellTranslation(String realResult) {

        }
    }

}
