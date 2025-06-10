package com.example.newsapp; // IMPORTANT: Ensure this matches your actual package name

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

// MODIFIED: Class name changed to Developer
public class developer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer); // Layout file name remains activity_developer.xml

        // Get reference to the back arrow ImageView
        ImageView devProfileBackArrow = findViewById(R.id.devProfileBackArrow);
        // Get reference to the EXIT button
        Button exitButton = findViewById(R.id.exitButton);

        // Set OnClickListener for the back arrow
        devProfileBackArrow.setOnClickListener(v -> {
            finish(); // Closes the current activity and returns to the previous one
        });

        // Set OnClickListener for the EXIT button
        exitButton.setOnClickListener(v -> {
            Toast.makeText(developer.this, "Exiting Developer Profile...", Toast.LENGTH_SHORT).show();
            finish(); // Closes the current activity and returns to the previous one
        });
    }
}