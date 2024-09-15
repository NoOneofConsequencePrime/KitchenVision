package com.example.kitchenvision;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import android.widget.ImageButton;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SearchActivity extends AppCompatActivity {
    private EditText searchField;  // Declare EditText
    private OkHttpClient client;  // Define OkHttpClient for network requests
    private String currentPhotoPath;
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);  // Layout for the search page
        Log.d("KitchenVision", "000");

        client = new OkHttpClient(); // Initialize OkHttpClient
        searchField = findViewById(R.id.search_recipe_page); // Initialize the search field

        // Set up a listener for when the user hits enter in the search field
        searchField.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the enter key is pressed
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    Toast.makeText(SearchActivity.this, "start", Toast.LENGTH_SHORT).show();
                    // Get the search query from the search field
                    String query = searchField.getText().toString();
                    makeGetRequest(query);  // Trigger the network request with the search query
                    return true;
                }
                return false;
            }
        });

        // camera button
        ImageButton cameraButton = findViewById(R.id.camera_search);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });

        // cancel search
        Button cancelButton = findViewById(R.id.cancel_search);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the current activity and return to MainActivity
                finish();
            }
        });
    }

    // Method to open camera
    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.kitchenvision.fileprovider", photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(cameraIntent, CAMERA_CAPTURE_CODE);
            }
        }
    }

    // Create a file to store the image
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        currentPhotoPath = image.getAbsolutePath();  // Ensure this is set correctly
        return image;
    }

    // Handle the result from camera intent
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_CAPTURE_CODE && resultCode == RESULT_OK) {
            File imageFile = new File(currentPhotoPath);
            if (imageFile.exists()) {
                // Now that the picture is taken, make a network request or show the image
                uploadImage(imageFile);
            } else {
                Toast.makeText(this, "Error: Image file not found", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadImage(File imageFile) {
        OkHttpClient client = new OkHttpClient();

        // Create request body to send the image file
        RequestBody fileBody = RequestBody.create(imageFile, MediaType.parse("image/jpeg"));
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", imageFile.getName(), fileBody)
                .build();

        // Create the request
        Request request = new Request.Builder()
                .url("http://10.0.0.142/upload.php") // Replace with your actual server URL
                .post(requestBody)
                .build();

        // Make the network request
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle failure (on background thread)
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(SearchActivity.this, "Upload failed", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // Handle response (on background thread)
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    runOnUiThread(() -> {
                        // Show success message on UI thread
                        Toast.makeText(SearchActivity.this, "Image uploaded successfully"+responseData, Toast.LENGTH_SHORT).show();
                    });
                } else {
                    runOnUiThread(() -> {
                        // Handle error response
                        Toast.makeText(SearchActivity.this, "Upload failed: " + response.message(), Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }


    // Handle permission results
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(SearchActivity.this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Method to make a network request
    private void makeGetRequest(String query) {
        // Define the URL to request
        String url = "http://10.0.0.142/getdata.php?data=1234";
        Log.d("KitchenVision", "your query is: "+query);

        // Build the request
        Request request = new Request.Builder()
                .url(url)
                .build();

        // Make asynchronous request using OkHttp
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle request failure (on background thread)
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(SearchActivity.this, "Network request failed", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Get the response as a string (on background thread)
                    final String responseData = response.body().string();

                    // Update UI on the main thread
                    runOnUiThread(() -> {
                        Toast.makeText(SearchActivity.this, "Response: " + responseData, Toast.LENGTH_SHORT).show();
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(SearchActivity.this, "Request failed", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }
}
