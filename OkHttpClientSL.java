package com.aodong.microfinance.remote;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by wangyc on 2017/9/18.
 */

public class OkHttpClientSL {

    private static OkHttpClient okHttpClient;

    //非常有必要，要不此类还是可以被new
    private OkHttpClientSL() {

    }

    public static OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            synchronized (OkHttpClientSL.class) {
                if (okHttpClient == null) {
                    okHttpClient = new OkHttpClient().newBuilder()
                            .connectTimeout(30, TimeUnit.SECONDS)
                            .build();
                }
            }
        }
        return okHttpClient;
    }

}
