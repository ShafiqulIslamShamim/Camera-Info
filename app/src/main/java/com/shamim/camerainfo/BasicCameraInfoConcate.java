package com.shamim.camerainfo;

import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.util.Range;
import android.util.Rational;
import java.util.ArrayList;
import java.util.List;

public final class BasicCameraInfoConcate {

  private BasicCameraInfoConcate() {}

  public static String describeAll(CameraCharacteristics ch) {
    try {
      StringBuilder sb = new StringBuilder();

      sb.append("CaptureFeatures = ").append(describeCaptureFeatures(ch)).append("\n");
      sb.append("AeModes = ").append(describeAeModes(ch)).append("\n");
      sb.append("AeLockAvailable = ").append(describeAeLock(ch)).append("\n");
      sb.append("AeCompensationRange = ").append(describeAeCompensationRange(ch)).append("\n");
      sb.append("AeCompensationStep = ").append(describeAeCompensationStep(ch)).append("\n");
      sb.append("AeTargetFpsRanges = ").append(describeAeTargetFpsRanges(ch)).append("\n");
      sb.append("FocusModes = ").append(describeAfModes(ch)).append("\n");
      sb.append("WhiteBalanceModes = ").append(describeAwbModes(ch)).append("\n");
      sb.append("AwbLockAvailable = ").append(describeAwbLock(ch)).append("\n");
      sb.append("AvailableCapabilities = ").append(describeAvailableCapabilities(ch)).append("\n");
      sb.append("VideoStabilizationModes = ").append(describeVideoStabModes(ch)).append("\n");
      sb.append("LensOpticalStabilizationModes = ").append(describeLensOisModes(ch)).append("\n");

      return sb.toString();
    } catch (Exception e) {
      return "Error reading camera info: " + e.getMessage();
    }
  }

