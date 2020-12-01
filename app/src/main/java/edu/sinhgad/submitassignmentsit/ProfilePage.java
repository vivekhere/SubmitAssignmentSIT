package edu.sinhgad.submitassignmentsit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfilePage extends AppCompatActivity {

    String password;
    String fullName;
    boolean isEdit = true;
    ImageView profileImageView;
    EditText profileNameEditText, profileCurrentPasswordEditText, profileNewPasswordEditText;
    TextView profileNameTextView, profileEmailTextView;
    Button editButton;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        profileImageView = findViewById(R.id.profileImageView);
        profileNameEditText = findViewById(R.id.profileNameEditText);
        profileNameTextView = findViewById(R.id.profileNameTextView);
        profileEmailTextView = findViewById(R.id.profileEmailTextView);
        profileCurrentPasswordEditText = findViewById(R.id.profileCurrentPasswordEditText);
        profileNewPasswordEditText = findViewById(R.id.profileNewPasswordEditText);
        editButton = findViewById(R.id.editButton);
        editButton.setText("Edit");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");

        databaseReference.child(firebaseAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                fullName = snapshot.child("fullName").getValue().toString();
                String email = snapshot.child("email").getValue().toString();
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
    }

    private void makeChanges() {
        editButton.setText("Save");
        profileNameEditText.setEnabled(true);
        profileCurrentPasswordEditText.setEnabled(true);
        profileNewPasswordEditText.setEnabled(true);
        isEdit = false;
    }

    private void saveChanges() {
        String currentPassword = profileCurrentPasswordEditText.getText().toString().trim();
        String name = profileNameEditText.getText().toString().trim();
        String newPassword = profileNewPasswordEditText.getText().toString().trim();
        if(TextUtils.isEmpty(name) || TextUtils.isEmpty(currentPassword) || TextUtils.isEmpty(newPassword)) {
            Toast.makeText(ProfilePage.this, "Empty Field.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(password.equals(currentPassword)) {
            databaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("fullName").setValue(name);
            databaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("password").setValue(newPassword);
        } else {
            Toast.makeText(ProfilePage.this, "Wrong Password", Toast.LENGTH_SHORT).show();
            profileNameEditText.setText(fullName);
        }
        profileCurrentPasswordEditText.setText(null);
        profileNewPasswordEditText.setText(null);
        profileNameEditText.setEnabled(false);
        profileCurrentPasswordEditText.setEnabled(false);
        profileNewPasswordEditText.setEnabled(false);
        editButton.setText("Edit");
        isEdit = true;
    }
}