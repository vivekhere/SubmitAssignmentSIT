package edu.sinhgad.submitassignmentsit;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashScreen extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    ImageView splashImageView;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void activityStarter(final FirebaseUser user) {
        final ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SplashScreen.this, splashImageView, "sASLogoTrans");

        if(user != null  && user.isEmailVerified()) {
            databaseReference.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        if(snapshot.child("isTeacher").getValue().toString().equals("false")) {
                            startActivity(new Intent(SplashScreen.this, StudentActivity.class));
                        } else if(snapshot.child("isTeacher").getValue().toString().equals("true")) {
                            startActivity(new Intent(SplashScreen.this, TeacherActivity.class));
                        }
                    } catch (Exception e) {
                        startActivity(new Intent(SplashScreen.this, RegistrationPage.class));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        } else {
            startActivity(new Intent(SplashScreen.this, MainActivity.class), options.toBundle());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        splashImageView = findViewById(R.id.splashImageView);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
        final FirebaseUser user = firebaseAuth.getCurrentUser();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                activityStarter(user);
            }
        }, 3000);
    }
}