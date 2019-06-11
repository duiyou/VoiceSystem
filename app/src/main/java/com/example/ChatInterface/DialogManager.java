package com.example.ChatInterface;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.voicesystem.R;

/**
 * 对话框类，在这里得到活动并在活动中展示对话框,设置对话框的图标，音量，文本
 */
public class DialogManager {
    private Dialog mDialog;//对话框
    private View view;//加载的视图view
    private ImageView mIcon;//对话框的图标
    private ImageView mVoice;//对话框的声音大小
    private TextView mlable;//对话框的标签
    private Context mContext;//上下文

    //创建DialogManager管理类时就初始化一些变量，之后就只用显示和关闭而已
    public DialogManager(Context context){
        mContext=context;

        //创建一个对话框在mContext上并且设置了一个theme
        mDialog=new Dialog(mContext, R.style.chat_activity_dialogtheme);
        //创建了一个布局，没有父布局，(书112页)
        view= LayoutInflater.from(mContext).inflate(R.layout.chat_activity_layout_recorderdialog,null);
        //对话框设置一个视图
        mDialog.setContentView(view);
        //获取各个控件实例,图标，音量，标签
        mIcon=(ImageView) mDialog.findViewById(R.id.chat_activity_recorderdialogicon);
        mVoice=(ImageView) mDialog.findViewById(R.id.chat_activity_recorderdialogvoice);
        mlable=(TextView) mDialog.findViewById(R.id.chat_activity_recorderdialoglable);
    }
    //显示对话框
    public void showRecordingDialog(){
        //如果对话框不为空并且没有在显示，则显示
        if(mDialog!=null&&!mDialog.isShowing()){
            //显示之前先初始化布局
            mIcon.setVisibility(View.VISIBLE);
            mVoice.setVisibility(View.VISIBLE);
            mlable.setVisibility(View.VISIBLE);
            mIcon.setImageResource(R.drawable.recorder);
            mVoice.setImageResource(R.drawable.v1);
            mlable.setText("手指上滑，取消发送");
            //显示对话框在mContext上
            mDialog.show();
        }

    }
    public void recording(){
        //如果mDialog正在show
        if(mDialog!=null&&mDialog.isShowing()){
            //设置录音时的图片和文字
            mIcon.setVisibility(View.VISIBLE);
            mVoice.setVisibility(View.VISIBLE);
            mlable.setVisibility(View.VISIBLE);
            mIcon.setImageResource(R.drawable.recorder);
            mlable.setText("手指上滑，取消发送");
        }
    }
    public void wantToCancel(){
        //如果mDialog正在show
        if(mDialog!=null&&mDialog.isShowing()){
            //音量键没了，就剩一个图标问是否取消发送
            mIcon.setVisibility(View.VISIBLE);
            mVoice.setVisibility(View.GONE);
            mlable.setVisibility(View.VISIBLE);
            mIcon.setImageResource(R.drawable.cancel);
            mlable.setText("松开手指，取消发送");
        }
    }
    public void tooShort(){
        //如果mDialog正在show
        if(mDialog!=null&&mDialog.isShowing()){
            //音量键没了，就剩一个图标标识时间太短
            mIcon.setVisibility(View.VISIBLE);
            mVoice.setVisibility(View.GONE);
            mlable.setVisibility(View.VISIBLE);
            mIcon.setImageResource(R.drawable.voice_to_short);
            mlable.setText("      录音时间过短      ");
        }
    }
    //对话框从屏幕上消失
    public void dismissDialog(){
        if(mDialog!=null&&mDialog.isShowing()){
            mDialog.dismiss();
            //mDialog在DialogManager构造时初始化，之后一直复用，不要设置为null
            //mDialog=null;
        }
    }
    //更新Voice上的图片，即音量大小
    public void updateVoiceLevel(int level){
        //如果mDialog正在show
        if(mDialog!=null&&mDialog.isShowing()){
            //说话时Dialog一直更新，所以即使为取消录音的Dialog也会调用updateVoiceLevel，不改变控件的可见性就可以了
//            mIcon.setVisibility(View.VISIBLE);
//            mVoice.setVisibility(View.VISIBLE);
//            mlable.setVisibility(View.VISIBLE);

            //通过方法名找到资源？？？？？？？？
            int resId=mContext.getResources().getIdentifier("v"+level,
                    "drawable",mContext.getPackageName());
            //不管是否为正在录音都更新控件的图片
           mVoice.setImageResource(resId);
        }
    }
}
