package com.example.newsapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils; // Import for TextUtils.isEmpty
import android.util.Log;
import android.util.Patterns; // Import for Patterns.EMAIL_ADDRESS
import android.view.View;
import android.widget.Button;
import android.widget.EditText; // Import EditText for email and password fields
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newsapp.home;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull; // Required for @NonNull annotation
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener; // Required for OnCompleteListener
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult; // Required for AuthResult
import com.google.firebase.auth.FirebaseAuth; // Required for FirebaseAuth

public class login extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth; // Declare FirebaseAuth instance

    // Declare EditText fields for email and password
    private EditText emailEditText; // Corrected ID usage based on XML
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize UI elements
        emailEditText = findViewById(R.id.nameEditText); // This should be emailEditText based on your XML hint
        passwordEditText = findViewById(R.id.passwordEditText);

        // Configure Google Sign-In options
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Setup OnClickListener for the "Signup !" TextView
        TextView signupText = findViewById(R.id.signupText);
        signupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(login.this, signin.class);
                startActivity(intent);
            }
        });

        // Setup OnClickListener for the "Log in" Button (for email/password login)
        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the email/password login method
                loginUserWithEmailAndPassword();
            }
        });

        // Setup OnClickListener for the Google icon ImageView
        ImageView googleIcon = findViewById(R.id.google_icon);
        googleIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * Handles traditional email and password login using Firebase Authentication.
     */
    private void loginUserWithEmailAndPassword() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Input validation
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is required.");
            emailEditText.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Please enter a valid email.");
            emailEditText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required.");
            passwordEditText.requestFocus();
            return;
        }

        // Authenticate user with Firebase
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("login", "signInWithEmail:success");
                            Toast.makeText(login.this, "Login successful!", Toast.LENGTH_SHORT).show();

                            // Navigate to home screen
                            Intent intent = new Intent(login.this, home.class);
                            startActivity(intent);
                            finish(); // Finish login activity

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("login", "signInWithEmail:failure", task.getException());
                            Toast.makeText(login.this, "Authentication failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    /**
     * Initiates the Google Sign-In flow.
     * This method creates a sign-in intent and launches it using startActivityForResult.
     */
    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /**
     * Handles the result of the activity launched by startActivityForResult.
     * This is where the Google Sign-In result is processed.
     *
     * @param requestCode The integer request code originally supplied to startActivityForResult(),
     * allowing you to identify who this result came from.
     * @param resultCode The integer result code returned by the child activity through its setResult().
     * @param data An Intent, which can carry the result data back to the parent activity.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check if the result is from our Google Sign-In request
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task); // Process the sign-in result
        }
    }

    /**
     * Processes the Google Sign-In result.
     * If sign-in is successful, it navigates to the home screen.
     * If sign-in fails, it displays an error message.
     *
     * @param completedTask The Task containing the GoogleSignInAccount or an ApiException if sign-in failed.
     */
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            // Attempt to get the GoogleSignInAccount from the completed task.
            // This will throw an ApiException if sign-in was not successful.
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Google Sign-In was successful!
            Toast.makeText(this, "Google Sign-In Successful!", Toast.LENGTH_SHORT).show();

            // Navigate to your home screen (HomeActivity)
            Intent intent = new Intent(login.this, home.class);
            startActivity(intent);
            finish(); // Finish the login activity to prevent going back to it

        } catch (ApiException e) {
            // Google Sign-In failed. Log the error and display a user-friendly message.
            // The ApiException status code provides more details about the failure.
            Log.w("GoogleSignIn", "signInResult:failed code=" + e.getStatusCode(), e);
            Toast.makeText(this, "Google Sign-In failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}