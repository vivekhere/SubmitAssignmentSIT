package edu.sinhgad.submitassignmentsit;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class VerifyEmail extends AppCompatActivity {

    EditText verifyEmailEditText, verifyPasswordEditText, verifyConfirmPasswordEditText;
    Button verifyEmailButton;
    TextView verifyEmailMessageTextView;
    Toolbar verifyEmailToolbar;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    Dialog dialog;
    MessagePopUp messagePopUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);

        verifyEmailMessageTextView = findViewById(R.id.verifyEmailMessageTextView);
        verifyEmailEditText = findViewById(R.id.verifyEmailEditText);
        verifyPasswordEditText = findViewById(R.id.verifyPasswordEditText);
        verifyConfirmPasswordEditText = findViewById(R.id.verifyConfirmPasswordEditText);
        verifyEmailButton = findViewById(R.id.verifyEmailButton);
        verifyEmailToolbar = findViewById(R.id.verifyEmailToolbar);

        setSupportActionBar(verifyEmailToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        dialog = new Dialog(VerifyEmail.this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");

        messagePopUp = new MessagePopUp(VerifyEmail.this, verifyEmailMessageTextView);

        verifyEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = verifyEmailEditText.getText().toString().trim();
                final String password =  verifyPasswordEditText.getText().toString().trim();
                String confirmPassword = verifyConfirmPasswordEditText.getText().toString().trim();

                if(TextUtils.isEmpty(email)) {
                    messagePopUp.viewMessage("Email field cannot be empty.");
                    return;
                } else if(TextUtils.isEmpty(password)) {
                    messagePopUp.viewMessage("Password field cannot be empty.");
                    return;
                } else if(TextUtils.isEmpty(confirmPassword)) {
                    messagePopUp.viewMessage("Confirm Password field cannot be empty.");
                    return;
                } else if(password.length() < 8) {
                    messagePopUp.viewMessage("Password should at least be 8 character long.");
                    return;
                } else if(!password.equals(confirmPassword)) {
                    messagePopUp.viewMessage("Password and Confirm Password do not match.");
                    return;
                }

                dialog.startLoadingDialog();

                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(VerifyEmail.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    final FirebaseUser user = firebaseAuth.getCurrentUser();
                                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()) {
                                                EmailPassword emailPassword = new EmailPassword(email, password);
                                                databaseReference.child(firebaseAuth.getCurrentUser().getUid()).setValue(emailPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        Toast.makeText(VerifyEmail.this, "Account Created. Please verify your email.", Toast.LENGTH_LONG).show();
                                                        startActivity(new Intent(getApplicationContext(), RegistrationPage.class));
                                                    }
                                                });
                                            } else {
                                                Toast.makeText(VerifyEmail.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
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