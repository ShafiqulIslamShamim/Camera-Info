package com.shamim.camerainfo;

import android.hardware.camera2.CameraCharacteristics;
import android.util.Log;
import android.util.SizeF;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class CameraLensClassifier {

  public enum LensType {
    MAIN,
    ULTRAWIDE,
    TELE,
    MACRO,
    DEPTH,
    LOGICAL,
    OTHER,
    LOGICAL_REPEATED;

    @Override
    public String toString() {
      switch (this) {
        case LOGICAL_REPEATED:
          return "LOGICAL & REPEATED";
        default:
          return name();
      }
    }
  }

  public static class LensResult {
    public LensType type;
    public float zoomFactor;

    public LensResult(LensType type, float zoomFactor) {
      this.type = type;
      this.zoomFactor = zoomFactor;
    }
  }

  static class LensInfo {
    String cameraId;
    int facing;
    float focalLength; // representative focal length (mm)
    float eqFocal; // 35mm equivalent (mm) — computed using diagonal
    float angleOfView; // diagonal AoV (degrees)
    boolean flashSupported;
    boolean hasAeModes;
    Float minFocusDist;
    LensType type = null;
    float zoomFactor = 1.0f;
  }

  // cache CameraCharacteristics for equality / logical detection
  private static final Map<String, CameraCharacteristics> cameraPropsMap = new HashMap<>();

  private static final Comparator<LensInfo> SORT_BY_AOV =
      Comparator.comparingDouble(i -> i.angleOfView);

  /** Main entry — returns one LensResult per cameraId in validCameraIds. */
  public static Map<String, LensResult> detectLensesAndReturnMap(List<String> validCameraIds) {
    Map<String, LensResult> lensMap = new HashMap<>();

    try {
      List<LensInfo> back = new ArrayList<>();
      List<LensInfo> front = new ArrayList<>();

      // Build one LensInfo per cameraId, preserve input order so first becomes MAIN
      for (String id : validCameraIds) {
        CameraCharacteristics cc = CameraCache.get(id);
        if (cc == null) continue;
        cameraPropsMap.put(id, cc);

        Integer facing = cc.get(CameraCharacteristics.LENS_FACING);
        float[] focalLengths = cc.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS);
        SizeF sensorSize = cc.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE);
        Boolean flashSupported = cc.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
        int[] aeModes = cc.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_MODES);
        Float minFocusDist = cc.get(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE);
        android.util.Size pixelArray = cc.get(CameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE);

        if (focalLengths == null
            || focalLengths.length == 0
            || sensorSize == null
            || pixelArray == null) continue;

        // Use the first focal length as representative (matches CameraIdentifier behavior)
        float repFocal = focalLengths[0];

        LensInfo info = new LensInfo();
        info.cameraId = id;
        info.facing = (facing != null) ? facing : -1;
        info.focalLength = repFocal;

        info.eqFocal = Camera2ApiKeysInfo.calculate35mmeqv(repFocal, sensorSize);

        // Diagonal Angle of View
        info.angleOfView =
            Camera2ApiKeysInfo.calculateAngleOfView(repFocal, sensorSize, pixelArray).floatValue();

        info.flashSupported = (flashSupported != null && flashSupported);
        info.hasAeModes = (aeModes != null && aeModes.length > 0);
        info.minFocusDist = minFocusDist;

        if (info.facing == CameraCharacteristics.LENS_FACING_BACK) back.add(info);
        else if (info.facing == CameraCharacteristics.LENS_FACING_FRONT) front.add(info);
      }

      // classify each side
      classifyAndComputeZoom(back, "Back");
      classifyAndComputeZoom(front, "Front");

      // produce result map (one entry per cameraId)
      for (LensInfo i : back) lensMap.put(i.cameraId, new LensResult(i.type, i.zoomFactor));
      for (LensInfo i : front) lensMap.put(i.cameraId, new LensResult(i.type, i.zoomFactor));

    } catch (Exception e) {
      e.printStackTrace();
    }

    return lensMap;
  }

  /**
   * Detect logical camera via API flag (physical camera ids), bit test on cameraId (parse as
   * integer), or equality of key CameraCharacteristics fields.
   */
  private static boolean isLogicalViaApi(String cameraId) {
    try {
      CameraCharacteristics cc = cameraPropsMap.get(cameraId);
      if (cc == null) return false;
      java.util.Set<String> physicalIds = cc.getPhysicalCameraIds();
      return (physicalIds != null && !physicalIds.isEmpty());
    } catch (Exception e) {
      return false;
    }
  }

  private static boolean bitCheck6(String cameraId) {
    try {
      int v = Integer.parseInt(cameraId);
      return ((v >> (6 - 1)) & 1) == 1;
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Robust equality check on a small set of CameraCharacteristics properties that commonly indicate
   * identical sensors. This avoids relying on CameraCharacteristics.equals() implementation.
   */
  private static boolean characteristicsEquivalent(
      CameraCharacteristics a, CameraCharacteristics b) {
    if (a == null || b == null) return false;

    // Facing
    Integer fa = a.get(CameraCharacteristics.LENS_FACING);
    Integer fb = b.get(CameraCharacteristics.LENS_FACING);
    if (fa == null ? fb != null : !fa.equals(fb)) return false;

    // Focal lengths
    float[] faLens = a.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS);
    float[] fbLens = b.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS);
    if (!floatArrayEquals(faLens, fbLens)) return false;

    // Sensor physical size
    SizeF sa = a.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE);
    SizeF sb = b.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE);
    if (!sizeFEquals(sa, sb)) return false;

    // Flash support
    Boolean fla = a.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
    Boolean flb = b.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
    if (fla == null ? flb != null : !fla.equals(flb)) return false;

    // AE modes length (presence)
    int[] aeA = a.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_MODES);
    int[] aeB = b.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_MODES);
    if (!intArrayEquals(aeA, aeB)) return false;

    // Minimum focus distance
    Float ma = a.get(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE);
    Float mb = b.get(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE);
    if (ma == null ? mb != null : Math.abs(ma - mb) > 1e-6) return false;

    return true;
  }

  private static boolean floatArrayEquals(float[] a, float[] b) {
    if (a == null && b == null) return true;
    if (a == null || b == null) return false;
    if (a.length != b.length) return false;
    for (int i = 0; i < a.length; i++) {
      if (Math.abs(a[i] - b[i]) > 1e-3) return false;
    }
    return true;
  }

  private static boolean intArrayEquals(int[] a, int[] b) {
    if (a == null && b == null) return true;
    if (a == null || b == null) return false;
    if (a.length != b.length) return false;
    for (int i = 0; i < a.length; i++) {
      if (a[i] != b[i]) return false;
    }
    return true;
  }

  private static boolean sizeFEquals(SizeF a, SizeF b) {
    if (a == null && b == null) return true;
    if (a == null || b == null) return false;
    return Math.abs(a.getWidth() - b.getWidth()) < 1e-3
        && Math.abs(a.getHeight() - b.getHeight()) < 1e-3;
  }

  /**
   * Core classification that follows the CameraIdentifier flow you provided. Preserves the input
   * order so the first camera in the list becomes MAIN.
   */
  private static void classifyAndComputeZoom(List<LensInfo> group, String label) {
    if (group.isEmpty()) return;

    // 1) MAIN = first element (preserve input order)
    LensInfo main = group.get(0);
    main.type = LensType.MAIN;
    main.zoomFactor = 1.0f;

    // 2) Detect logical cameras exactly as CameraIdentifier:
    //    - getPhysicalCameraIds() non-empty OR bit-6 set on id OR characteristics equal to another
    // camera
    for (LensInfo info : group) {
      if (info == main) continue;

      if (isLogicalViaApi(info.cameraId) || bitCheck6(info.cameraId)) {
        info.type = LensType.LOGICAL;
        continue;
      }

      CameraCharacteristics mine = cameraPropsMap.get(info.cameraId);
      if (mine != null) {
        for (Map.Entry<String, CameraCharacteristics> e : cameraPropsMap.entrySet()) {
          String otherId = e.getKey();
          if (otherId.equals(info.cameraId)) continue;
          CameraCharacteristics other = e.getValue();
          if (characteristicsEquivalent(mine, other)) {
            info.type = LensType.LOGICAL_REPEATED;
            break;
          }
        }
      }
    }

    // 3) Remove already-typed (except main) for further classification
    List<LensInfo> remaining = new ArrayList<>();
    for (LensInfo i : group) {
      if (i == main) continue;
      if (i.type == null) remaining.add(i);
    }

    // 4) sort remaining by AoV
    remaining.sort(SORT_BY_AOV);

    // 5) Compute zoom for every remaining (mm35 / main.mm35) — this fixes cases like camera id 2
    for (LensInfo info : remaining) {
      if (main.eqFocal > 0f && info.eqFocal > 0f) {
        info.zoomFactor = info.eqFocal / main.eqFocal;
      } else {
        info.zoomFactor = 1.0f;
      }
    }

    // 6) Depth / Other detection first, then split into wider/narrower
    TreeSet<LensInfo> widerThanMain = new TreeSet<>(SORT_BY_AOV);
    List<LensInfo> narrowerThanMain = new ArrayList<>();

    for (LensInfo info : remaining) {
      // OTHER if no AE modes
      if (!info.hasAeModes) {
        info.type = LensType.OTHER;
        continue;
      }
      // DEPTH if no flash (keeps zoom already set)
      if (!info.flashSupported) {
        info.type = LensType.DEPTH;
        continue;
      }

      // only the remaining (with AE and flash) go into wider/narrower decision
      if (info.angleOfView > main.angleOfView) widerThanMain.add(info);
      else narrowerThanMain.add(info);
    }

    // 7) Wider than main -> last (largest AoV) = ULTRAWIDE, rest = MACRO
    if (!widerThanMain.isEmpty()) {
      float maxAoV = widerThanMain.last().angleOfView;
      for (LensInfo w : widerThanMain) {
        if (Float.compare(w.angleOfView, maxAoV) == 0) {
          w.type = LensType.ULTRAWIDE;
        } else {
          w.type = LensType.MACRO;
        }
      }
    }

    // 8) Narrower -> TELE
    for (LensInfo n : narrowerThanMain) {
      n.type = LensType.TELE;
    }

    // Logging for debugging (one entry per camera in the group)
    Log.d("LensClassifier", "=== " + label + " Cameras ===");
    for (LensInfo info : group) {
      Log.d(
          "LensClassifier",
          "ID: "
              + info.cameraId
              + " | EqFocal: "
              + String.format("%.2f", info.eqFocal)
              + "mm"
              + " | AoV: "
              + String.format("%.1f", info.angleOfView)
              + "°"
              + " | Type: "
              + info.type
              + " | Zoom: "
              + String.format("%.2fx", info.zoomFactor)
              + " | AE: "
              + info.hasAeModes
              + " | Flash: "
              + info.flashSupported);
    }
  }
}
