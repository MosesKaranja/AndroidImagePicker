package com.example.photopickerapp;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class ApiInterface {

    @FormUrlEncoded
    @POST("upload.php")
    Call<ImageClass> uploadImage(@Field("title") String title, @Field("image") String image) {
        return null;
    }

}
