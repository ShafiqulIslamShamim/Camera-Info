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

  /**
   * Initializes the application context for querying device information safely.
   *
   * @param context The Context to initialize with.
   */
  public static void init(Context context) {
    appContext = context.getApplicationContext();
  }

  /**
   * Safe getter for the application context, throwing an exception if not initialized.
   *
   * @return The stored Application Context.
   */
  private static Context getAppContext() {
    if (appContext == null) {
      throw new IllegalStateException("DeviceInfo not initialized. Call init(context) first.");
    }
    return appContext;
  }

  /**
   * Obtains build-specific info properties of the OS using BuildPropHelper.
   *
   * @return The raw build property information.
   */
  public static String getBuildInfo() {
    return BuildPropHelper.getBuildPropInfo("android.os.Build");
  }

  /**
   * Obtains build version info properties of the OS using BuildPropHelper.
   *
   * @return The raw build version property information.
   */
  public static String getVersionInfo() {
    return BuildPropHelper.getBuildPropInfo("android.os.Build$VERSION");
  }

  /**
   * Computes the screen resolution formatted as width x height. Uses modern APIs for newer Android
   * versions and falls back to legacy methods for older versions.
   *
   * @return The formatted resolution string.
   */
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

  /**
   * Computes the screen resolution using legacy Display APIs.
   *
   * @param windowManager The system WindowManager instance.
   * @return The formatted resolution string.
   */
  @SuppressWarnings("deprecation")
  public static String getLegacyResolutionString(WindowManager windowManager) {

    Display defaultDisplay = windowManager.getDefaultDisplay();
    Point point = new Point();
    defaultDisplay.getRealSize(point);
    return point.x + "x" + point.y;
  }

  /**
   * Detects the system language currently in use by the system.
   *
   * @return The current system language code.
   */
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

  /**
   * Extracts and formats the total system RAM from the system /proc/meminfo.
   *
   * @return The human-readable RAM size (e.g., in GB, MB).
   */
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

  /**
   * Retrieves the package name of the application.
   *
   * @return The current application package name.
   */
  public static String getPackageName() {
    return getAppContext().getPackageName();
  }

  /**
   * Generates a date/time stamp string using the default locale and system date.
   *
   * @param i The date style.
   * @param i2 The time style.
   * @return Formatted date-time stamp.
   */
  public static String getTimeStamp(int i, int i2) {
    return DateFormat.getDateTimeInstance(i, i2, Locale.ROOT).format(new Date());
  }

  /**
   * Returns a complete, detailed description of device properties and system info.
   *
   * @return A verbose string describing the system.
   */
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

  /**
   * Gathers simplified, high-level device branding and OS info.
   *
   * @return A clean, short device info string.
   */
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

  /**
   * Gathers simplified device branding formatted nicely with a top and bottom boundary header.
   *
   * @return A clean, bounded short device info string.
   */
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

  /**
   * Direct helper method that resolves the current user's preference logging mode and returns
   * either verbose or short device details accordingly.
   *
   * @param context The context used to query preferences and read device metrics.
   * @return The formatted device info.
   */
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
