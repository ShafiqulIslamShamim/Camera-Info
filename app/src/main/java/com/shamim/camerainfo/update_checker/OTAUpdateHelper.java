/*
 * Copyright (c) 2026 Shafiqul Islam Shamim
 * GitHub: https://github.com/ShafiqulIslamShamim/Camera-Info
 *
 * All Rights Reserved.
 *
 * This source code is made publicly available solely for viewing, collaboration,
 * educational reference, and submitting pull requests to the official repository.
 *
 * No permission is granted to copy, modify, redistribute, sublicense, or use
 * this source code, in whole or in part, for personal, commercial, or any other
 * purpose without the prior written permission of the copyright holder.
 */
package com.shamim.camerainfo.update_checker;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.appupdate.AppUpdateOptions;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;

@SuppressWarnings("deprecation")
public class OTAUpdateHelper {

  private static final int RC_APP_UPDATE = 9001;

  /**
   * Evaluates if there is an active network connection available. Utilizes newer APIs for API 29+
   * and falls back to legacy connectivity checks on older systems.
   *
   * @param context The Context to fetch connectivity service from.
   * @return true if internet transport is detected (WiFi, Cellular, or Ethernet), false otherwise.
   */
  public static boolean isInternetAvailable(@NonNull Context context) {
    ConnectivityManager cm =
        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    if (cm == null) return false;

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      Network network = cm.getActiveNetwork();
      if (network == null) return false;
      NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
      return capabilities != null
          && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
              || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
              || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
    } else {
      return checkInternetConnectionLegacy(cm);
    }
  }

  /**
   * Helper to perform legacy connectivity checking on older Android versions.
   *
   * @param cm The system ConnectivityManager.
   * @return true if network is connected, false otherwise.
   */
  @SuppressWarnings("deprecation")
  private static boolean checkInternetConnectionLegacy(ConnectivityManager cm) {
    android.net.NetworkInfo activeNetwork = cm.getNetworkInfo(cm.getActiveNetwork());
    if (activeNetwork == null) {
      activeNetwork = cm.getActiveNetworkInfo();
    }
    return activeNetwork != null && activeNetwork.isConnected();
  }

  /**
   * Unwraps a given Context to find and return the associated Activity, walking up ContextWrappers
   * if needed.
   *
   * @param context The context to unwrap.
   * @return The underlying Activity, or null if none can be found.
   */
  private static Activity getActivity(Context context) {
    if (context instanceof Activity) {
      return (Activity) context;
    } else if (context instanceof ContextWrapper) {
      return getActivity(((ContextWrapper) context).getBaseContext());
    }
    return null;
  }

  /**
   * Triggers an manual check for app updates from the Google Play Store. Displays progress and, if
   * an update is found, launches the immediate update flow. Falls back to redirecting the user to
   * the Play Store page on failure.
   *
   * @param context Context associated with the request (must unwrap to an Activity).
   */
  public static void hookPreference(Context context) {
    Activity activity = getActivity(context);
    if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
      Toast.makeText(
              context,
              "Unable to check for updates: Activity context is not available.",
              Toast.LENGTH_SHORT)
          .show();
      return;
    }

    if (!isInternetAvailable(activity)) {
      Toast.makeText(activity, "No internet connection available.", Toast.LENGTH_LONG).show();
      return;
    }

    AlertDialog progressDialog =
        new MaterialAlertDialogBuilder(activity)
            .setTitle("Checking for Updates")
            .setMessage("Please wait, searching for the latest version...")
            .setCancelable(false)
            .show();

    AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(activity);
    appUpdateManager
        .getAppUpdateInfo()
        .addOnSuccessListener(
            appUpdateInfo -> {
              if (progressDialog.isShowing()) {
                progressDialog.dismiss();
              }

              if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                  && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                try {
                  appUpdateManager.startUpdateFlowForResult(
                      appUpdateInfo,
                      activity,
                      AppUpdateOptions.defaultOptions(AppUpdateType.IMMEDIATE),
                      RC_APP_UPDATE);
                } catch (Exception e) {
                  e.printStackTrace();
                  Toast.makeText(
                          activity,
                          "Failed to launch update: " + e.getMessage(),
                          Toast.LENGTH_SHORT)
                      .show();
                }
              } else {
                new MaterialAlertDialogBuilder(activity)
                    .setTitle("Up to Date")
                    .setMessage("You are already using the latest version of Camera Info.")
                    .setPositiveButton("OK", null)
                    .show();
              }
            })
        .addOnFailureListener(
            e -> {
              if (progressDialog.isShowing()) {
                progressDialog.dismiss();
              }

              try {
                String packageName = activity.getPackageName();
                android.content.Intent intent =
                    new android.content.Intent(
                        android.content.Intent.ACTION_VIEW,
                        android.net.Uri.parse(
                            "https://play.google.com/store/apps/details?id=" + packageName));
                activity.startActivity(intent);
              } catch (Exception ex) {
                Toast.makeText(activity, "Failed to open Google Play Store.", Toast.LENGTH_SHORT)
                    .show();
              }
            });
  }

  /**
   * Throttled check for app updates that triggers automatically if 24 hours have passed since the
   * last check. If an update exists and is in progress, resumes it immediately. Otherwise prompts
   * the user if a new update is found.
   *
   * @param context Context associated with the automatic update query.
   */
  public static void checkForUpdatesIfDue(Context context) {
    Activity activity = getActivity(context);
    if (activity == null || activity.isFinishing() || activity.isDestroyed()) return;

    if (!isInternetAvailable(activity)) return;

    AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(activity);

    // Check if there is an update already in progress to resume it immediately
    appUpdateManager
        .getAppUpdateInfo()
        .addOnSuccessListener(
            appUpdateInfo -> {
              if (appUpdateInfo.updateAvailability()
                  == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                try {
                  appUpdateManager.startUpdateFlowForResult(
                      appUpdateInfo,
                      activity,
                      AppUpdateOptions.defaultOptions(AppUpdateType.IMMEDIATE),
                      RC_APP_UPDATE);
                } catch (Exception e) {
                  e.printStackTrace();
                }
                return;
              }

              // Throttle auto-checking to once every 24 hours
              final String PREF_NAME = "update_pref";
              final String KEY_LAST_CHECK = "last_check_time";
              final long CHECK_INTERVAL = 24L * 60 * 60 * 1000; // 24 hours

              SharedPreferences prefs =
                  activity.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
              long lastCheck = prefs.getLong(KEY_LAST_CHECK, 0);
              long currentTime = System.currentTimeMillis();

              if (currentTime - lastCheck >= CHECK_INTERVAL) {
                prefs.edit().putLong(KEY_LAST_CHECK, currentTime).apply();

                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {

                  new MaterialAlertDialogBuilder(activity)
                      .setTitle("Update Available")
                      .setMessage(
                          "A new version of Camera Info is available. Update now to receive the latest features and bug fixes.")
                      .setPositiveButton(
                          "Update Now",
                          (dialog, which) -> {
                            try {
                              appUpdateManager.startUpdateFlowForResult(
                                  appUpdateInfo,
                                  activity,
                                  AppUpdateOptions.defaultOptions(AppUpdateType.IMMEDIATE),
                                  RC_APP_UPDATE);
                            } catch (Exception e) {
                              e.printStackTrace();
                            }
                          })
                      .setNegativeButton("Later", null)
                      .setCancelable(true)
                      .show();
                }
              }
            });
  }
}
