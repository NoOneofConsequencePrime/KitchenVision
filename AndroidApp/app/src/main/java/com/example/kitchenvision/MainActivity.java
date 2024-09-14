package com.example.kitchenvision;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

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
        itemList.add(new Item(R.drawable.food_image, "Recipe 1", "Delicious food"));
        itemList.add(new Item(R.drawable.food_image2, "Recipe 2", "Tasty meal"));
        itemList.add(new Item(R.drawable.food_image3, "Recipe 3", "Healthy dish"));

        // Initialize the RecyclerView adapter and set it to the RecyclerView
        adapter = new RecyclerViewAdapter(itemList, this);
        recyclerView.setAdapter(adapter);

        // Find and set up the cancel button to hide the keyboard when clicked
        Button cancelButton = findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(v -> hideKeyboard()); // Call hideKeyboard() when clicked

        // Initialize BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set up the BottomNavigationView listener to handle item selections
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_inventory) {
                // Show pop-up menu for the inventory item
                showPopupMenu(findViewById(R.id.navigation_inventory));
                return true;
            } else if (itemId == R.id.navigation_search) {
                // Handle Search item click (add action if needed)
                return true;
            } else if (itemId == R.id.navigation_add) {
                // Handle Add item click (add action if needed)
                return true;
            }
            return false;
        });
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
