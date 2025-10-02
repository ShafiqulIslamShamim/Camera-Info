package com.shamim.camerainfo;

import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CameraCache {
  private static final Map<String, CameraCharacteristics> cache = new HashMap<>();

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

  public static CameraCharacteristics get(String cameraId) {
    return cache.get(cameraId);
  }

  public static Set<String> getAllIds() {
    return cache.keySet();
  }
}
