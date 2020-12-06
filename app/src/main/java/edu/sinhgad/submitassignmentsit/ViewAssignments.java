package edu.sinhgad.submitassignmentsit;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ViewAssignments#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewAssignments extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    RecyclerView studentRecyclerView;
    RecyclerAdapter recyclerAdapter;
    RecyclerView.LayoutManager layoutManager;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference studentActivityDatabaseReference;
    UploadAssignment uploadAssignment;
    ArrayList<UploadAssignment> uploadAssignments;
    String[] uploads, dates, times, uploaderNames;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ViewAssignments() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ViewAssignments.
     */
    // TODO: Rename and change types and number of parameters
    public static ViewAssignments newInstance(String param1, String param2) {
        ViewAssignments fragment = new ViewAssignments();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public void removeAssignment(final int position) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference.child("Assignments").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i=0;
               for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                   if(i == position) {
                       dataSnapshot.getRef().removeValue();
                       break;
                   }
                   i++;
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    public void viewAllAssignments() {
        studentActivityDatabaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("Assignments").addValueEventListener(new ValueEventListener() {
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

                recyclerAdapter = new RecyclerAdapter(getActivity(), getActivity(), uploads, dates, times, uploaderNames, true);
                studentRecyclerView.setLayoutManager(layoutManager);
                studentRecyclerView.setAdapter(recyclerAdapter);

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_view_assignments, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        studentActivityDatabaseReference = firebaseDatabase.getReference("Users");

        uploadAssignments = new ArrayList<>();
        studentRecyclerView = view.findViewById(R.id.studentRecyclerView);
        studentRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());

        viewAllAssignments();

        return view;
    }

}