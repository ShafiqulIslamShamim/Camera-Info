package com.shamim.camerainfo;

import android.app.Application;
import android.content.Context;

public class CamerainfoApp extends Application {
  private static Context appContext;

  @Override
  public void onCreate() {
    super.onCreate();
    AppContext.init(this);
  }
}
