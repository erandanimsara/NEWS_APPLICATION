package com.example.newsapp; // Your package name

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler; // Import the Handler class
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class splash extends AppCompatActivity {

    // Define the duration for the splash screen (e.g., 2 seconds)
    private static final int SPLASH_SCREEN_DURATION = 3000; // milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Keep your EdgeToEdge setup
        setContentView(R.layout.activity_splash); // Your splash screen layout (activity_main.xml)

        // Apply window insets, keeping your existing code
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Use a Handler to delay the transition to the next activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Create an Intent to start loading (your loading screen)
                Intent intent = new Intent(splash.this, loading.class);
                startActivity(intent); // Start loading
                finish(); // Close splash so the user can't go back to the splash screen
            }
        }, SPLASH_SCREEN_DURATION); // The delay in milliseconds
    }
}
