package com.example.AllUtil;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class TranslateManager {
    private static final String TAG = "TranslateManager";

    // 在平台申请的APP_ID 详见 http://api.fanyi.baidu.com/api/trans/product/desktop?req=developer
    private static final String APP_ID = "20191011000340546";
    private static final String SECURITY_KEY = "_dVYrWt6PEvAzpxGAgGh";

    //传送文本的url
    private static final String url = "http://api.fanyi.baidu.com/api/trans/vip/translate";


    //监听接口，语音识别好就把识别内容传过去
    public interface TranslationListener{
        void wellTranslation(String realResult);
    }
    private TranslationListener mListener;
    public void setTranslationListener(TranslationListener listener){
        mListener = listener;
    }

    //构造器
    public TranslateManager(){

    }

    //翻译方法
    public static final int C_TO_E = 0x22;  //中-英
    public static final int E_TO_C = 0x11;  //英-中

    //公共翻译方法，传入内容和翻译方式
    public void translate(String content, int translateWay){
        if(translateWay == C_TO_E){
            translate(content,"zh" ,"en" );
        }else if(translateWay == E_TO_C){
            translate(content,"en" ,"zh" );
        }
    }

    //翻译
    private void translate(final String content, final String from, final String to){
        //如果有翻译内容
        if(content != null || content.trim().length() > 0){
            //开启一个线程进行翻译了
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //首先构建键值对
                    Map<String, String> params = buildParams(content, from, to);

                    //得到翻译结果（并在工具类直接过滤出真正结果）
                    String translateResult = TranslateHttpUtil.get(url, params);

                    //如果监听不为空
                    if(mListener != null){
                        //调用监听的完成翻译方法，并传入翻译结果
                        mListener.wellTranslation(translateResult);
                    }
                }
            }).start();
        }else{
            /***************系统提示翻译文本不能为空********************/
            Log.d(TAG, "translate翻译，系统提示翻译文本不能为空");
        }
    }

    private Map<String,String> buildParams(String content, String from, String to){
        Map<String, String> params = new HashMap<String, String>();
        //放入内容、源语言、目的语言
        params.put("q", content);
        params.put("from", from);
        params.put("to", to);

        //放入APPID
        params.put("appid", APP_ID);

        // 用当前时间来构建随机数
        String salt = String.valueOf(System.currentTimeMillis());
        //放入随机数
        params.put("salt", salt);

        // 签名
        String src = APP_ID + content + salt + SECURITY_KEY; // 加密前的原文
        //放入md5加密后的签名
        params.put("sign", md5(src));

        return params;

    }

    //md5加密
    private static String md5(String input) {
        if (input == null)
            return null;

        try {
            // 拿到一个MD5转换器（如果想要SHA1参数换成”SHA1”）
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            // 输入的字符串转换成字节数组
            byte[] inputByteArray = input.getBytes("utf-8");
            // inputByteArray是输入字符串转换得到的字节数组
            messageDigest.update(inputByteArray);
            // 转换并返回结果，也是字节数组，包含16个元素
            byte[] resultByteArray = messageDigest.digest();
            // 字符数组转换成字符串返回
            return byteArrayToHex(resultByteArray);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            return null;
        }
    }

    // 首先初始化一个字符数组，用来存放每个16进制字符
    private static final char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
            'e', 'f' };

    //字符数组转换成字符串
    private static String byteArrayToHex(byte[] byteArray) {
        // new一个字符数组，这个就是用来组成结果字符串的（解释一下：一个byte是八位二进制，也就是2位十六进制字符（2的8次方等于16的2次方））
        char[] resultCharArray = new char[byteArray.length * 2];
        // 遍历字节数组，通过位运算（位运算效率高），转换成字符放到字符数组中去
        int index = 0;
        for (byte b : byteArray) {
            resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
            resultCharArray[index++] = hexDigits[b & 0xf];
        }

        // 字符数组组合成字符串返回
        return new String(resultCharArray);

    }



}
