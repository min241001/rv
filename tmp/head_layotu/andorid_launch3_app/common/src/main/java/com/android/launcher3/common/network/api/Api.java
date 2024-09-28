package com.android.launcher3.common.network.api;

import com.android.launcher3.common.BuildConfig;
import com.android.launcher3.common.network.interceptor.HeaderInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class Api {

    public static final String BASE_URL_CDN = BuildConfig.CDN;// 测试环境
    //读超时长，单位：毫秒
    public static final long READ_TIME_OUT = 30L;
    //连接时长，单位：毫秒
    public static final long CONNECT_TIME_OUT = 30L;
    static final String BASE_URL = BuildConfig.HOST;// 开发者环境
    private static ApiService apiService;

    private Api() {
        // 拦截器
        Interceptor headerInterceptor = new HeaderInterceptor();

        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(READ_TIME_OUT, TimeUnit.SECONDS)
                .connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS)
                .addInterceptor(headerInterceptor) // 拦截器
                .build();

        // 创建Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()) // 请求的结果转为实体类
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    public static ApiService getService() {
        if (apiService == null) {
            synchronized (Api.class) {
                if (apiService == null) {
                    new Api();
                }
            }
        }
        return apiService;
    }

    /**
     * 获取cdn相关的下载路径
     *
     * @param url
     * @return
     */
    public static String getCdnUrl(String url) {
        return String.format("%s%s", BASE_URL_CDN, url);
    }

}
