package com.example.TranslateInterface;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.AllUtil.AudioManager;
import com.example.AllUtil.SpeechRecognitionManager;
import com.example.AllUtil.SpeechSynthesisManager;
import com.example.AllUtil.TranslateManager;


public class ButtonUtil /******实现录音、识别、翻译、合成接口*******/
        implements AudioManager.AudioStateListener,
SpeechRecognitionManager.speechRecognitionListener,
        TranslateManager.TranslationListener {
    private static final String TAG = "ButtonUtil";

    //本活动所在的Context
    Context context;

    //录音管理器类
    private AudioManager mAudioManager;

    //语音识别管理器类
    private SpeechRecognitionManager mSpeechRecognitionManager;

    //翻译管理器类
    private TranslateManager mTranslateManager;

    //语音合成管理类
    private SpeechSynthesisManager mSpeechSynthesisManager;

    //录音状态,默认没录音
    private boolean recordState = false;

    //录音时间,默认为0
    private float recordTime = 0;

    //文件夹路径
    private String filesPath = "/translateActivity_Audios";

    //翻译方式
    private int translateWay = 0;

    //构造器
    ButtonUtil(Context context){
        this.context = context;
        //初始化对话框类
        initDialogManager();
        //初始化录音类
        initAudioManager();
        //初始化语音识别类
        initRecognitionManager();
        //初始化翻译类
        initTranslateManager();
        //初始化语音合成类
        initSynthesisManager();
    }
    //初始化对话框类
    private void initDialogManager(){

    }
    //初始化录音类
    private void initAudioManager(){
        //相对路径（文件夹路径），getExternalStorageDirectory是获取外部存储文件夹，即SD卡
        String relativePath = Environment.getExternalStorageDirectory()+filesPath;
        //单例mAudioManager，传入存储文件夹地址
        mAudioManager = AudioManager.getInstance(relativePath);
        //设置监听，工具类监听录音类
        mAudioManager.setOnAudioStateListener(this);
    }
    //初始化语音识别类
    private void initRecognitionManager(){
        //初始化语音识别类
        mSpeechRecognitionManager = new SpeechRecognitionManager();
        mSpeechRecognitionManager.setSpeechRecognitionListener(this);
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


    /*********************以下是关于对话框的方法****************************/



    /*********************以下是关于录音的方法****************************/

    //准备好录音以后调用该方法
    @Override
    public void wellPrepared() {
        //准备好录音了就设置录音状态未true
        recordState = true;
        /**********显示对话框*************/
        //准备完以后就开启线程计时啊
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(recordState){
                    try {
                        Thread.sleep(100);
                        recordTime += 0.1f;
                    } catch (InterruptedException e) {
                        Log.d(TAG, "wellPrepared准备录音后开始计时出错");
                        //e.printStackTrace();
                    }
                }
                //没有录音了就重置
                recordTime = 0;
            }
        }).start();
    }
    //得到录音状态
    public boolean getRecordState(){
        return recordState;
    }
    //得到录音时间
    public float getRecordTime(){
        return recordTime;
    }
    //准备录音
    public void prepareAudio(){
        //录音类准备
        mAudioManager.prepareAudio();

        Log.d(TAG, "propareAudio准备好录音了");
    }
    //结束录音三种状态
    public static final int ENDRECORD_NORMAL_STATE = 0;
    public static final int ENDRECORD_CANCEL_STATE = 1;
    public static final int ENDRECORD_TOOSHORT_STATE = 2;
    //结束录音(无状态）
    public void endRecord(int endState){
        //如果是太短
        if(endState == ENDRECORD_TOOSHORT_STATE){
            /***********过1.2s再消失对话框*************/
            //取消录音(内部会始放资源）
            mAudioManager.cancel();
            Log.d(TAG, "endRecord结束录音，录音太短了");
        //如果是想取消
        }else if(endState == ENDRECORD_CANCEL_STATE){
            /***********直接消失对话框****************/
            //取消录音(内部会始放资源）
            mAudioManager.cancel();
            Log.d(TAG, "endRecord结束录音录音取消");
        //如果正常结束了
        }else if(endState == ENDRECORD_NORMAL_STATE){
            /***********直接消失对话框****************/
            //录音类释放资源即可
            mAudioManager.release();
            //录音文件绝对路径
            String filePath = mAudioManager.getCurrentFilePath();
            //语音识别
            this.speechRecognize(filePath);
            Log.d(TAG, "endRecord结束录音，录音正常");
        }
        //重置录音标志
        resetRecordFlag();
    }

    //结束录音(有状态状态）
    public void endRecord(int endState, int translateWay){
        //传入状态表示有翻译方式
        this.translateWay = translateWay;
        endRecord(endState);
    }
    //重置录音标志
    private void resetRecordFlag(){
        recordState = false;
        recordTime = 0;
    }




    /*********************以下是关于语音识别的的方法****************************/
    //语音识别放入Bundle的key
    private static final String recognitionContent = "recognitionContent";

    //语音识别好以后调用的方法
    @Override
    public void wellRecognition(String responseResult) {
        Log.d(TAG, "wellRecogition返回识别结果：："+responseResult);


        //如果语音识别为空
        if(responseResult == null || responseResult.trim().length() == 0){
            /***********系统提示识别不成功*************/
        }else{
            //识别成功了就去翻译好吗
            translate(responseResult, translateWay);


            /*****这里应该要让主线程显示识别结果
             //由于语音识别开启了线程，所以调用改方法的时候是在别的线程执行，必须异步处理
             //为Message传入信息，参考https://blog.csdn.net/qq_37321098/article/details/81535449
             Bundle bundle = new Bundle();
             //传入key，value
             bundle.putString(ButtonUtil.recognitionContent,responseResult);
             Message message = Message.obtain();
             message.setData(bundle);
             message.what = MSG_SEND;
             mHandler.sendMessage(message);
             //更新列表以后让机器人回复
             mRobotUtile.robotReply(responseResult);
             ************/
        }
    }
    //调用语音类识别
    private void speechRecognize(String filePath){
        mSpeechRecognitionManager.speechRecognition(filePath);
    }



    /*********************以下是关于文本翻译的的方法****************************/

    //翻译文本，传入翻译方式，让翻译类去翻译
    private void translate(String content, int translateWay){
        mTranslateManager.translate(content, translateWay);

    }

    //翻译结束后调用的方法
    @Override
    public void wellTranslation(String realResult) {
        Log.d(TAG, "wellTranslation得到语音翻译结果" + realResult);
        if(realResult == null || realResult.trim().length() == 0){
            /****************系统提示翻译不正确***************/
        }else{
            speechSynthesis(realResult);
        }
        resetTranslateFlag();
    }

    //重置翻译标志
    private void resetTranslateFlag(){
        translateWay = 0;
    }



    /*********************以下是关于语音合成的的方法****************************/

    private void speechSynthesis(String content){
        //先播放了再去显示界面
        //如果是空信息则有问题
        if(content == null || content.trim().length() == 0){
            /*****************系统提示语音合成无内容****************/
        }else{
            Log.d(TAG, "speechSynthesis语音合成，准备去speak了");
            mSpeechSynthesisManager.speak(content);
        }

        /****************要让界面显示合成的内容啊
        //为Message传入信息，参考https://blog.csdn.net/qq_37321098/article/details/81535449
        Bundle bundle=new Bundle();
        //传入key，value
        bundle.putString("robotResultContent",robotReply);
        Message message = Message.obtain();
        message.setData(bundle);
        message.what = MSG_RECEIVED;
        mHandler.sendMessage(message);
         **********************/
    }








    //信息常量，任意值
    private static final  int MSG_AUDIO_PREPARED = 0X110; //录音
    private static final  int MSG_VOICE_CHANGED = 0X111;  //改变音量图片
    private static final  int MSG_AUDIO_DIMISS = 0X112;   //关闭对话框
    private static final int MSG_SEND = 0X113;            //发送信息
    private static final int MSG_RECEIVED = 0X114;        //接收信息
    @SuppressLint("HandlerLeak")
    //在当前线程创建线程处理类，而当前线程就是主线程
    private Handler mHandler=new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            Bundle mBundle;
            switch (msg.what){
                //正在录音
                case MSG_AUDIO_PREPARED:

                    break;
                //改变音量
                case MSG_VOICE_CHANGED:

                    break;
                //关闭对话框
                case MSG_AUDIO_DIMISS:

                    break;
                //发送信息
                case MSG_SEND://List插入新的发送信息的元素

                    break;
                //接收信息
                case MSG_RECEIVED://List插入新的接收信息的元素

                    break;
            }
        };
    };






}
