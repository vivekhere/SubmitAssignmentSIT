package edu.sinhgad.submitassignmentsit;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class AlertRecyclerDialog {

    Activity activity;
    TextView yesTextView, noTextView;
    AlertDialog alertDialog;

    int position;

    AlertRecyclerDialog(Activity activity, int position) {
        this.activity = activity;
        this.position = position;
    }

    public void showAlert() {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.alert_dialog, null);

        yesTextView = view.findViewById(R.id.yesTextView);
        noTextView = view.findViewById(R.id.noTextView);

        yesTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewAssignments viewAssignments = new ViewAssignments();
                viewAssignments.removeAssignment(position);
                alertDialog.dismiss();
            }
        });

        noTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        alertDialog = new AlertDialog.Builder(activity)
                .setView(view)
                .create();
        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

}
