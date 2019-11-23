package com.moon.ailatrieuphu.api;

import android.icu.util.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {
    private static Retrofit retrofit=null;
    public static Retrofit getClient(String baseURL){
        Gson gson=new GsonBuilder().setLenient().create();
        OkHttpClient build=new OkHttpClient.Builder()
                .readTimeout(5000, java.util.concurrent.TimeUnit.MILLISECONDS)
                .writeTimeout(5000, java.util.concurrent.TimeUnit.MILLISECONDS)
                .connectTimeout(5000, java.util.concurrent.TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(true)
                .build();
        retrofit=new Retrofit.Builder()
                .baseUrl(baseURL)
                .client(build)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        return retrofit;
    }


}
