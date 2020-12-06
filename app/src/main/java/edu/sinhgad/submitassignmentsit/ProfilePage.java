package edu.sinhgad.submitassignmentsit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfilePage extends AppCompatActivity {

    String password, fullName, email;
    boolean isEdit = true;
    ImageView profileImageView;
    EditText profileNameEditText;
    TextView profileNameTextView, profileEmailTextView;
    Button editButton, changePasswordButton;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ChangeProfilePicture changeProfilePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        profileImageView = findViewById(R.id.profileImageView);
        profileNameEditText = findViewById(R.id.profileNameEditText);
        profileNameTextView = findViewById(R.id.profileNameTextView);
        profileEmailTextView = findViewById(R.id.profileEmailTextView);
        changePasswordButton = findViewById(R.id.changePasswordButton);
        editButton = findViewById(R.id.editButton);
        editButton.setText("Edit");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");

        changeProfilePicture = new ChangeProfilePicture(ProfilePage.this, profileImageView);

        databaseReference.child(firebaseAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                fullName = snapshot.child("fullName").getValue().toString();
                email = snapshot.child("email").getValue().toString();
                password = snapshot.child("password").getValue().toString();
                profileNameEditText.setText(fullName);
                profileNameTextView.setText(fullName);
                profileEmailTextView.setText(email);
                try {
                    String imageUri = snapshot.child("profilePictureUrl").getValue().toString();
                    Picasso.with(ProfilePage.this).load(imageUri).into(profileImageView);
                } catch (Exception ignored) {}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isEdit) {
                    makeChanges();
                } else {
                    saveChanges();
                }
            }
        });

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangePassword changePassword = new ChangePassword(ProfilePage.this);
                changePassword.sendPasswordResetEmail(email);
            }
        });

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeProfilePicture.choosePicture();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        changeProfilePicture.activityResult(requestCode, resultCode, data);
    }

    private void makeChanges() {
        editButton.setText("Save");
        profileNameEditText.setEnabled(true);
        isEdit = false;
    }

    private void saveChanges() {
        String name = profileNameEditText.getText().toString().trim();
        if(TextUtils.isEmpty(name)) {
            Toast.makeText(ProfilePage.this, "Empty Field.", Toast.LENGTH_SHORT).show();
            return;
        }
        databaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("fullName").setValue(name);
        profileNameEditText.setEnabled(false);
        editButton.setText("Edit");
        isEdit = true;
    }
}