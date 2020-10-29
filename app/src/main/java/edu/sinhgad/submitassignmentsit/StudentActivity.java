package edu.sinhgad.submitassignmentsit;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StudentActivity extends AppCompatActivity {

    Spinner studentSubjectsSpinner, teachersSpinner;
    EditText assignmentNameEditText;
    Button uploadButton, studentLogoutButton;
    ProgressBar simpleProgressBar;
    TextView progressTextView, userNameTextView;
    List<UploadAssignment> uploadAssignments;
    ArrayAdapter<CharSequence> subjectsArrayAdapter;
    ArrayAdapter<String> teachersArrayAdapter;
    ValueEventListener valueEventListener;
    ArrayList<String> teachersArrayList;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference studentActivityDatabaseReference;
    private StorageReference storageReference;
    UploadAssignment uploadAssignment;
    RecyclerView studentRecyclerView;

    private void viewAllAssignments() {

        studentActivityDatabaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("Assignments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                uploadAssignments.clear();
                for(DataSnapshot postSnapshot : snapshot.getChildren()) {
                    uploadAssignment = postSnapshot.getValue(UploadAssignment.class);
                    uploadAssignments.add(uploadAssignment);
                }
                String[] uploads = new String[uploadAssignments.size()];
                String[] dates = new String[uploadAssignments.size()];
                String[] times = new String[uploadAssignments.size()];
                for(int i=0; i < uploads.length; i++) {
                    uploads[i] = uploadAssignments.get(i).getAssignmentName();
                    dates[i] = uploadAssignments.get(i).getDate();
                    times[i] = uploadAssignments.get(i).getTime();
                }
                RecyclerAdapter recyclerAdapter = new RecyclerAdapter(getApplicationContext(), uploads, dates, times);
                studentRecyclerView.setAdapter(recyclerAdapter);
                studentRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}

        });

    }

    public void retrieveTeacherData(final String subject) {

        valueEventListener = studentActivityDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot item : snapshot.getChildren()) {
                    try {
                        if(item.child("isTeacher").getValue().toString().equals("true")) {
                            if(item.child("subject").getValue().toString().equals(subject)) {
                                teachersArrayList.add(item.child("fullName").getValue().toString());
                            }
                        }
                    } catch (Exception e) {}
                }
                teachersArrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void selectAssignmentFile() {

        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select PDF File"), 1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uploadAssignmentFile(data.getData());
        }

    }

    private void uploadAssignmentFile(Uri data) {

        StorageReference reference = storageReference.child("Assignments/").child(studentSubjectsSpinner.getSelectedItem().toString()).child(teachersSpinner.getSelectedItem().toString() + "/" + assignmentNameEditText.getText().toString() + ".pdf");
        reference.putFile(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                while(!uri.isComplete());
                Uri assignmentUrl = uri.getResult();

                String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                String currentTime = new SimpleDateFormat("h:mm a", Locale.getDefault()).format(new Date());
                final UploadAssignment uploadAssignment = new UploadAssignment(assignmentNameEditText.getText().toString(), assignmentUrl.toString(), currentDate, currentTime);
                studentActivityDatabaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("Assignments").child(studentActivityDatabaseReference.push().getKey()).setValue(uploadAssignment);
                studentActivityDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot item : snapshot.getChildren()) {
                            if(item.child("fullName").getValue().toString().equals(teachersSpinner.getSelectedItem().toString())) {
                                studentActivityDatabaseReference.child(item.getKey()).child("Assignments").child(studentActivityDatabaseReference.push().getKey()).setValue(uploadAssignment);
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                Toast.makeText(StudentActivity.this, "Assignment Uploaded.", Toast.LENGTH_SHORT).show();
                simpleProgressBar.setVisibility(View.INVISIBLE);
                progressTextView.setVisibility(View.INVISIBLE);
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                simpleProgressBar.setVisibility(View.VISIBLE);
                progressTextView.setVisibility(View.VISIBLE);
                double progress = (100 * snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                simpleProgressBar.setProgress((int) progress);
                progressTextView.setText("Uploading -- " + (int) progress + "% --");

            }
        });

    }

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
        setContentView(R.layout.activity_student);

        studentLogoutButton = findViewById(R.id.studentLogoutButton);
        userNameTextView = findViewById(R.id.userNameTextView);
        studentSubjectsSpinner = findViewById(R.id.studentSubjectsSpinner);
        teachersSpinner = findViewById(R.id.teachersSpinner);
        assignmentNameEditText = findViewById(R.id.assignmentNameEditText);
        uploadButton = findViewById(R.id.uploadButton);
        simpleProgressBar = findViewById(R.id.simpleProgressBar);
        progressTextView = findViewById(R.id.progressTextView);
        studentRecyclerView = findViewById(R.id.studentRecyclerView);
        teachersArrayList = new ArrayList<>();
        uploadAssignments = new ArrayList<>();

        subjectsArrayAdapter = ArrayAdapter.createFromResource(this, R.array.subjectsSpinner, android.R.layout.simple_spinner_item);
        subjectsArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        studentSubjectsSpinner.setAdapter(subjectsArrayAdapter);

        teachersArrayAdapter = new ArrayAdapter<String>(StudentActivity.this, android.R.layout.simple_spinner_dropdown_item, teachersArrayList);
        teachersSpinner.setAdapter(teachersArrayAdapter);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        studentActivityDatabaseReference = firebaseDatabase.getReference("Users");
        storageReference = FirebaseStorage.getInstance().getReference();

        studentActivityDatabaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("fullName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.getValue().toString();
                userNameTextView.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        viewAllAssignments();

//        studentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                uploadAssignment = uploadAssignments.get(i);
//
//                Intent intent = new Intent();
//                intent.setData(Uri.parse(uploadAssignment.getAssignmentUrl()));
//                startActivity(intent);
//            }
//
//        });

        studentSubjectsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String subject = studentSubjectsSpinner.getSelectedItem().toString();
                teachersArrayList.clear();
                retrieveTeacherData(subject);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String assignmentName = assignmentNameEditText.getText().toString().trim();

                if(teachersSpinner.getSelectedItem() == null) {
                    Toast.makeText(StudentActivity.this, "Teacher not selected.", Toast.LENGTH_SHORT).show();
                    return;
                } else if(TextUtils.isEmpty(assignmentName)) {
                    Toast.makeText(StudentActivity.this, "Assignment name cannot be empty.", Toast.LENGTH_SHORT).show();
                    return;
                }

                selectAssignmentFile();
            }

        });

        studentLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                startActivity(new Intent(StudentActivity.this, MainActivity.class));
                finish();
            }
        });

    }
}