  // -------- Capture Features (show known feature constants and mark ✓/✘) --------
  public static String describeCaptureFeatures(CameraCharacteristics ch) {
    try {
      int[] caps = ch.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES);
      List<Integer> sup = toList(caps);

      // Known feature constants we want to show explicitly (use numeric constant then name)
      int[] known = {
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_RAW,
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_BURST_CAPTURE,
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_BACKWARD_COMPATIBLE,
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MANUAL_SENSOR,
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MANUAL_POST_PROCESSING,
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_PRIVATE_REPROCESSING,
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_READ_SENSOR_SETTINGS,
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_YUV_REPROCESSING,
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_DEPTH_OUTPUT,
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_CONSTRAINED_HIGH_SPEED_VIDEO
      };

      List<String> out = new ArrayList<>();
      for (int k : known) {
        boolean ok = sup.contains(k);
        out.add(mapCapability(k) + (ok ? "✓" : "✘"));
      }

      // If there are any unknown capability values present, also list them
      if (caps != null) {
        for (int c : caps) {
          boolean knownOne = false;
          for (int k : known)
            if (k == c) {
              knownOne = true;
              break;
            }
          if (!knownOne) out.add(c + "(UNKNOWN)✓");
        }
      }

      return join(out);
    } catch (Exception e) {
      return "Unavailable";
    }
  }

  // -------- AE Modes (show all known AE mode constants) --------
  public static String describeAeModes(CameraCharacteristics ch) {
    try {
      int[] vals = ch.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_MODES);
      List<Integer> sup = toList(vals);

      int[] allAeModes = {
        CaptureRequest.CONTROL_AE_MODE_OFF,
        CaptureRequest.CONTROL_AE_MODE_ON,
        CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH,
        CaptureRequest.CONTROL_AE_MODE_ON_ALWAYS_FLASH,
        CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH_REDEYE
      };

      List<String> out = new ArrayList<>();
      for (int m : allAeModes) {
        boolean ok = sup.contains(m);
        out.add(mapAeMode(m) + (ok ? "✓" : "✘"));
      }

      // include any unknown present values
      if (vals != null) {
        for (int v : vals) {
          boolean known = false;
          for (int m : allAeModes)
            if (m == v) {
              known = true;
              break;
            }
          if (!known) out.add(v + "(UNKNOWN)" + "✓");
        }
      }

      return join(out);
    } catch (Exception e) {
      return "Unavailable";
    }
  }

  private static String mapAeMode(int v) {
    switch (v) {
      case CaptureRequest.CONTROL_AE_MODE_OFF:
        return v + "(MANUAL_EXPOSURE)";
      case CaptureRequest.CONTROL_AE_MODE_ON:
        return v + "(AUTO_EXPOSURE)";
      case CaptureRequest.CONTROL_AE_MODE_ON_ALWAYS_FLASH:
        return v + "(AUTO_EXPOSURE_ALWAYS_FLASH)";
      case CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH:
        return v + "(AUTO_EXPOSURE_AUTO_FLASH)";
      case CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH_REDEYE:
        return v + "(AUTO_EXPOSURE_AUTO_FLASH_REDEYE)";
      default:
        return v + "(UNKNOWN)";
    }
  }

  public static String describeAeLock(CameraCharacteristics ch) {
    try {
      Boolean available = ch.get(CameraCharacteristics.CONTROL_AE_LOCK_AVAILABLE);
      return available == null ? "Unknown" : String.valueOf(available);
    } catch (Exception e) {
      return "Unknown";
    }
  }

  public static String describeAeCompensationRange(CameraCharacteristics ch) {
    try {
      Range<Integer> range = ch.get(CameraCharacteristics.CONTROL_AE_COMPENSATION_RANGE);
      if (range == null) return "Unavailable";
      return range.getLower() + " .. " + range.getUpper() + " (EV steps)";
    } catch (Exception e) {
      return "Unavailable";
    }
  }

  public static String describeAeCompensationStep(CameraCharacteristics ch) {
    try {
      Rational step = ch.get(CameraCharacteristics.CONTROL_AE_COMPENSATION_STEP);
      if (step == null) return "Unavailable";
      double d = step.doubleValue();
      return step + " (" + String.format("%.2f", d) + " EV)";
    } catch (Exception e) {
      return "Unavailable";
    }
  }

  public static String describeAeTargetFpsRanges(CameraCharacteristics ch) {
    try {
      Range<Integer>[] ranges =
          ch.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES);
      if (ranges == null) return "Unavailable";
      List<String> out = new ArrayList<>();
      for (Range<Integer> r : ranges) {
        int lo = r.getLower();
        int hi = r.getUpper();
        if (lo >= 1000 && hi >= 1000) out.add((lo / 1000.0) + " - " + (hi / 1000.0) + " fps");
        else out.add(lo + " - " + hi + " fps");
      }
      return join(out);
    } catch (Exception e) {
      return "Unavailable";
    }
  }

  // -------- AF Modes (show all known AF mode constants) --------
  public static String describeAfModes(CameraCharacteristics ch) {
    try {
      int[] vals = ch.get(CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES);
      List<Integer> sup = toList(vals);

      int[] allModes = {
        CaptureRequest.CONTROL_AF_MODE_OFF,
        CaptureRequest.CONTROL_AF_MODE_AUTO,
        CaptureRequest.CONTROL_AF_MODE_MACRO,
        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_VIDEO,
        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE,
        CaptureRequest.CONTROL_AF_MODE_EDOF
      };

      List<String> out = new ArrayList<>();
      for (int m : allModes) {
        boolean ok = sup.contains(m);
        out.add(mapAfMode(m) + (ok ? "✓" : "✘"));
      }

      // include any unknown present values
      if (vals != null) {
        for (int v : vals) {
          boolean known = false;
          for (int m : allModes)
            if (m == v) {
              known = true;
              break;
            }
          if (!known) out.add(v + "(UNKNOWN)" + "✓");
        }
      }

      return join(out);
    } catch (Exception e) {
      return "Unavailable";
    }
  }

  private static String mapAfMode(int v) {
    switch (v) {
      case CaptureRequest.CONTROL_AF_MODE_OFF:
        return v + "(OFF)";
      case CaptureRequest.CONTROL_AF_MODE_AUTO:
        return v + "(AUTO)";
      case CaptureRequest.CONTROL_AF_MODE_MACRO:
        return v + "(MACRO)";
      case CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_VIDEO:
        return v + "(CONTINUOUS_VIDEO)";
      case CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE:
        return v + "(CONTINUOUS_PICTURE)";
      case CaptureRequest.CONTROL_AF_MODE_EDOF:
        return v + "(EDOF)";
      default:
        return v + "(UNKNOWN)";
    }
  }

  // -------- AWB Modes (show all known AWB mode constants) --------
  public static String describeAwbModes(CameraCharacteristics ch) {
    try {
      int[] vals = ch.get(CameraCharacteristics.CONTROL_AWB_AVAILABLE_MODES);
      List<Integer> sup = toList(vals);

      int[] allAwbModes = {
        CaptureRequest.CONTROL_AWB_MODE_OFF,
        CaptureRequest.CONTROL_AWB_MODE_AUTO,
        CaptureRequest.CONTROL_AWB_MODE_INCANDESCENT,
        CaptureRequest.CONTROL_AWB_MODE_FLUORESCENT,
        CaptureRequest.CONTROL_AWB_MODE_WARM_FLUORESCENT,
        CaptureRequest.CONTROL_AWB_MODE_DAYLIGHT,
        CaptureRequest.CONTROL_AWB_MODE_CLOUDY_DAYLIGHT,
        CaptureRequest.CONTROL_AWB_MODE_TWILIGHT,
        CaptureRequest.CONTROL_AWB_MODE_SHADE
      };

      List<String> out = new ArrayList<>();
      for (int m : allAwbModes) {
        boolean ok = sup.contains(m);
        out.add(mapAwbMode(m) + (ok ? "✓" : "✘"));
      }

      if (vals != null) {
        for (int v : vals) {
          boolean known = false;
          for (int m : allAwbModes)
            if (m == v) {
              known = true;
              break;
            }
          if (!known) out.add(v + "(UNKNOWN)" + "✓");
        }
      }

      return join(out);
    } catch (Exception e) {
      return "Unavailable";
    }
  }

  private static String mapAwbMode(int v) {
    switch (v) {
      case CaptureRequest.CONTROL_AWB_MODE_OFF:
        return v + "(OFF)";
      case CaptureRequest.CONTROL_AWB_MODE_AUTO:
        return v + "(AUTO)";
      case CaptureRequest.CONTROL_AWB_MODE_INCANDESCENT:
        return v + "(INCANDESCENT)";
      case CaptureRequest.CONTROL_AWB_MODE_FLUORESCENT:
        return v + "(FLUORESCENT)";
      case CaptureRequest.CONTROL_AWB_MODE_WARM_FLUORESCENT:
        return v + "(WARM_FLUORESCENT)";
      case CaptureRequest.CONTROL_AWB_MODE_DAYLIGHT:
        return v + "(DAYLIGHT)";
      case CaptureRequest.CONTROL_AWB_MODE_CLOUDY_DAYLIGHT:
        return v + "(CLOUDY_DAYLIGHT)";
      case CaptureRequest.CONTROL_AWB_MODE_TWILIGHT:
        return v + "(TWILIGHT)";
      case CaptureRequest.CONTROL_AWB_MODE_SHADE:
        return v + "(SHADE)";
      default:
        return v + "(UNKNOWN)";
    }
  }

  public static String describeAwbLock(CameraCharacteristics ch) {
    try {
      Boolean available = ch.get(CameraCharacteristics.CONTROL_AWB_LOCK_AVAILABLE);
      return available == null ? "Unknown" : String.valueOf(available);
    } catch (Exception e) {
      return "Unknown";
    }
  }

  // -------- Capabilities (show all known capability constants) --------
  public static String describeAvailableCapabilities(CameraCharacteristics ch) {
    try {
      int[] caps = ch.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES);
      List<Integer> sup = toList(caps);

      int[] allCaps = {
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_BACKWARD_COMPATIBLE,
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MANUAL_SENSOR,
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MANUAL_POST_PROCESSING,
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_RAW,
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_PRIVATE_REPROCESSING,
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_READ_SENSOR_SETTINGS,
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_BURST_CAPTURE,
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_YUV_REPROCESSING,
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_DEPTH_OUTPUT,
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_CONSTRAINED_HIGH_SPEED_VIDEO
      };

      List<String> out = new ArrayList<>();
      for (int c : allCaps) {
        boolean ok = sup.contains(c);
        out.add(mapCapability(c) + (ok ? "✓" : "✘"));
      }

      if (caps != null) {
        for (int c : caps) {
          boolean known = false;
          for (int k : allCaps)
            if (k == c) {
              known = true;
              break;
            }
          if (!known) out.add(c + "(UNKNOWN)✓");
        }
      }

      return join(out);
    } catch (Exception e) {
      return "Unavailable";
    }
  }

  private static String mapCapability(int c) {
    switch (c) {
      case CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_BACKWARD_COMPATIBLE:
        return c + "(BACKWARD_COMPATIBLE)";
      case CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MANUAL_SENSOR:
        return c + "(MANUAL_SENSOR)";
      case CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MANUAL_POST_PROCESSING:
        return c + "(MANUAL_POST_PROCESSING)";
      case CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_RAW:
        return c + "(RAW)";
      case CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_PRIVATE_REPROCESSING:
        return c + "(PRIVATE_REPROCESSING)";
      case CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_READ_SENSOR_SETTINGS:
        return c + "(READ_SENSOR_SETTINGS)";
      case CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_BURST_CAPTURE:
        return c + "(BURST_CAPTURE)";
      case CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_YUV_REPROCESSING:
        return c + "(YUV_REPROCESSING)";
      case CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_DEPTH_OUTPUT:
        return c + "(DEPTH_OUTPUT)";
      case CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_CONSTRAINED_HIGH_SPEED_VIDEO:
        return c + "(CONSTRAINED_HIGH_SPEED_VIDEO)";
      default:
        return c + "(UNKNOWN)";
    }
  }

  // -------- Video Stabilization --------
  public static String describeVideoStabModes(CameraCharacteristics ch) {
    try {
      int[] vals = ch.get(CameraCharacteristics.CONTROL_AVAILABLE_VIDEO_STABILIZATION_MODES);
      List<Integer> sup = toList(vals);

      int[] all = {
        CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE_OFF,
        CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE_ON
      };

      List<String> out = new ArrayList<>();
      for (int v : all) {
        boolean ok = sup.contains(v);
        out.add(mapVideoStabMode(v) + (ok ? "✓" : "✘"));
      }

      if (vals != null) {
        for (int v : vals) {
          boolean known = false;
          for (int a : all)
            if (a == v) {
              known = true;
              break;
            }
          if (!known) out.add(v + "(UNKNOWN)" + "✓");
        }
      }

      return join(out);
    } catch (Exception e) {
      return "Unavailable";
    }
  }

  private static String mapVideoStabMode(int v) {
    switch (v) {
      case CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE_OFF:
        return v + "(OFF)";
      case CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE_ON:
        return v + "(ON)";
      default:
        return v + "(UNKNOWN)";
    }
  }

  // -------- Lens OIS --------
  public static String describeLensOisModes(CameraCharacteristics ch) {
    try {
      int[] vals = ch.get(CameraCharacteristics.LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION);
      List<Integer> sup = toList(vals);

      int[] all = {
        CameraMetadata.LENS_OPTICAL_STABILIZATION_MODE_OFF,
        CameraMetadata.LENS_OPTICAL_STABILIZATION_MODE_ON
      };

      List<String> out = new ArrayList<>();
      for (int v : all) {
        boolean ok = sup.contains(v);
        out.add(mapLensOisMode(v) + (ok ? "✓" : "✘"));
      }

      if (vals != null) {
        for (int v : vals) {
          boolean known = false;
          for (int a : all)
            if (a == v) {
              known = true;
              break;
            }
          if (!known) out.add(v + "(UNKNOWN)" + "✓");
        }
      }

      return join(out);
    } catch (Exception e) {
      return "Unavailable";
    }
  }

  private static String mapLensOisMode(int v) {
    switch (v) {
      case CameraMetadata.LENS_OPTICAL_STABILIZATION_MODE_OFF:
        return v + "(OIS_OFF)";
      case CameraMetadata.LENS_OPTICAL_STABILIZATION_MODE_ON:
        return v + "(OIS_ON)";
      default:
        return v + "(UNKNOWN)";
    }
  }

  // -------- Helpers --------
  private static List<Integer> toList(int[] arr) {
    List<Integer> list = new ArrayList<>();
    if (arr == null) return list;
    for (int i : arr) list.add(i);
    return list;
  }

  private static String join(List<String> items) {
    try {
      if (items == null || items.isEmpty()) return "";
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < items.size(); i++) {
        if (i > 0) sb.append(", ");
        sb.append(items.get(i));
      }
      return sb.toString();
    } catch (Exception e) {
      return "";
    }
  }
}
