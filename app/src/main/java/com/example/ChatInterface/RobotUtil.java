package com.example.ChatInterface;

import android.util.Log;

import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpCallback;

import org.json.JSONException;
import org.json.JSONObject;

/*
//        当我们调用接口时，基本都是json数据返回，那么如果返回的json数据特别大，
//        那么一般就是显示一行，然后一直往后、往后、往后延伸。除非我们自己手动进行换行。
//        但是在JSONObject中，有一个toString方法是可以通过参数来解决这个小问题的，
//        那就是通过传入一个数值，来让其自动换行，然后按指定参数长度来缩进（参数为多少，就缩进多少）。
        String jsonStr = "{'a': 'a','b': {'ba':'ba', 'bb':'bb'}}";
                JSONObject jsonO = null;
                try {
                jsonO = new JSONObject(jsonStr);
                //System.out.println(jsonO.toString(4));
                Log.d("MainActivity",jsonO.toString(8));
                } catch (JSONException e) {
                e.printStackTrace();
                }
*/


/**
 * 机器人工具类
 */
public class RobotUtil {
    private static final String key="58ce7ff98c5b4c989d6fbfc1ac270b60";
    public RobotUtil(){

    }
    //监听接口，得到机器人回复就把回复内容传过去
    public interface robotReplyListener{
        void wellRobotReply(String robotReply);
    }
    private robotReplyListener mListener;
    public void setRobotReplyListener(robotReplyListener listener){
        mListener=listener;
    }
    //开启了一个线程得到机器人回复，得到以后就返回监听
    public void robotReply(final String responseResult){
        new Thread(new Runnable() {
            @Override
            public void run() {
                //直接用语音识别结果访问的URL
                String url ="http://www.tuling123.com/openapi/api?"+
                        "key="+key+"&info="+responseResult;
                //RxVolley将信息发出（添加RxVolley依赖，
                // 在app的build.gradle的ependencies中添加compile 'com.kymjs.rxvolley:rxvolley:1.1.4'）
                RxVolley.get(url, new HttpCallback() {
                    //成功之后调用的函数，参数为返回的String的JSON数据
                    @Override
                    public void onSuccess(String t) {
                        //解析返回的JSON数据
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(t);
                            //通过key（text）获取value，即获得机器人回复的结果
                            String text = jsonObject.getString("text");
                            if(mListener!=null){
                                mListener.wellRobotReply(text);
                            }
                            Log.d("RobotUtil","机器人回复："+text);
                        } catch (JSONException e) {
                            //e.printStackTrace();
                            Log.d("RobotUtil", "得到机器人回复结果解析有问题");
                        }
                    }
                });
            }
        }).start();

    }
}
