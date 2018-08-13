package com.zhys.protobufdemo.http;


import com.zhys.protobufdemo.LoginRequestOuterClass;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * 所有的web接口都在此
 *
 * @author HDL
 */
public interface ApiService {

    /**
     * 登录
     *
     * @return
     */
    @POST("login.action")
    Observable<LoginRequestOuterClass.LoginResponse> login(@Body RequestBody bytes);
}
