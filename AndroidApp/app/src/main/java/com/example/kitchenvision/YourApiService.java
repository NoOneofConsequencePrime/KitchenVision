package com.example.kitchenvision;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface YourApiService {

    @Multipart
    @POST("/upload")  // Replace with your actual API endpoint
    Call<ServerResponse> uploadImage(
            @Part MultipartBody.Part image
    );
}
