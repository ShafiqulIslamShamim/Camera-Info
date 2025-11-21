package com.shamim.camerainfo;

import android.app.Application;
import android.content.Context;

public class CameraInfoApp extends Application {
  private static Context appContext;

  @Override
  public void onCreate() {
    super.onCreate();
    CrashHandler.getInstance().registerGlobal(this);
    AppContext.init(this);
  }
}
