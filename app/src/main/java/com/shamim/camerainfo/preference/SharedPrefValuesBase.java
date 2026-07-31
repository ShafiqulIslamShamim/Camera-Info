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
import androidx.annotation.NonNull;
import com.shamim.camerainfo.activity.*;
import com.shamim.camerainfo.c2api_key.*;
import com.shamim.camerainfo.exception_catcher.*;
import com.shamim.camerainfo.recycle_view.*;
import com.shamim.camerainfo.update_checker.*;
import com.shamim.camerainfo.util.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Simple SharedPreferences helper with listener support. Initialize once in Application.onCreate():
 * SharedPrefValues.init(appContext);
 */
public final class SharedPrefValuesBase {

  private static final String PREF_NAME = "app_prefs_v1";
  private static SharedPreferences prefs;
  private static final Set<OnPrefChangeListener> listeners =
      Collections.synchronizedSet(new HashSet<>());

  /** Callback interface to notify observers when a preference value changes. */
  public interface OnPrefChangeListener {
    /**
     * Triggered when a preference value is changed.
     *
     * @param key The key of the updated preference.
     * @param newValue The new value stored under that key.
     */
    void onPrefChanged(@NonNull String key, @NonNull String newValue);
  }

  /**
   * Initializes the SharedPreferences instance using private mode.
   *
   * @param context The Context used to retrieve the shared preferences.
   */
  public static void init(@NonNull Context context) {
    if (prefs == null) {
      prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
  }

  /**
   * Retrieves a string preference value.
   *
   * @param key The preference key.
   * @param defaultValue The default fallback value.
   * @return The stored string preference value, or the default.
   */
  public static String getValue(@NonNull String key, @NonNull String defaultValue) {
    ensureInit();
    return prefs.getString(key, defaultValue);
  }

  /**
   * Sets/updates a string preference and notifies any registered listeners of the change.
   *
   * @param key The preference key.
   * @param value The value to write.
   */
  public static void setValue(@NonNull String key, @NonNull String value) {
    ensureInit();
    String old = prefs.getString(key, null);
    if (value.equals(old)) return; // nothing changed

    prefs.edit().putString(key, value).apply();

    // notify listeners
    synchronized (listeners) {
      for (OnPrefChangeListener l : listeners) {
        try {
          l.onPrefChanged(key, value);
        } catch (Exception ignore) {
          // defensive: a misbehaving listener won't crash the loop
        }
      }
    }
  }

  /**
   * Registers a listener to observe preference updates.
   *
   * @param l The preference change listener.
   */
  public static void addListener(@NonNull OnPrefChangeListener l) {
    listeners.add(l);
  }

  /**
   * Unregisters a listener to stop observing preference updates.
   *
   * @param l The preference change listener to remove.
   */
  public static void removeListener(@NonNull OnPrefChangeListener l) {
    listeners.remove(l);
  }

  /**
   * Asserts that init has been successfully executed prior to any preference read/write.
   *
   * @throws IllegalStateException If the preference instance has not been initialized.
   */
  private static void ensureInit() {
    if (prefs == null) {
      throw new IllegalStateException(
          "SharedPrefValues not initialized. Call SharedPrefValues.init(context) in"
              + " Application.onCreate()");
    }
  }
}
