package com.shamim.camerainfo.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import com.airbnb.lottie.LottieAnimationView;
import com.shamim.camerainfo.R;

public class IntroActivity extends BaseActivity {

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
