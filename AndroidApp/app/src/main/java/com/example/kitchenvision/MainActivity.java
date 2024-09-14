package com.example.kitchenvision;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.PopupMenu;

import androidx.activity.EdgeToEdge;
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

    private RecyclerView recyclerView;
    private List<Item> itemList;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Set up edge-to-edge display with insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set up RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Sample data for RecyclerView
        itemList = new ArrayList<>();
        itemList.add(new Item(R.drawable.food_image, "Recipe 1", "Delicious food"));
        itemList.add(new Item(R.drawable.food_image2, "Recipe 2", "Tasty meal"));
        itemList.add(new Item(R.drawable.food_image3, "Recipe 3", "Healthy dish"));

        // Set the RecyclerView adapter
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(itemList);
        recyclerView.setAdapter(adapter);

        // Find the cancel button
        Button cancelButton = findViewById(R.id.cancel_button);

        // Set a click listener to hide the keyboard
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
            }
        });

        // Initialize BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_navigation);  // Correct initialization

        // Set up the BottomNavigationView listener for item selections
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.navigation_inventory) {
                    // Always create and show the pop-up menu when Parent Item is clicked
                    showPopupMenu(findViewById(R.id.navigation_inventory));
                    return true;
                } else if (itemId == R.id.navigation_search) {
                    // Handle Search item click
                    return true;
                } else if (itemId == R.id.navigation_add) {
                    // Handle Add item click
                    return true;
                }
                return false;
            }
        });
    }

    // Method to show the pop-up menu for sub-items
    private void showPopupMenu(View anchorView) {
        // Always create a new PopupMenu instance
        PopupMenu popupMenu = new PopupMenu(this, anchorView);

        // Inflate the menu resource into the pop-up menu
        popupMenu.getMenuInflater().inflate(R.menu.activity_submenu, popupMenu.getMenu());

        // Handle submenu item clicks
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int itemId = menuItem.getItemId();

                if (itemId == R.id.navigationChild_recipe) {
                    // Handle Child Item 1 (Recipe) click
                    return true;
                } else if (itemId == R.id.navigationChild_food) {
                    // Handle Child Item 2 (Food) click
                    return true;
                } else {
                    return false;
                }
            }
        });

        // Show the pop-up menu
        popupMenu.show();
    }

    // Method to hide the keyboard
    private void hideKeyboard() {
        // Get the input method manager
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        // Check if the current focus view is not null
        View view = getCurrentFocus();
        if (view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
