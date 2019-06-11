package com.example.ChatInterface;

import android.util.Log;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 语音识别工具类
 * Android9.0有问题，不能使用HTTP，sendVoiceFile(final String filePath)中的connection.getOutputStream()有问题
 * 参考https://blog.csdn.net/zwh1109/article/details/86062495
 */

public class SpeechRecognitionUtil {
    //服务器的URL
    private static final String serverURL = "http://vop.baidu.com/server_api";
    //APIkey
    private static final String apiKey = "HiqW9i6DFZWB0aUNU4O0dBlG";
    //Secret Key
    private static final String secretKey = "DTTH6pGU87RxV4F1n9wfKHdMsH6yACsh";
    //  由于是安卓开发，因此填写手机的唯一标识码，获取方式为拨号处输入： *#06#
    private static final String cuid = "869384030814139";//唯一表示码
    //private static final String cuid = "9YE0218426017022";//唯一表示码
    //private static final String cuid = "A000008E4D3962";//唯一表示码
    //private static final String cuid = "869384030617284";//唯一表示码
    //private static final String cuid = "358240051111110";//原本的唯一表示码
    //请求码，请求之后网站的回调信息
    private static String token=null;
    //语音识别的结果
    private String responseResult=null;

    //监听接口，语音识别好就把识别内容传过去
    public interface speechRecognitionListener{
        void wellRecognition(String responseResult);
    }
    private speechRecognitionListener mListener;
    public void setSpeechRecognitionListener(speechRecognitionListener listener){
        mListener=listener;
    }
    //构造函数
    public SpeechRecognitionUtil(){

    }
    //语音识别,传入文件
    public void speechRecognition(String filePath){
        getTokenAndSendVoiceFile(filePath);
    }
    //获取请求码，获取之后直接发送录音文件
    private void getTokenAndSendVoiceFile(final String filePath){
        //开启一个线程去获取请求码
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection=null;
                //获取请求码的URL
                String getTokenURL="https://openapi.baidu.com/oauth/2.0/token?grant_type=client_credentials"
                        + "&client_id=" + apiKey
                        + "&client_secret=" + secretKey;
                try {
                    //连接URL
                    connection=(HttpURLConnection)new URL(getTokenURL).openConnection();
                    //获取请求码
                    token = new JSONObject(getResponse(connection)).getString("access_token");
                    //发送录音
                    sendVoiceFile(filePath);
                } catch (Exception e) {
                    //e.printStackTrace();
                    Log.d("baiduVoice","获取请求码有问题");
                }
            }
        }).start();
    }
    //发送录音文件
    private void sendVoiceFile(final String filePath){
        //开启一个线程去发送文件内容
        new Thread(new Runnable() {
            @Override
            public void run() {
                //网络类
                try {
                    //发送网络请求
                    HttpURLConnection connection = (HttpURLConnection) new URL(serverURL
                            + "?cuid=" + cuid
                            + "&token=" + token).openConnection();
                    //方式为发送数据POST
                    connection.setRequestMethod("POST");
                    //????????
                    connection.setRequestProperty("Content-Type", "audio/amr; rate=16000");
                    //????????
                    connection.setDoInput(true);
                    //????????
                    connection.setDoOutput(true);

                    //获取网络流,真机有问题！！！！！！！！！！！！！！！！！
                    DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                    Log.d("baiduVoice","？？？？？？");
                    //把文件内容写入流中
                    wr.write(getFileBytes(new File(filePath)));
                    //强制写入流
                    wr.flush();
                    //关闭流
                    wr.close();
                    //上面写入数据以后会回传信息给conn
                    responseResult=getText(getResponse(connection),"[","]");

                    //利用监听把回复内容回调
                    if(mListener!=null){
                        mListener.wellRecognition(responseResult);
                    }
                    Log.d("SpeechRecognitionUtil","语音识别结果："+responseResult);
                } catch (Exception e) {
                    //e.printStackTrace();
                    Log.d("baiduVoice","发送录音文件有问题");
                }
                //reSet初始化
                reSet();
            }

        }).start();
    }

    //得到HttpURLConnection里的内容JSON数据
    private String getResponse(HttpURLConnection connection) throws Exception {
        //如果请求码不是两百就啥也没有
        if (connection.getResponseCode() != 200) {
            return "";
        }
        //获取网络流
        InputStream inputStream = connection.getInputStream();
        //过滤流
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        //一行读取的内容
        String line;
        //JSON转化的字符串
        StringBuilder response = new StringBuilder();
        //一行一行读取流
        while ((line = bufferedReader.readLine()) != null) {
            response.append(line);
            //读了一行加一个回车
            response.append('\r');
        }
        //关闭流
        bufferedReader.close();
        //返回JSON数据
        return response.toString();
    }

    //得到文件的byte数组
    private byte[] getFileBytes(File file) throws IOException {
        InputStream inputStream = new FileInputStream(file);
        //文件长度？？？？？
        long filelength = file.length();
        //开一个byte存
        byte[] bytes = new byte[(int) filelength];
        //存入的下标
        int index = 0;
        //读取的长度
        int readLength = 0;
        //循环读取文件，read一个一个读？？？？？
        while (index < bytes.length
                && (readLength = inputStream.read(bytes, index, bytes.length - index)) >= 0) {
            index += readLength;
        }
        //如果没能完整读取文件
        if (index < bytes.length) {
            inputStream.close();
            throw new IOException("Could not completely read file " + file.getName());
        }
        inputStream.close();
        return bytes;
    }
    //截取某段内容（针对最后回复的JSON，还排除了最后的标点符号）
    private String getText(String str,String strStart, String strEnd){
        if (str.indexOf(strStart)<0 || str.indexOf(strEnd) < 0) return "";
        return str.substring(str.indexOf(strStart) + strStart.length()+1, str.indexOf(strEnd)-2);
    }

    //初始化原来的东西
    private void reSet(){
        token=null;
        responseResult=null;
    }
}
