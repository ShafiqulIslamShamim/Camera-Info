package com.shamim.camerainfo.util;

import android.content.Context;
import android.graphics.*;
import android.os.Build;
import android.view.*;
import android.view.WindowManager;
import com.shamim.camerainfo.activity.*;
import com.shamim.camerainfo.c2api_key.*;
import com.shamim.camerainfo.exception_catcher.*;
import com.shamim.camerainfo.preference.*;
import com.shamim.camerainfo.recycle_view.*;
import com.shamim.camerainfo.update_checker.*;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeviceInfo {

  private static Context appContext;

  public static void init(Context context) {
    appContext = context.getApplicationContext();
  }

  private static Context getAppContext() {
    if (appContext == null) {
      throw new IllegalStateException("DeviceInfo not initialized. Call init(context) first.");
    }
    return appContext;
  }

  public static String getBuildInfo() {
    return BuildPropHelper.getBuildPropInfo("android.os.Build");
  }

  public static String getVersionInfo() {
    return BuildPropHelper.getBuildPropInfo("android.os.Build$VERSION");
  }

  public static String getResolutionString() {
    WindowManager windowManager =
        (WindowManager) getAppContext().getSystemService(Context.WINDOW_SERVICE);

    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
      // Modern approach for API 30+
      android.view.WindowMetrics windowMetrics = windowManager.getCurrentWindowMetrics();
      android.graphics.Rect bounds = windowMetrics.getBounds();
      return bounds.width() + "x" + bounds.height();
    } else {
      // Legacy approach for older devices
      return getLegacyResolutionString(windowManager);
    }
  }

  @SuppressWarnings("deprecation")
  public static String getLegacyResolutionString(WindowManager windowManager) {

    Display defaultDisplay = windowManager.getDefaultDisplay();
    Point point = new Point();
    defaultDisplay.getRealSize(point);
    return point.x + "x" + point.y;
  }

  public static String getSystemLanguage() {
    Context context = getAppContext();
    android.content.res.Configuration config = context.getResources().getConfiguration();

    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
      // Modern approach for API 24+
      return config.getLocales().get(0).getLanguage();
    } else {
      // Legacy approach
      @SuppressWarnings("deprecation")
      String systemLan = config.locale.getLanguage();
      return systemLan;
    }
  }

  public static String getTotalRAM() {
    DecimalFormat decimalFormat = new DecimalFormat("#.##");
    try {
      RandomAccessFile randomAccessFile = new RandomAccessFile("/proc/meminfo", "r");
      Matcher matcher = Pattern.compile("(\\d+)").matcher(randomAccessFile.readLine());
      String str = "";
      while (matcher.find()) {
        str = matcher.group(1);
      }
      randomAccessFile.close();
      double parseDouble = Double.parseDouble(str);
      double d2 = parseDouble / 1024.0d;
      double d3 = parseDouble / 1048576.0d;
      double d4 = parseDouble / 1.073741824E9d;
      return d4 > 1
          ? decimalFormat.format(d4).concat(" TB")
          : d3 > 1
              ? decimalFormat.format(d3).concat(" GB")
              : d2 > 1
                  ? decimalFormat.format(d2).concat(" MB")
                  : decimalFormat.format(parseDouble).concat(" KB");
    } catch (IOException e) {
      e.printStackTrace();
      return "";
    }
  }

  public static String getPackageName() {
    return getAppContext().getPackageName();
  }

  public static String getTimeStamp(int i, int i2) {
    return DateFormat.getDateTimeInstance(i, i2, Locale.ROOT).format(new Date());
  }

  @Override
  public String toString() {
    return "Resolution : "
        + getResolutionString()
        + "\nSystem Language : "
        + getSystemLanguage()
        + "\nTotal RAM : "
        + getTotalRAM()
        + "\n"
        + getBuildInfo()
        + getVersionInfo()
        + "Package Name : "
        + getPackageName()
        + "\nCurrent Time : "
        + getTimeStamp(0, 0)
        + "\n\n============================\n";
  }

  public static String getShortDeviceInfo() {
    return "Device : "
        + Build.BRAND
        + " "
        + Build.MODEL
        + " ("
        + Build.DEVICE
        + ")\n"
        + "Manufacturer : "
        + Build.MANUFACTURER
        + "\n"
        + "Android : "
        + Build.VERSION.RELEASE
        + "\n"
        + "Fingerprint : "
        + Build.FINGERPRINT
        + "\n"
        + "\n============================\n";
  }

  public static String getShortDeviceInfoMassage() {
    return "\n============================\n\n"
        + "Device : "
        + Build.BRAND
        + " "
        + Build.MODEL
        + " ("
        + Build.DEVICE
        + ")\n"
        + "Manufacturer : "
        + Build.MANUFACTURER
        + "\n"
        + "Android : "
        + Build.VERSION.RELEASE
        + "\n"
        + "Fingerprint : "
        + Build.FINGERPRINT
        + "\n"
        + "\n============================";
  }

  public static String getDeviceInfoText(Context context) {
    int logmode = SharedPrefValues.getValue("pref_log_mode", 0);

    if (logmode == 0) {
      return getShortDeviceInfo();
    } else {
      DeviceInfo.init(context);
      DeviceInfo deviceInfo = new DeviceInfo();
      return deviceInfo.toString();
    }
  }
}
