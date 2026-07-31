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
package com.shamim.camerainfo;

import android.app.Application;
import android.content.Context;
import com.shamim.camerainfo.activity.*;
import com.shamim.camerainfo.c2api_key.*;
import com.shamim.camerainfo.exception_catcher.*;
import com.shamim.camerainfo.preference.*;
import com.shamim.camerainfo.recycle_view.*;
import com.shamim.camerainfo.update_checker.*;
import com.shamim.camerainfo.util.*;
import io.github.mohammedbaqernull.seasonal.SeasonalEffects;

public class CameraInfoApp extends Application {
  private static Context appContext;

  /**
   * Called when the application is starting, before any activity, service, or receiver objects have
   * been created. Initializes application-wide context and configures seasonal Christmas/winter
   * visual effects if enabled and currently winter.
   */
  @Override
  public void onCreate() {
    super.onCreate();
    AppContext.init(this);

    boolean seasonalEffect = SharedPrefValues.getValue("disable_seasonal_effect", false);

    if (seasonalEffect != true && GlobalWinterSystem.isWinterNow()) {
      SeasonalEffects.INSTANCE.init(this);
      SeasonalEffects.INSTANCE.enableChristmas();
      SeasonalEffects.INSTANCE.setSnowflakeCount(20);
    }
  }
}
