package com.example.kitchenvision;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Item> itemList;

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
