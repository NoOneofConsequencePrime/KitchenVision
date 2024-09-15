package com.example.kitchenvision;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchActivity extends AppCompatActivity {
    private EditText searchField;  // Declare EditText
    private OkHttpClient client;  // Define OkHttpClient for network requests

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
