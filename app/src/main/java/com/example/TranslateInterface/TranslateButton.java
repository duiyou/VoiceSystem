package com.example.TranslateInterface;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.AllUtil.TranslateManager;


public class TranslateButton
        extends android.support.v7.widget.AppCompatButton
        implements View.OnLongClickListener {
    private static final String TAG = "ButtonUtil";

    //是否长按按钮
    private boolean isLongClick = false;

    //移动手指取消录音的距离
    private static final int CANCEL_DISTANCE = 100;

    //按钮的三种状态：正常状态，中-英状态，英-中状态，想取消状态
    private static final int STATE_NORMAL = 0x111;
    private static final int STATE_C_TO_E = 0x113;
    private static final int STATE_E_TO_C = 0x112;
    private static final int STATE_WANT_TO_CANCEL = 0X114;

    //默认按钮状态：正常状态
    private int nowState = STATE_NORMAL;

    private ButtonUtil buttonUtil;


    //构造器
    public TranslateButton(Context context) {
        this(context,null);
    }
    public TranslateButton(Context context, AttributeSet attrs){
        super(context, attrs);
        //创建工具类
        buttonUtil = new ButtonUtil(getContext());
        //此按钮长按监听
        this.setOnLongClickListener(this);
    }
    //此按钮设置的长按监听
    @Override
    public boolean onLongClick(View v) {
        //长按标志
        isLongClick = true;
        //长按了就准备录音
        buttonUtil.prepareAudio();
        /********手机震动*********/
        return false;
    }
    //屏幕触摸触发方法
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //手指在控件上的x，y坐标
        int x = (int) event.getX();
        int y = (int) event.getY();
        //按键状态，获取事件类型
        switch (event.getAction()){
            //手指触摸屏幕
            case MotionEvent.ACTION_DOWN:
                //变为中-英状态
                changeState(STATE_C_TO_E);
                break;


            //手指在屏幕上滑动，会多次触发
            case MotionEvent.ACTION_MOVE:
                //如果正在录音
                if(buttonUtil.getRecordState()){
                    //如果想取消
                    if(buttonToState(x, y) == 1){
                        //变为取消状态
                        changeState(STATE_WANT_TO_CANCEL);
                    //如果想变为中-英状态
                    }else if(buttonToState(x,y) == -1){
                        //变为中-英状态
                        changeState(STATE_C_TO_E);
                    //如果想变为英-中状态
                    }else if(buttonToState(x,y) == -2){
                        //变为英-中状态
                        changeState(STATE_E_TO_C);
                    }
                }
                break;


            //手指离开屏幕
            case MotionEvent.ACTION_UP:
                //如果没有长按，工具类啥都没干，那我们也不用干啥
                if(!isLongClick){

                //还没准备好录音或者，录音太短
                }else if(!buttonUtil.getRecordState() || buttonUtil.getRecordTime() <= 0.5f){
                    //结束录音，tooshort状态
                    buttonUtil.endRecord(ButtonUtil.ENDRECORD_TOOSHORT_STATE);
                //正在录音但是想取消录音
                }else if(nowState == STATE_WANT_TO_CANCEL){
                    //结束录音，cancel状态
                    buttonUtil.endRecord(ButtonUtil.ENDRECORD_CANCEL_STATE);
                //正常结束，中-英
                }else if(nowState == STATE_C_TO_E){
                    //结束录音，中-英状态
                    buttonUtil.endRecord(ButtonUtil.ENDRECORD_NORMAL_STATE, TranslateManager.C_TO_E);
                //正常结束，英-中
                }else if(nowState == STATE_E_TO_C){
                    //结束录音，英-中状态
                    buttonUtil.endRecord(ButtonUtil.ENDRECORD_NORMAL_STATE, TranslateManager.E_TO_C);
                }
                //反正离开屏幕就一定要初始化一些东西
                reset();
                break;
        }

        //系统添加的
        return super.onTouchEvent(event);
    }
    //重置一些属性
    private void reset(){
        isLongClick = false;
        changeState(STATE_NORMAL);
    }

    //判断按钮想要干啥，默认为中-英
    //1表示想取消，-1表示左边为中-英，-2表示右边为英-中
    /***********需要根据按钮形状修改*****************/
    private int buttonToState(int x, int y) {
        //y方向如果距离控件某个范围则为取消发送录音
        if(y < -CANCEL_DISTANCE || y > getHeight() + CANCEL_DISTANCE){
            return 1;
        }
        //x方向在空间外则为英-中
        if(x < 0 || x > getWidth()){
            return -2;
        }
        //默认为中-英
        return -1;
    }

    //改变此时按钮状态
    private void changeState(int state){
        //状态改变时才修改
        if(state != nowState){
            //先改变状态
            nowState = state;
            //根据要改的状态弄代码
            switch(state){
                //变为正常状态
                case STATE_NORMAL:
                    /***********修改按钮的背景啊文本啊什么的*************/
                    /***********工具类关闭对话框*************/
                    Log.d(TAG, "关闭了对话框");
                    break;
                //变为中-英状态
                case STATE_C_TO_E:
                    /***********修改按钮的背景啊文本啊什么的*************/
                    /***********工具类改为录音对话框**************/
                    Log.d(TAG, "对话框改为中-英翻译");
                    break;
                //变为英-中状态
                case STATE_E_TO_C:
                    /***********修改按钮的背景啊文本啊什么的*************/
                    /***********工具类改为录音对话框**************/
                    Log.d(TAG, "对话框改为英-中翻译");
                    break;
                //变为取消状态
                case STATE_WANT_TO_CANCEL:
                    /***********修改按钮的背景啊文本啊什么的*************/
                    /*********工具类改为取消对话框************/
                    Log.d(TAG, "对话框改为取消");
                    break;
            }
        }
    }

}
