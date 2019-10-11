package com.example.ChatInterface;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.voicesystem.R;

/**
 * 自定义录音按钮
 * 这个按钮就已经包括了所有业务逻辑，并且和对话框相关联，不需要在主活动中设置
 * 自定义控件的时候会发现Android studio提示Button已被AppCompatButton取代，
 * AppCompatButton继承自Button，新增加了对动态的背景颜色等功能的支持.
 */

//自身继承监听接口，可以监听到MedieReocrder的信息
public class AudioRecordButton extends android.support.v7.widget.AppCompatButton implements AudioManager.AudioStateListener,SpeechRecognitionUtil.speechRecognitionListener,RobotUtil.robotReplyListener{
    //移动手指取消录音的距离
    private static final int DISTANCE_Y_CANCEL = 100;
    //按钮的三种状态正常状态，录音状态，想取消状态
    private static final int STATE_NORMAL = 1;
    private static final int STATE_RECORDING = 2;
    private static final int STATE_WANT_TO_CANCEL = 3;
    //当前按钮状态,默认为NORMAL状态
    private int nowCurState = STATE_NORMAL;
    //判断是否正在录音
    private boolean isRecording = false;
    //对话框管理类
    private DialogManager mDialogManager;
    //录音管理类
    private AudioManager mAudioManager;
    //语音识别类
    private SpeechRecognitionUtil mSpeechRecognitionUtil;
    //图灵机器人工具类
    private RobotUtil mRobotUtile;
    //用于计时
    private float mTime=0;
    //是否触发longclick，长按了按钮
    private boolean isLongClick = false;
    //当前活动
    private ChatActivity mChatActivity;
    //语音合成类
    private SpeechSynthesisUtil mSpeechSynthesisUtil;
    //文件路径
    private String filePath = "/imooc_recorder_audios";
    //语音识别结果
    //private String recognitionContent;
    //机器人回复
    //private String robotResultContent;

    public AudioRecordButton(Context context){
        this(context, null);
    }
    //构造函数
    public AudioRecordButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        //获取该控件所在的活动
        mChatActivity = (ChatActivity)getContext();
        //语音合成单例类
        mSpeechSynthesisUtil = SpeechSynthesisUtil.getInstance(getContext());
        //初始化语音合成（应当读取设置文本的！！！！！！！！！！！！！！！！！！）
        mSpeechSynthesisUtil.init("0","5","6","3");
        //创建机器人工具类
        mRobotUtile = new RobotUtil();
        //设置机器人工具类监听
        mRobotUtile.setRobotReplyListener(this);
        //创建对话框管理类,传入当前Context
        mDialogManager = new DialogManager(getContext());


//        你的apk装到哪个盘里，那个盘就认为是外部存储器，这么说，不是很准确。
//        可以这么说，除了你的后来装到手机上的内存卡叫sdcard外，手机本身也有sdcard的成分，
//        手机总的存储空间分为系统空间和手机U盘空间两部分，后者手机U盘空间就是sdcard。
//        从你的试验中知道，如果不插内存卡（我们都知道的sdcard），还是可以获取到sdcard的总存储容量和可用容量的，
//        这个时候，总存储容量应该是你手机总的存储容量减去系统容量； 如果，插上内存卡，但是，你把apk安装到了手机上，
//        而不是内存卡上，那么实验结果应该是和你不插内存卡的情况一样，但若是，你把apk安装到内存卡上，这个时候，
//        你获取的总容量就是你内存卡的总量，可用容量就是你内存卡的可用容量。

        //在存储卡创建的存放音频的文件夹名称,getExternalStorageDirectory是获取外部存储文件夹，即SD卡
        String dir = Environment.getExternalStorageDirectory()+filePath;
        //单例mAudioManager，传入存储文件地址
        mAudioManager = AudioManager.getInstance(dir);
        //mAudioManager设置监听，即自定义Button监听到了AudioManager，AudioManager准备好了录音就会让本Button知道
        mAudioManager.setOnAudioStateListener(this);
        //初始化语音识别类
        mSpeechRecognitionUtil = new SpeechRecognitionUtil();
        //mSpeechRecognitionUtil设置监听，即自定义Button监听到了mSpeechRecognitionUtil，mSpeechRecognitionUtil识别完后就让本Button知道
        mSpeechRecognitionUtil.setSpeechRecognitionListener(this);

