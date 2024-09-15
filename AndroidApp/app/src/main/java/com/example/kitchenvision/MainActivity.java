package com.example.kitchenvision;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.kitchenvision.Item;
import com.example.kitchenvision.R;

import android.widget.EditText;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_CODE = 101;
    private String currentPhotoPath;
    private static final String UPLOAD_URL = "http://10.0.0.142/upload";  // Change to your server URL

    // Class-level declarations for RecyclerView, BottomNavigationView, and Adapter
    private RecyclerView recyclerView;
    private List<Item> itemList;
    private BottomNavigationView bottomNavigationView;
    private RecyclerViewAdapter adapter;
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Set the layout for the activity

        // Initialize OkHttpClient (if necessary)
        client = new OkHttpClient();

        // Set up edge-to-edge display with insets for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize RecyclerView (example setup)
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize list of items (recipes) to be displayed in RecyclerView
        itemList = new ArrayList<>();
        initializeRecipeItems(); // This should now work

        // Initialize RecyclerView adapter and set it
        adapter = new RecyclerViewAdapter(itemList, this);
        recyclerView.setAdapter(adapter);

        // Set up the camera button to open the camera when clicked
        ImageButton cameraButton = findViewById(R.id.camera_button);
        cameraButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_REQUEST_CODE);
            }
        });

        // Initialize BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set the listener for BottomNavigationView item selection
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_search) {
                    // Focus on the search bar when the "Search" item is clicked
                    EditText searchEditText = findViewById(R.id.search_edit_text);
                    searchEditText.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT);
                    }
                    return true;
                } else if (id == R.id.navigation_add) {
                    // Launch the SearchActivity when the "Add" button is clicked
                    Intent searchIntent = new Intent(MainActivity.this, SearchActivity.class);
                    startActivity(searchIntent);  // This will start the SearchActivity
                    return true;
                } else if (id == R.id.navigation_inventory) {
                    // Show pop-up menu for the inventory item
                    showPopupMenu(findViewById(R.id.navigation_inventory));
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    // Method to open the camera
    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();  // Create the file to store the image
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.kitchenvision.fileprovider",  // Ensure this matches your manifest
                        photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(cameraIntent, CAMERA_CAPTURE_CODE);
            }
        }
    }

    // Method to create a file to store the image
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    // Handle the result from the camera intent
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_CAPTURE_CODE && resultCode == RESULT_OK) {
            File file = new File(currentPhotoPath);
            uploadFile(file);  // Implement the upload logic
        }
    }

// Other methods (initializeRecipeItems, showPopupMenu, etc.) will go here


    // Method to initialize recipe items
    private void initializeRecipeItems() {
        String shawarmaIngredients = "1 kg boneless chicken thighs\n3 tbsp plain yogurt\n4 cloves garlic, minced\n...";
        String shawarmaInstructions = "1. Marinate the chicken with yogurt, garlic, oil, spices, and lemon juice...";
        itemList.add(new Item(R.drawable.food_image, "Shawarma", "Delicious food", shawarmaIngredients, shawarmaInstructions));

        String braisedBeefIngredients = "1.5 kg beef chuck\n2 tbsp olive oil\n1 large onion, chopped\n...";
        String braisedBeefInstructions = "1. Season beef with salt and pepper. Brown it in a large pot with olive oil...";
        itemList.add(new Item(R.drawable.food_image2, "Braised Beef", "Tasty meal", braisedBeefIngredients, braisedBeefInstructions));

        String lemonTartIngredients = "1 1/4 cups all-purpose flour\n1/2 cup butter\n1/4 cup sugar\n...";
        String lemonTartInstructions = "1. Prepare the crust by mixing flour, butter, and sugar. Press into a tart pan...";
        itemList.add(new Item(R.drawable.food_image3, "Lemon Tart", "Healthy dish", lemonTartIngredients, lemonTartInstructions));
    }

    // Method to upload the captured image file
    private void uploadFile(File file) {
        if (file.exists()) {
            RequestBody fileBody = RequestBody.create(file, MediaType.parse("image/jpeg"));
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", file.getName(), fileBody)
                    .build();

            Request request = new Request.Builder()
                    .url(UPLOAD_URL)
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Upload failed", Toast.LENGTH_SHORT).show());
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, "File uploaded successfully", Toast.LENGTH_SHORT).show());
                    } else {
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, "Upload failed: " + response.message(), Toast.LENGTH_SHORT).show());
                    }
                }
            });
        }
    }

    // Method to show the pop-up menu for inventory or other items
    private void showPopupMenu(View anchorView) {
        // Create a new PopupMenu instance
        PopupMenu popupMenu = new PopupMenu(this, anchorView);

        // Inflate the menu resource into the pop-up menu
        popupMenu.getMenuInflater().inflate(R.menu.activity_submenu, popupMenu.getMenu());

        // Handle submenu item clicks
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            int itemId = menuItem.getItemId();

            if (itemId == R.id.navigationChild_recipe) {
                // Handle child item 1 (Recipe) click
                Toast.makeText(this, "Recipe selected", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.navigationChild_food) {
                // Handle child item 2 (Food) click
                Toast.makeText(this, "Food selected", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });

        // Show the pop-up menu
        popupMenu.show();
    }

    // Method to hide the keyboard
    private void hideKeyboard() {
        // Get the input method manager
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        // Check if there is a focused view and hide the keyboard
        View view = getCurrentFocus();
        if (view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
