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

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import com.shamim.camerainfo.activity.*;
import com.shamim.camerainfo.c2api_key.*;
import com.shamim.camerainfo.exception_catcher.*;
import com.shamim.camerainfo.preference.*;
import com.shamim.camerainfo.recycle_view.*;
import com.shamim.camerainfo.update_checker.*;

public class ColoredTextHelper {

  /**
   * Generates a SpannableStringBuilder where keys, separators, and values are colored based on
   * pre-defined color resources. Splits the given text by lines and parses for separators like '='
   * or ':'.
   *
   * @param fullText The source text containing key-value pairs.
   * @param keyColor The color to apply to keys.
   * @param separatorColor The color to apply to separators.
   * @param valueColor The color to apply to values.
   * @return A styled SpannableStringBuilder.
   */
  public static SpannableStringBuilder setColoredText(
      String fullText, int keyColor, int separatorColor, int valueColor) {
    SpannableStringBuilder spannable = new SpannableStringBuilder();

    // Split with limit to preserve trailing newlines
    String[] lines = fullText.split("\n", -1);

    for (int i = 0; i < lines.length; i++) {
      String line = lines[i];
      boolean isLastLine = i == lines.length - 1;

      if (line.isEmpty()) {
        if (!isLastLine) {
          spannable.append("\n");
        }
        continue;
      }

      // Find separator index in one pass
      int separatorIndex = -1;
      char separatorChar = '\0';
      for (int j = 0; j < line.length(); j++) {
        char c = line.charAt(j);
        if (c == '=' || c == ':') {
          separatorIndex = j;
          separatorChar = c;
          break;
        }
      }

      if (separatorIndex != -1) {
        // Key, separator, and value
        String key = line.substring(0, separatorIndex);
        String separator = String.valueOf(separatorChar);
        String value = line.substring(separatorIndex + 1);

        int start = spannable.length();
        spannable.append(key);
        spannable.setSpan(
            new ForegroundColorSpan(keyColor),
            start,
            start + key.length(),
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        start = spannable.length();
        spannable.append(separator);
        spannable.setSpan(
            new ForegroundColorSpan(separatorColor),
            start,
            start + 1,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        start = spannable.length();
        spannable.append(value);
        spannable.setSpan(
            new ForegroundColorSpan(valueColor),
            start,
            start + value.length(),
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
      } else {
        spannable.append(line);
      }

      if (!isLastLine) {
        spannable.append("\n");
      }
    }
    return spannable;
  }
}
