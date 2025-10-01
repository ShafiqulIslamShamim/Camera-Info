package com.shamim.camerainfo;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.widget.TextView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import android.util.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.core.graphics.Insets;
import androidx.core.view.*;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import androidx.core.content.FileProvider;
import androidx.activity.EdgeToEdge;
import android.util.TypedValue;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import android.content.Context;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.hardware.camera2.*;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

  public static MainActivity ActivityContext;
    private final String[] infoOptions = {"Basic info", "All info", "Camcorder profile info"};
    TextView textView;
    FloatingActionButton shareFab;
    private CircularProgressIndicator progressIndicator;
    CameraManager cameraManager;
    private static final long MIN_SHOW_TIME = 500; // Minimum spinner visibility in ms
private long showStartTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applyLocalTheme();
    super.onCreate(savedInstanceState);

    applySystemBarIconColors();
    ActivityContext = this;

    OTAUpdateHelper.checkForUpdatesIfDue(this);

    boolean logcat = SharedPrefValues.getValue("enable_logcat", false);
    if (logcat) {
      StoragePermissionHelper.checkAndRequestStoragePermission(this);
      if (StoragePermissionHelper.isPermissionGranted(this)) {
        LogcatSaver.RunLog(this); // Pass context since LogcatSaver now uses SAF
      }
    }
        setContentView(R.layout.activity_main);
        
            // ✅ Apply insets to root view
    View rootView = findViewById(android.R.id.content);
    ViewCompat.setOnApplyWindowInsetsListener(
        rootView,
        (v, insets) -> {
          Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
          v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
          return insets;
        });
        
       cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
       
       textView = findViewById(R.id.logView); // আপনার TextView এর আইডি বসান
        progressIndicator = findViewById(R.id.progress_indicator);
        progressIndicator.setIndeterminate(true);
        
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
    setSupportActionBar(toolbar);
    
    MaterialCardView prefCard = findViewById(R.id.pref_select_info);
TextView prefSummary = findViewById(R.id.pref_summary);

// SharedPreferences থেকে লোড করো
int savedIndex = SharedPrefValues.getValue("pref_log_mode", 0);
prefSummary.setText(infoOptions[savedIndex]);

prefCard.setOnClickListener(v -> {
    new MaterialAlertDialogBuilder(this)
           // .setTitle("Select Info Type")
            .setCustomTitle(
                    DialogUtils.createStyledDialogTitle(
                        this, "Select Info Type"))
            .setSingleChoiceItems(infoOptions, SharedPrefValues.getValue("pref_log_mode", 0), (dialog, which) -> {
                // Save user choice
                PreferenceManager.getDefaultSharedPreferences(this)
                        .edit()
                        .putString("pref_log_mode", String.valueOf(which))
                        .apply();

                // Update summary text
                prefSummary.setText(infoOptions[which]);

                // Update data
                setInfoToTextView();

                dialog.dismiss();
            })
            .show();
});
        
        
        shareFab = findViewById(R.id.sharetext);

        shareFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareTextAsFile();
            }
        });
        setInfoToTextView();
    }

    
   // থিম থেকে রঙ আনার জন্য হেল্পার মেথড
private int getColorFromAttr(Context context, int attr) {
    TypedValue typedValue = new TypedValue();
    context.getTheme().resolveAttribute(attr, typedValue, true);
    return typedValue.data;
}
    
    
    private void shareTextAsFile() {
    String text = textView.getText().toString();

    try {
        // cache ডিরেক্টরিতে টেম্প ফাইল বানানো
        File file = new File(getCacheDir(), "camera Info.txt");
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(text.getBytes());
        fos.close();

        // FileProvider দিয়ে Uri তৈরি
        Uri uri = FileProvider.getUriForFile(
                this,
                getPackageName() + ".provider",
                file
        );

        // শেয়ার করার Intent
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");

        // ফাইল অ্যাটাচ করা
        intent.putExtra(Intent.EXTRA_STREAM, uri);

        // মেসেজ যুক্ত করা
        intent.putExtra(Intent.EXTRA_TEXT, DeviceInfo.getShortDeviceInfoMassage());

        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(Intent.createChooser(intent, "Share text file via"));

    } catch (IOException e) {
        e.printStackTrace();
    }
}
    
    

