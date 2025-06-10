package com.example.newsapp; // IMPORTANT: Ensure this matches your actual package name

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler; // Import the Handler class
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class loading extends AppCompatActivity { // This is your Loading Screen Activity

    // Define a duration for the loading screen (e.g., 3 seconds)
    private static final int LOADING_SCREEN_DURATION = 3000; // milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Keep your EdgeToEdge setup
        setContentView(R.layout.activity_loading); // Your loading screen layout (activity_main2.xml)

        // Apply window insets, keeping your existing code
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Use a Handler to delay the transition to the login activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your login activity here
                Intent intent = new Intent(loading.this, swipupscreen.class); // Start the login activity
                startActivity(intent);
                finish(); // Close loading so the user can't go back to it
            }
        }, LOADING_SCREEN_DURATION); // The delay in milliseconds
    }
}
