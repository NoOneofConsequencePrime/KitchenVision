package com.example.kitchenvision;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.File;
import java.io.IOException;

public class FileUpload {

    // Define the URL and media type
    private static final String URL = "http://10.0.0.142/upload.py";  // Update this to your server's endpoint
    private static final MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpeg");  // Change this to match your file type

    public static void main(String[] args) {
        // Path to the file you want to upload
        File file = new File("path/to/your/image.jpg");  // Replace with your file's path

        // Create OkHttpClient instance
        OkHttpClient client = new OkHttpClient();

        // Create the RequestBody with file and media type
        RequestBody fileBody = RequestBody.create(file, MEDIA_TYPE_JPG);

        // Build the multipart request body for the file upload
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), fileBody)  // Replace "file" with your expected form field name
                .build();

        // Create the POST request
        Request request = new Request.Builder()
                .url(URL)
                .post(requestBody)
                .build();

        // Send the request asynchronously
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                System.out.println("File upload failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Success: handle the response
                    System.out.println("File uploaded successfully: " + response.body().string());
                } else {
                    // Failure: handle the failure case
                    System.out.println("File upload failed: " + response.code() + " " + response.body().string());
                }
            }
        });
    }
}
