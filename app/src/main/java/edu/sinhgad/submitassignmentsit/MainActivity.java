package edu.sinhgad.submitassignmentsit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView submitTextView, assignmentTextView, sITTextView;
    Button registrationPageButton, loginPageButton;
    ImageView logoImageView;

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        submitTextView = findViewById(R.id.submitTextView);
        logoImageView = findViewById(R.id.logoImageView);
        assignmentTextView = findViewById(R.id.assignmentTextView);
        sITTextView = findViewById(R.id.sITTextView);
        registrationPageButton = findViewById(R.id.registrationPageButton);
        loginPageButton = findViewById(R.id.loginPageButton);

        submitTextView.setX(2000);
        assignmentTextView.setX(3000);
        sITTextView.setX(4000);
        logoImageView.setAlpha((float) 0.0);

        submitTextView.animate().translationXBy(-2000).setDuration(500);
        assignmentTextView.animate().translationXBy(-3000).setDuration(1000);
        sITTextView.animate().translationXBy(-4000).setDuration(1500);
        logoImageView.animate().alpha((float) 1.0).setDuration(2000);

        registrationPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, VerifyEmail.class));
            }
        });

        loginPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, LoginPage.class));
            }
        });

    }
}