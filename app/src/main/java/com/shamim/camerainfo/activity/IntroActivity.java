package com.shamim.camerainfo.activity;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import androidx.core.widget.NestedScrollView;
import com.airbnb.lottie.LottieAnimationView;
import com.shamim.camerainfo.R;

public class IntroActivity extends BaseActivity {

  private ValueAnimator scrollAnimator;
  private boolean userInteracted = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_intro);

    LottieAnimationView animationView = findViewById(R.id.introLottie);
    animationView.setScaleX(1.3f);
    animationView.setScaleY(1.3f);

    NestedScrollView scrollView = findViewById(R.id.introScroll);
    View getStartedBtn = findViewById(R.id.getStartedBtn);

    // Detect any user interaction → disable auto-scroll forever
    scrollView.setOnTouchListener(
        (v, event) -> {
          if (event.getAction() == MotionEvent.ACTION_DOWN) {
            userInteracted = true;
            stopAutoScroll();
          }
          return false; // allow normal scrolling
        });

    // Wait until layout is measured
    scrollView.post(
        () -> {
          if (shouldAutoScroll(scrollView) && !userInteracted) {
            startAutoScroll(scrollView, getStartedBtn);
          }
        });

    // Button click
    getStartedBtn.setOnClickListener(
        v -> {
          SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
          prefs.edit().putBoolean("intro_shown", true).apply();
          startActivity(new Intent(this, MainActivity.class));
          finish();
        });
  }

  /** Auto-scroll only if content is taller than screen */
  private boolean shouldAutoScroll(NestedScrollView scrollView) {
    View content = scrollView.getChildAt(0);
    return content != null && content.getHeight() > scrollView.getHeight();
  }

  private void startAutoScroll(NestedScrollView scrollView, View target) {
    int startY = scrollView.getScrollY();
    int endY = target.getTop();

    scrollAnimator = ValueAnimator.ofInt(startY, endY);
    scrollAnimator.setDuration(6000); // 6 seconds
    scrollAnimator.addUpdateListener(
        animation -> {
          if (!userInteracted) {
            scrollView.scrollTo(0, (int) animation.getAnimatedValue());
          } else {
            stopAutoScroll();
          }
        });
    scrollAnimator.start();
  }

  private void stopAutoScroll() {
    if (scrollAnimator != null && scrollAnimator.isRunning()) {
      scrollAnimator.cancel();
    }
  }
}
