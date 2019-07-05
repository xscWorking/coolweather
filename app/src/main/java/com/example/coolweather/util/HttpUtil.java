package com.example.coolweather.util;
import android.util.Log;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HttpUtil {
    public static void sendOkHttpRequest(String address,okhttp3.Callback callback){
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().url(address).build();
        Log.d("MainActivity", "准备发送请求 ");
        client.newCall(request).enqueue(callback);//实际就看这个函数执行！！！其中的的Callback别有洞天,f
        Log.d("MainActivity", "请求就已经发送 ");//能正常显示，表明上一句开启了子线程，因为它里面的内容后于这里显示,
        // 主要是因为主线程中不能进行网络资源访问
    }
}
