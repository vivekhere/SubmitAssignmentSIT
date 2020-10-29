package edu.sinhgad.submitassignmentsit;

import android.app.Activity;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

public class MessagePopUp {

    TextView messageTextView;
    private Activity activity;

    public MessagePopUp(Activity activity, TextView messageTextView) {
        this.activity = activity;
        this.messageTextView = messageTextView;
    }

    public void viewMessage(final String message) {
        messageTextView.setText(message);
        messageTextView.setVisibility(View.VISIBLE);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                messageTextView.setVisibility(View.INVISIBLE);
            }
        }, 2500);
    }

}
