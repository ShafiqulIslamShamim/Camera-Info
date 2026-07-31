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
import android.util.AttributeSet;
import androidx.preference.SwitchPreferenceCompat;
import com.shamim.camerainfo.R;
import com.shamim.camerainfo.activity.*;
import com.shamim.camerainfo.c2api_key.*;
import com.shamim.camerainfo.exception_catcher.*;
import com.shamim.camerainfo.recycle_view.*;
import com.shamim.camerainfo.update_checker.*;
import com.shamim.camerainfo.util.*;

public class StringSwitchPreference extends SwitchPreferenceCompat {

  /**
   * Constructs a StringSwitchPreference, applying a custom Material 3 switch widget layout.
   *
   * @param context The Context to use.
   * @param attrs The XML attribute set.
   */
  public StringSwitchPreference(Context context, AttributeSet attrs) {
    super(context, attrs);

    // Use Material3 switch widget
    setWidgetLayoutResource(R.layout.preference_switch_material3);
  }

  /** Persists a boolean as a string representation ("1" or "0"). */
  @Override
  protected boolean persistBoolean(boolean value) {
    return persistString(value ? "1" : "0");
  }

  /** Reads and parses a persisted string value back into a boolean. */
  @Override
  public boolean getPersistedBoolean(boolean defaultReturnValue) {
    String stringValue = getPersistedString(defaultReturnValue ? "1" : "0");
    return "1".equals(stringValue);
  }
}
