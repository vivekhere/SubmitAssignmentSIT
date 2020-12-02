package edu.sinhgad.submitassignmentsit;

import android.content.Intent;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import edu.sinhgad.submitassignmentsit.SendNotificationPack.Token;

public class TeacherActivity extends AppCompatActivity {

    TextView userNameTextView;
    ImageView teacherProfilePicture;
    Toolbar teacherToolbar;
    RecyclerView teacherRecyclerView;
    RecyclerAdapter recyclerAdapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<UploadAssignment> uploadAssignments;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    UploadAssignment uploadAssignment;
    String[] uploads, dates, times, uploaderNames;

    private void viewAllAssignments() {

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
                recyclerAdapter = new RecyclerAdapter(TeacherActivity.this, getApplicationContext(), uploads, dates, times, uploaderNames);
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
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:support@submitassignmentsit.xyz"));
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

    private void updateToken() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Token token = new Token(refreshedToken);
        databaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("token").setValue(token);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

        teacherToolbar = findViewById(R.id.teacherToolbar);
        teacherProfilePicture = findViewById(R.id.teacherProfilePicture);
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

        databaseReference.child(firebaseAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("fullName").getValue().toString();
                userNameTextView.setText(name);
                try {
                    String imageUri = snapshot.child("profilePictureUrl").getValue().toString();
                    Picasso.with(TeacherActivity.this).load(imageUri).into(teacherProfilePicture);
                } catch (Exception ignored) {}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        viewAllAssignments();

        teacherProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TeacherActivity.this, ProfilePage.class));
            }
        });

        updateToken();
    }
}