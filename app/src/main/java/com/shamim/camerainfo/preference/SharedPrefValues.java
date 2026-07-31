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
package com.shamim.camerainfo.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import androidx.preference.PreferenceManager;
import com.shamim.camerainfo.activity.*;
import com.shamim.camerainfo.c2api_key.*;
import com.shamim.camerainfo.exception_catcher.*;
import com.shamim.camerainfo.recycle_view.*;
import com.shamim.camerainfo.update_checker.*;
import com.shamim.camerainfo.util.*;

public class SharedPrefValues {

  /**
   * Retrieves the default SharedPreferences instance.
   *
   * @return Default SharedPreferences associated with the AppContext.
   */
  private static SharedPreferences getSharedPreferences() {

    Context context = AppContext.get();
    return PreferenceManager.getDefaultSharedPreferences(context);
  }

  /**
   * Safe getter to retrieve a string value from preferences.
   *
   * @param key The preference key.
   * @param defaultValue The default fallback value.
   * @return The string preference value or the default.
   */
  public static String getValue(String key, String defaultValue) {
    SharedPreferences prefs = getSharedPreferences();
    if (prefs != null && prefs.contains(key)) {
      String value = prefs.getString(key, null);
      return !TextUtils.isEmpty(value) ? value : defaultValue;
    }
    return defaultValue;
  }

  /**
   * Safe getter to retrieve an integer value from preferences.
   *
   * @param key The preference key.
   * @param defaultValue The default fallback value.
   * @return The parsed int preference value or the default.
   */
  public static int getValue(String key, int defaultValue) {
    String value = getValue(key, String.valueOf(defaultValue));
    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  }

  /**
   * Safe getter to retrieve a float value from preferences.
   *
   * @param key The preference key.
   * @param defaultValue The default fallback value.
   * @return The parsed float preference value or the default.
   */
  public static float getValue(String key, float defaultValue) {
    String value = getValue(key, String.valueOf(defaultValue));
    try {
      return Float.parseFloat(value);
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  }

  /**
   * Safe getter to retrieve a double value from preferences.
   *
   * @param key The preference key.
   * @param defaultValue The default fallback value.
   * @return The parsed double preference value or the default.
   */
  public static double getValue(String key, double defaultValue) {
    String value = getValue(key, String.valueOf(defaultValue));
    try {
      return Double.parseDouble(value);
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  }

  /**
   * Safe getter to retrieve a boolean value from preferences (where they may be stored as "1" or
   * "0").
   *
   * @param key The preference key.
   * @param defaultValue The default fallback value.
   * @return The parsed boolean preference value or the default.
   */
  public static boolean getValue(String key, boolean defaultValue) {
    String value = getValue(key, defaultValue ? "1" : "0");

    try {
      return Integer.parseInt(value) != 0;
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  }

  /**
   * Flexibly parses string representations ("true", "1", etc.) into a boolean.
   *
   * @param value The string to parse.
   * @return true if matches true/1, false otherwise.
   */
  public static boolean parseFlexibleBoolean(String value) {
    if (value == null) return false;
    value = value.trim().toLowerCase();
    return value.equals("true") || value.equals("1");
  }

  /**
   * Converts a boolean value to integer representation (1 for true, 0 for false).
   *
   * @param value The boolean to convert.
   * @return The equivalent integer.
   */
  public static int booleanToInt(boolean value) {
    return value ? 1 : 0;
  }

  /**
   * Puts/commits a string value into preferences immediately.
   *
   * @param key The key to save under.
   * @param value The string value to commit.
   */
  public static void putValue(String key, String value) {
    SharedPreferences prefs = getSharedPreferences();
    SharedPreferences.Editor editor = prefs.edit();
    editor.putString(key, value);
    editor.commit();
  }

  /**
   * Saves a default value to preferences only if that key is not already present.
   *
   * @param key The key to check and save.
   * @param defaultValue The default value to set if absent.
   */
  public static void putValueIfAbsent(String key, String defaultValue) {
    SharedPreferences prefs = getSharedPreferences();
    if (!prefs.contains(key)) {
      SharedPreferences.Editor editor = prefs.edit();
      editor.putString(key, defaultValue);
      editor.commit();
    }
  }
}
