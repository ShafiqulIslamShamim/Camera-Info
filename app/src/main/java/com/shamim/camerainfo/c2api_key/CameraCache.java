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
package com.shamim.camerainfo.c2api_key;

import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import com.shamim.camerainfo.activity.*;
import com.shamim.camerainfo.exception_catcher.*;
import com.shamim.camerainfo.preference.*;
import com.shamim.camerainfo.recycle_view.*;
import com.shamim.camerainfo.update_checker.*;
import com.shamim.camerainfo.util.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CameraCache {
  private static final Map<String, CameraCharacteristics> cache = new HashMap<>();

  /**
   * Caches CameraCharacteristics dynamically for all verified camera IDs to optimize reflection
   * performance.
   *
   * @param cm System CameraManager.
   * @param validCameraIds List of valid camera IDs.
   */
  public static void loadAll(CameraManager cm, List<String> validCameraIds) {
    cache.clear();

    for (String id : validCameraIds) {
      try {
        CameraCharacteristics c = cm.getCameraCharacteristics(id);
        cache.put(id, c);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Retrieves the cached CameraCharacteristics for a given camera ID.
   *
   * @param cameraId Numeric target camera ID.
   * @return Cached CameraCharacteristics, or null if not found.
   */
  public static CameraCharacteristics get(String cameraId) {
    return cache.get(cameraId);
  }

  /**
   * Collects all camera IDs registered within the current cache instance.
   *
   * @return Set containing cached camera ID strings.
   */
  public static Set<String> getAllIds() {
    return cache.keySet();
  }
}
