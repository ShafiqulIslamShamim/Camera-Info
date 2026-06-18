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
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;

public class OTAUpdateHelper {

  private static final int RC_APP_UPDATE = 9001;

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

  @SuppressWarnings("deprecation")
  private static boolean checkInternetConnectionLegacy(ConnectivityManager cm) {
    android.net.NetworkInfo activeNetwork = cm.getNetworkInfo(cm.getActiveNetwork());
    if (activeNetwork == null) {
      activeNetwork = cm.getActiveNetworkInfo();
    }
    return activeNetwork != null && activeNetwork.isConnected();
  }

  private static Activity getActivity(Context context) {
    if (context instanceof Activity) {
      return (Activity) context;
    } else if (context instanceof ContextWrapper) {
      return getActivity(((ContextWrapper) context).getBaseContext());
    }
    return null;
  }

  public static void hookPreference(Context context) {
    Activity activity = getActivity(context);
    if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
      Toast.makeText(context, "Unable to check for updates: Activity context is not available.", Toast.LENGTH_SHORT).show();
      return;
    }

    if (!isInternetAvailable(activity)) {
      Toast.makeText(activity, "No internet connection available.", Toast.LENGTH_LONG).show();
      return;
    }

    AlertDialog progressDialog = new MaterialAlertDialogBuilder(activity)
        .setTitle("Checking for Updates")
        .setMessage("Please wait, searching for the latest version...")
        .setCancelable(false)
        .show();

    AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(activity);
    appUpdateManager.getAppUpdateInfo()
        .addOnSuccessListener(appUpdateInfo -> {
          if (progressDialog.isShowing()) {
            progressDialog.dismiss();
          }

          if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
              && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
            try {
              appUpdateManager.startUpdateFlowForResult(
                  appUpdateInfo,
                  AppUpdateType.IMMEDIATE,
                  activity,
                  RC_APP_UPDATE);
            } catch (Exception e) {
              e.printStackTrace();
              Toast.makeText(activity, "Failed to launch update: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
          } else {
            new MaterialAlertDialogBuilder(activity)
                .setTitle("Up to Date")
                .setMessage("You are already using the latest version of Camera Info.")
                .setPositiveButton("OK", null)
                .show();
          }
        })
        .addOnFailureListener(e -> {
          if (progressDialog.isShowing()) {
            progressDialog.dismiss();
          }
          
                try {
                String packageName = activity.getPackageName();
                  android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_VIEW,
                      android.net.Uri.parse("https://play.google.com/store/apps/details?id=" + packageName));
                  activity.startActivity(intent);
                } catch (Exception ex) {
                  Toast.makeText(activity, "Failed to open Google Play Store.", Toast.LENGTH_SHORT).show();
                }
        });
  }

  public static void checkForUpdatesIfDue(Context context) {
    Activity activity = getActivity(context);
    if (activity == null || activity.isFinishing() || activity.isDestroyed()) return;

    if (!isInternetAvailable(activity)) return;

    AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(activity);
    
    // Check if there is an update already in progress to resume it immediately
    appUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
      if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
        try {
          appUpdateManager.startUpdateFlowForResult(
              appUpdateInfo,
              AppUpdateType.IMMEDIATE,
              activity,
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

      SharedPreferences prefs = activity.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
      long lastCheck = prefs.getLong(KEY_LAST_CHECK, 0);
      long currentTime = System.currentTimeMillis();

      if (currentTime - lastCheck >= CHECK_INTERVAL) {
        prefs.edit().putLong(KEY_LAST_CHECK, currentTime).apply();

        if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
            && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
          
          new MaterialAlertDialogBuilder(activity)
              .setTitle("Update Available")
              .setMessage("A new version of Camera Info is available. Update now to receive the latest features and bug fixes.")
              .setPositiveButton("Update Now", (dialog, which) -> {
                try {
                  appUpdateManager.startUpdateFlowForResult(
                      appUpdateInfo,
                      AppUpdateType.IMMEDIATE,
                      activity,
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
