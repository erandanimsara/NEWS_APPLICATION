package com.example.newsapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.EditorInfo; // For keyboard action
import android.widget.EditText; // Import for EditText
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Fnews extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fnews);

        ImageView newsImageView = findViewById(R.id.newsImageView);
        TextView newsTitleTextView = findViewById(R.id.newsTitleTextView);
        TextView newsDetailTextView = findViewById(R.id.newsDetailTextView); // Get reference to news detail TextView
        TextView newsTypeTitleTextView = findViewById(R.id.tabContentContainer); // Get reference to the TextView for the tab name


        // Get references to all interactive elements

        ImageView thumbUpIcon = findViewById(R.id.thumb_up_icon);
        ImageView thumbDownIcon = findViewById(R.id.thumb_down_icon);
        // MODIFIED: Get reference as EditText
        EditText addCommentEditText = findViewById(R.id.add_comment_bubble);


        // --- Debugging: Log if any view is not found ---

        if (thumbUpIcon == null) Log.e("Fnews", "ERROR: thumb_up_icon not found! Check XML ID.");
        if (thumbDownIcon == null) Log.e("Fnews", "ERROR: thumb_down_icon not found! Check XML ID.");
        if (addCommentEditText == null) Log.e("Fnews", "ERROR: add_comment_bubble (EditText) not found! Check XML ID.");
        if (newsDetailTextView == null) Log.e("Fnews", "ERROR: newsDetailTextView not found! Check XML ID.");
        if (newsTypeTitleTextView == null) Log.e("Fnews", "ERROR: newsTypeTitleTextView (tabContentContainer) not found! Check XML ID.");
        // --- End Debugging ---


        int imageResource = getIntent().getIntExtra("imageResource", 0);
        String titleText = getIntent().getStringExtra("titleText");
        String newsDetailText = getIntent().getStringExtra("newsDetailText");
        String newsCategory = getIntent().getStringExtra("newsCategory"); // Retrieve news category (tab name)

        Log.d("Fnews", "Received Image Resource: " + imageResource);
        Log.d("Fnews", "Received Title: " + titleText);
        Log.d("Fnews", "Received News Detail: " + newsDetailText);
        Log.d("Fnews", "Received News Category: " + newsCategory);


        if (newsImageView != null && imageResource != 0) {
            newsImageView.setImageResource(imageResource);
        } else {
            Log.w("Fnews", "Invalid imageResource or newsImageView is null, using placeholder or not setting image.");
        }

        if (newsTitleTextView != null && titleText != null) {
            newsTitleTextView.setText(titleText);
        } else {
            if (newsTitleTextView != null) newsTitleTextView.setText("No Title Available");
            Log.w("Fnews", "titleText is null or newsTitleTextView is null, displaying default text.");
        }

        // Set the news detail text
        if (newsDetailTextView != null && newsDetailText != null) {
            newsDetailTextView.setText(newsDetailText);
        } else {
            if (newsDetailTextView != null) newsDetailTextView.setText("No detailed news content available.");
            Log.w("Fnews", "newsDetailText is null or newsDetailTextView is null, displaying default text.");
        }

        // Set the news category (tab name)
        if (newsTypeTitleTextView != null && newsCategory != null && !newsCategory.isEmpty()) {
            newsTypeTitleTextView.setText(newsCategory.toUpperCase()); // Set the text to uppercase for consistency
        } else {
            if (newsTypeTitleTextView != null) newsTypeTitleTextView.setText("FOT NEWS"); // Fallback to original text
            Log.w("Fnews", "newsCategory is null/empty or newsTypeTitleTextView is null, displaying default text.");
        }


        // Set OnClickListener for the Like icon
        if (thumbUpIcon != null) {
            thumbUpIcon.setOnClickListener(v -> {
                Toast.makeText(Fnews.this, "You liked this news!", Toast.LENGTH_SHORT).show();
                // Add your actual like logic here
            });
        }

        // Set OnClickListener for the Dislike icon
        if (thumbDownIcon != null) {
            thumbDownIcon.setOnClickListener(v -> {
                Toast.makeText(Fnews.this, "You disliked this news!", Toast.LENGTH_SHORT).show();
                // Add your actual dislike logic here
            });
        }

        // For the comment EditText:
        // You can add an OnEditorActionListener to handle keyboard "Done" action
        if (addCommentEditText != null) {
            addCommentEditText.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String comment = addCommentEditText.getText().toString().trim();
                    if (!comment.isEmpty()) {
                        Toast.makeText(Fnews.this, "Comment entered: " + comment, Toast.LENGTH_LONG).show();
                        // TODO: Here you would typically send the comment to a server or save it.
                        // addCommentEditText.setText(""); // Clear the EditText after submission
                    } else {
                        Toast.makeText(Fnews.this, "Comment is empty.", Toast.LENGTH_SHORT).show();
                    }
                    return true; // Consume the event
                }
                return false; // Let the system handle other actions
            });
        }
    }
}
