package com.example.retrofitdemo.services;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface MessageService {
    //when we call this method we should match it to an HTTP get request to this("messages") URL(we only defined the path below rather than the full url,
    //since the base URL with our domain will be defined in another class
    @GET("messages")
    Call<String> getMessages();

    //Retrofit will see the @Url annotation and understand that it needs to ignore the base URL in our serviceBuilder instead it will just use the entire value we pass in as
    //the  URL for this request
    @GET
    Call<String> getMessage(@Url String altUrl);
}
