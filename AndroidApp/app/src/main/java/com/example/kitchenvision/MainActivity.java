package com.example.kitchenvision;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import android.view.Gravity;  // Make sure to import this
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.lang.ref.WeakReference;
import android.util.*;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_CODE = 101;
    private String currentPhotoPath;
    private ImageView imageView;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private List<Item> itemList;
    private BottomNavigationView bottomNavigationView;
    private OkHttpClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize RecyclerView and item list
        recyclerView = findViewById(R.id.recyclerView);
        itemList = new ArrayList<>();
        initializeRecipeItems();  // Load example recipes
        setupRecyclerView();

        // Initialize the BottomNavigationView and set listener for the pop-up menu
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_search) {
                // Handle home button click
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
                return true;
            } else if (item.getItemId() == R.id.nav_add) {
                // Handle the add button (to launch camera)
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            CAMERA_REQUEST_CODE);
                }
                return true;
            } else if (item.getItemId() == R.id.nav_inventory) {
                // Show pop-up menu
                showPopupMenu(bottomNavigationView);
                return true;
            } else {
                return false;
            }
        });

        // Set up the camera button to open camera when clicked
        ImageButton cameraButton = findViewById(R.id.camera_button);
        cameraButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        CAMERA_REQUEST_CODE);
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
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    // Handle the result from camera intent
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_CAPTURE_CODE && resultCode == RESULT_OK) {
            // Handle the image capture result here
            File imageFile = new File(currentPhotoPath); // Assuming you have a variable currentPhotoPath for the file path
            if (imageFile.exists()) {
                // Now that the picture is taken, make a network request
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
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Upload failed", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // Handle response (on background thread)
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    runOnUiThread(() -> {
                        // Show success message on UI thread
                        Toast.makeText(MainActivity.this, "Image uploaded successfully"+responseData, Toast.LENGTH_SHORT).show();
                    });
                } else {
                    runOnUiThread(() -> {
                        // Handle error response
                        Toast.makeText(MainActivity.this, "Upload failed: " + response.message(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Initialize the RecyclerView
    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerViewAdapter(itemList, this);
        recyclerView.setAdapter(adapter);
    }


    private void openAddRecipeActivity() {
        Intent intent = new Intent(MainActivity.this, AddRecipeActivity.class);
        startActivity(intent);
    }

    // Method to show the pop-up menu for the bottom navigation bar
    private void showPopupMenu(View anchorView) {
        PopupMenu popupMenu = new PopupMenu(this, anchorView);
        popupMenu.getMenuInflater().inflate(R.menu.activity_submenu, popupMenu.getMenu());

        // Set the gravity to end (right side)
        popupMenu.setGravity(Gravity.END);

        // Initialize OkHttpClient
        client = new OkHttpClient();

        popupMenu.setOnMenuItemClickListener(menuItem -> {
            if (menuItem.getItemId() == R.id.nav_child_recipe) {
                makeGetRequest();
                return true;
            } else if (menuItem.getItemId() == R.id.nav_child_food) {
                Toast.makeText(this, "Food Submenu clicked", Toast.LENGTH_SHORT).show();
                return true;
            } else {
                return false;
            }
        });
        popupMenu.show();
    }

    private void makeGetRequest() {
        // Define the URL to request
        String url = "http://10.0.0.142/getdata.php?data=1234";
        Log.d("KitchenVision", "start");

        // Build the request
        Request request = new Request.Builder()
                .url(url)
                .build();

        // Make asynchronous request
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("KitchenVision", "Request failed: " + e.getMessage());
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Network request failed", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e("KitchenVision", "Request failed with code: " + response.code());
                    return;
                }

                final String responseData = response.body().string();
                Log.d("KitchenVision", "Response received: " + responseData);

                // Always update the UI on the main thread
                runOnUiThread(() -> {
                    // Show the Toast message from the main thread
                    Toast.makeText(MainActivity.this, "Response: " + responseData, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }


    // Example of initializing the recipe items
    private void initializeRecipeItems() {
        // Example recipe items
        String shawarmaIngredients = "1 kg boneless chicken thighs\n3 tbsp plain yogurt\n...";
        String shawarmaInstructions = "1. Marinate the chicken with yogurt, garlic, oil, and spices...\n2. Cook until golden.";
        itemList.add(new Item(R.drawable.food_image, "Shawarma", "Delicious chicken shawarma", shawarmaIngredients, shawarmaInstructions));

        String braisedBeefIngredients = "1.5 kg beef chuck\n2 tbsp olive oil\n...";
        String braisedBeefInstructions = "1. Brown the beef...\n2. Simmer with veggies until tender.";
        itemList.add(new Item(R.drawable.food_image2, "Braised Beef", "Hearty braised beef", braisedBeefIngredients, braisedBeefInstructions));

        String lemonTartIngredients = "1 cup flour\n1/2 cup butter\n...";
        String lemonTartInstructions = "1. Prepare the tart shell...\n2. Fill with lemon curd and bake.";
        itemList.add(new Item(R.drawable.food_image3, "Lemon Tart", "Tart and sweet lemon dessert", lemonTartIngredients, lemonTartInstructions));
    }
}
