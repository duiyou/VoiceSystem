package com.example.AllUtil;

import android.media.MediaRecorder;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class AudioManager {
    private static final String TAG = "AudioManager";

    //录音类
    private MediaRecorder mMediaRecorder;
    //存放音频的文件夹
    private String mDir;
    //文件全路径
    private String mCurrentFilePath;
    //全局静态录音类
    private static AudioManager mInstance;
    //是否已经准备好录音了
    private boolean isPrepaerd = false;

    //构造函数，传入存储音频文件夹路径
    private AudioManager(String dir){
        mDir=dir;
    }

    //创建接口，用于回调，准备完毕
    public interface AudioStateListener{
        void wellPrepared();
    }
    private AudioStateListener mListener;
    public void setOnAudioStateListener(AudioStateListener listener){
        mListener=listener;
    }


//    在写程序库代码时，有时有一个类需要被所有的其它类使用，但又要求这个类只能实例化一次，
//    是个服务类，定义一次，其它类使用同一个这个类的实例,如果在每一个需要的类中都声明一个类的对象，
//    那么会导致有N个类，每类的定义都不一样，但是这个类是服务类，只能定义一次，定义多个N类会导致不同的内存地址,
//    总而言之，就是具有单例模式可以防止 数据的冲突，节省内存空间的作用。

//    此类使用单例模式，方法取得单例类对象
    public static AudioManager getInstance(String dir){
        if(mInstance == null){
            //加锁，在实例化的过程中不允许其他地方实例化
            synchronized (AudioManager.class){
                if(mInstance == null){
                    mInstance = new AudioManager(dir);
                }
            }
        }
        //返回AudioManager单例
        return mInstance;
    }

    //准备
    public void prepareAudio(){
        try {
            //还没准备好之前默认为false
            isPrepaerd = false;
            //存储的文件夹
            File dir=new File(mDir);
            if(!dir.exists()){
                //如果不存在创建文件夹
                dir.mkdir();
            }

            //存储文件名，随机获取
            String fileName=generateFileName();
            //创建文件
            File file=new File(dir,fileName);
            //文件全路径
            mCurrentFilePath=file.getAbsolutePath();
            //new录音类
            mMediaRecorder=new MediaRecorder();
            //设置输出的文件
            mMediaRecorder.setOutputFile(mCurrentFilePath);
            //设置音频源为麦克风MIC
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

            /*************语音识别需要设置的参数****************/
            //设置所录制的声音的采样率为8000
            mMediaRecorder.setAudioSamplingRate(8000);
            //单声道采样
            mMediaRecorder.setAudioChannels(1);
            //设置音频的格式,API为10以上用AMR_NB,以下用RAW_AMR
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_WB);
            //设置音频编码为amr
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
            //设置所录制的声音的编码位率。
            mMediaRecorder.setAudioEncodingBitRate(16000);


            //录音准备好了
            mMediaRecorder.prepare();
            //开始录音
            mMediaRecorder.start();
            //准备完成
            isPrepaerd=true;
            //回滚通知已经准备好了
            if(mListener!=null){
                mListener.wellPrepared();
            }
        } catch (IOException e) {
            Log.d(TAG, "prepareAudio准备录音出错");
            //e.printStackTrace();
        }
    }

    /**
     * 随机生成文件名称
     * @return
     */
    private String generateFileName() {
        return UUID.randomUUID().toString()+".amr";
    }

    //得到存储文件全路径
    public String getCurrentFilePath(){
        return mCurrentFilePath;
    }

    //获取当前音量等级,maxLevel为最大等级
    public int getVoiceLevel(int maxLevel){
        //如果已经准备好了
        if(isPrepaerd){
            //捕获异常防止有错误
            try {
                //getMaxAmplitude得到麦克风的振幅0-32767中的数
                //getMaxAmplitude得到最大振幅32767,用来除于32768则有0-1之间的小数
                //在次之前乘与最大等级7则有0-6之间的整数，加一则为1 -7
                return maxLevel*mMediaRecorder.getMaxAmplitude()/32768+1;
            } catch (IllegalStateException e) {
                //e.printStackTrace();
                Log.d(TAG, "getVoiceLevel得到音量大小出错");
            }
        }
        //有异常则默认返回1
        return 1;//默认第一级
    }
    //停止录音并且释放录音资源
    public void release(){
        isPrepaerd=false;
        mMediaRecorder.stop();
        mMediaRecorder.release();
        mMediaRecorder=null;
    }
    //取消录音，此时不仅释放资源，还要删除录下的音频文件
    public void cancel(){
        release();
        if(mCurrentFilePath!=null){
            File file=new File(mCurrentFilePath);
            file.delete();
            mCurrentFilePath=null;
        }

    }
}
