package edu.sinhgad.submitassignmentsit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TeacherActivity extends AppCompatActivity {

    TextView userNameTextView;
    Button teacherLogoutButton;
    RecyclerView teacherRecyclerView;
    List<UploadAssignment> uploadAssignments;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    UploadAssignment uploadAssignment;

    private void viewAllAssignments() {

        databaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("Assignments").addValueEventListener(new ValueEventListener() {
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
                teacherRecyclerView.setAdapter(recyclerAdapter);
                teacherRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}

        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

        teacherLogoutButton = findViewById(R.id.teacherLogoutButton);
        userNameTextView = findViewById(R.id.userNameTextView);
        teacherRecyclerView = findViewById(R.id.teacherRecyclerView);
        uploadAssignments = new ArrayList<>();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");

        databaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("fullName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.getValue().toString();
                userNameTextView.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        viewAllAssignments();

//        recyclerAdapter.setOnItemClickListener(onItemClickListener);

//        teacherRecyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

        teacherLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                startActivity(new Intent(TeacherActivity.this, MainActivity.class));
                finish();
            }
        });

    }
}