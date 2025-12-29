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

public class CameraInfoApp extends Application {
  private static Context appContext;

  @Override
  public void onCreate() {
    super.onCreate();
    CrashHandler.getInstance().registerGlobal(this);
    AppContext.init(this);
  }
}
