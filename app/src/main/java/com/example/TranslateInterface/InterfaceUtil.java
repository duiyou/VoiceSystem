package com.example.TranslateInterface;

import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.voicesystem.R;

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

    //对应的活动
    private TranslateActivity translateActivity;

    InterfaceUtil(TranslateActivity translateActivity){
        this.translateActivity = translateActivity;
        init();
    }
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
            recyclerView.setLayoutManager(mLayoutManager);
            msgAdapter = new MsgAdapter(list);
            recyclerView.setAdapter(msgAdapter);
        }

        //测试
        notifyItemInserted("你现在在做什么",1);
        notifyItemInserted("what are you doing now？",0);
    }

    //**************************初始化进度条
    private SmoothProgressBar progressBar;
    private void initProgressBar(){
        if(translateActivity != null){
            progressBar = translateActivity.findViewById(R.id.translate_activity_progressbutton);
            //一开始不知道为啥就是要运行动画，无奈只好设置不可见并且再停止进度条了
            progressBar.progressiveStop();
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
                    .progressiveStopSpeed(5f)
                    .reversed(false)
                    .mirrorMode(false)
                    .progressiveStart(false)
                    .build());
        }
    }

    //**************************初始化语音按钮
    private ImageButton speechButton;
    //是否按了按钮（准备好说话才表示按了）
    private boolean isPress = false;
    private void initSpeechButton(){
        if(translateActivity != null){
            speechButton = translateActivity.findViewById(R.id.translate_activity_speechbutton);
            speechButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!isPress){
                        if(backgroundUtil != null){
                            //如果是中-英
                            if(translateWay == TRANSLATEWAY_C_TO_E){
                                backgroundUtil.startSpeech(null, BackgroundUtil.SPEECH_CHINESE);
                            }else{
                                backgroundUtil.startSpeech(null, BackgroundUtil.SPEECH_ENGLISH);
                            }
                        }
                    }else{
                        if(backgroundUtil != null){
                            backgroundUtil.stopRecognize();
                        }
                        changeUIState();
                    }

                }
            });
        }
    }

    //后台工具类监听
    class MyBackgroundUtilListener implements BackgroundUtil.BackgroundUtilListener{
        //准备好语音了
        @Override
        public void prepareSpeech() {
            Log.d(TAG, "prepareSpeech: 执行到这里了吗");
            //改变进度条和按钮
            changeUIState();
        }
        //完成识别了
        @Override
        public void wellRecognize(String result) {
            //改变进度条和按钮
            changeUIState();
            if(result != null){
                notifyItemInserted(result,1);
            }else{
                /***********报错****************/
            }
        }
        //完成翻译了
        @Override
        public void wellTranslate(String result) {
            if(result != null){
                notifyItemInserted(result,0);
            }else{
                /***********报错****************/
            }
        }
    }

    //改变进度条和语音按钮的状态
    //默认不在识别状态
    private boolean isRecognizing = false;
    private void changeUIState(){
        //设置可见啊
        if(progressBar != null){
            progressBar.setVisibility(View.VISIBLE);
        }
        //如果此时在录音状态
        if(isRecognizing){
            isPress = false;
            isRecognizing = false;
            if(progressBar != null)progressBar.progressiveStop();
            if(speechButton != null)speechButton.setImageResource(R.drawable.translateactivity_audio1);
        }else{
            isPress = true;
            isRecognizing = true;
            if(progressBar != null)progressBar.progressiveStart();
            if(speechButton != null)speechButton.setImageResource(R.drawable.translateactivity_audio2);
        }
    }

    //改变翻译方式
    private void changeWay(){
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
