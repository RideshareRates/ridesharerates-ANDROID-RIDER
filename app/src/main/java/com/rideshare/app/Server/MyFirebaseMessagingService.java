package com.rideshare.app.Server;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.rideshare.app.R;
import com.rideshare.app.acitivities.HomeActivity;
import com.rideshare.app.Server.session.SessionManager;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.rideshare.app.connection.ApiClient;
import com.rideshare.app.connection.ApiNetworkCall;
import com.rideshare.app.custom.CheckConnection;
import com.rideshare.app.pojo.CheckDeviceTokenResponse;

import org.json.JSONObject;

import java.util.Map;

import cz.msebera.android.httpclient.Header;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by android on 18/4/17.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public static int NOTIFICATION_ID = 1;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Map<String, String> pushPayload = remoteMessage.getData();
        pushPayload.put("title",remoteMessage.getNotification().getTitle());
        pushPayload.put("msg",remoteMessage.getNotification().getBody());
        Log.d("Pradnya", "onMessge " + pushPayload);
        sendNotification(remoteMessage.getData());
        sendMessage();
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
//        sendRegistrationToServer(s);
        Log.e("TOKEN", s);
    }

    // to send notification
    private void sendNotification(Map<String, String> data) {

        int num = ++NOTIFICATION_ID;
        Bundle msg = new Bundle();
        for (String key : data.keySet()) {
            Log.e(key, data.get(key));
            msg.putString(key, data.get(key));
        }
        Intent intent = new Intent(this, HomeActivity.class);
        if (msg.containsKey("action")) {
            intent.putExtra("action", msg.getString("action"));
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, num /* Request code */, intent,
                PendingIntent.FLAG_IMMUTABLE);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, String.valueOf(NOTIFICATION_ID))
                .setSmallIcon(R.drawable.ridesharelogo)
                .setContentTitle(msg.getString("title"))
                .setContentText(msg.getString("msg"))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel =
                    new NotificationChannel(String.valueOf(NOTIFICATION_ID), "MyNotification", importance);
            notificationManager.createNotificationChannel(mChannel);
        }
        Log.d("actionValue", msg.getString("action"));

//        if (!msg.getString("action").isEmpty()) {
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//        }

        if (msg.getString("action").equalsIgnoreCase("Accepted")) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        if (msg.getString("action").equalsIgnoreCase("DRIVER_AT_PICKUP_LOCATION")) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        if (msg.getString("action").equalsIgnoreCase("start_ride")) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        if (msg.getString("action").equalsIgnoreCase("AT_DESTINATION")) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        if (msg.getString("action").equalsIgnoreCase("completed")) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        if (msg.getString("action").equalsIgnoreCase("DRIVER_AT_STOP")) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        if (msg.getString("action").equalsIgnoreCase("START_FROM_STOP")) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        if (msg.getString("action").equalsIgnoreCase("LOGOUTFROMADMIN")) {
            checkDeviceTokenApi();
        }


        notificationManager.notify(num, notificationBuilder.build());
    }

    // to send message
    private void sendMessage() {
        Log.d("sender", "Broadcasting message");
        Intent intent = new Intent("custom-event-name");
        // You can also include some extra data.
        intent.putExtra("message", "This is my message!");
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    public void checkDeviceTokenApi() {
        if (CheckConnection.haveNetworkConnection(getApplicationContext())) {

            ApiNetworkCall apiService = ApiClient.getApiService();

            Call<CheckDeviceTokenResponse> call =
                    apiService.checkDeviceToken("Bearer " + SessionManager.getKEY());

            call.enqueue(new Callback<CheckDeviceTokenResponse>() {
                @Override
                public void onResponse(Call<CheckDeviceTokenResponse> call, Response<CheckDeviceTokenResponse> response) {
                    CheckDeviceTokenResponse jsonResponse = response.body();
                    Log.d("API_RESPONSE", response.toString());

                    if (jsonResponse != null) {
                        Log.d("ParsedResponse", jsonResponse.toString());

                        if (Boolean.TRUE.equals(jsonResponse.getStatus())) {
                            String apiDeviceToken = String.valueOf(jsonResponse.getDevice_token());
                            if (!apiDeviceToken.equals(SessionManager.getDeviceId())) {
                                SessionManager.logoutUser(getApplicationContext());
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), jsonResponse.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Log.e("API_ERROR", "Response body is null");
                    }
                }

                @Override
                public void onFailure(Call<CheckDeviceTokenResponse> call, Throwable t) {
                    Log.e("API_ERROR", "Retrofit call failed: " + t.getMessage());
                }
            });

        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.network), Toast.LENGTH_LONG).show();
        }
    }


}
