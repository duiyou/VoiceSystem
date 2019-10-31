package com.example.TranslateInterface;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.voicesystem.R;

import java.util.List;

/**
 * List的适配器，参考博客https://blog.csdn.net/Mr_Leixiansheng/article/details/85061618
 */
public class MsgAdapter extends RecyclerView.Adapter {

    //适配器的信息集合
    private List<Msg> mList;

    //构造函数，此时传入集合mList
    public MsgAdapter(List<Msg> mList){
        this.mList=mList;
    }

    //发送信息的ViewHolder
    public static class SendViewHolder extends RecyclerView.ViewHolder{
        //TextView控件
        public TextView mTextView;
        //构造时直接传入了View，然后得到了控件的实例
        public SendViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextView=(TextView)itemView.findViewById(R.id.translate_activity_list_usertextview);
        }
    }
    //接收信息的ViewHolder
    public static class ReceivedViewHolder extends RecyclerView.ViewHolder{
        //TextView控件
        public TextView mTextView;
        //构造时直接传入了View，然后得到了控件的实例
        public ReceivedViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextView=(TextView)itemView.findViewById(R.id.translate_activity_list_translatetextview);
        }
    }

    //返回当前子项的类型，1为send类型，0为recevied，在onCreateViewHolder调用前调用
    @Override
    public int getItemViewType(int position) {
        switch (mList.get(position).getType()){
            case Msg.TYPE_SEND:
                return Msg.TYPE_SEND;
            case Msg.TYPE_RECEIVED:
                return Msg.TYPE_RECEIVED;
        }
        return super.getItemViewType(position);
    }

    //为每一个子项创建ViewHolder视图并且保存
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //先定义一个ViewHolder并不实例（有两种）
        RecyclerView.ViewHolder mViewHolder;
        //View视图传入到ViewHolder中
        View mView;
        //从getItemViewType返回的类型
        switch (viewType){
            case Msg.TYPE_SEND:
                //创建视图
                mView= LayoutInflater.from(parent.getContext()).inflate(R.layout.translateactivity_listuser, parent, false);
                //创建ViewHolder并传入视图
                mViewHolder = new SendViewHolder(mView);
                return mViewHolder;
            case Msg.TYPE_RECEIVED:
                //创建视图
                mView= LayoutInflater.from(parent.getContext()).inflate(R.layout.translateactivity_listtranslate, parent, false);
                //创建ViewHolder并传入视图
                mViewHolder = new ReceivedViewHolder(mView);
                return mViewHolder;
            default:
                return null;

        }

    }
    //每次显示到某个子项的时候就刷新布局了
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        //得到集合里面某个元素的内容content
        String content = mList.get(position).getContent();
        //instanceof 运算符是用来在运行时指出对象是否是特定类的一个实例。instanceof通过返回一个布尔值来指出，
        // 这个对象是否是这个特定类或者是它的子类的一个实例。
        if(viewHolder instanceof SendViewHolder){
            ((SendViewHolder)viewHolder).mTextView.setText(content);
        }else if(viewHolder instanceof ReceivedViewHolder){
            ((ReceivedViewHolder)viewHolder).mTextView.setText(content);
        }
    }

    //反正就是要重写，返回集合长度
    @Override
    public int getItemCount() {
        return mList.size();
    }
}
