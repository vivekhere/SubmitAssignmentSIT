package edu.sinhgad.submitassignmentsit;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class StudentActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    NavController navController;
    TextView userNameTextView;
    Toolbar studentToolbar;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference studentActivityDatabaseReference;
    ImageView studentProfilePicture;

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.support:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:support@submitassignmentsit.xyz"));
                startActivity(browserIntent);
                return true;

            case R.id.logout:
                firebaseAuth.signOut();
                startActivity(new Intent(StudentActivity.this, MainActivity.class));
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        navController = Navigation.findNavController(this, R.id.fragment);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        bottomNavigationView.setItemIconTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
        userNameTextView = findViewById(R.id.userNameTextView);
        studentToolbar = findViewById(R.id.studentToolbar);
        studentProfilePicture = findViewById(R.id.studentProfilePicture);

        setSupportActionBar(studentToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        studentActivityDatabaseReference = firebaseDatabase.getReference("Users");

        studentActivityDatabaseReference.child(firebaseAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("fullName").getValue().toString();
                userNameTextView.setText(name);
                try {
                    String imageUri = snapshot.child("profilePictureUrl").getValue().toString();
                    Picasso.with(StudentActivity.this).load(imageUri).into(studentProfilePicture);
                } catch (Exception ignored) {}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        studentProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StudentActivity.this, ProfilePage.class));
            }
        });

    }
}