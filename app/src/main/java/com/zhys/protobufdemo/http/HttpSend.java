package com.zhys.protobufdemo.http;

import android.content.Context;

import com.zhys.protobufdemo.LoginRequestOuterClass;

import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.RequestBody;


/**
 * 网络发送器
 * Created by HDL on 2017/11/20.
 *
 * @author HDL
 */

public class HttpSend {
    private static HttpSend mHttpSend;
    private ApiService apiService;

    private HttpSend() {
    }

    public static HttpSend getInstance() {
        if (mHttpSend == null) {
            synchronized (HttpSend.class) {
                if (mHttpSend == null) {
                    mHttpSend = new HttpSend();
                }
            }
        }
        return mHttpSend;
    }

    /**
     * 初始化上下文对象
     *
     * @param context
     */
    public void initContext(Context context) {
        HttpConfiger.getInstance().initContext(context.getApplicationContext());
        apiService = HttpConfiger.getInstance().getRetrofit().create(ApiService.class);
    }

    /**
     * 用户登陆
     *
     * @param username   用户名
     * @param pwd        密码
     * @param subscriber
     */
    public void login(String username, String pwd, ResultCallbackListener<LoginRequestOuterClass.LoginResponse> subscriber) {
        //构建请求信息
        LoginRequestOuterClass.LoginRequest loginRequest = LoginRequestOuterClass.LoginRequest.newBuilder().setUsername(username).setPwd(pwd).build();
        RequestBody parms = RequestBody.create(MediaType.parse("application/octet-stream"), loginRequest.toByteArray());
        Observable<LoginRequestOuterClass.LoginResponse> login = apiService.login(parms);
        HttpConfiger.getInstance().toSubscribe(login).subscribe(subscriber);
    }

}
