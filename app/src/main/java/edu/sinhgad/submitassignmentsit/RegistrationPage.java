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

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
        registrationMessageTextView = findViewById(R.id.registrationMessageTextView);

        arrayAdapter = ArrayAdapter.createFromResource(this, R.array.subjectsSpinner, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subjectsSpinner.setAdapter(arrayAdapter);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");

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
                } else if(teacherRadioButton.isChecked()) {
                    databaseReference.child(user.getUid()).child("fullName").setValue(fullName);
                    databaseReference.child(user.getUid()).child("isTeacher").setValue("true");
                    databaseReference.child(user.getUid()).child("subject").setValue(subject);
                    startActivity(new Intent(getApplicationContext(), LoginPage.class));
                }

            }

        });
    }
}