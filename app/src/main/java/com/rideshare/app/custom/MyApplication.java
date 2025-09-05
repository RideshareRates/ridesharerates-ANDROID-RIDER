package com.rideshare.app.custom;

import static com.rideshare.app.Server.Server.BASE_URL;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.multidex.MultiDexApplication;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.rideshare.app.BuildConfig;
import com.rideshare.app.R;
import com.rideshare.app.Server.Server;
import com.rideshare.app.Server.session.SessionManager;
import com.rideshare.app.connection.ApiClient;
import com.rideshare.app.connection.ApiNetworkCall;
import com.rideshare.app.pojo.AppVersionUpdateResponse;
import com.rideshare.app.pojo.CheckDeviceTokenResponse;
import com.mapbox.mapboxsdk.Mapbox;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by android on 15/3/17.
 */

public class MyApplication extends MultiDexApplication {
    Handler handler = new Handler();
    Runnable runnable;
    int delay = 5000;

    Timer timer;
    public static TimerTask timerTask;
    Handler hand = new Handler(Looper.getMainLooper());
    Boolean userLoggedIn = false;
    Boolean userPaymentAdded = false;
    String userDeviceToken;

    @Override
    public void onCreate() {
        super.onCreate();
        // Fabric.with(this, new Crashlytics());
        Mapbox.getInstance(getApplicationContext(), String.valueOf(R.string.mapboxkey));
        SessionManager.initialize(getApplicationContext());

        FirebaseApp.initializeApp(this);
        // Optional: Log non-fatal exceptions
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    Log.e("FirebaseTokenErr", "Fetching FCM registration token failed", task.getException());
                } else {
                    Log.d("FirebaseToken", task.getResult());
                }
            }
        });

//        TimerTask timerTask = new TimerTask() {
//            @Override
//            public void run() {
//                Log.e("TIMER", "CheckDeviceToken");
//
//                handler.post(() -> {
//                    boolean userLoggedIn = SessionManager.getStatus();
//
//                    // Optional: Check for payment saved
//                    // boolean userPaymentAdded = SessionManager.getIsCardSaved();
//
//                    if (userLoggedIn) {
//                        userDeviceToken = SessionManager.getDeviceId();
//                        checkDeviceTokenApi(); // Make sure this method is thread-safe
//                    }
//                });
//            }
//        };
//
//        timer = new Timer();
//        timer.scheduleAtFixedRate(timerTask, 5000, 15 * 1000); // Start after 5 sec, repeat every 15 sec


        // Check for update and show dialog if available
//        checkAppVersionApi();
        appVersionUpdateApi();


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
                                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();
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


    //    // APi for update app version to backend
    public void appVersionUpdateApi() {

        // final ProgressDialog progressDialog = new ProgressDialog(requireContext());
        if (CheckConnection.haveNetworkConnection(getApplicationContext())) {
            // progressDialog.setMessage("Session checking.....");
            //progressDialog.setCancelable(false);
            //progressDialog.show();
            Map<String, String> param = new HashMap();
            param.put("device_type", "android");
            param.put("app_version", "4.1.8");
            param.put("user_type", "1");

            ApiNetworkCall apiService = ApiClient.getApiService();

            Call<AppVersionUpdateResponse> call =
                    apiService.updateAppVersionApi("Bearer " + SessionManager.getKEY(), param);
            call.enqueue(new Callback<AppVersionUpdateResponse>() {
                @Override
                public void onResponse(Call<AppVersionUpdateResponse> call, retrofit2.Response<AppVersionUpdateResponse> response) {
                    AppVersionUpdateResponse jsonResponse = response.body();
                    Log.d("response", response.toString());
                    if (jsonResponse != null) {
                        Log.d("UpdateResponse", jsonResponse.toString());
                        if (jsonResponse.getStatus()) {
                            Log.d("UpdateResponse", jsonResponse.getMessage().toString());
                        } else {
                            //progressDialog.cancel();
                            Toast.makeText(getApplicationContext(), jsonResponse.getMessage(), Toast.LENGTH_LONG).show();

                        }
                    }
                }

                @Override
                public void onFailure(Call<AppVersionUpdateResponse> call, Throwable t) {
                    Log.d("Failed", "RetrofitFailed");
                    //   progressDialog.cancel();
                }
            });
        } else {
            //progressDialog.cancel();
            Toast.makeText(getApplicationContext(), getString(R.string.network), Toast.LENGTH_LONG).show();
            // progressDialog.dismiss();
        }
    }

}