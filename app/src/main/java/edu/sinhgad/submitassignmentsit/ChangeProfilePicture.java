package edu.sinhgad.submitassignmentsit;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class ChangeProfilePicture {

    ImageView profilePictureImageView;
    Activity activity;
    public Uri imageUri;

    ChangeProfilePicture(Activity activity, ImageView profilePictureImageView) {
        this.activity = activity;
        this.profilePictureImageView = profilePictureImageView;
    }

    public void choosePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(intent, 1);
    }

    public void activityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            profilePictureImageView.setImageURI(imageUri);
            uploadPicture();
        }
    }

    public void uploadPicture() {
        Dialog dialog = new Dialog(activity);
        dialog.startLoadingDialog();
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        FirebaseStorage.getInstance().getReference().child("ProfilePictures/").child(currentDate + currentTime).putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                        while(!uri.isComplete());
                        Uri imageUrl = uri.getResult();
                        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("profilePictureUrl").setValue(imageUrl.toString());
                        dialog.dismissDialog();
                        Snackbar.make(activity.findViewById(android.R.id.content), "Profile picture set successfully.", Snackbar.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        dialog.dismissDialog();
                        Toast.makeText(activity, "Failed to set the profile picture.", Toast.LENGTH_LONG).show();
                    }
                });
    }

}
