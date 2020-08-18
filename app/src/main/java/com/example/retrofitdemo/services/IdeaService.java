package com.example.retrofitdemo.services;

import com.example.retrofitdemo.models.Idea;
import java.util.HashMap;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface IdeaService {

    @GET("ideas")
    Call<List<Idea>> getIdeas();
//    Call<List<Idea>> getIdeas(@Query("owner")String owner);
//    Call<List<Idea>> getIdeas(@QueryMap HashMap<String,String> filters);


    @GET("ideas/{id}")
    Call<Idea> getIdea(@Path("id")int id);

    //creating new idea
    //@Body annotation tells retrofit to serialize the object we pass in as JSON in the request body.
    @POST("ideas")
    Call<Idea> createIdea(@Body Idea newIdea);

    //we r declaring the @PUT annotation at top of our method,which we will use to signal an update operation to our server.The web service will fined an
    //existing item using the id we supply and then update the rest of the properties accordingly.Those properties are passed into our method using multiple
    //parameters.So we are passing in each property of the object one at a time with the @Field annotation rather than as an entire object.
    //@Field annotations allow us to easily send data in a FormUrlEncoded format instead of JSON,which can be useful in some scenarios.This format is also easy to use
    //if uou only have a few data points to send or if those data points are too unrelated to easily combine into a strong typed model.
    @FormUrlEncoded
    @PUT("ideas/{id}")
    Call<Idea> updateIdea(
            @Path("id")int id,
            @Field("name")String name,
            @Field("description")String desc,
            @Field("status")String status,
            @Field("owner")String owner
    );

    @DELETE("ideas/{id}")
    Call<Void> deleteIdea(@Path("id") int id);
}
