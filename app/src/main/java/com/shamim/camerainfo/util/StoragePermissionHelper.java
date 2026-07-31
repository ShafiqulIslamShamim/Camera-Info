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
package com.shamim.camerainfo.util;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.shamim.camerainfo.activity.*;
import com.shamim.camerainfo.c2api_key.*;
import com.shamim.camerainfo.exception_catcher.*;
import com.shamim.camerainfo.preference.*;
import com.shamim.camerainfo.recycle_view.*;
import com.shamim.camerainfo.update_checker.*;

public class StoragePermissionHelper {

  public static final String PREF_LOG_FOLDER_URI = "log_folder_uri";

  /**
   * Checks for appropriate storage/folder permissions based on the system's SDK level. Prompts the
   * user or launches a folder picker if the permission is missing.
   *
   * @param activity The AppCompatActivity calling this.
   * @param folderPickerLauncher The launcher used to initiate the system folder selection.
   */
  public static void checkAndRequestStoragePermission(
      final AppCompatActivity activity, ActivityResultLauncher<Intent> folderPickerLauncher) {

    if (Build.VERSION.SDK_INT < 23) return;

    if (Build.VERSION.SDK_INT >= 30) {
      SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
      String folderUriStr = prefs.getString(PREF_LOG_FOLDER_URI, null);

      if (folderUriStr == null) {
        showFolderPermissionDialog(activity, folderPickerLauncher);
      }
    } else {
      if (activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
              != PackageManager.PERMISSION_GRANTED
          || activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
              != PackageManager.PERMISSION_GRANTED) {

        activity.requestPermissions(
            new String[] {
              Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
            },
            1001);
      }
    }
  }

  /**
   * Displays a Material alert dialog informing the user why folder access is necessary.
   *
   * @param activity The AppCompatActivity used to display the dialog.
   * @param launcher The activity result launcher for the folder picker.
   */
  private static void showFolderPermissionDialog(
      AppCompatActivity activity, ActivityResultLauncher<Intent> launcher) {

    new MaterialAlertDialogBuilder(activity)
        .setCustomTitle(DialogUtils.createStyledDialogTitle(activity, "Folder Access Needed"))
        .setMessage("This app needs permission to save log files. Please select a folder.")
        .setPositiveButton("Select Folder", (d, w) -> openFolderPicker(launcher))
        .setNegativeButton("Cancel", null)
        .show();
  }

  /**
   * Opens the system folder picker UI for picking a directory tree.
   *
   * @param launcher The launcher to trigger the document tree action intent.
   */
  private static void openFolderPicker(ActivityResultLauncher<Intent> launcher) {
    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
    intent.addFlags(
        Intent.FLAG_GRANT_READ_URI_PERMISSION
            | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
    launcher.launch(intent);
  }

  /**
   * Handles the result returned by the folder picker, requesting persistable permissions and saving
   * the URI to shared preferences.
   *
   * @param activity The active Activity.
   * @param data The intent containing the chosen directory URI.
   */
  public static void handleFolderPickerResult(Activity activity, Intent data) {
    if (data == null) return;

    Uri treeUri = data.getData();
    if (treeUri == null) return;

    activity
        .getContentResolver()
        .takePersistableUriPermission(
            treeUri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
    prefs.edit().putString(PREF_LOG_FOLDER_URI, treeUri.toString()).apply();
  }

  /**
   * Checks whether the necessary storage or folder access permissions have been granted.
   *
   * @param activity The AppCompatActivity to check permissions against.
   * @return true if permission is granted, false otherwise.
   */
  public static boolean isPermissionGranted(AppCompatActivity activity) {
    if (Build.VERSION.SDK_INT >= 30) {
      SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
      return prefs.getString(PREF_LOG_FOLDER_URI, null) != null;
    } else {
      return activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
              == PackageManager.PERMISSION_GRANTED
          && activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
              == PackageManager.PERMISSION_GRANTED;
    }
  }
}
