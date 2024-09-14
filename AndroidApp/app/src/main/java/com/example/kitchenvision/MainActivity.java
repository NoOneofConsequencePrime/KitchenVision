package com.example.kitchenvision;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.kitchenvision.Item;
import com.example.kitchenvision.R;
import android.widget.EditText;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_CODE = 101;

    // Class-level declarations for RecyclerView, BottomNavigationView, and Adapter
    private RecyclerView recyclerView;
    private List<Item> itemList;
    private BottomNavigationView bottomNavigationView;
    private RecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Set the layout for the activity

        // Set up edge-to-edge display with insets for system bars (status bar, navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize and set up RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize list of items (recipes) to be displayed in RecyclerView
        itemList = new ArrayList<>();

        String shawarmaIngredients = "1 kg boneless chicken thighs\n3 tbsp plain yogurt\n4 cloves garlic, minced\n2 tbsp olive oil\n1 tsp ground cumin\n1 tsp paprika\n1 tsp coriander\n1 tsp turmeric\n1/2 tsp cinnamon\n1/2 tsp black pepper\n1/2 tsp cardamom\n1/2 tsp chili powder\n1 tsp salt\njuice of 1 lemon";
        String shawarmaInstructions = "1. Marinate the chicken with yogurt, garlic, oil, spices, and lemon juice. Rest for 1 hour.\n\n2. Grill or cook in a pan until fully cooked. Slice thinly.\n\n3. Serve in pita with cucumbers, tomatoes, pickles, onions, and garlic sauce.";
        itemList.add(new Item(R.drawable.food_image, "Shawarma", "Delicious food", shawarmaIngredients, shawarmaInstructions));

        String braisedBeefIngredients = "1.5 kg beef chuck\n2 tbsp olive oil\n1 large onion, chopped\n4 cloves garlic, minced\n2 carrots, chopped\n2 celery stalks, chopped\n2 cups beef broth\n1 cup red wine\n2 tbsp tomato paste\n2 bay leaves\n1 tsp thyme\nsalt and pepper to taste";
        String braisedBeefInstructions = "1. Season beef with salt and pepper. Brown it in a large pot with olive oil.\n\n2. Remove beef and sauté onions, garlic, carrots, and celery.\n\n3. Add tomato paste, red wine, and beef broth.\n\n4. Return beef to the pot, add bay leaves and thyme, and simmer for 2-3 hours until tender.";
        itemList.add(new Item(R.drawable.food_image2, "Braised Beef", "Tasty meal", braisedBeefIngredients, braisedBeefInstructions));

        String lemonTartIngredients = "1 1/4 cups all-purpose flour\n1/2 cup butter\n1/4 cup sugar\n4 large eggs\n1 cup sugar\n1 tbsp lemon zest\n1/2 cup fresh lemon juice\n1/2 cup heavy cream\nPowdered sugar for garnish";
        String lemonTartInstructions = "1. Prepare the crust by mixing flour, butter, and sugar. Press into a tart pan and bake at 350°F (175°C) for 20 minutes.\n\n" +
                "2. Whisk eggs, sugar, lemon zest, juice, and cream. Pour into the baked crust.\n\n" +
                "3. Bake for 20-25 minutes until the filling is set. Let cool and dust with powdered sugar before serving.";
        itemList.add(new Item(R.drawable.food_image3, "Lemon Tart", "Healthy dish", lemonTartIngredients, lemonTartInstructions));


        // Initialize the RecyclerView adapter and set it to the RecyclerView
        adapter = new RecyclerViewAdapter(itemList, this);
        recyclerView.setAdapter(adapter);

        // Set up camera button to open camera when clicked
        ImageButton cameraButton = findViewById(R.id.camera_button);
        cameraButton.setOnClickListener(v -> {
            // Check for camera permission
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                // If permission is granted, open the camera
                openCamera();
            } else {
                // If permission is not granted, request permission
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
            }
        });

        // Find and set up the cancel button to hide the keyboard when clicked
        Button cancelButton = findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(v -> hideKeyboard()); // Call hideKeyboard() when clicked

        // Initialize BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set up the BottomNavigationView listener to handle item selections
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_search:
                        // Focus on the search bar when the "Search" item is clicked
                        EditText searchEditText = findViewById(R.id.search_edit_text);
                        searchEditText.requestFocus();  // Moves focus to the search bar
                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT);  // Shows the keyboard
                        }
                        return true;

                    case R.id.navigation_add:
                        // Open the camera for adding a new recipe
                        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                                == PackageManager.PERMISSION_GRANTED) {
                            openCamera();
                        } else {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
                        }
                        return true;

                    case R.id.navigation_inventory:
                        // Show pop-up menu for the inventory item
                        showPopupMenu(findViewById(R.id.navigation_inventory));
                        return true;

                    default:
                        return false;
                }
            }
        });

    }


    // Method to open the camera
    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, CAMERA_CAPTURE_CODE);
        } else {
            Toast.makeText(this, "No camera app found", Toast.LENGTH_SHORT).show();
        }
    }

    // Handle the result from the camera intent
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_CAPTURE_CODE && resultCode == RESULT_OK) {
            // Photo captured successfully, handle the data here (you can save or display it)
            Toast.makeText(this, "Photo captured successfully!", Toast.LENGTH_SHORT).show();
        }
    }

    // Handle camera permission request results
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

    // Method to show the pop-up menu for sub-items
    private void showPopupMenu(View anchorView) {
        // Create a new PopupMenu instance
        PopupMenu popupMenu = new PopupMenu(this, anchorView);

        // Inflate the menu resource into the pop-up menu
        popupMenu.getMenuInflater().inflate(R.menu.activity_submenu, popupMenu.getMenu());

        // Handle submenu item clicks
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            int itemId = menuItem.getItemId();

            if (itemId == R.id.navigationChild_recipe) {
                // Handle Child Item 1 (Recipe) click
                return true;
            } else if (itemId == R.id.navigationChild_food) {
                // Handle Child Item 2 (Food) click
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
