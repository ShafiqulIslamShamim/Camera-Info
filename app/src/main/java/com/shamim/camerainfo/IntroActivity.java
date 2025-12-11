package com.shamim.camerainfo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import com.airbnb.lottie.LottieAnimationView;

public class IntroActivity extends BaseActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_intro);

    LottieAnimationView animationView = findViewById(R.id.introLottie);
    animationView.setScaleX(1.3f);
    animationView.setScaleY(1.3f);

    /*

    // ------- 1. Collect multiple dynamic Material You colors -------
    List<Integer> colorList = new ArrayList<>();
    colorList.add(MaterialColors.getColor(this, androidx.appcompat.R.attr.colorPrimary, Color.BLACK));
    colorList.add(MaterialColors.getColor(this, com.google.android.material.R.attr.colorSecondary, Color.BLACK));
    colorList.add(MaterialColors.getColor(this, com.google.android.material.R.attr.colorTertiary, Color.BLACK));
    colorList.add(MaterialColors.getColor(this, com.google.android.material.R.attr.colorPrimaryContainer, Color.BLACK));
    colorList.add(MaterialColors.getColor(this, com.google.android.material.R.attr.colorSecondaryContainer, Color.BLACK));
    colorList.add(MaterialColors.getColor(this, com.google.android.material.R.attr.colorTertiaryContainer, Color.BLACK));
    colorList.add(MaterialColors.getColor(this, com.google.android.material.R.attr.colorOutline, Color.BLACK));

    // ------- 2. Apply dynamic tint to DIFFERENT layers -------
    animationView.addLottieOnCompositionLoadedListener(composition -> {

        // Resolve ALL keypaths inside animation
        List<KeyPath> allKeyPaths = animationView.resolveKeyPath(new KeyPath("**"));

        int colorIndex = 0;

        for (KeyPath kp : allKeyPaths) {

            // Only change fill/stroke/color related layers
            String name = kp.toString().toLowerCase();
            if (!(name.contains("fill") || name.contains("stroke") || name.contains("color"))) {
                continue;
            }

            int selectedColor = colorList.get(colorIndex % colorList.size());

            ColorFilter filter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                    selectedColor,
                    BlendModeCompat.SRC_ATOP
            );

            animationView.addValueCallback(
                    kp,
                    LottieProperty.COLOR_FILTER,
                    new LottieValueCallback<>(filter)
            );

            colorIndex++;
        }
    });

    */

    // ------- Button -------
    findViewById(R.id.getStartedBtn)
        .setOnClickListener(
            v -> {
              SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
              prefs.edit().putBoolean("intro_shown", true).apply();
              startActivity(new Intent(this, MainActivity.class));
              finish();
            });
  }
}
