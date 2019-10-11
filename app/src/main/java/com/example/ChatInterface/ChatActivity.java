package com.example.ChatInterface;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.voicesystem.BaseActivity;
import com.example.voicesystem.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 聊天活动
 * 问题：back以后按道理没有销毁活动应该是恢复原样，为啥啥都没有了！！！！！！！！！！！！！！！！！
 */
public class ChatActivity extends BaseActivity {
    //适配器的信息集合
    private List<Msg> mList = new ArrayList<Msg>();
    //适配器
    private MsgAdapter mMsgAdapter;
    //滚动控件RecyclerView
    RecyclerView mRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity_layout);
        //初始化滚动控件
        mRecyclerView = (RecyclerView)findViewById(R.id.chatactivity_list);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mMsgAdapter = new MsgAdapter(mList);
        mRecyclerView.setAdapter(mMsgAdapter);

    }
    //刷新滚动控件，显示最后一行
    public void notifyItemInserted(String content,int type){
        //创建Msg并传入类型
        Msg mMsg = new Msg(content,type);
        //添加到集合（因为传入的就是这个集合，所以集合add以后另外一边也是同样的mList，刷新即可）
        mList.add(mMsg);
        //刷新滚动控件
        mMsgAdapter.notifyItemInserted(mList.size() - 1);
        //显示到最后一行
        mRecyclerView.scrollToPosition(mList.size() - 1);
    }
    //销毁活动的时候
    @Override
    protected void onDestroy() {
        if(SpeechSynthesisUtil.mSpeechSynthesisUtil != null){
            //关闭语音合成工具类
            SpeechSynthesisUtil.mSpeechSynthesisUtil.Destpry();
        }
        super.onDestroy();
    }
    //准备去打开另一个活动时就暂停播放哦
    @Override
    protected void onPause() {
        if(SpeechSynthesisUtil.mSpeechSynthesisUtil != null){
            SpeechSynthesisUtil.mSpeechSynthesisUtil.stop();
        }
        super.onPause();

    }
    //开始这个活动
    public static void actionStart(Context context){
        Intent intent = new Intent(context, ChatActivity.class);
        context.startActivity(intent);
    }
}
