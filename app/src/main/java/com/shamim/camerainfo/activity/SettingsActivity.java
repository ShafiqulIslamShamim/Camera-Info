package com.shamim.camerainfo.activity;

import android.content.*;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.*;
import android.graphics.*;
import android.net.Uri;
import android.os.Bundle;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.*;
import androidx.annotation.NonNull;
import androidx.core.content.pm.PackageInfoCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.shamim.camerainfo.R;
import com.shamim.camerainfo.c2api_key.*;
import com.shamim.camerainfo.exception_catcher.*;
import com.shamim.camerainfo.preference.*;
import com.shamim.camerainfo.recycle_view.*;
import com.shamim.camerainfo.update_checker.*;
import com.shamim.camerainfo.util.*;

public class SettingsActivity extends BaseActivity {

  private static final String EXTRA_PREF_KEY = "pref_key";
  private static final String EXTRA_PREF_TITLE = "pref_title";
  private static final String EXTRA_PARENT_KEY = "parent_key";
  private static final String EXTRA_PARENT_TITLE = "parent_title";
  private static final String PREF_CHANGE_FLAG = "preference_changed";

  //  Preference Keys
  public static final String KEY_DEVELOPER = "pref_developer_name_key";
  public static final String KEY_NEWS = "pref_news_information_key";
  public static final String KEY_CHECK_UPDATES = "pref_updates_checker_key";
  public static final String KEY_PRIVACY = "pref_privacy_policy_key";

  public static final String KEY_RATE_IT = "pref_rate_it_key";
  public static final String KEY_MORE_APPS = "pref_try_more_apps_key";
  public static final String KEY_FEEDBACK = "pref_feedback_key";

  private MaterialToolbar toolbar;

  public static Intent createIntent(
      Context context, String prefKey, String prefTitle, String parentKey, String parentTitle) {
    Intent intent = new Intent(context, SettingsActivity.class);
    intent.putExtra(EXTRA_PREF_KEY, prefKey);
    intent.putExtra(EXTRA_PREF_TITLE, prefTitle);
    intent.putExtra(EXTRA_PARENT_KEY, parentKey);
    intent.putExtra(EXTRA_PARENT_TITLE, parentTitle);
    return intent;
  }

  public static Intent createRootIntent(Context context) {
    return createIntent(context, "main_settings", "Settings", null, null);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_settings);

    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

