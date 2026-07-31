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
package com.shamim.camerainfo.activity;

import android.content.*;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.activity.*;
import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import com.shamim.camerainfo.R;
import com.shamim.camerainfo.c2api_key.*;
import com.shamim.camerainfo.exception_catcher.*;
import com.shamim.camerainfo.preference.*;
import com.shamim.camerainfo.recycle_view.*;
import com.shamim.camerainfo.update_checker.*;
import com.shamim.camerainfo.util.*;

public abstract class BaseActivity extends AppCompatActivity {

  protected String lastAppliedThemePref;
  protected String lastAppliedAppThemePref;
  protected boolean lastAppliedAmoled;

  /**
   * Initializes theme styling, activates modern edge-to-edge support, and restores activity state
   * bundles.
   *
   * @param savedInstanceState If the activity is being re-initialized, this contains previous saved
   *     state info.
   */
  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {

    applyLocalTheme();
    // Modern Android edge-to-edge
    EdgeToEdge.enable(this);

    super.onCreate(savedInstanceState);
  }

  /**
   * Validates active theme state changes and triggers an interface recreation flow when
   * configuration shifts.
   */
  @Override
  protected void onResume() {
    super.onResume();

    String currentThemePref = SharedPrefValues.getValue("theme_preference", "0");
    String currentAppThemePref = SharedPrefValues.getValue("app_theme_preference", "0");
    boolean currentAmoled = SharedPrefValues.getValue("amoled_black_mode", false);

    if (lastAppliedThemePref != null
        && (!lastAppliedThemePref.equals(currentThemePref)
            || !lastAppliedAppThemePref.equals(currentAppThemePref)
            || lastAppliedAmoled != currentAmoled)) {

      lastAppliedThemePref = currentThemePref;
      lastAppliedAppThemePref = currentAppThemePref;
      lastAppliedAmoled = currentAmoled;

      recreate();
    }
  }

  /**
   * Safe wrapper around system content set flows executing initial window setups.
   *
   * @param layoutResID XML layout resource identifier.
   */
  @Override
  public void setContentView(int layoutResID) {
    super.setContentView(layoutResID);
    afterContentSet();
  }

  /**
   * Safe wrapper around system content set views executing initial window setups.
   *
   * @param view Target inflated view layout.
   */
  @Override
  public void setContentView(View view) {
    super.setContentView(view);
    afterContentSet();
  }

  /** Internal hook post layout inflating to apply required edge padding and bar styling. */
  private void afterContentSet() {
    setupEdgeToEdgePadding();
    getWindow().getDecorView().post(this::applySystemBarAppearance);
  }

  // THEME LOGIC (Light/Dark + AppTheme/AppThemeDefault)
  /**
   * Evaluates active user preferences to bind appropriate Material Light/Dark style overlays
   * locally.
   */
  protected void applyLocalTheme() {
    boolean isLight = isLightThemeActive();
    String appThemePref = SharedPrefValues.getValue("app_theme_preference", "0");
    boolean amoledEnabled = SharedPrefValues.getValue("amoled_black_mode", false);

    lastAppliedThemePref = SharedPrefValues.getValue("theme_preference", "0");
    lastAppliedAppThemePref = appThemePref;
    lastAppliedAmoled = amoledEnabled;

    final int themeRes;

    if (appThemePref.equals("0")) {
      themeRes = isLight ? R.style.AppThemeLight : R.style.AppThemeDark;
    } else if (appThemePref.equals("1")) {
      themeRes = isLight ? R.style.AppThemeEmeraldLight : R.style.AppThemeEmeraldDark;
    } else if (appThemePref.equals("2")) {
      themeRes = isLight ? R.style.AppThemeBlossomLight : R.style.AppThemeBlossomDark;
    } else if (appThemePref.equals("3")) {
      themeRes = isLight ? R.style.AppThemeOceanLight : R.style.AppThemeOceanDark;
    } else if (appThemePref.equals("4")) {
      themeRes = isLight ? R.style.AppThemeAmberLight : R.style.AppThemeAmberDark;
    } else if (appThemePref.equals("5")) {
      themeRes = isLight ? R.style.AppThemeCoralLight : R.style.AppThemeCoralDark;
    } else {
      themeRes = isLight ? R.style.AppThemeLight : R.style.AppThemeDark;
    }

    setTheme(themeRes);

    if (!isLight && amoledEnabled) {
      getTheme().applyStyle(R.style.AmoledOverlay, true);
    }
  }

  // Status + Navigation bar icon color (Light/Dark)
  /** Updates status bar and navigation bar visual appearance. */
  protected void applySystemBarAppearance() {
    boolean isLight = isLightThemeActive();

    View decorView = getWindow().getDecorView();
    WindowInsetsControllerCompat controller =
        new WindowInsetsControllerCompat(getWindow(), decorView);

    controller.setAppearanceLightStatusBars(isLight);
    controller.setAppearanceLightNavigationBars(isLight);
  }

  // Edge-to-edge safe padding
  /**
   * Binds custom window insets listener to safely padded root frames avoiding notch/camera cutouts.
   */
  private void setupEdgeToEdgePadding() {
    View root = findViewById(android.R.id.content);
    if (root == null) return;

    ViewCompat.setOnApplyWindowInsetsListener(
        root,
        (v, insets) -> {
          Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
          v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
          return insets;
        });
  }

  // Detect whether current UI should be "light" appearance
  /**
   * Checks whether light-theme styles should be evaluated based on system conditions and overrides.
   *
   * @return true if light appearance is active, false otherwise.
   */
  protected boolean isLightThemeActive() {

    String themePref = SharedPrefValues.getValue("theme_preference", "0");

    switch (themePref) {
      case "2": // Dark forced
        return false;

      case "3": // Light forced
        return true;

      default: // Follow system
        int mode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        return mode != Configuration.UI_MODE_NIGHT_YES;
    }
  }

  /**
   * Displays long Toast notifications safely across standard UI threads.
   *
   * @param message String notification body.
   */
  protected void showToast(String message) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
  }
}
