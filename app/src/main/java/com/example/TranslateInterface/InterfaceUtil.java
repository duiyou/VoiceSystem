package com.example.TranslateInterface;

import android.annotation.SuppressLint;
import android.graphics.Color;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.voicesystem.R;
import com.tuyenmonkey.mkloader.MKLoader;

import java.util.ArrayList;
import java.util.List;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import fr.castorflex.android.smoothprogressbar.SmoothProgressDrawable;
import it.beppi.tristatetogglebutton_library.TriStateToggleButton;

public class InterfaceUtil {
    private static final String TAG = "InterfaceUtil";

    //两种翻译方式变量
    private final int TRANSLATEWAY_C_TO_E = 1; //中-英
    private final int TRANSLATEWAY_E_TO_C = 0; //英-中
    //翻译方式,默认为中-英
    private int translateWay = TRANSLATEWAY_C_TO_E;

    //表示正在工作
    private boolean isWorking = false;

    //对应的活动
    private TranslateActivity translateActivity;

    InterfaceUtil(TranslateActivity translateActivity){
        this.translateActivity = translateActivity;
        //初始化所有东西
        init();
    }
    //初始化所有东西
    private void init(){
        //初始化后台工具类
        initBackgroundUtil();
        //初始化中英文TextView
        initTextView();
        //初始化开关按钮
        initToggleButton();
        //初始化List
        initRecyclerView();
        //初始化进度条
        initProgressBar();
        /**********初始化键盘按钮************/

        //初始化语音按钮
        initSpeechButton();
        //初始化加载控件
        initMKLoader();
    }


    //**************************初始化后台工具类
    private BackgroundUtil backgroundUtil;
    private void initBackgroundUtil(){
        this.backgroundUtil = new BackgroundUtil(translateActivity);
        backgroundUtil.setBackgroundUtilListener(new MyBackgroundUtilListener());
    }


    //**************************初始化上面两个文本textview
    private TextView leftText;
    private TextView rightText;
    private void initTextView(){
        if(translateActivity != null){
            leftText = translateActivity.findViewById(R.id.translate_activity_lefttextview);
            rightText = translateActivity.findViewById(R.id.translate_activity_righttextview);
        }
    }

    //**************************初始化开关按钮
    private TriStateToggleButton toggleButton;
    private void initToggleButton(){
        if(translateActivity != null){
            toggleButton = translateActivity.findViewById(R.id.translate_activity_togglebutton);
            //设置状态改变监听
            toggleButton.setOnToggleChanged(new TriStateToggleButton.OnToggleChanged() {
                @Override
                public void onToggle(TriStateToggleButton.ToggleStatus toggleStatus, boolean b, int i) {
                    //按一下就改变翻译方式
                    changeWay();
                }
            });
        }
    }

    //**************************初始化list
    //适配器的信息集合
    private List<Msg> list = new ArrayList<>();
    //适配器
    private MsgAdapter msgAdapter;
    //滚动控件RecyclerView
    private RecyclerView recyclerView;
    private void initRecyclerView(){
        if(translateActivity != null){
            //初始化滚动控件
            recyclerView = (RecyclerView)translateActivity.findViewById(R.id.translate_activity_list);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(translateActivity);
            if(recyclerView != null)recyclerView.setLayoutManager(mLayoutManager);
            msgAdapter = new MsgAdapter(list);
            if(recyclerView != null)recyclerView.setAdapter(msgAdapter);
        }
    }

    //**************************初始化进度条
    private SmoothProgressBar progressBar;
    private void initProgressBar(){
        if(translateActivity != null){
            progressBar = translateActivity.findViewById(R.id.translate_activity_progressbutton);
            if(progressBar != null){
                //一开始不知道为啥就是要运行动画，无奈只好设置不可见并且再停止进度条了
                progressBar.progressiveStop();
                progressBar.setVisibility(View.INVISIBLE);
                //颜色数组
                int[] colors = {Color.RED, Color.YELLOW, Color.GREEN, Color.BLUE};
                //他的代码，没怎么看懂，不推荐修改！！！！！！
                progressBar.setIndeterminateDrawable(new SmoothProgressDrawable.Builder(translateActivity)
                        .colors(colors)
                        .interpolator(new DecelerateInterpolator())
                        .sectionsCount(4)
                        .separatorLength(8)         //You should use Resources#getDimensionPixelSize
                        .strokeWidth(8f)            //You should use Resources#getDimension
                        .speed(2f)                 //2 times faster
                        .progressiveStartSpeed(2)
                        .progressiveStopSpeed(10f)
                        .reversed(false)
                        .mirrorMode(false)
                        .progressiveStart(false)
                        .build());
            }
        }
    }