        //长时间按住按钮的监听，准备好以后才回调给Button让Button显示对话框
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //长按判断
                isLongClick = true;
                //准备录音
                mAudioManager.prepareAudio();
                return false;
            }
        });
    }
    /**
     * 录音完成后的回调接口(未使用）
     */
    public interface AudioFinishRecorderListener {
        void onFinish(float seconds, String filePath);
    }
    private AudioFinishRecorderListener mListener;
    public void setAudioFinishRecorderListener(AudioFinishRecorderListener listener){
        mListener = listener;
    }

    /**
     * 获取音量大小的线程
     */
    private Runnable mVoiceLevelRunnable=new Runnable(){
        @Override
        public void run() {
            //正在录音时就一直获取大小
            while(isRecording){
                try {
                    //当正在录音时，每0.1秒都会去让Handle改变音量大小
                    Thread.sleep(100);
                    //计时
                    mTime += 0.1f;
                    //必须让mHandle去改变声音大小，不能在开的线程改变，因为只有在mHandle里换图片时UI才会刷新
                    mHandler.sendEmptyMessage(MSG_VOICE_CHANGED);
                } catch (InterruptedException e) {
                    Log.d("AudioRecordButton","每0.1秒调整音量有问题");
                    //e.printStackTrace();

                }
            }

        }
    };

    //信息常量，任意值
    private static final  int MSG_AUDIO_PREPARED = 0X110; //录音
    private static final  int MSG_VOICE_CHANGED = 0X111;  //改变音量图片
    private static final  int MSG_AUDIO_DIMISS = 0X112;   //关闭对话框
    private static final int MSG_SEND = 0X113;            //发送信息
    private static final int MSG_RECEIVED = 0X114;        //接收信息
    @SuppressLint("HandlerLeak")
    //线程处理类
    private Handler mHandler=new Handler()
    {

        @Override
        public void handleMessage(Message msg) {
            Bundle mBundle;
            switch (msg.what){
                //正在录音
                case MSG_AUDIO_PREPARED:
                    //判断为正在录音
                    isRecording = true;
                    //显示对话框，默认就是正在录音的布局了，不必再mDialogManager.recording()
                    mDialogManager.showRecordingDialog();
                    //准备好录音以后子线程开始更改音量，即每
                    new Thread(mVoiceLevelRunnable).start();
                    break;
                //改变音量
                case MSG_VOICE_CHANGED:
                    mDialogManager.updateVoiceLevel(mAudioManager.getVoiceLevel(7));
                    break;
                //关闭对话框
                case MSG_AUDIO_DIMISS:
                    //关闭对话框
                    mDialogManager.dismissDialog();
                    break;
                //发送信息
                case MSG_SEND://List插入新的发送信息的元素
                    mBundle = msg.getData();
                    String recognitionContent = mBundle.getString("recognitionContent");
                    mChatActivity.notifyItemInserted(recognitionContent,Msg.TYPE_SEND);
                    break;
                //接收信息
                case MSG_RECEIVED://List插入新的接收信息的元素
                    mBundle = msg.getData();
                    String robotResultContent =mBundle.getString("robotResultContent");
                    mChatActivity.notifyItemInserted(robotResultContent,Msg.TYPE_RECEIVED);
                    break;
            }
        };
    };

    //录音类准备好录音时回调给Button，让Button去修改Dialog
    @Override
    public void wellPrepared() {
        //发送已经准备好信息
        mHandler.sendEmptyMessage(MSG_AUDIO_PREPARED);
    }
    //语音识别工具类识别好结果responseResult给Button，让Button进行接下来的操作
    public void wellRecognition(String responseResult){
        //为Message传入信息，参考https://blog.csdn.net/qq_37321098/article/details/81535449
        Bundle bundle = new Bundle();
        //传入key，value
        bundle.putString("recognitionContent",responseResult);
        Message message = Message.obtain();
        message.setData(bundle);
        message.what = MSG_SEND;
        mHandler.sendMessage(message);
        //更新列表以后让机器人回复
        mRobotUtile.robotReply(responseResult);
    }
    //机器人回复了robotReply，让Button进行接下来的操作
    public void wellRobotReply(String robotReply){
        //先播放再去显示
        mSpeechSynthesisUtil.speak(robotReply);
        //为Message传入信息，参考https://blog.csdn.net/qq_37321098/article/details/81535449
        Bundle bundle=new Bundle();
        //传入key，value
        bundle.putString("robotResultContent",robotReply);
        Message message = Message.obtain();
        message.setData(bundle);
        message.what = MSG_RECEIVED;
        mHandler.sendMessage(message);

    }

    //屏幕触摸监听
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //手指在控件上的x，y坐标
        int x = (int) event.getX();
        int y = (int) event.getY();
        //按键状态，获取事件类型
        switch (event.getAction()){
            //手指触摸屏幕
            case MotionEvent.ACTION_DOWN:
                //改变为录音状态
                changeState(STATE_RECORDING);
                break;
            //手指在屏幕上滑动，会多次触发
            case MotionEvent.ACTION_MOVE:
                //如果在录音时
                if(isRecording) {
                    if(isWantToCancel(x,y)){
                        //如果手指移动时x，y的位置表示想取消发送录音则为取消发送状态
                        changeState(STATE_WANT_TO_CANCEL);
                    }else{
                        //否则继续录音状态
                        changeState(STATE_RECORDING);
                    }
                }
                break;

            //手指离开屏幕
            case MotionEvent.ACTION_UP:
                //没有长按也就没准备录音，就直接恢复标志位就好了
                if(!isLongClick){
                    reset();
                    //系统添加的
                    return super.onTouchEvent(event);
                }
                //1.如果长按了，但是还没有准备好录音isRecording（长按以后并且准备好录音后才会在子线程isRecording=true）
                //2.长按了，isRecording好了，但是录音太短了
                if(!isRecording||mTime<0.6f){
                    //对话框显示太短了
                    mDialogManager.tooShort();
                    //录音取消保存
                    mAudioManager.cancel();
                    //在延迟了1.3秒之后发送录音关闭语句，在那里关闭对话框，但之后的代码也会继续执行，异步
                    mHandler.sendEmptyMessageDelayed(MSG_AUDIO_DIMISS,1300);
                }else if(nowCurState == STATE_RECORDING){      //如果正常录制结束
                    //直接关闭对话框
                    mDialogManager.dismissDialog();
                    //释放录音资源
                    mAudioManager.release();

                    /***语音识别*****/
                    //语音识别（传入文件路径，内部开启了一个线程去识别了）
                    mSpeechRecognitionUtil.speechRecognition(mAudioManager.getCurrentFilePath());


                    //回调给监听自定义按钮的活动,告诉已经保存好了录音了
                    if(mListener != null){
                        mListener.onFinish(mTime,mAudioManager.getCurrentFilePath());
                    }
                //如果是想取消录音的
                }else if(nowCurState == STATE_WANT_TO_CANCEL){
                    //关闭对话框
                    mDialogManager.dismissDialog();
                    //取消保存录音
                    mAudioManager.cancel();
                }
                //手指离开屏幕后初始化一些标志
                reset();
                break;
        }
        //系统添加的
        return super.onTouchEvent(event);
    }
    //每次按键结束以后恢复一些标志位
    private void reset() {
        isRecording = false;
        isLongClick = false;
        mTime = 0;
        changeState(STATE_NORMAL);
    }
    //判断手指是否在区域外
    private boolean isWantToCancel(int x, int y) {
        //x方向上只要在控件左边或右边则取消发送录音
        if(x<0||x>getWidth()){
            return true;
        }
        //y方向如果距离控件某个范围则为取消发送录音
        if(y<-DISTANCE_Y_CANCEL||y>getHeight()+DISTANCE_Y_CANCEL){
            return true;
        }
        return false;
    }
    //改变此时按钮状态，同时也改变对话框
    private void changeState(int state) {
        //如果此时状态和改变状态不同才需要改变
        if(nowCurState != state){
            //先改变状态
            nowCurState=state;
            switch(state){
                //如果变为NORMAL则为正常background和正常text
                case STATE_NORMAL:
                    setBackgroundResource(R.drawable.record_normal);
                    setText(R.string.str_record_normal);
                    //恢复NORMAL时只改变按钮的信息就好了，不要关闭Dialog，让按钮UP时延迟1.3秒发送关闭对话框语句
                    break;
                //录音background和录音text
                case STATE_RECORDING:
                    setBackgroundResource(R.drawable.record_recording);
                    setText(R.string.str_record_recording);
                    if(isRecording){
                        //对话框变了，正在录音，还有音量大小
                        mDialogManager.recording();
                    }
                    break;
                case STATE_WANT_TO_CANCEL:
                    setBackgroundResource(R.drawable.record_recording);
                    setText(R.string.str_record_want_cancel);
                    //对话框变了，取消录音
                    mDialogManager.wantToCancel();
                    break;
            }
        }
    }


}
