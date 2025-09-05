package com.rideshare.app.acitivities;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.rideshare.app.R;
import com.rideshare.app.Server.session.SessionManager;
import com.thebrownarrow.permissionhelper.ActivityManagePermission;
import com.thebrownarrow.permissionhelper.PermissionResult;
import com.thebrownarrow.permissionhelper.PermissionUtils;

public class SplashActivity extends ActivityManagePermission {
    String permissionAsk[] = {PermissionUtils.Manifest_CAMERA, PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE,
            PermissionUtils.Manifest_READ_EXTERNAL_STORAGE, PermissionUtils.Manifest_ACCESS_FINE_LOCATION,
            PermissionUtils.Manifest_ACCESS_COARSE_LOCATION, Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.POST_NOTIFICATIONS};
    private VideoView videoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash_activity);
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoView = findViewById(R.id.videoView);

        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.intro_rideshare;
        Uri videoUri = Uri.parse(videoPath);
        videoView.setVideoURI(videoUri);

        // After complete the video
        videoView.setOnCompletionListener(mp -> {
            if (!(ActivityCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(getApplicationContext(),
                            Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
                requestLocationWithDisclosure();
            } else {
                performCircularExit();
            }
        });

        videoView.start();
    }

    // Request to enable the device location
    private void requestLocationWithDisclosure() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Location Permission");
        alert.setMessage("This application requests location permission to calculate the traveled distance by the user.");
        alert.setPositiveButton("Accept", (dialog, which) -> {
            askCompactPermissions(permissionAsk, new PermissionResult() {
                @Override
                public void permissionGranted() {
                    performCircularExit();
                }

                @Override
                public void permissionDenied() {
                    performCircularExit();
                }

                @Override
                public void permissionForeverDenied() {
                    performCircularExit();
                }
            });
            dialog.dismiss();
        });
        alert.setNegativeButton("Deny", (dialog, which) -> {
            SessionManager.setLocationPermission(false);
            performCircularExit();
            dialog.dismiss();
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    // To start the video with animaton
    private void performCircularExit() {
        int centerX = videoView.getWidth() / 2;
        int centerY = videoView.getHeight() / 2;
        float startRadius = Math.max(videoView.getWidth(), videoView.getHeight()) * 0.5f;
        float endRadius = 0;

        Animator circularReveal = ViewAnimationUtils.createCircularReveal(videoView, centerX, centerY, startRadius, endRadius);
        circularReveal.setDuration(1000);
        circularReveal.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                navigateToNextScreen();
            }
        });
        circularReveal.start();
    }

    // To Navigate the Login Screen OR Home Screen
    private void navigateToNextScreen() {
        Class<?> nextActivity = SessionManager.getStatus() ? HomeActivity.class : LoginActivity.class;
        startActivity(new Intent(SplashActivity.this, nextActivity));
        overridePendingTransition(R.anim.motion_in, R.anim.motion_out);
        finish();
    }
}
