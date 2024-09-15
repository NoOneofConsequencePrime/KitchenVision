package com.example.kitchenvision;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 100;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private List<Item> itemList;
    private BottomNavigationView bottomNavigationView;

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
                Toast.makeText(MainActivity.this, "search clicked", Toast.LENGTH_SHORT).show();
                return true;
            } else if (item.getItemId() == R.id.nav_add) {
                // Handle the add button (to launch camera)
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
                    openAddRecipeActivity();
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
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

        // Set up camera button to open AddRecipeActivity when clicked
        ImageButton cameraButton = findViewById(R.id.camera_button);
        cameraButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                openAddRecipeActivity();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
            }
        });
    }

    // Initialize the RecyclerView
    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerViewAdapter(itemList, this);
        recyclerView.setAdapter(adapter);
    }

    // Method to open AddRecipeActivity
    private void openAddRecipeActivity() {
        Intent intent = new Intent(MainActivity.this, AddRecipeActivity.class);
        startActivity(intent);
    }

    // Handle the result of permission requests
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE && grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openAddRecipeActivity();
        } else {
            Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to show the pop-up menu for the bottom navigation bar
    private void showPopupMenu(View anchorView) {
        PopupMenu popupMenu = new PopupMenu(this, anchorView);
        popupMenu.getMenuInflater().inflate(R.menu.activity_submenu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(menuItem -> {
            if (menuItem.getItemId() == R.id.nav_child_recipe) {
                Toast.makeText(this, "Recipe Submenu clicked", Toast.LENGTH_SHORT).show();
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
