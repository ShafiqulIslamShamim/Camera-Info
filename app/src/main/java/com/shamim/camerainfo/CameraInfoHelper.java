package com.shamim.camerainfo;

import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.util.SizeF;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CameraInfoHelper {

  private static final List<String> validCameraIds = new ArrayList<>();

  private static void scanCameras(CameraManager cameraManager) {
    validCameraIds.clear();
    if (cameraManager != null) {
      for (int id = 0; id < 512; id++) {
        try {
          cameraManager.getCameraCharacteristics(String.valueOf(id));
          validCameraIds.add(String.valueOf(id));
        } catch (IllegalArgumentException ignored) {
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }

  public static String getAllCameraInfo(CameraManager cm) {

    // extra info
    int logmode = SharedPrefValues.getValue("pref_log_mode", 0);

    StringBuilder sb = new StringBuilder();

    try {
      scanCameras(cm);

      CameraCache.loadAll(cm, validCameraIds);
      Map<String, CameraLensClassifier.LensResult> lensMap =
          CameraLensClassifier.detectLensesAndReturnMap(validCameraIds);

      sb.append("CameraManager IDs List = ")
          .append(BuildPropHelper.objectToString(cm.getCameraIdList()))
          .append("\n")
          .append("\n============================\n");

      sb.append("\nAll Camera IDs = ").append(validCameraIds.toString()).append("\n");
      sb.append("\n=================================\n\n");

      for (String id : validCameraIds) {
        CameraCharacteristics c = CameraCache.get(id);
        if (c == null) continue;

        // CameraID
        sb.append("CameraID = [").append(id).append("] ");
        // Logical lens detection
        sb.append(Camera2ApiKeysInfo.DetectPhysicalLens(c)).append("\n");

        // Facing
        String facingStr = "UNKNOWN";
        try {
          Integer facing = c.get(CameraCharacteristics.LENS_FACING);
          if (facing != null) {
            facingStr =
                (facing == CameraCharacteristics.LENS_FACING_BACK
                    ? "BACK"
                    : facing == CameraCharacteristics.LENS_FACING_FRONT ? "FRONT" : "EXTERNAL");
          }
        } catch (Exception ignored) {
        }
        sb.append("Facing = ").append(facingStr).append("\n");

        // Lens type & zoomFactor
        CameraLensClassifier.LensResult lensResult = lensMap.get(id);
        if (lensResult != null) {
          try {
            sb.append("Type = ").append(lensResult.type.toString()).append("\n");
            sb.append("Zoom = ").append(String.format("%.2fx", lensResult.zoomFactor)).append("\n");
          } catch (Exception ignored) {
            sb.append("Type = UNKNOWN\n");
            sb.append("Zoom = ?\n");
          }
        } else {
          sb.append("Type = UNKNOWN\n");
          sb.append("Zoom = ?\n");
        }

        // Focal length & 35mm equivalent
        try {
          float[] focalLengths = c.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS);
          SizeF sensorSize = c.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE);
          if (focalLengths != null && focalLengths.length > 0 && sensorSize != null) {
            float focal = focalLengths[0];

            if (logmode != 1) {
              sb.append("FocalLength = ").append(String.format("%.2fmm", focal)).append("\n");
            }

            float focalEq = Camera2ApiKeysInfo.calculate35mmeqv(focal, sensorSize);
            sb.append("35mm eqv FocalLength = ")
                .append(String.format("%.2fmm", focalEq))
                .append("\n");
          } else {
            if (logmode != 1) {
              sb.append("FocalLength = ?\n");
            }
            sb.append("35mm eqv FocalLength = ?\n");
          }
        } catch (Exception ignored) {
          if (logmode != 1) {
            sb.append("FocalLength = ?\n");
          }
          sb.append("35mm eqv FocalLength = ?\n");
        }

        if (logmode == 0) {
          sb.append(Camera2ApiKeysInfo.buildExtraDetails(c));
          sb.append(Camera2ApiKeysInfo.getMoreInfos(c)).append("\n");
        } else if (logmode == 2) {
          sb.append(CamcorderProfileLogger.getCamcorderLog(Integer.parseInt(id)));
        } else if (logmode == 1) {
          sb.append(Camera2ApiKeysInfo.formatHwLevel(c));
          sb.append("\n\n");
          sb.append(Camera2ApiKeysInfo.formatAvailableCapabilities(c));
          sb.append("\n\n");
          sb.append(CameraResolationFormatter.formatOutputSizes(c));
          sb.append("\n\n");
          sb.append(CameraCharacteristicsFormatter.formatCameraCharacteristics(c));
          sb.append("\n");
          sb.append(CaptureResultFormatter.formatResultKeys(c));
          sb.append("\n");
          sb.append(CaptureRequestFormatter.formatRequestKeys(c));
        }
        sb.append("\n");
        sb.append("=================================\n\n");
      }
    } catch (Exception e) {
      e.printStackTrace();
      sb.append("Error: ").append(e.getMessage());
    }

    return sb.toString();
  }
}
