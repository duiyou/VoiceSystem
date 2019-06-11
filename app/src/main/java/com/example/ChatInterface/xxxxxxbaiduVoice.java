package com.example.ChatInterface;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
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

public class xxxxxxbaiduVoice {
    private static final String serverURL = "http://vop.baidu.com/server_api";   //语音识别网关
    private static String token=null;
    private static final String apiKey = "6MWklZjrp4038E1EgS8oLhta";             // API Key 
    private static final String secretKey = "321ffa4a53cdd4c434508517ada16aa4";  // Secret Key
    private static final String cuid = "868540756380665";//唯一表示码

    private File filePath= new File("/mnt/sdcard", "myfile.amr");
  //  由于是安卓开发，因此填写手机的唯一标识码，获取方式为拨号处输入： *#06#

   // APIKey、SecretKey和cuid 需要填写你自己的，否则可能无法正常使用。

 //                2）GetToken

    private void getToken(){
        //开启线程是为了防止终端UI线程,没有用while
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                String getTokenURL = "https://openapi.baidu.com/oauth/2.0/token?grant_type=client_credentials" +"&client_id=" + apiKey + "&client_secret=" + secretKey;
                try {
                    //连接了网络，之后会回传信息给connexction
                    connection = (HttpURLConnection) new URL(getTokenURL).openConnection();
                    token = new JSONObject(printResponse(connection)).getString("access_token");
                    SpeechRecognition();    //开始语音识别
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if(connection!=null) connection.disconnect();
                }
            }
        }).start();
    }
//      3）语音识别

    private void SpeechRecognition(){
        //开启线程是为了防止终端UI线程,没有用while
        new Thread(new Runnable() {
            @Override
            public void run() {
                String strc;
                try {
                    File pcmFile = new File(filePath.getAbsolutePath());
                    HttpURLConnection conn = (HttpURLConnection) new URL(serverURL+ "?cuid=" + cuid + "&token=" + token).openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "audio/amr; rate=16000");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                    wr.write(loadFile(pcmFile));
                    wr.flush();
                    wr.close();
                    //上面写入数据以后会回传信息给conn
                    strc=printResponse(conn);
                    Message message = new Message();
                    message.what = 0x02;
                    message.obj =strc;
                    handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
//         4）字符处理

    private  String printResponse(HttpURLConnection conn) throws Exception {
        if (conn.getResponseCode() != 200) {
            return "";
        }
        InputStream is = conn.getInputStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        String line;
        StringBuffer response = new StringBuffer();
        while ((line = rd.readLine()) != null) {
            response.append(line);
            response.append('\r');
        }
        rd.close();
        Message message = new Message();
        message.what = 0x01;
        message.obj = new JSONObject(response.toString()).toString(4);
        handler.sendMessage(message);
        return response.toString();
    }
//           5文件加载

    private byte[] loadFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);
        long length = file.length();
        byte[] bytes = new byte[(int) length];
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }
        if (offset < bytes.length) {
            is.close();
            throw new IOException("Could not completely read file " + file.getName());
        }
        is.close();
        return bytes;
    }
//        6）为了在线程中将信息传递出来，需要一个handler来实现

    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler(){
        public void handleMessage(Message msg){
            String response=(String)msg.obj;
            String strc=null;
            switch (msg.what){
                case 0x01:
                    Log.e("return:",response);            //得到返回的所有结果
                    break;
                case 0x02:
                    strc=getRectstr(response,"[","]");    //得到返回语音内容
                    Log.e("return:",strc);
                    break;
                default:
                    break;
            }
        }
    };
//        7）识别内容获取

    private String getRectstr(String str,String strStart, String strEnd){
        if (str.indexOf(strStart) < 0 || str.indexOf(strEnd) < 0) return "";
        return str.substring(str.indexOf(strStart) + strStart.length()+1, str.indexOf(strEnd)-2);
    }

}