    toolbar = findViewById(R.id.toolbar);
    CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar);
    setSupportActionBar(toolbar);

    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setDisplayShowTitleEnabled(true);
      collapsingToolbar.setTitle("Settings");
    }

    String prefKey = getIntent().getStringExtra(EXTRA_PREF_KEY);
    String prefTitle = getIntent().getStringExtra(EXTRA_PREF_TITLE);

    if (getSupportActionBar() != null) {
      getSupportActionBar().setTitle(prefTitle != null ? prefTitle : "Settings");
    }

    if (savedInstanceState == null) {
      SettingsFragment fragment = new SettingsFragment();
      Bundle args = new Bundle();
      args.putString(PreferenceFragmentCompat.ARG_PREFERENCE_ROOT, prefKey);
      fragment.setArguments(args);
      getSupportFragmentManager()
          .beginTransaction()
          .replace(R.id.settings_container, fragment)
          .commit();
    }

    // Re-set title just in case
    if (getSupportActionBar() != null) {
      getSupportActionBar().setTitle(prefTitle != null ? prefTitle : "Settings");
    }

    // Onbackpressed modern handling
    getOnBackPressedDispatcher()
        .addCallback(
            this,
            new OnBackPressedCallback(true) {
              @Override
              public void handleOnBackPressed() {

                String parentKey = getIntent().getStringExtra(EXTRA_PARENT_KEY);

                if (parentKey == null) { // Root screen

                  SharedPreferences prefs =
                      PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);

                  boolean prefChanged = prefs.getBoolean(PREF_CHANGE_FLAG, false);

                  if (prefChanged) {
                    // Reset flag
                    prefs.edit().putBoolean(PREF_CHANGE_FLAG, false).apply();

                    Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                    intent.setFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    return;
                  }
                }

                // Default back behavior
                setEnabled(false);
                getOnBackPressedDispatcher().onBackPressed();
              }
            });
  }

  @Override
  public boolean onSupportNavigateUp() {
    getOnBackPressedDispatcher().onBackPressed();
    return true;
  }

  public static class SettingsFragment extends PreferenceFragmentCompat
      implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
      setPreferencesFromResource(R.xml.preferences, rootKey);

      PreferenceScreen root = getPreferenceScreen();
      setupPreferenceScreenListeners(root);

      Preference aboutInfoPreference = findPreference("pref_about_info_key");
      if (aboutInfoPreference != null) {
        aboutInfoPreference.setOnPreferenceClickListener(
            preference -> {
              // You can launch an AboutActivity or dialog here if you want
              return true;
            });
      }
    }

    @Override
    public void onResume() {
      super.onResume();
      PreferenceManager.getDefaultSharedPreferences(requireContext())
          .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
      super.onPause();
      PreferenceManager.getDefaultSharedPreferences(requireContext())
          .unregisterOnSharedPreferenceChangeListener(this);
    }

    private void setupPreferenceScreenListeners(PreferenceScreen preferenceScreen) {
      String parentKey = preferenceScreen.getKey();
      String parentTitle =
          preferenceScreen.getTitle() != null ? preferenceScreen.getTitle().toString() : "Settings";

      for (int i = 0; i < preferenceScreen.getPreferenceCount(); i++) {
        Preference preference = preferenceScreen.getPreference(i);
        if (preference instanceof PreferenceScreen) {
          PreferenceScreen subScreen = (PreferenceScreen) preference;
          subScreen.setOnPreferenceClickListener(
              p -> {
                Intent intent =
                    createIntent(
                        requireContext(),
                        p.getKey(),
                        p.getTitle().toString(),
                        parentKey,
                        parentTitle);
                requireActivity().startActivity(intent);
                return true;
              });
          setupPreferenceScreenListeners(subScreen);
        }
      }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
      if (key == null || key.isEmpty()) return;
      sharedPreferences.edit().putBoolean(PREF_CHANGE_FLAG, true).apply();

      if (key.equals("disable_seasonal_effect")) {
        restartAppDelayed(requireContext());

      }

      // শুধু theme_preference হলে
      else if (key.equals("theme_preference") || key.equals("app_theme_preference")) {

        requireActivity().recreate();
      }
    }

    private void restartAppDelayed(Context context) {
      new android.os.Handler(android.os.Looper.getMainLooper())
          .postDelayed(
              () -> {
                Intent intent = new Intent(context, SettingsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);

                System.exit(0);
              },
              400);
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {

      String key = preference.getKey();
      Context ctx = getContext();

      switch (key) {
        case KEY_DEVELOPER:
          // Developer Telegram
          openUrl(ctx, "https://t.me/md_shamim12");
          return true;

        case KEY_NEWS:
          showNewsDialog(ctx);
          return true;

        case KEY_CHECK_UPDATES:
          // OTA update checker
          OTAUpdateHelper.hookPreference(ctx);
          return true;

        case KEY_PRIVACY:
          openUrl(
              ctx,
              "https://github.com/ShafiqulIslamShamim/Camera-Info/blob/main/PrivacyPolicy.txt");
          return true;

        case KEY_RATE_IT:
          openUrl(ctx, "https://play.google.com/store/apps/details?id=com.shamim.camerainfo");
          return true;

        case KEY_MORE_APPS:
          openUrl(
              ctx, "https://play.google.com/store/search?q=pub:Shafiqul%20Islam%20Shamim&c=apps");
          return true;

        case KEY_FEEDBACK:
          openEmail(ctx, "shafiqulislamshamimofficial@gmail.com");
          return true;
      }

      return super.onPreferenceTreeClick(preference);
    }

    // -----------------------
    // 🔗 Utility Methods
    // -----------------------

    private void openUrl(Context context, String url) {
      Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
      context.startActivity(intent);
    }

    public static void openEmail(@NonNull Context context, @NonNull String toEmail) {

      String appName = "Unknown App";
      String versionName = "unknown";
      int versionCode = -1;

      try {
        PackageManager pm = context.getPackageManager();
        PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);

        appName =
            pm.getApplicationLabel(pm.getApplicationInfo(context.getPackageName(), 0)).toString();

        versionName = pi.versionName != null ? versionName : "unknown";
        versionCode = (int) PackageInfoCompat.getLongVersionCode(pi);

      } catch (Exception ignored) {
      }

      String subject = "Feedback - " + appName + " v" + versionName + " (Code " + versionCode + ")";

      Intent intent = new Intent(Intent.ACTION_SENDTO);
      intent.setData(Uri.parse("mailto:")); // <-- MUST be plain mailto
      intent.putExtra(Intent.EXTRA_EMAIL, new String[] {toEmail});
      intent.putExtra(Intent.EXTRA_SUBJECT, subject);

      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

      context.startActivity(Intent.createChooser(intent, "Send Feedback"));
    }

    private void showNewsDialog(Context ctx) {

      // Items
      String[] titles = {"Facebook Page", "GitHub Repository"};
      int[] icons = {R.drawable.facebook, R.drawable.github};

      // RecyclerView Adapter
      NewsAdapter adapter =
          new NewsAdapter(
              titles,
              icons,
              pos -> {
                if (pos == 0) {
                  // Facebook
                  openUrl(ctx, "https://www.facebook.com/share/18wbmDDERe/");
                } else if (pos == 1) {
                  // GitHub
                  openUrl(ctx, "https://github.com/ShafiqulIslamShamim/");
                }
              });

      // RecyclerView Layout
      RecyclerView recyclerView = new RecyclerView(ctx);
      recyclerView.setLayoutManager(new LinearLayoutManager(ctx));
      recyclerView.setAdapter(adapter);
      recyclerView.setPadding(30, 30, 30, 30);

      // Material Dialog
      new MaterialAlertDialogBuilder(ctx)
          .setTitle("News & Updates")
          .setView(recyclerView)
          .setPositiveButton("Close", null)
          .show();
    }
  }
}
