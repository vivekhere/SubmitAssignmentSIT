package edu.sinhgad.submitassignmentsit.SendNotificationPack;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

import edu.sinhgad.submitassignmentsit.R;

public class MyFireBaseMessagingService extends FirebaseMessagingService {

    private final String CHANNEL_ID = "my_channel";

    String title, message;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        title = remoteMessage.getData().get("Title");
        message = remoteMessage.getData().get("Message");

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Random random = new Random();
        int notificationID = random.nextInt(30);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
             createNotificationChannel(notificationManager);
        }

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_logo14)
                        .setContentTitle(title)
                        .setContentText(message);
        notificationManager.notify(notificationID, builder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel(NotificationManager notificationManager) {
        String channelName = "channelName";
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("My channel Description");
        channel.enableLights(true);
        channel.setLightColor(Color.GREEN);
        notificationManager.createNotificationChannel(channel);
    }

}
