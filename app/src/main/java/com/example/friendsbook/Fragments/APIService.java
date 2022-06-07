package com.example.friendsbook.Fragments;

import com.example.friendsbook.Notifications.MyResponse;
import com.example.friendsbook.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAlnxPhV8:APA91bForiqKRwxWtZBVby3xuu7xyt_A9r6LIuLNfmqA2GxNV6HHv19YhDlWfqXXrbcL59BzZPEeNgDJDxlFtkAKlQreG0dLH71G1x0TwlkC2dvo2nobbJWz9gEhoWMPFSXv6rqjVL47\t\n"
            }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
