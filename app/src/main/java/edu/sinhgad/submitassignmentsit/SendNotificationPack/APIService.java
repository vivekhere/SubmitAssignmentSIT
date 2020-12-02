package edu.sinhgad.submitassignmentsit.SendNotificationPack;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type: application/json",
                    "Authorization: key = AAAAYXVA5tU:APA91bFF55RIqNARpz-cyjuFIEJNs8pIUhBIrxYxhRjdb3bqiTWSWjf2e_8krx-dLelJtgXaW8JtWJesdxWUMlj46pJaAI1FXAVj47EM1hSikD-oql4RAgojQqniwCzn1Ag0BzbLx3GB"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body NotificationSender body);
}

