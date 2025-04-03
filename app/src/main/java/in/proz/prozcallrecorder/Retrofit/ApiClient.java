package in.proz.prozcallrecorder.Retrofit;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit retrofit;
    //"Content-Type: application/x-www-form-urlencoded"
    public static Retrofit getApiClient()
    {
        retrofit=null;
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.connectTimeout(5, TimeUnit.MINUTES);
        httpClient.readTimeout(5, TimeUnit.MINUTES);

        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();

                Request authenticatedRequest = request.newBuilder()
                        .header("Accept","application/json")
                        .header("Content-Type", "multipart/form-data")

                        .build();
                return chain.proceed(authenticatedRequest);
            }

        });
        CommonClass commonClass=new CommonClass();
        String BASE_URL = commonClass.LoginURL();
        Log.d("BaseURL"," url2 "+BASE_URL);
        return new Retrofit.Builder().baseUrl(BASE_URL)
                .client(httpClient.build()).addConverterFactory(GsonConverterFactory.create())
                .build();
    }
    public static Retrofit getTokenRetrofit(final String token){
        retrofit=null;
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.connectTimeout(5, TimeUnit.MINUTES);
        httpClient.readTimeout(5, TimeUnit.MINUTES);

        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();

                Request authenticatedRequest = request.newBuilder()
                        .header("Authorization", token)
                        .header("Accept","application/json")
                        .build();
                return chain.proceed(authenticatedRequest);
            }

        });
        CommonClass commonClass=new CommonClass();
        String BASE_URL = commonClass.commonURL();
        Log.d("BaseURL"," url2 "+BASE_URL);
        return new Retrofit.Builder().baseUrl(BASE_URL)
                .client(httpClient.build()).addConverterFactory(GsonConverterFactory.create())
                .build();
    }

}
