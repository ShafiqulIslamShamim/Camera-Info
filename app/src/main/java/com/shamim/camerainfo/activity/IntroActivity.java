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

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import com.airbnb.lottie.LottieAnimationView;
import com.shamim.camerainfo.R;

public class IntroActivity extends BaseActivity {

  /**
   * Initializes the intro screen layout, configures custom scale values for the Lottie asset, and
   * handles get started button clicks.
   *
   * @param savedInstanceState Saved state bundle.
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_intro);

    LottieAnimationView animationView = findViewById(R.id.introLottie);
    animationView.setScaleX(1.3f);
    animationView.setScaleY(1.3f);

    View getStartedBtn = findViewById(R.id.getStartedBtn);

    // Button click
    getStartedBtn.setOnClickListener(
        v -> {
          SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
          prefs.edit().putBoolean("intro_shown", true).apply();
          startActivity(new Intent(this, MainActivity.class));
          finish();
        });
  }
}
