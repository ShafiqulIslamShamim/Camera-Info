package com.shamim.camerainfo;

import android.content.Context;
import android.util.AttributeSet;
import androidx.preference.SwitchPreferenceCompat;

public class StringSwitchPreference extends SwitchPreferenceCompat {

  public StringSwitchPreference(Context context, AttributeSet attrs) {
    super(context, attrs);

    // Use Material3 switch widget
    setWidgetLayoutResource(R.layout.preference_switch_material3);
  }

  @Override
  protected boolean persistBoolean(boolean value) {
    return persistString(value ? "1" : "0");
  }

  @Override
  public boolean getPersistedBoolean(boolean defaultReturnValue) {
    String stringValue = getPersistedString(defaultReturnValue ? "1" : "0");
    return "1".equals(stringValue);
  }
}
