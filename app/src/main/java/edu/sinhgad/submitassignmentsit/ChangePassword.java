package edu.sinhgad.submitassignmentsit;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

public class ChangePassword {

    Activity activity;

    public ChangePassword(Activity activity) {
        this.activity = activity;
    }

    public void sendPasswordResetEmail(String email) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Snackbar.make(activity.findViewById(android.R.id.content), "Email sent.", Snackbar.LENGTH_LONG).show();
                        } else {
                            Snackbar.make(activity.findViewById(android.R.id.content), "Email not sent.", Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
