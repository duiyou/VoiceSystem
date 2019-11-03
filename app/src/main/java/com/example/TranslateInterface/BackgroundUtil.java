package com.example.TranslateInterface;

import android.content.Context;
import android.util.Log;

import com.baidu.speech.EventListener;
import com.baidu.speech.asr.SpeechConstant;
import com.example.AllUtil.SpeechRecognitionSDKManager;
import com.example.AllUtil.SpeechSynthesisManager;
import com.example.AllUtil.SpeechWakeUpManager;
import com.example.AllUtil.TranslateManager;

import org.json.JSONException;
import org.json.JSONObject;

public class BackgroundUtil {
    private static final String TAG = "BackgroundUtil";

    //监听接口
    public interface BackgroundUtilListener{
        void speechWakeUp(String wakeUpWord);
        void startSpeech();
        void tempRecoginze(String temp);
        void endSpeech();
        void wellRecognize();
        void wellTranslate(String result);
    }
    private BackgroundUtilListener mListener;
    public void setBackgroundUtilListener(BackgroundUtilListener mListener){
        this.mListener = mListener;
    }

    //活动的Context
    private Context context;

    BackgroundUtil(Context context){
        this.context = context;
        //初始化语音识别类
        initRecognitionManager();
        //初始化翻译类
        initTranslateManager();
        //初始化语音合成类
        initSynthesisManager();
        //初始化语音唤醒类
        initWakeUpManager();
    }

    //初始化语音识别类**********************
    private SpeechRecognitionSDKManager mSpeechRecognitionSDKManager;
    private void initRecognitionManager(){
        //初始话语音识别类，还要传入监听类
        mSpeechRecognitionSDKManager = SpeechRecognitionSDKManager.getInstance(this.context,new MySpeechRecognitionListener());
    }

    //初始化翻译类*****************************
    private TranslateManager mTranslateManager;
    private void initTranslateManager(){
        mTranslateManager = new TranslateManager();
        //设置翻译监听
        mTranslateManager.setTranslationListener(new MyTranslationListener());
    }

    //初始化语音合成类*****************************
    private SpeechSynthesisManager mSpeechSynthesisManager;
    private void initSynthesisManager(){
        mSpeechSynthesisManager = SpeechSynthesisManager.getInstance(context);
        mSpeechSynthesisManager.init("4","5","6","5");
    }

    //初始化语音唤醒类*****************************
    private SpeechWakeUpManager mSpeechWakeUpManager;
    private void initWakeUpManager(){
        mSpeechWakeUpManager = SpeechWakeUpManager.getInstance(this.context,new MySpeechWakeUpListener());
        //直接开启唤醒啊
        mSpeechWakeUpManager.startWakeUp();
    }

    /****************************************下面是语音识别的操作*****************************/
    //识别方式常量
    public static final int SPEECH_CHINESE = SpeechRecognitionSDKManager.RECOGNITION_TYPE_CHINESE;
    public static final int SPEECH_ENGLISH = SpeechRecognitionSDKManager.RECOGNITION_TYPE_ENGLISH;
    //开始语音识别
    public void startSpeech(String outFilePath, int recognitionType){
        if(mSpeechRecognitionSDKManager != null){
            mSpeechRecognitionSDKManager.startSpeech(outFilePath, recognitionType);
        }
    }

    //停止语音识别
    public void stopRecognize(){
        if(mSpeechRecognitionSDKManager != null){
            mSpeechRecognitionSDKManager.stop();
        }
    }

    //语音识别监听
    class MySpeechRecognitionListener implements EventListener{
        @Override
        public void onEvent(String name, String params, byte[] data, int offset, int length) {
            /*
            String result = "name：" + name;
            if (length > 0 && data.length > 0) {
                result += ", 语义解析结果：" + new String(data, offset, length);
                //Log.d(TAG, "识别到了："+ result);
            }
            */
            if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_READY)) {
                // 引擎准备就绪，可以开始说话
                Log.d(TAG, "准备开始说话：");
                //回调给界面，开始进度条和改变按钮图片表示开始说话
                if(mListener != null){
                    mListener.startSpeech();
                }

            } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_BEGIN)) {
                // 检测到用户的已经开始说话
                Log.d(TAG, "检测到开始说话：");

            } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_END)) {
                // 检测到用户的已经停止说话
                Log.d(TAG, "检测到已经停止说话了：");
                if(mListener != null){
                    mListener.endSpeech();
                }
            } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL)) {
                //临时识别结果, 长语音模式需要从此消息中取出结果
                String realResult = getRealResult(params);
                Log.d(TAG, "onEvent: 临时得到的结果："+realResult);
                if(mListener != null){
                    mListener.tempRecoginze(realResult);
                }

            } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_FINISH)) {
                // 识别结束， 最终识别结果或可能的错误
                ///////////***********这里有问题！！！！！！********///////////////////////////////
                Log.d(TAG, "onEvent: 识别结束了"+params);
                //结束了就告诉界面工具
                if(mListener != null){
                    mListener.wellRecognize();
                }
            }
        }
    }

    //从json数据解析识别结果
    //json中翻译结果的键
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
                //还要再修改以下才是纯文字
                if(realResult != null && !realResult.isEmpty()){
                    realResult = realResult.substring(2 , realResult.length() - 2);
                }

            } catch (JSONException e) {
                //e.printStackTrace();
                Log.d(TAG, "getRealResult：解析识别结果json数据有问题");
            }finally {
                //有没问题都要返回，空结果也返回
                return realResult;
            }
        }
        return realResult;
    }



    /****************************************下面是翻译的操作*****************************/

    //翻译方式常量
    public static final int TRANSLATEWAY_C_TO_E = TranslateManager.C_TO_E;
    public static final int TRANSLATEWAY_E_TO_C = TranslateManager.E_TO_C;
    public void translate(String content, int translateWay){
        if(mTranslateManager != null){
            mTranslateManager.translate(content , translateWay);
        }
    }

    //翻译监听
    class MyTranslationListener implements TranslateManager.TranslationListener {
        @Override
        public void wellTranslation(String realResult) {
            //传回界面工具翻译结果
            if(mListener != null){
                mListener.wellTranslate(realResult);
            }
        }
    }


    /****************************************下面是语音合成的操作*****************************/
    public void speak(String content){
        if(mSpeechSynthesisManager != null)mSpeechSynthesisManager.speak(content);
    }



    /****************************************下面是语音唤醒的操作*****************************/

    //唤醒词
    public static final String WAKEUPWORD_STARTSPEECH = "小白翻译";

    class MySpeechWakeUpListener implements EventListener{
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
                        if(wakeUpWord != null && !wakeUpWord.isEmpty()){
                            if(mListener != null){
                                mListener.speechWakeUp(wakeUpWord);
                            }
                        }

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
    }


}
