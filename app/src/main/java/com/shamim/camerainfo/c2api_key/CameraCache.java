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
