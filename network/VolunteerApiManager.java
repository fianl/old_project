package vdream.vd.com.vdream.network;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

import kr.zeroweb.bill.api.ApiService;
import kr.zeroweb.bill.api.VolunteerApiService;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class VolunteerApiManager {
    private static VolunteerApiManager instance;
    private String BASE_URL = "http://openapi.1365.go.kr/openapi/service/rest/";
    private OkHttpClient.Builder httpClient;
    public VolunteerApiService apiService;
    private String token = null;

    public VolunteerApiManager() {
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
                return chain.proceed(requestBuilder.build());
            }
        });
    }

    private void init() {
        initCookies();

        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
        apiService = retrofit.create(VolunteerApiService.class);
    }

    static public VolunteerApiManager getInstance(){
        if(instance == null){
            instance = new VolunteerApiManager();
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