public void setInfoToTextView() {
    // Show progress indicator
    progressIndicator.setVisibility(View.VISIBLE);
    progressIndicator.setIndeterminate(true);
    showStartTime = System.currentTimeMillis();

    textView.setText("");

    // Use a single thread executor
    ExecutorService executor = Executors.newSingleThreadExecutor();

    executor.execute(() -> {
        // Collect info
        String deviceInfo = DeviceInfo.getDeviceInfoText(this);
        String cameraInformation = CameraInfoHelper.getAllCameraInfo(cameraManager);

        StringBuilder combinedInfo = new StringBuilder();
        combinedInfo.append(deviceInfo);
        if (!deviceInfo.isEmpty() && !cameraInformation.isEmpty()) {
            combinedInfo.append("\n");
        }
        combinedInfo.append(cameraInformation);

        // Switch back to UI thread
        runOnUiThread(() -> {
            long elapsed = System.currentTimeMillis() - showStartTime;
            long remaining = MIN_SHOW_TIME - elapsed;

            Runnable updateUI = () -> {
                progressIndicator.setVisibility(View.GONE);

                int keyColor = getColorFromAttr(this, com.google.android.material.R.attr.colorPrimary);
                int separatorColor = getColorFromAttr(this, com.google.android.material.R.attr.colorTertiary);
                int valueColor = getColorFromAttr(this, com.google.android.material.R.attr.colorSecondary);

                ColoredTextHelper.setColoredText(textView, combinedInfo.toString(),
                        keyColor, separatorColor, valueColor);
            };

            if (remaining > 0) {
                progressIndicator.postDelayed(updateUI, remaining);
            } else {
                updateUI.run();
            }
        });

        // Shutdown executor
        executor.shutdown();
    });
}



private void applyLocalTheme() {
    String themePref = SharedPrefValues.getValue("theme_preference", "0");
    switch (themePref) {
      case "2": // Dark
        setTheme(R.style.AppThemeDark);
        break;
      case "3": // Light
        setTheme(R.style.AppThemeLight);
        break;
      default: // System/default
        setTheme(R.style.AppTheme);
        break;
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    int menuId = getResources().getIdentifier("main_menu", "menu", getPackageName());
    if (menuId == 0) {
      Log.e(TAG, "Menu resource 'main_menu' not found");
      return false;
    }

    getMenuInflater().inflate(menuId, menu);

    // Settings button
    int settingsId = getResources().getIdentifier("settings", "id", getPackageName());
    int settingsIconId = getResources().getIdentifier("ic_settings", "drawable", getPackageName());
    if (settingsId != 0 && settingsIconId != 0) {
      menu.findItem(settingsId).setIcon(settingsIconId);
    } else {
      Log.e(TAG, "Menu item 'settings' or drawable 'ic_settings' not found");
    }

    // Reset button
    int ResetId = getResources().getIdentifier("action_reset", "id", getPackageName());
    int ResetIconId = getResources().getIdentifier("ic_reset", "drawable", getPackageName());
    if (ResetId != 0 && ResetIconId != 0) {
      menu.findItem(ResetId).setIcon(ResetIconId);
    } else {
      Log.e(TAG, "Menu item 'Reset' or drawable 'ic_reset' not found");
    }

    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    int id = item.getItemId();

    int settingsId = getResources().getIdentifier("settings", "id", getPackageName());

    if (id == settingsId) {
      startActivity(new Intent(this, SettingsActivity.class));
      return true;
    }

    if (id == R.id.action_reset) {
      // Restart the activity
      Intent intent = new Intent(this, MainActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
      finish(); // Close the current instance
      startActivity(intent); // Start a new instance
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  private void applySystemBarIconColors() {
    String themePref = SharedPrefValues.getValue("theme_preference", "0");

    boolean isLightTheme;

    switch (themePref) {
      case "2": // Dark theme
        isLightTheme = false;
        break;
      case "3": // Light theme
        isLightTheme = true;
        break;
      default:
        // Follow system theme
        int nightModeFlags =
            getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        isLightTheme = (nightModeFlags != Configuration.UI_MODE_NIGHT_YES);
        break;
    }
    
    // Enable edge-to-edge (backward compatible)
        EdgeToEdge.enable(this);

  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    // Handle folder picker result
    if (requestCode == StoragePermissionHelper.REQUEST_CODE_OLD_STORAGE) {
      StoragePermissionHelper.handleFolderPickerResult(this, requestCode, resultCode, data);

      boolean logcat = SharedPrefValues.getValue("enable_logcat", false);
      if (logcat && StoragePermissionHelper.isPermissionGranted(this)) {
        LogcatSaver.RunLog(this);
      }
    }
  }

  @Override
  public void onRequestPermissionsResult(
      int requestCode, String[] permissions, int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    if (requestCode == StoragePermissionHelper.REQUEST_CODE_OLD_STORAGE) {
      boolean granted = true;
      for (int result : grantResults) {
        if (result != PackageManager.PERMISSION_GRANTED) {
          granted = false;
          break;
        }
      }

      boolean logcat = SharedPrefValues.getValue("enable_logcat", false);
      if (granted && logcat && StoragePermissionHelper.isPermissionGranted(this)) {
        LogcatSaver.RunLog(this);
      }
    }
  }


}
