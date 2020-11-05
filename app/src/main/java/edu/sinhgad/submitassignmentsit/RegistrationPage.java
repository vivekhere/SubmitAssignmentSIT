package edu.sinhgad.submitassignmentsit;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegistrationPage extends AppCompatActivity {

    EditText registerFullNameEditText;
    RadioButton studentRadioButton, teacherRadioButton;
    TextView chooseSubjectTextView, registrationMessageTextView;
    Spinner subjectsSpinner;
    Button registerButton;
    ArrayAdapter<CharSequence> arrayAdapter;
    private FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    MessagePopUp messagePopUp;
    Toolbar registerToolbar;
    String email;

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    private void sendEmail() {
        SendMail sendMail = new SendMail(this, email, registerFullNameEditText.getText().toString());
        sendMail.execute();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_page);

        registerFullNameEditText = findViewById(R.id.registerFullNameEditText);
        chooseSubjectTextView = findViewById(R.id.chooseSubjectTextView);
        studentRadioButton = findViewById(R.id.studentRadioButton);
        teacherRadioButton = findViewById(R.id.teacherRadioButton);
        subjectsSpinner = findViewById(R.id.subjectsSpinner);
        registerButton = findViewById(R.id.registerButton);
        registrationMessageTextView = findViewById(R.id.registerMessageTextView);
        registerToolbar = findViewById(R.id.registerToolbar);

        setSupportActionBar(registerToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        arrayAdapter = ArrayAdapter.createFromResource(this, R.array.subjectsSpinner, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subjectsSpinner.setAdapter(arrayAdapter);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");

        databaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("email").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                email = snapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        teacherRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseSubjectTextView.setVisibility(View.VISIBLE);
                subjectsSpinner.setVisibility(View.VISIBLE);
            }
        });

        studentRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseSubjectTextView.setVisibility(View.INVISIBLE);
                subjectsSpinner.setVisibility(View.INVISIBLE);
            }

        });

        messagePopUp = new MessagePopUp(RegistrationPage.this, registrationMessageTextView);

        registerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final String fullName = registerFullNameEditText.getText().toString().trim();
                final String subject = subjectsSpinner.getSelectedItem().toString();

                if(TextUtils.isEmpty(fullName)) {
                    messagePopUp.viewMessage("Full Name field cannot be empty.");
                    return;
                } else if(!studentRadioButton.isChecked() && !teacherRadioButton.isChecked()) {
                    messagePopUp.viewMessage("Please specify your Occupation.");
                    return;
                }

                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(studentRadioButton.isChecked()) {
                    databaseReference.child(user.getUid()).child("fullName").setValue(fullName);
                    databaseReference.child(user.getUid()).child("isTeacher").setValue("false");
                    startActivity(new Intent(getApplicationContext(), LoginPage.class));
                    sendEmail();
                } else if(teacherRadioButton.isChecked()) {
                    databaseReference.child(user.getUid()).child("fullName").setValue(fullName);
                    databaseReference.child(user.getUid()).child("isTeacher").setValue("true");
                    databaseReference.child(user.getUid()).child("subject").setValue(subject);
                    startActivity(new Intent(getApplicationContext(), LoginPage.class));
                    sendEmail();
                }

            }

        });
    }
}