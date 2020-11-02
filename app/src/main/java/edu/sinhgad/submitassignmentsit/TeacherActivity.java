package edu.sinhgad.submitassignmentsit;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
    Toolbar teacherToolbar;
    RecyclerView teacherRecyclerView;
    RecyclerAdapter recyclerAdapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<UploadAssignment> uploadAssignments;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    UploadAssignment uploadAssignment;
    Dialog dialog;
    String[] uploads, dates, times, uploaderNames;

    private void viewAllAssignments() {

        dialog.startLoadingDialog();

        databaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("Assignments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                uploadAssignments.clear();
                for(DataSnapshot postSnapshot : snapshot.getChildren()) {
                    uploadAssignment = postSnapshot.getValue(UploadAssignment.class);
                    uploadAssignments.add(uploadAssignment);
                }
                uploads = new String[uploadAssignments.size()];
                dates = new String[uploadAssignments.size()];
                times = new String[uploadAssignments.size()];
                uploaderNames = new String[uploadAssignments.size()];
                for(int i=0; i < uploads.length; i++) {
                    uploads[i] = uploadAssignments.get(i).getAssignmentName();
                    dates[i] = uploadAssignments.get(i).getDate();
                    times[i] = uploadAssignments.get(i).getTime();
                    uploaderNames[i] = uploadAssignments.get(i).getUploaderName();
                }
                recyclerAdapter = new RecyclerAdapter(getApplicationContext(), uploads, dates, times, uploaderNames);
                teacherRecyclerView.setLayoutManager(layoutManager);
                teacherRecyclerView.setAdapter(recyclerAdapter);

                recyclerAdapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        uploadAssignment = uploadAssignments.get(position);

                        Intent intent = new Intent();
                        intent.setDataAndType(Uri.parse(uploadAssignment.getAssignmentUrl()), "application/pdf");
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}

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
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.support:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:submitsitassignment@gmail.com"));
                startActivity(browserIntent);
                return true;

            case R.id.logout:
                firebaseAuth.signOut();
                startActivity(new Intent(TeacherActivity.this, MainActivity.class));
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

        teacherToolbar = findViewById(R.id.teacherToolbar);
        userNameTextView = findViewById(R.id.userNameTextView);
        teacherRecyclerView = findViewById(R.id.teacherRecyclerView);
        layoutManager = new LinearLayoutManager(this);
        uploadAssignments = new ArrayList<>();

        setSupportActionBar(teacherToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");

        dialog = new Dialog(TeacherActivity.this);

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

        dialog.dismissDialog();
    }
}