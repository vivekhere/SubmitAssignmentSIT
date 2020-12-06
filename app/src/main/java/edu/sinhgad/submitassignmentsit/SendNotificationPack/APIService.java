package edu.sinhgad.submitassignmentsit.SendNotificationPack;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type: application/json",
                    "Authorization: key = AAAAY7-xdH4:APA91bGvfEj27-ab7s3tn7ljW3rmUs9jkXEkH3jJ0dpWmSOEEodkmP74MXivWArUpQuCp8OaRoZL3GCU8BIi-ZQKm4UlTeHcs2AF4WSF6AZyRIJJrL4dQ6wTTVgRCQlmC1NH66oev5Ay"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body NotificationSender body);
}

