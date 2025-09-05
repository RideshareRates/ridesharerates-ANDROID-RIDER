package com.rideshare.app.custom

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.rideshare.app.R

class UtilsNew {
    companion object {
        // Show GPS Alert Message
        @JvmStatic
        fun showAlertToTurnOnGPS(context: Context) {
            val builder = AlertDialog.Builder(context)
            builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton(
                    "Yes"
                ) { dialog, id -> context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
                .setNegativeButton(
                    "No"
                ) { dialog, id -> dialog.cancel() }
            val alert = builder.create()
            alert.show()
        }

        // Show GPS Alert Message
        @JvmStatic
        fun checkPermission(context: Context) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                showAlertToCheckPermission(context, "ACCESS_FINE_LOCATION");
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    showAlertToCheckPermission(context, "POST_NOTIFICATIONS")
                }
            }
        }

        private fun showAlertToCheckPermission(context: Context, str: String) {
            // Inflate the custom layout/view
            val dialogView =
                LayoutInflater.from(context).inflate(R.layout.location_permission_popup, null)

            // Initialize the views in the custom layout
            val dialogIcon = dialogView.findViewById<ImageView>(R.id.dialogIcon)
            val dialogTitle = dialogView.findViewById<TextView>(R.id.dialogTitle)
            val dialogDescription = dialogView.findViewById<TextView>(R.id.dialogDescription)
            val dialogButton = dialogView.findViewById<Button>(R.id.dialogButton)

            if (str.equals("POST_NOTIFICATIONS")) {
                dialogIcon.setImageResource(R.drawable.notification)
                dialogTitle.text = "Notification Permission not enabled"
                dialogDescription.text =
                    "Please enable notification permission for a better experience"
            }

            // Create the AlertDialog
            val dialogBuilder = AlertDialog.Builder(context)
                .setView(dialogView)
                .setCancelable(false)

            // Show the dialog
            val dialog = dialogBuilder.create()
            dialog.show()

            // Handle the button click
            dialogButton.setOnClickListener {
                redirectToNotificationSettings(context, str)
                // Handle the input text or perform any action
                dialog.dismiss()
            }
        }

        private fun redirectToNotificationSettings(context: Context, str: String) {
            if (str.equals("POST_NOTIFICATIONS")) {
                val intent = Intent().apply {
                    when {
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                            // For Android 8.0 and above, open the specific permission screen
                            action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                        }

                        else -> {
                            // For Android versions below, open the general app settings
                            action = Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS
                        }
                    }
                }
                context.startActivity(intent)
            } else {
                val intent = Intent().apply {
                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    data = Uri.fromParts("package", context.packageName, null)
                }
                context.startActivity(intent)
            }
        }

        fun convertTime(time: String): String {
            try {
                val parts = time.split(":").map { it.toInt() }
                val (hours, minutes) = parts

                return when {
                    hours != 0 -> "$time"
                    minutes != 0 -> "$minutes"
                    else -> "$minutes"
                }
            } catch (e: Exception) {
                Log.e("Exception", e.toString())
            }
            return time
        }

        fun convertTimeText(time: String): String {
            try {
                val parts = time.split(":").map { it.toInt() }
                val (hours, minutes) = parts

                return when {
                    hours != 0 -> "Hours"
                    minutes != 0 -> "Minutes"
                    else -> "Minutes"
                }
            } catch (e: Exception) {
                Log.e("Exception", e.toString())
            }
            return "Minutes"
        }
    }
}