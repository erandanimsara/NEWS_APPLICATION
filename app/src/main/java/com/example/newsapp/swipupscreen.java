package com.example.newsapp; // IMPORTANT: Ensure this matches your actual package name

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.content.Intent; // Import for Intent

/**
 * swipupscreen displays a welcome screen with a "Swipe up" button.
 * This activity handles the basic UI setup and a click listener for the button.
 */
public class swipupscreen extends AppCompatActivity { // Class name changed to match file name

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the content view to the XML layout defined in activity_swipupscreen.xml
        setContentView(R.layout.activity_swipupscreen);

        // Find the "Swipe up" button by its ID from the layout
        Button btnSwipeUp = findViewById(R.id.btnSwipeUp);

        // Set an OnClickListener to respond to button taps
        btnSwipeUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the LoginActivity
                Intent intent = new Intent(swipupscreen.this, login.class);
                startActivity(intent);
                // Optionally, finish this activity so the user cannot go back to it
                // finish();
            }
        });
    }
}
