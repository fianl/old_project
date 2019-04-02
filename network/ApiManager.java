package vdream.vd.com.vdream.network;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.BuildConfig;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

import kr.zeroweb.bill.api.ApiService;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiManager {
    private static ApiManager instance;
    private String BASE_URL = "http://vdream-api-network-lb-7a6f4eeb3ec87412.elb.ap-northeast-2.amazonaws.com:5000";
    private String debug_url = "http://ec2-13-209-75-36.ap-northeast-2.compute.amazonaws.com:5000";
    private OkHttpClient.Builder httpClient;
    public ApiService apiService;
    private String token = null;

    public ApiManager() {
        instance = this;
        init();
    }

    private void initCookies() {
        instance.httpClient = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(@NonNull Chain chain) throws IOException {
                Request original = chain.request();
                Log.i("ApiManager", original.url().toString());
                Request.Builder requestBuilder = original.newBuilder().url(original.url().toString());
                requestBuilder.addHeader("content-type", "application/json");

                if(token != null)
                    requestBuilder.addHeader("token", token);

                requestBuilder.addHeader("mac", getMacAddress());
                requestBuilder.addHeader("device", "ANDROID");
                return chain.proceed(requestBuilder.build());
            }
        });
    }

    private void init() {
        initCookies();

        String url = "";

        if(BuildConfig.DEBUG){
            url = BASE_URL;
        }else{
            url = debug_url;
        }

        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()))
                .client(httpClient.build())
                .build();
        apiService = retrofit.create(ApiService.class);
    }

    static public ApiManager getInstance(){
        if(instance == null){
            instance = new ApiManager();
        }

        return instance;
    }

    private String getMacAddress(){
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X",b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
            //handle exception
        }
        return "";
    }

    public void setToken(@NonNull String token){
        this.token = token;
        init();
    }

    public String getToken() {
        return token;
    }
}