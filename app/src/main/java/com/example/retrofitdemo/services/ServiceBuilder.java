package com.example.retrofitdemo.services;

import android.os.Build;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceBuilder {
    private static final String URL = "http://10.0.2.2:9000/";

    //create logger
   private static HttpLoggingInterceptor logging = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);

    //create OkHttp client
   private static OkHttpClient client = new OkHttpClient.Builder()
            //the default waiting time of retrofit is 10 sec. here we r extending it to wait to 15 sec
            .readTimeout(15, TimeUnit.SECONDS)
            //if we add the interceptor after the logging the new headers will still sent with the request,but they won't show up in our logs.
            //so with this code we are creating a new interceptor and attaching it to the OKHttp client.
            //the interceptor exposed an intercept() method and a "chain" parameter we can use to access the request pipeline. we then use the
            //request builder class to add additional headers(Headers that need to be added to every request ) to the request chain.
            //THE chain.proceed () method resumes the request pipeline,which allows our headers to be sent.
            .addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request request = chain.request();

                    request = request.newBuilder()
                            .addHeader("x-device-type", Build.DEVICE)
                            .addHeader("Accept-Language", Locale.getDefault().getLanguage())
                            .build();

                    return chain.proceed(request);
                }
            })

            .addInterceptor(logging)
            .build();


    private static Retrofit.Builder builder = new Retrofit.Builder().baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client.newBuilder().build());

    private static Retrofit retrofit = builder.build();

    //this is a method that acts as a helper for us to build services. Our builder provided with this
    //Retrofit object,which can be used to instantiate classes that implement the type of interface we pass it.
    //so in our case this method can create an implementation of our message service,which we can actually use to make our for example getMessage call.
    //This service also handles declaring items as static so they are reused through our app and there is no need for us to have multiple Retrofit builders.
    //with these two classes in place(MessageService.java and ServiceBuilder.java) we finally ready to start making  HTTP requests.
    public static <S> S buildService(Class<S> serviceType){
        return retrofit.create(serviceType);
    }
}
