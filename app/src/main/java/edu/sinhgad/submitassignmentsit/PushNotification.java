package edu.sinhgad.submitassignmentsit;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class PushNotification {

    NotificationChannel notificationChannel;
    NotificationManager notificationManager;
    NotificationCompat.Builder builder;
    NotificationManagerCompat notificationManagerCompat;
    Activity activity;
    String assignmentName;
    String studentName;

    public PushNotification(Activity activity, String assignmentName, String studentName) {
        this.activity = activity;
        this.assignmentName = assignmentName;
        this.studentName = studentName;
    }

    public void createNotification(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            notificationChannel = new NotificationChannel("MyNOTIFICATIONS", "MyNotifications", NotificationManager.IMPORTANCE_DEFAULT);

            notificationManager = activity.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);

        }

        builder = new NotificationCompat.Builder(activity,"MyNOTIFICATIONS")
                .setSmallIcon(R.drawable.ic_logo14)
                .setContentTitle(assignmentName)
                .setAutoCancel(true)
                .setContentText(studentName);

        notificationManagerCompat = NotificationManagerCompat.from(activity);
        notificationManagerCompat.notify(999,builder.build());
    }

}
