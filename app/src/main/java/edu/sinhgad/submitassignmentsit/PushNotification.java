package edu.sinhgad.submitassignmentsit;


import android.app.Activity;
import android.widget.Toast;

import edu.sinhgad.submitassignmentsit.SendNotificationPack.APIService;
import edu.sinhgad.submitassignmentsit.SendNotificationPack.Client;
import edu.sinhgad.submitassignmentsit.SendNotificationPack.Data;
import edu.sinhgad.submitassignmentsit.SendNotificationPack.MyResponse;
import edu.sinhgad.submitassignmentsit.SendNotificationPack.NotificationSender;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PushNotification {

    Activity activity;

    PushNotification(Activity activity) {
        this.activity = activity;
    }

    public void sendNotification(String userToken, String title, String message) {
        Data data = new Data(title, message);
        NotificationSender notificationSender =  new NotificationSender(data, userToken);
        APIService apiService = Client.getClient("https://fcm.googleapis.com").create(APIService.class);
        apiService.sendNotification(notificationSender).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                if(response.code() == 200) {
                    if(response.body().success != 1) {
                        Toast.makeText(activity, "Notification not sent.", Toast.LENGTH_LONG);
                    }
                }
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {}
        });
    }

}
