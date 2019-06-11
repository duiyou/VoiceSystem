package com.example.ChatInterface;

public class Msg {
    //发送常量
    public static final int TYPE_SEND=1;
    //接收常量
    public static final int TYPE_RECEIVED=0;
    //该消息的类型
    private int type;
    //该消息内容
    private String content;
    public Msg(String content,int type){
        this.content=content;
        this.type=type;
    }

    public int getType() {
        return type;
    }

    public String getContent() {
        return content;
    }
}
