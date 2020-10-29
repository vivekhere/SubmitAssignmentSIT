package edu.sinhgad.submitassignmentsit;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginPage extends AppCompatActivity {

    EditText loginEmailEditText, loginPasswordEditText;
    TextView loginMessageTextView;
    Toolbar loginToolBar;
    Button loginButton;
    private FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference loginPageDatabaseReference;
    Dialog dialog;
    MessagePopUp messagePopUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        loginEmailEditText = findViewById(R.id.loginEmailEditText);
        loginPasswordEditText = findViewById(R.id.loginPasswordEditText);
        loginButton = findViewById(R.id.loginButton);
        loginMessageTextView = findViewById(R.id.loginMessageTextView);
        loginToolBar = findViewById(R.id.loginToolbar);

        setSupportActionBar(loginToolBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        loginPageDatabaseReference = firebaseDatabase.getReference("Users");

        messagePopUp = new MessagePopUp(LoginPage.this, loginMessageTextView);
        dialog = new Dialog(LoginPage.this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = loginEmailEditText.getText().toString().trim();
                String password = loginPasswordEditText.getText().toString().trim();

                if(TextUtils.isEmpty(email)) {
                    messagePopUp.viewMessage("Email field cannot be empty.");
                    return;
                } else if(TextUtils.isEmpty(password)) {
                    messagePopUp.viewMessage("Password field cannot be empty.");
                    return;
                }

                dialog.startLoadingDialog();

                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginPage.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    if(user.isEmailVerified()) {
                                        loginPageDatabaseReference.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                try {
                                                    if(snapshot.child("isTeacher").getValue().toString().equals("false")) {
                                                        startActivity(new Intent(getApplicationContext(), StudentActivity.class));
                                                        Toast.makeText(LoginPage.this, "Login Successful.", Toast.LENGTH_SHORT).show();
                                                    } else if(snapshot.child("isTeacher").getValue().toString().equals("true")) {
                                                        startActivity(new Intent(getApplicationContext(), TeacherActivity.class));
                                                        Toast.makeText(LoginPage.this, "Login Successful.", Toast.LENGTH_SHORT).show();
                                                    }
                                                } catch (Exception e) {
                                                    startActivity(new Intent(getApplicationContext(), RegistrationPage.class));
                                                }
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {}
                                        });
                                    } else {
                                        messagePopUp.viewMessage("Please verify your email.");
                                    }
                                } else {
                                    // If sign in fails, display a message to the user.
                                    messagePopUp.viewMessage("Authentication failed.");
                                }
                                dialog.dismissDialog();
                            }
                        });

            }
        });
    }
}