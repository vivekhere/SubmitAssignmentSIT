package edu.sinhgad.submitassignmentsit;

import android.app.Activity;
import android.app.ProgressDialog;

class Dialog {

    private Activity activity;
    ProgressDialog dialogProgressBar;

    Dialog(Activity myActivity) {
        activity = myActivity;
    }

    void startLoadingDialog() {
        dialogProgressBar = new ProgressDialog(activity);
        dialogProgressBar.show();
        dialogProgressBar.setContentView(R.layout.custom_login_dialog);
        dialogProgressBar.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialogProgressBar.setCancelable(false);
    }

    void dismissDialog() {
        dialogProgressBar.dismiss();
    }
}
