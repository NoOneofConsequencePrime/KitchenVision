package com.example.kitchenvision;

import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.content.Intent;
import androidx.annotation.Nullable;
import android.content.Intent;
import android.provider.MediaStore;
import android.widget.Toast;
import android.app.AlertDialog;
import java.io.ByteArrayOutputStream;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class AddRecipeActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 101;
    private ImageView capturedImageView;
    private Bitmap capturedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        capturedImageView = findViewById(R.id.capturedImage);

        // Open the camera when the activity starts
        openCamera();
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            capturedImage = (Bitmap) data.getExtras().get("data");
            capturedImageView.setImageBitmap(capturedImage);

            // Show dialog to ask if the user wants to retake or proceed
            showRetakeOrProceedDialog();
        }
    }

    private void showRetakeOrProceedDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Picture Taken")
                .setMessage("Do you want to retake the picture?")
                .setPositiveButton("Retake", (dialog, which) -> {
                    // User chose to retake the picture
                    openCamera();
                })
                .setNegativeButton("Proceed", (dialog, which) -> {
                    // User chose to proceed, send picture to the server
                    sendPictureToServer();
                })
                .show();
    }

    private void sendPictureToServer() {
        // Convert the bitmap to a byte array
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        capturedImage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();

        // TODO: Implement your server API request logic here
        // For example, using Retrofit or OkHttp to upload the imageBytes to your server.
        uploadImageToServer(imageBytes);
    }

    private void uploadImageToServer(byte[] imageBytes) {
        // Example using Retrofit (ensure you have the necessary libraries for Retrofit)
        // You can adjust this code according to your server's API.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://your-server.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Define your API interface
        YourApiService apiService = retrofit.create(YourApiService.class);

        // Prepare the image as part of a request body
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imageBytes);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", "image.jpg", requestFile);

        // Call the upload API
        Call<ServerResponse> call = apiService.uploadImage(body);
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddRecipeActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AddRecipeActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Toast.makeText(AddRecipeActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
