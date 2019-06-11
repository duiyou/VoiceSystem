package com.example.SpeechSetInterface;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.example.ChatInterface.SpeechSynthesisUtil;
import com.example.voicesystem.BaseActivity;
import com.example.voicesystem.R;

public class SpeechSetActivity extends BaseActivity implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {
    //音量
    private SeekBar speechVolumeSeekBar;
    private TextView speechVolumeTextView;
    //语速
    private SeekBar speechSpeedSeekBar;
    private TextView speechSpeedTextView;
    //音调
    private SeekBar speechPitchSeekBar;
    private TextView speechPitchTextView;
    //单选按钮组
    private RadioGroup mRadioGruop;
    //语音播放按钮
    private Button speechPlayButton;
    //清空文本按钮
    private Button clearTextButton;
    //输入文本
    private EditText mEditText;
    //语音合成工具类
    private SpeechSynthesisUtil mSpeechSynthesisUtil;
    //是否在播放
    boolean isPlay=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.speechsetactivity_layout);
        //音量SeekBar和TextView
        speechVolumeSeekBar=findViewById(R.id.speechsetactivity_speechvolumeseekbar);
        speechVolumeSeekBar.setOnSeekBarChangeListener(this);
        speechVolumeTextView=findViewById(R.id.speechsetactivity_speechvolumetextview);
        //语速SeekBar和TextView
        speechSpeedSeekBar=findViewById(R.id.speechsetactivity_speechspeedseekbar);
        speechSpeedSeekBar.setOnSeekBarChangeListener(this);
        speechSpeedTextView=findViewById(R.id.speechsetactivity_speechspeedtextview);
        //音调SeekBar和TextView
        speechPitchSeekBar=findViewById(R.id.speechsetactivity_speechpitchseekbar);
        speechPitchSeekBar.setOnSeekBarChangeListener(this);
        speechPitchTextView=findViewById(R.id.speechsetactivity_speechpitchtextview);
        //单选按钮组
        mRadioGruop=findViewById(R.id.speechsetactivity_radiogruop);
        //播放按钮
        speechPlayButton=findViewById(R.id.speechsetactivity_speechplay);
        speechPlayButton.setOnClickListener(this);
        //清空文本按钮
        clearTextButton=findViewById(R.id.speechsetactivity_cleartext);
        clearTextButton.setOnClickListener(this);
        //编辑框
        mEditText=findViewById(R.id.speechsetactivity_edittext);
        //语音合成工具
        mSpeechSynthesisUtil=SpeechSynthesisUtil.getInstance(SpeechSetActivity.this);
        //默认初始化
        mSpeechSynthesisUtil.init("0", "5", "5", "5");
        //设置监听
        mSpeechSynthesisUtil.setSpeechSynthesizerListener(new SpeechSynthesizerListener() {
            //开始合成
            @Override
            public void onSynthesizeStart(String s) {
                Log.e("SpeechSetActivity", "开始合成");
            }

            @Override
            public void onSynthesizeDataArrived(String s, byte[] bytes, int i) {

            }
            //合成结束
            @Override
            public void onSynthesizeFinish(String s) {
                Log.e("SpeechSetActivity", "合成结束");
            }
            //开始播放
            @Override
            public void onSpeechStart(String s) {
                isPlay=true;
                mHandler.sendEmptyMessage(SPEECHSTART);
                Log.e("SpeechSetActivity", "开始播放");
            }
            //播放中
            @Override
            public void onSpeechProgressChanged(String s, int i) {

            }
            //播放完成
            @Override
            public void onSpeechFinish(String s) {
                isPlay=false;
                mHandler.sendEmptyMessage(SPEECHFINISH);
                Log.e("SpeechSetActivity", "播放完成");
            }

            @Override
            public void onError(String s, SpeechError speechError) {
                Log.e("SpeechSetActivity", "错误了啊"+speechError.toString());
            }
        });
    }
    //按钮监听
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.speechsetactivity_speechplay:
                if(isPlay==true){
                    isPlay=false;
                    mSpeechSynthesisUtil.stop();
                    speechPlayButton.setBackgroundResource(R.drawable.speechplay1);
                    break;
                }
                String synthesisContent;
                if(mEditText.getText().toString().length()==0){
                    synthesisContent="请在这里输入想播放的语音";
                }else{
                    synthesisContent=mEditText.getText().toString();
                }
                String speechSpeak=null;
                switch(mRadioGruop.getCheckedRadioButtonId()){
                    case R.id.speechsetactivity_radiobutton0:
                        speechSpeak="0";
                        break;
                    case R.id.speechsetactivity_radiobutton1:
                        speechSpeak="1";
                        break;
                    case R.id.speechsetactivity_radiobutton2:
                        speechSpeak="2";
                        break;
                    case R.id.speechsetactivity_radiobutton3:
                        speechSpeak="3";
                        break;
                    case R.id.speechsetactivity_radiobutton4:
                        speechSpeak="4";
                        break;
                }
                mSpeechSynthesisUtil.init(speechSpeak,
                        speechVolumeSeekBar.getProgress()+"",
                        speechSpeedSeekBar.getProgress()+"",
                        speechPitchSeekBar.getProgress()+"");
                mSpeechSynthesisUtil.speak(synthesisContent);
                Log.e("SpeechSetActivity", "啊啊啊啊啊啊");
                break;
            case R.id.speechsetactivity_cleartext:
                if(mEditText.getText().toString().length()!=0)mEditText.setText("");
                break;
        }
    }

    //SeekBar的值改变
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()){
            //音量
            case R.id.speechsetactivity_speechvolumeseekbar:
                speechVolumeTextView.setText("音量："+progress);
                break;
            case R.id.speechsetactivity_speechspeedseekbar:
                speechSpeedTextView.setText("语速："+progress);
                break;
            case R.id.speechsetactivity_speechpitchseekbar:
                speechPitchTextView.setText("音调："+progress);
                break;
        }
    }
    //开始拖动SeekBar
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }
    //停止拖动SeekBar
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    //信息常量，任意值
    private static final  int SPEECHSTART=1;
    private static final  int SPEECHFINISH=2;
    @SuppressLint("HandlerLeak")
    //线程处理类
    private Handler mHandler=new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                //开始播放
                case SPEECHSTART:
                    speechPlayButton.setBackgroundResource(R.drawable.speechplay2);
                    break;
                //播放结束
                case SPEECHFINISH:
                    speechPlayButton.setBackgroundResource(R.drawable.speechplay1);
                    break;
            }

        };

    };
    //开始这个活动
    public static void actionStart(Context context){
        Intent intent=new Intent(context, SpeechSetActivity.class);
        context.startActivity(intent);
    }
    //销毁活动的时候
    @Override
    protected void onDestroy() {
        if(SpeechSynthesisUtil.mSpeechSynthesisUtil!=null){
            SpeechSynthesisUtil.mSpeechSynthesisUtil.Destpry();
        }
        super.onDestroy();
    }
    //准备去打开另一个活动时就暂停播放哦
    @Override
    protected void onPause() {
        if(SpeechSynthesisUtil.mSpeechSynthesisUtil!=null){
            SpeechSynthesisUtil.mSpeechSynthesisUtil.stop();
        }
        super.onPause();

    }
}
