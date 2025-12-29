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

  public static void init(Context context) {
    if (appContext == null) {
      appContext = context.getApplicationContext();
    }
  }

  public static Context get() {
    if (appContext == null) {
      throw new IllegalStateException("AppContext not initialized! Call init() first.");
    }
    return appContext;
  }
}
