package com.example.newsapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.EditText;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.util.Log;
import android.util.Patterns;
import android.content.DialogInterface; // Import for AlertDialog.Builder (though we're using custom layout)

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnCompleteListener;
import androidx.annotation.NonNull;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;

public class profile extends AppCompatActivity {

    private TextView userNameTextView;
    private TextView userEmailTextView;
    private FirebaseAuth mAuth;
    private AlertDialog editProfileDialog; // Keep a reference to the dialog

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();

        LinearLayout editProfileRow = findViewById(R.id.editProfileRow);
        Switch modeSwitch = findViewById(R.id.modeSwitch);
        LinearLayout languageRow = findViewById(R.id.languageRow);
        LinearLayout aboutRow = findViewById(R.id.aboutRow);
        LinearLayout termsAndConditionsRow = findViewById(R.id.termsAndConditionsRow);
        LinearLayout privacyPolicyRow = findViewById(R.id.privacyPolicyRow);
        Button signOutButton = findViewById(R.id.signOutButton);
        ImageView backArrow = findViewById(R.id.backArrow);

        userNameTextView = findViewById(R.id.userName);
        userEmailTextView = findViewById(R.id.userEmail);

        loadUserProfile();

        editProfileRow.setOnClickListener(v -> showEditProfileDialog());

        modeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Toast.makeText(profile.this, "Dark Mode ON", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(profile.this, "Dark Mode OFF", Toast.LENGTH_SHORT).show();
            }
        });

        languageRow.setOnClickListener(v -> {
            Toast.makeText(profile.this, "Language settings clicked", Toast.LENGTH_SHORT).show();
        });

        aboutRow.setOnClickListener(v -> {
            Intent intent = new Intent(profile.this, developer.class);
            startActivity(intent);
        });

        termsAndConditionsRow.setOnClickListener(v -> {
            Toast.makeText(profile.this, "Terms and Conditions clicked", Toast.LENGTH_SHORT).show();
        });

        privacyPolicyRow.setOnClickListener(v -> {
            Toast.makeText(profile.this, "Privacy Policy clicked", Toast.LENGTH_SHORT).show();
        });

        // CHANGE HERE: Call the custom sign-out dialog method
        signOutButton.setOnClickListener(v -> {
            showCustomSignOutConfirmationDialog();
        });

        backArrow.setOnClickListener(v -> finish());
    }

    private void loadUserProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            userNameTextView.setText(user.getDisplayName() != null ? user.getDisplayName() : "No Name");
            userEmailTextView.setText(user.getEmail() != null ? user.getEmail() : "No Email");
        } else {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(profile.this, login.class);
            startActivity(intent);
            finish();
        }
    }

    private void showEditProfileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_profile, null);
        builder.setView(dialogView);

        EditText editUsername = dialogView.findViewById(R.id.edit_username);
        EditText editEmail = dialogView.findViewById(R.id.edit_email);
        Button buttonSave = dialogView.findViewById(R.id.button_save);
        Button buttonCancel = dialogView.findViewById(R.id.button_cancel_edit);

        editUsername.setText(userNameTextView.getText().toString());
        editEmail.setText(userEmailTextView.getText().toString());

        editProfileDialog = builder.create();

        if (editProfileDialog.getWindow() != null) {
            editProfileDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        buttonSave.setOnClickListener(v -> {
            String newUsername = editUsername.getText().toString().trim();
            String newEmail = editEmail.getText().toString().trim();

            if (newUsername.isEmpty()) {
                Toast.makeText(profile.this, "Username cannot be empty!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (newEmail.isEmpty()) {
                Toast.makeText(profile.this, "Email cannot be empty!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                Toast.makeText(profile.this, "Please enter a valid email address!", Toast.LENGTH_SHORT).show();
                return;
            }

            saveProfileUpdates(newUsername, newEmail);
        });

        buttonCancel.setOnClickListener(v -> {
            Toast.makeText(profile.this, "Edit cancelled.", Toast.LENGTH_SHORT).show();
            editProfileDialog.dismiss();
        });

        editProfileDialog.show();
    }

    private void saveProfileUpdates(String newUsername, String newEmail) {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            if (!newEmail.equals(user.getEmail())) {
                showReAuthDialog(newUsername, newEmail);
            } else {
                updateDisplayName(newUsername);
            }
        } else {
            Toast.makeText(this, "No user logged in to update profile.", Toast.LENGTH_SHORT).show();
            if (editProfileDialog != null) {
                editProfileDialog.dismiss();
            }
        }
    }

    private void showReAuthDialog(String newUsername, String newEmail) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_reauthenticate, null);
        builder.setView(dialogView);
        builder.setCancelable(false);

        EditText reAuthPassword = dialogView.findViewById(R.id.reauth_password);
        Button reAuthButton = dialogView.findViewById(R.id.button_reauthenticate);
        Button cancelButton = dialogView.findViewById(R.id.button_cancel_reauth);

        final AlertDialog reAuthDialog = builder.create();
        if (reAuthDialog.getWindow() != null) {
            reAuthDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        reAuthButton.setOnClickListener(v -> {
            String currentPassword = reAuthPassword.getText().toString().trim();
            if (currentPassword.isEmpty()) {
                reAuthPassword.setError("Password is required for re-authentication.");
                reAuthPassword.requestFocus();
                return;
            }
            reAuthenticateUser(newUsername, newEmail, currentPassword, reAuthDialog);
        });

        cancelButton.setOnClickListener(v -> {
            Toast.makeText(profile.this, "Email update cancelled (re-authentication required).", Toast.LENGTH_SHORT).show();
            reAuthDialog.dismiss();
            if (editProfileDialog != null) {
                editProfileDialog.dismiss();
            }
        });

        reAuthDialog.show();
    }

    private void reAuthenticateUser(String newUsername, String newEmail, String currentPassword, AlertDialog reAuthDialog) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && user.getEmail() != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);

            user.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("ReAuth", "User re-authenticated.");
                                reAuthDialog.dismiss();
                                updateEmailAndDisplayName(newUsername, newEmail);
                            } else {
                                Log.w("ReAuth", "Re-authentication failed.", task.getException());
                                String errorMessage = "Re-authentication failed.";
                                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                    errorMessage = "Invalid password. Please try again.";
                                } else if (task.getException() instanceof FirebaseTooManyRequestsException) {
                                    errorMessage = "Too many attempts. Please try again later.";
                                } else if (task.getException() != null) {
                                    errorMessage += ": " + task.getException().getMessage();
                                }
                                Toast.makeText(profile.this, errorMessage, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(this, "No user logged in for re-authentication.", Toast.LENGTH_SHORT).show();
            reAuthDialog.dismiss();
            if (editProfileDialog != null) {
                editProfileDialog.dismiss();
            }
        }
    }

    private void updateEmailAndDisplayName(String newUsername, String newEmail) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.updateEmail(newEmail)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> emailTask) {
                            if (emailTask.isSuccessful()) {
                                Log.d("ProfileUpdate", "User email address updated in Firebase.");
                                updateDisplayName(newUsername);
                            } else {
                                Log.w("ProfileUpdate", "Failed to update user email after re-authentication.", emailTask.getException());
                                String errorMessage = "Failed to update email.";
                                if (emailTask.getException() instanceof FirebaseAuthInvalidUserException) {
                                    errorMessage = "User not found or disabled. Please log in again.";
                                    performSignOut(); // Force sign out if user is invalid
                                } else if (emailTask.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                    errorMessage = "Invalid email or password provided.";
                                } else if (emailTask.getException() instanceof FirebaseTooManyRequestsException) {
                                    errorMessage = "Too many attempts. Please try again later.";
                                } else if (emailTask.getException() != null) {
                                    errorMessage += ": " + emailTask.getException().getMessage();
                                }
                                Toast.makeText(profile.this, errorMessage, Toast.LENGTH_LONG).show();
                                if (editProfileDialog != null) {
                                    editProfileDialog.dismiss();
                                }
                            }
                        }
                    });
        }
    }

    private void updateDisplayName(String newUsername) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(newUsername)
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> profileTask) {
                            if (profileTask.isSuccessful()) {
                                Log.d("ProfileUpdate", "User display name updated in Firebase.");
                                user.reload().addOnCompleteListener(reloadTask -> {
                                    if(reloadTask.isSuccessful()){
                                        loadUserProfile();
                                    } else {
                                        Log.w("ProfileUpdate", "Failed to reload user after display name update.", reloadTask.getException());
                                    }
                                });
                                Toast.makeText(profile.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                                if (editProfileDialog != null) {
                                    editProfileDialog.dismiss();
                                }
                            } else {
                                Log.w("ProfileUpdate", "Failed to update user display name.", profileTask.getException());
                                String errorMessage = "Failed to update profile name.";
                                if (profileTask.getException() != null) {
                                    errorMessage += ": " + profileTask.getException().getMessage();
                                }
                                Toast.makeText(profile.this, errorMessage, Toast.LENGTH_LONG).show();
                                if (editProfileDialog != null) {
                                    editProfileDialog.dismiss();
                                }
                            }
                        }
                    });
        }
    }

    // New method to show the custom sign-out confirmation dialog
    private void showCustomSignOutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_sign_out_confirmation, null); // Use your custom layout
        builder.setView(dialogView);

        // Get references to buttons in your custom dialog layout
        Button buttonYes = dialogView.findViewById(R.id.button_yes);
        Button buttonCancel = dialogView.findViewById(R.id.button_cancel);

        final AlertDialog customSignOutDialog = builder.create();

        // Optional: Make the dialog background transparent if you have a custom background set in XML
        if (customSignOutDialog.getWindow() != null) {
            customSignOutDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        buttonYes.setOnClickListener(v -> {
            // User clicked YES
            customSignOutDialog.dismiss(); // Dismiss the dialog
            performSignOut(); // Perform the actual sign out
        });

        buttonCancel.setOnClickListener(v -> {
            // User clicked CANCEL
            customSignOutDialog.dismiss(); // Dismiss the dialog
            Toast.makeText(profile.this, "Sign out cancelled.", Toast.LENGTH_SHORT).show();
        });

        customSignOutDialog.show();
    }

    // This method contains the actual sign-out logic
    private void performSignOut() {
        mAuth.signOut();
        Toast.makeText(this, "Signed out successfully.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(profile.this, login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}