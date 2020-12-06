package edu.sinhgad.submitassignmentsit;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import edu.sinhgad.submitassignmentsit.SendNotificationPack.APIService;
import edu.sinhgad.submitassignmentsit.SendNotificationPack.Client;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SubmitAssignment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SubmitAssignment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    Spinner studentSubjectsSpinner, teachersSpinner;
    EditText assignmentNameEditText;
    Button uploadButton;
    TextView userNameTextView, studentMessageTextView;
    ArrayAdapter<CharSequence> subjectsArrayAdapter;
    ArrayAdapter<String> teachersArrayAdapter;
    ValueEventListener valueEventListener;
    ArrayList<String> teachersArrayList;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference studentActivityDatabaseReference;
    private StorageReference storageReference;
    MessagePopUp messagePopUp;
    Dialog dialog;
    String uploaderName;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SubmitAssignment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SubmitAssignment.
     */
    // TODO: Rename and change types and number of parameters
    public static SubmitAssignment newInstance(String param1, String param2) {
        SubmitAssignment fragment = new SubmitAssignment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uploadAssignmentFile(data.getData());
            dialog.startLoadingDialog();
        }

    }

    private void uploadAssignmentFile(Uri data) {

        StorageReference reference = storageReference.child("Assignments/").child(studentSubjectsSpinner.getSelectedItem().toString()).child(teachersSpinner.getSelectedItem().toString() + "/" + assignmentNameEditText.getText().toString() + ".pdf");
        reference.putFile(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                while (!uri.isComplete()) ;
                Uri assignmentUrl = uri.getResult();

                String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                String currentTime = new SimpleDateFormat("h:mm a", Locale.getDefault()).format(new Date());
                final UploadAssignment uploadAssignment = new UploadAssignment(assignmentNameEditText.getText().toString(), assignmentUrl.toString(), currentDate, currentTime, uploaderName);
                studentActivityDatabaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("Assignments").child(studentActivityDatabaseReference.push().getKey()).setValue(uploadAssignment);
                studentActivityDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot item : snapshot.getChildren()) {
                            String fullName = item.child("fullName").getValue().toString();
                            if (fullName.equals(teachersSpinner.getSelectedItem().toString())) {
                                studentActivityDatabaseReference.child(item.getKey()).child("Assignments").child(studentActivityDatabaseReference.push().getKey()).setValue(uploadAssignment);
                                PushNotification pushNotification = new PushNotification(getActivity());
                                String token = item.child("token").child("token").getValue().toString();
                                pushNotification.sendNotification(token, assignmentNameEditText.getText().toString().trim(), fullName + " sent an assignment.");
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
                dialog.dismissDialog();
                messagePopUp.viewMessage("Assignment submitted.");
            }
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
        View view =  inflater.inflate(R.layout.fragment_submit_assignment, container, false);

        studentMessageTextView = view.findViewById(R.id.studentMessageTextView);
        userNameTextView = view.findViewById(R.id.userNameTextView);
        studentSubjectsSpinner = view.findViewById(R.id.studentSubjectsSpinner);
        teachersSpinner = view.findViewById(R.id.teachersSpinner);
        assignmentNameEditText = view.findViewById(R.id.assignmentNameEditText);
        uploadButton = view.findViewById(R.id.uploadButton);
        teachersArrayList = new ArrayList<>();

        subjectsArrayAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.subjectsSpinner, android.R.layout.simple_spinner_item);
        subjectsArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        studentSubjectsSpinner.setAdapter(subjectsArrayAdapter);

        teachersArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, teachersArrayList);
        teachersSpinner.setAdapter(teachersArrayAdapter);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        studentActivityDatabaseReference = firebaseDatabase.getReference("Users");
        storageReference = FirebaseStorage.getInstance().getReference();

        studentActivityDatabaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("fullName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                uploaderName = snapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

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

        messagePopUp = new MessagePopUp(getActivity(), studentMessageTextView);
        dialog = new Dialog(getActivity());

        uploadButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String assignmentName = assignmentNameEditText.getText().toString().trim();

                if(teachersSpinner.getSelectedItem() == null) {
                    messagePopUp.viewMessage("Teacher not selected.");
                    return;
                } else if(TextUtils.isEmpty(assignmentName)) {
                    messagePopUp.viewMessage("Assignment name cannot be empty.");
                    return;
                }

                selectAssignmentFile();
            }

        });

        return view;
    }
}