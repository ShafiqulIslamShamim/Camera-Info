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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.ListPreference;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.shamim.camerainfo.activity.*;
import com.shamim.camerainfo.c2api_key.*;
import com.shamim.camerainfo.exception_catcher.*;
import com.shamim.camerainfo.recycle_view.*;
import com.shamim.camerainfo.update_checker.*;
import com.shamim.camerainfo.util.*;

public class MaterialListPreference extends ListPreference {
  private int mClickedDialogEntryIndex;

  /**
   * Constructs a MaterialListPreference with style resources.
   *
   * @param context The Context to use.
   * @param attrs The XML attributes associated.
   * @param defStyleAttr Default style attribute pointer.
   * @param defStyleRes Default style resource pointer.
   */
  public MaterialListPreference(
      @NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }

  /**
   * Constructs a MaterialListPreference with attributes.
   *
   * @param context The Context to use.
   * @param attrs The XML attributes associated.
   */
  public MaterialListPreference(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  /**
   * Constructs a MaterialListPreference.
   *
   * @param context The Context to use.
   */
  public MaterialListPreference(@NonNull Context context) {
    super(context);
  }

  /**
   * Displays a Material choice dialog upon clicking the preference, enabling Material 3 styled UI
   * support.
   */
  @Override
  protected void onClick() {
    // If no entries or not enabled/persisted, don't show dialog
    if (getEntries() == null || getEntryValues() == null || !isEnabled() || !isPersistent()) {
      return;
    }

    // Find the index of current value
    mClickedDialogEntryIndex = findIndexOfValue(getValue());

    // Create Material dialog
    MaterialAlertDialogBuilder builder =
        new MaterialAlertDialogBuilder(getContext())

            // .setTitle(getDialogTitle())
            .setCustomTitle(DialogUtils.createStyledDialogTitle(getContext(), getDialogTitle()))
            .setSingleChoiceItems(
                getEntries(),
                mClickedDialogEntryIndex,
                (dialog, which) -> {
                  mClickedDialogEntryIndex = which;
                  // Update value when item is clicked
                  if (callChangeListener(getEntryValues()[which].toString())) {
                    setValueIndex(which);
                  }
                  dialog.dismiss();
                })
            .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss());

    // Optional: Add positive button if needed
    // builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
    //     if (mClickedDialogEntryIndex >= 0 && getEntryValues() != null) {
    //         String value = getEntryValues()[mClickedDialogEntryIndex].toString();
    //         if (callChangeListener(value)) {
    //             setValue(value);
    //         }
    //     }
    // });

    builder.show();
  }
}
