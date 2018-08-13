package com.zhys.protobufdemo.http;

import android.content.Context;

import com.google.protobuf.ExtensionRegistry;
import com.hdl.elog.ELog;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.zhys.protobufdemo.LoginRequestOuterClass;
import com.zhys.protobufdemo.utils.HUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.protobuf.ProtoConverterFactory;

/**
 * http请求配置项
 *
 * @author HDL
 */
public class HttpConfiger {
    /**
     * 读取超时时间
     */
    private static final int DEFAULT_READ_TIMEOUT = 60;
    /**
     * 链接超时时间
     */
    private static final int DEFAULT_CONN_TIMEOUT = 10;
    /**
     * 上传超时时间
     */
    private static final int DEFAULT_WRITE_TIMEOUT = 5 * 60;
    /**
     * 请求地址
     */
    private String url = "http://192.168.0.162:8080/protobuf/";
    /**
     * 上下文对象,用来对sp做存取管理
     */
    private Context context;
    /**
     * retrofit对象
     */
    private Retrofit retrofit;
    private Retrofit.Builder retrofitBuilder;

    private static HttpConfiger mHttpConfiger;

    private HttpConfiger() {
    }

    public static HttpConfiger getInstance() {
        if (mHttpConfiger == null) {
            synchronized (HttpConfiger.class) {
                if (mHttpConfiger == null) {
                    mHttpConfiger = new HttpConfiger();
                }
            }
        }
        return mHttpConfiger;
    }

    /**
     * 注册 OkHttp和Retrofit
     *
     * @param context
     */
    public void initContext(Context context) {
        this.context = context.getApplicationContext();
        initHttp();
    }

    private void initHttp() {
        OkHttpClient.Builder httpBuilder = new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_CONN_TIMEOUT, TimeUnit.SECONDS)//连接超时时间
                .readTimeout(DEFAULT_READ_TIMEOUT, TimeUnit.SECONDS)//读取超时时间设置
                .writeTimeout(DEFAULT_WRITE_TIMEOUT, TimeUnit.SECONDS)//写入超时时间设置
                .addInterceptor(httpInterceptor)//添加拦截器
                .retryOnConnectionFailure(true)//错误重连
                .hostnameVerifier(new HostnameVerifier() {//无条件信任所有证书
                    @Override
                    public boolean verify(String s, SSLSession sslSession) {
                        return true;
                    }
                });
        //只有debug版本打印网络请求日志
        if (HUtils.isDebug(context)) {
            httpBuilder.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
        }
        ExtensionRegistry registry = ExtensionRegistry.newInstance();
        retrofitBuilder = new Retrofit.Builder()
                .client(httpBuilder.build())
                .baseUrl(url)//设置域名
                .addConverterFactory(ProtoConverterFactory.createWithRegistry(registry))//一定要在gsonconvert的前面
                .addConverterFactory(GsonConverterFactory.create())//设置Json数据的转换器为Gson
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create());//添加RxJava适配器
        retrofit = retrofitBuilder.build();
    }

    /**
     * 切换域名
     *
     * @param url
     */
    public void convertServiceHub(String url) {
        this.url = url;
        retrofitBuilder.baseUrl(url);
    }

    /**
     * 获取域名
     *
     * @return
     */
    public String getUrl() {
        return url;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

    /**
     * 统一线程处理 HTTP 请求
     *
     * @param <T>
     * @param observable
     */
    public <T> Observable<T> toSubscribe(Observable<T> observable) {
        return observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 创建拦截器
     */
    private Interceptor httpInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = addPublicParameter(chain.request());
            Response response = chain.proceed(request);
            return response;
        }

    };

    /**
     * 给拦截器request路径添加公共参数
     * 登录前和登录后所添加的公共参数不一样
     *
     * @param request
     * @return
     */
    private Request addPublicParameter(Request request) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        HttpUrl.Builder builder = request
                .url()
                .newBuilder()
                .addQueryParameter("timestamp",timestamp);//添加公共参数(每个接口都携带的参数，还可以继续add)
        ELog.e("url = " + builder.build());
        Request newRequest = request
                .newBuilder()
                .method(request.method(), request.body())
                .url(builder.build())
                .build();
        return newRequest;
    }


}