    //**************************初始化语音按钮
    private ImageButton speechButton;
    //是否按了按钮（准备好说话才表示按了）
    private boolean isPress = false;
    //是否正在识别，默认不在识别状态
    private boolean isRecognizing = false;
    private void initSpeechButton(){
        if(translateActivity != null){
            speechButton = translateActivity.findViewById(R.id.translate_activity_speechbutton);
            if(speechButton != null){
                speechButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pressButton();
                    }
                });
            }
        }
    }
    private void pressButton(){
        if(!isPress){
            if(backgroundUtil != null){
                //从按下那一刻开始就开始工作了
                isWorking = true;
                //如果是中-英
                if(translateWay == TRANSLATEWAY_C_TO_E){
                    backgroundUtil.startSpeech(null, BackgroundUtil.SPEECH_CHINESE);
                }else{
                    backgroundUtil.startSpeech(null, BackgroundUtil.SPEECH_ENGLISH);
                }
                //此时表示按了按钮
                isPress = true;
            }
        }else{
            //按下了按钮，此时正在录音了才能stop，否则啥也干不了
            if(isRecognizing){
                if(backgroundUtil != null){
                    backgroundUtil.stopRecognize();
                }
                //停止语音直接设为没有按按钮
                isPress = false;
                //停止语音后结束UI
                stopUI();
                //按了语音停止了以后就直接显示加载控件
                showMKLoader();
            }

        }
    }

    //**************************初始化加载进度条控件
    private MKLoader mkLoader;
    private void initMKLoader(){
        if(translateActivity != null){
            mkLoader = translateActivity.findViewById(R.id.translate_activity_mkloader);
        }
    }


    //后台工具类监听******************************************
    class MyBackgroundUtilListener implements BackgroundUtil.BackgroundUtilListener{
        //临时结果
        private String tempResult = null;

        //语音唤醒了翻译
        @Override
        public void speechWakeUp(String wakeUpWord) {
            if(wakeUpWord.equals(BackgroundUtil.WAKEUPWORD_STARTSPEECH)){
                //去开始语音啊
                pressButton();
            }
        }

        //可以开始说话
        @Override
        public void startSpeech() {
            //开始UI
            startUI();
        }

        //临时识别结果
        @Override
        public void tempRecoginze(String temp) {
            //每次临时的结果就存储下来
            tempResult = temp;
        }

        //结束说话
        @Override
        public void endSpeech() {
            //结束UI
            stopUI();
            //结束说话时就显示加载
            showMKLoader();
        }

        //完成识别了
        @Override
        public void wellRecognize() {
            //不管三七二十一，结束了就一定要停止UI
            stopUI();
            //完成识别就隐藏加载
            hideMKLoader();
            if(tempResult != null && !tempResult.isEmpty()){
                notifyItemInserted(tempResult,1);
                //这时去翻译啊
                if(backgroundUtil != null){
                    if(translateWay == TRANSLATEWAY_C_TO_E){
                        backgroundUtil.translate(tempResult , BackgroundUtil.TRANSLATEWAY_C_TO_E);
                    }else{
                        backgroundUtil.translate(tempResult , BackgroundUtil.TRANSLATEWAY_E_TO_C);
                    }
                }
            }else{
                //不去翻译证明结束流程，重置
                reset();
                //提示用户没有说话
                Toast.makeText(translateActivity,"检测到用户没有说话",Toast.LENGTH_SHORT).show();
            }
            //识别完就设置为空
            tempResult = null;
        }

        //完成翻译了
        @Override
        public void wellTranslate(String result) {
            if(result != null && !result.isEmpty()){
                Log.d(TAG, "wellTranslate: 翻译结束了");
                //由于翻译时开启了线程，所以必须异步操作控件
                //为Message传入信息，参考https://blog.csdn.net/qq_37321098/article/details/81535449
                Bundle bundle = new Bundle();
                //传入key，value
                bundle.putString(translateResultKey,result);
                Message message = Message.obtain();
                message.setData(bundle);
                message.what = SHOW_TRANSLATE_RESULT;
                mHandler.sendMessage(message);

                /**************翻译完以后让语音合成啊******************/
                if(backgroundUtil != null)backgroundUtil.speak(result);
            }else{
                /***********翻译出错****************/
                Log.d(TAG, "wellTranslate: 翻译为空了，哪里出错了");
            }
            //不管有没有错误，都要重置，因为语音合成不管重置的事情了，不会影响
            reset();
        }
    }

    //********************************加载控件方法
    //显示加载控件
    private void showMKLoader(){
        if(speechButton != null && mkLoader != null){
            speechButton.setVisibility(View.GONE);
            mkLoader.setVisibility(View.VISIBLE);
        }
    }
    //隐藏加载控件
    private void hideMKLoader(){
        if(speechButton != null && mkLoader != null){
            speechButton.setVisibility(View.VISIBLE);
            mkLoader.setVisibility(View.GONE);
        }
    }

    //******************************UI变化的方法
    //UI是否开始了
    private boolean isUIStart = false;
    //开始变化UI
    private void startUI(){
        //设置可见啊
        if(progressBar != null){
            progressBar.setVisibility(View.VISIBLE);
        }
        //开始UI一定正在录音了
        isRecognizing = true;
        //开始进度条和换图片(UI没开始再开始好吗）
       if(!isUIStart){
           isUIStart = true;
           if(progressBar != null)progressBar.progressiveStart();
           if(speechButton != null)speechButton.setImageResource(R.drawable.translateactivity_audio2);
       }
    }
    //停止UI变化
    private void stopUI(){
        //按钮没按下了
        isPress = false;
        //不在录音了
        isRecognizing = false;
        //停止进度条和改变按钮图片（UI开始了才能停止啊）
       if(isUIStart){
           isUIStart = false;
           if(progressBar != null && !progressBar.isActivated())progressBar.progressiveStop();
           if(speechButton != null)speechButton.setImageResource(R.drawable.translateactivity_audio1);
       }
    }


    //异步处理线程******************************************************

    //翻译结果传入Bundle的key
    private static final String translateResultKey = "translateResult";

    //信息常量
    private static final int SHOW_TRANSLATE_RESULT = 0X114;        //显示翻译结果
    @SuppressLint("HandlerLeak")
    //线程处理类
    private Handler mHandler=new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            Bundle mBundle;
            switch (msg.what){
                //正在录音
                case SHOW_TRANSLATE_RESULT:
                    mBundle = msg.getData();
                    String translateResult = mBundle.getString(translateResultKey);
                    notifyItemInserted(translateResult,0);
                    break;
            }
        };
    };


    //重置一些东西
    private void reset(){
        isWorking = false;
    }

    //改变翻译方式
    private void changeWay(){
        //只有不在工作的时候才能修改
        if(!isWorking){
            if(leftText != null && rightText != null){
                //如果此时是中-英
                if(translateWay == TRANSLATEWAY_C_TO_E){
                    translateWay = TRANSLATEWAY_E_TO_C;
                    //文本改变，左边英文右边中文
                    leftText.setText(R.string.str_translate_english);
                    rightText.setText(R.string.str_translate_chinese);
                }else{
                    translateWay = TRANSLATEWAY_C_TO_E;
                    //文本改变，左边中文右边英文
                    leftText.setText(R.string.str_translate_chinese);
                    rightText.setText(R.string.str_translate_english);
                }
            }
        }
    }

    //刷新滚动控件，显示最后一行
    public void notifyItemInserted(String content,int type){
        //创建Msg并传入类型
        Msg mMsg = new Msg(content,type);
        //添加到集合（因为传入的就是这个集合，所以集合add以后另外一边也是同样的mList，刷新即可）
        list.add(mMsg);
        //刷新滚动控件
        msgAdapter.notifyItemInserted(list.size() - 1);
        //显示到最后一行
        recyclerView.scrollToPosition(list.size() - 1);
    }
}
