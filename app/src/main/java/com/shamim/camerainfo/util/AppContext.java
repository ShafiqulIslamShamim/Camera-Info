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

import android.content.Context;
import com.shamim.camerainfo.activity.*;
import com.shamim.camerainfo.c2api_key.*;
import com.shamim.camerainfo.exception_catcher.*;
import com.shamim.camerainfo.preference.*;
import com.shamim.camerainfo.recycle_view.*;
import com.shamim.camerainfo.update_checker.*;

public class AppContext {
  private static Context appContext;

  /**
   * Initializes the global application context if it is not already initialized.
   *
   * @param context The context to initialize the application context with.
   */
  public static void init(Context context) {
    if (appContext == null) {
      appContext = context.getApplicationContext();
    }
  }

  /**
   * Retrieves the globally stored application context.
   *
   * @return The application context.
   * @throws IllegalStateException If the context has not been initialized.
   */
  public static Context get() {
    if (appContext == null) {
      throw new IllegalStateException("AppContext not initialized! Call init() first.");
    }
    return appContext;
  }
}
