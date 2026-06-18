package com.shamim.camerainfo.c2api_key;

import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ImageReader;
import android.media.MediaCodec;
import android.media.MediaRecorder;
import android.os.Build;
import android.renderscript.Allocation;
import android.util.*;
import android.util.SparseArray;
import com.shamim.camerainfo.activity.*;
import com.shamim.camerainfo.exception_catcher.*;
import com.shamim.camerainfo.preference.*;
import com.shamim.camerainfo.recycle_view.*;
import com.shamim.camerainfo.update_checker.*;
import com.shamim.camerainfo.util.*;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;

public class Camera2ApiKeysInfo {
  public static final String TAG = "Camera2ApiKeysInfo";
  public static final SparseArray<String> sCapabilitiesMap;
  public static final SparseArray<String> sHardwareLevelMap;
  public static final SparseArray<String> sImageFormats;
  public static final SparseArray<String> sLensFacing;
  private CameraManager mCameraManager;

  static {
    SparseArray<String> sparseArray = new SparseArray<>();
    sHardwareLevelMap = sparseArray;
    SparseArray<String> sparseArray2 = new SparseArray<>();
    sCapabilitiesMap = sparseArray2;
    SparseArray<String> sparseArray3 = new SparseArray<>();
    sImageFormats = sparseArray3;
    SparseArray<String> sparseArray4 = new SparseArray<>();
    sLensFacing = sparseArray4;
    sparseArray.put(2, "INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY");
    sparseArray.put(0, "INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED");
    sparseArray.put(1, "INFO_SUPPORTED_HARDWARE_LEVEL_FULL");
    sparseArray.put(3, "INFO_SUPPORTED_HARDWARE_LEVEL_3");
    sparseArray2.put(0, "BACKWARD_COMPATIBLE");
    sparseArray2.put(1, "MANUAL_SENSOR");
    sparseArray2.put(2, "MANUAL_POST_PROCESSING");
    sparseArray2.put(3, "RAW");
    sparseArray2.put(4, "PRIVATE_REPROCESSING");
    sparseArray2.put(5, "READ_SENSOR_SETTINGS");
    sparseArray2.put(6, "BURST_CAPTURE");
    sparseArray2.put(7, "YUV_REPROCESSING");
    sparseArray2.put(8, "DEPTH_OUTPUT");
    sparseArray2.put(9, "CONSTRAINED_HIGH_SPEED_VIDEO");
    sparseArray3.put(0, "UNKNOWN");
    sparseArray3.put(4, "RGB_565");
    sparseArray3.put(34, "PRIVATE");
    sparseArray3.put(257, "DEPTH_POINT_CLOUD");
    sparseArray3.put(4098, "RAW_DEPTH");
    sparseArray3.put(42, "FLEX_RGBA_8888");
    sparseArray3.put(0x44363159, "DEPTH16");
    sparseArray3.put(41, "FLEX_RGB_888");
    sparseArray3.put(40, "YUV_444_888");
    sparseArray3.put(39, "YUV_422_888");
    sparseArray3.put(35, "YUV_420_888");
    sparseArray3.put(256, "JPEG");
    sparseArray3.put(20, "YUY2");
    sparseArray3.put(17, "NV21");
    sparseArray3.put(16, "NV16");
    sparseArray3.put(0x20363159, "Y16");
    sparseArray3.put(0x20203859, "Y8");
    sparseArray3.put(0x32315659, "YV12");
    sparseArray3.put(32, "RAW_SENSOR");
    sparseArray3.put(36, "RAW_PRIVATE");
    sparseArray3.put(37, "RAW10");
    sparseArray3.put(38, "RAW12");
    sparseArray3.put(1, "RGBA_8888");
    sparseArray3.put(2, "RGBX_8888");
    sparseArray3.put(3, "RGB_888");
    sparseArray4.put(0, "LENS_FACING_FRONT");
    sparseArray4.put(1, "LENS_FACING_BACK");
    sparseArray4.put(2, "LENS_FACING_EXTERNAL");
  }

  public Camera2ApiKeysInfo(CameraManager cameraManager) {
    this.mCameraManager = cameraManager;
  }

  public static String formatCameraFacing(CameraCharacteristics cameraCharacteristics) {
    return "Facing: "
        + getValueFromMap(
            sLensFacing, cameraCharacteristics.get(CameraCharacteristics.LENS_FACING));
  }

  public static String formatHwLevel(CameraCharacteristics cameraCharacteristics) {
    return "SupportedHardwareLevel = "
        + getValueFromMap(
            sHardwareLevelMap,
            cameraCharacteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL));
  }

  private static String getCapabilityName(int key) {
    switch (key) {
      case 0:
        return "BACKWARD_COMPATIBLE";
      case 1:
        return "MANUAL_SENSOR";
      case 2:
        return "MANUAL_POST_PROCESSING";
      case 3:
        return "RAW";
      case 4:
        return "BURST_CAPTURE";
      case 5:
        return "READ_SENSOR_SETTINGS";
      case 6:
        return "YUV_REPROCESSING";
      case 7:
        return "PRIVATE_REPROCESSING";
      case 8:
        return "DEPTH_OUTPUT";
      case 9:
        return "CONSTRAINED_HIGH_SPEED_VIDEO";
      case 10:
        return "LOGICAL_MULTI_CAMERA";
      case 11:
        return "DYNAMIC_RANGE_TEN_BIT";
      case 12:
        return "DYNAMIC_RANGE_HDR10";

      default:
        return "UNKNOWN_CAPABILITY";
    }
  }

  public static String formatOutputSizes(CameraCharacteristics cameraCharacteristics) {
    StringBuilder sb = new StringBuilder();
    StreamConfigurationMap streamConfigurationMap =
        cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
    if (streamConfigurationMap != null) {
      int[] outputFormats = streamConfigurationMap.getOutputFormats();
      sb.append("Output formats: [");
      for (int i = 0; i < outputFormats.length; i++) {
        sb.append(getFormatName(outputFormats[i]));
        if (i != outputFormats.length - 1) {
          sb.append(", ");
        }
      }
      sb.append("]\n\n");
      for (int i2 : outputFormats) {
        sb.append(formatSizes(streamConfigurationMap, i2));
        sb.append("\n\n");
      }
      boolean isOutputSupportedFor = StreamConfigurationMap.isOutputSupportedFor(ImageReader.class);
      boolean isOutputSupportedFor2 =
          StreamConfigurationMap.isOutputSupportedFor(SurfaceTexture.class);
      boolean isOutputSupportedFor4 = StreamConfigurationMap.isOutputSupportedFor(MediaCodec.class);
      boolean isOutputSupportedFor5 =
          StreamConfigurationMap.isOutputSupportedFor(MediaRecorder.class);
      if (isOutputSupportedFor) {
        sb.append(formatSizes(streamConfigurationMap, ImageReader.class));
        sb.append("\n\n");
      }
      if (isOutputSupportedFor2) {
        sb.append(formatSizes(streamConfigurationMap, SurfaceTexture.class));
        sb.append("\n\n");
      }
      getAllocationClass(streamConfigurationMap, sb);
      if (isOutputSupportedFor4) {
        sb.append(formatSizes(streamConfigurationMap, MediaCodec.class));
        sb.append("\n\n");
      }
      if (isOutputSupportedFor5) {
        sb.append(formatSizes(streamConfigurationMap, MediaRecorder.class));
        sb.append("\n\n");
      }
      sb.append(formatHighSpeedSizes(streamConfigurationMap));
    }
    return sb.toString();
  }

  @SuppressWarnings("deprecation")
  private static void getAllocationClass(
      StreamConfigurationMap streamConfigurationMap, StringBuilder sb) {

    boolean isOutputSupportedFor3 = StreamConfigurationMap.isOutputSupportedFor(Allocation.class);

    if (isOutputSupportedFor3) {
      sb.append(formatSizes(streamConfigurationMap, Allocation.class));
      sb.append("\n\n");
    }
  }

  public static String formatAvailableCapabilities(CameraCharacteristics cameraCharacteristics) {
    if (cameraCharacteristics != null) {
      StringBuilder sb = new StringBuilder("Available capabilities: [");
      int[] iArr = cameraCharacteristics.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES);
      if (iArr != null) {
        for (int i = 0; i < iArr.length; i++) {
          sb.append(getValueFromMap(sCapabilitiesMap, Integer.valueOf(iArr[i])));
          if (i != iArr.length - 1) {
            sb.append(", ");
          }
        }
      }
      sb.append("]");
      return sb.toString();
    }
    return "No info";
  }

  public static String formatRequestKeys(CameraCharacteristics cameraCharacteristics) {
    if (cameraCharacteristics != null) {
      StringBuilder sb = new StringBuilder("Request keys:\n\n");
      Iterator<CaptureRequest.Key<?>> it =
          cameraCharacteristics.getAvailableCaptureRequestKeys().iterator();
      while (it.hasNext()) {
        sb.append(String.format(Locale.US, "%s\n", it.next().getName()));
      }
      return sb.toString();
    }
    return "No info";
  }

  public static String formatResultKeys(CameraCharacteristics cameraCharacteristics) {
    if (cameraCharacteristics != null) {
      StringBuilder sb = new StringBuilder("Result keys:\n\n");
      Iterator<CaptureResult.Key<?>> it =
          cameraCharacteristics.getAvailableCaptureResultKeys().iterator();
      while (it.hasNext()) {
        sb.append(String.format(Locale.US, "%s\n", it.next().getName()));
      }
      return sb.toString();
    }
    return "No info";
  }

  public static String formatCameraCharacteristics(CameraCharacteristics cameraCharacteristics) {
    if (cameraCharacteristics != null) {
      StringBuilder sb = new StringBuilder("Camera characteristics:\n\n");
      for (CameraCharacteristics.Key<?> key : cameraCharacteristics.getKeys()) {
        sb.append(String.format(Locale.US, "%s:  ", key.getName()));
        Object obj = cameraCharacteristics.get(key);
        if (obj != null) {
          if (obj.getClass().isArray()) {
            int length = Array.getLength(obj);
            sb.append("[");
            int i = 0;
            while (i < length) {
              Locale locale = Locale.US;
              Object obj2 = Array.get(obj, i);
              i++;
              sb.append(String.format(locale, "%s%s", obj2, i == length ? "" : ", "));
            }
            sb.append("]\n");
          } else {
            sb.append(String.format(Locale.US, "%s\n", obj.toString()));
          }
        }
      }
      return sb.toString();
    }
    return "No info";
  }

  public static String formatSizes(StreamConfigurationMap streamConfigurationMap, int i) {
    Size[] outputSizes = streamConfigurationMap.getOutputSizes(i);
    Size[] m =
        Build.VERSION.SDK_INT >= 23 ? streamConfigurationMap.getHighResolutionOutputSizes(i) : null;
    if (m != null && m.length != 0) {
      outputSizes = concatenate(outputSizes, m);
    }
    StringBuilder sb =
        new StringBuilder(String.format(Locale.US, "%s sizes: \n[", getFormatName(i)));
    for (int i2 = 0; i2 < outputSizes.length; i2++) {
      Size size = outputSizes[i2];
      long outputMinFrameDuration = streamConfigurationMap.getOutputMinFrameDuration(i, size);
      float f = ((float) outputMinFrameDuration) / 1000000.0f;
      long outputStallDuration = streamConfigurationMap.getOutputStallDuration(i, size);
      float f2 = ((float) outputStallDuration) / 1000000.0f;
      if (outputMinFrameDuration == 0 && outputStallDuration == 0) {
        sb.append(String.format(Locale.US, "%s", size));
      } else {
        sb.append(
            String.format(Locale.US, "%s@(%.3f, %.1f)", size, Float.valueOf(f), Float.valueOf(f2)));
      }
      if (i2 != outputSizes.length - 1) {
        sb.append(", ");
      }
    }
    sb.append("]");
    return sb.toString();
  }

  public static String formatSizes(StreamConfigurationMap streamConfigurationMap, Class<?> cls) {
    Size[] outputSizes = streamConfigurationMap.getOutputSizes(cls);
    StringBuilder sb =
        new StringBuilder(String.format(Locale.US, "%s sizes: \n[", cls.getSimpleName()));
    for (int i = 0; i < outputSizes.length; i++) {
      Size size = outputSizes[i];
      long outputMinFrameDuration = streamConfigurationMap.getOutputMinFrameDuration(cls, size);
      float f = ((float) outputMinFrameDuration) / 1000000.0f;
      long outputStallDuration = streamConfigurationMap.getOutputStallDuration(cls, size);
      float f2 = ((float) outputStallDuration) / 1000000.0f;
      if (outputMinFrameDuration == 0 && outputStallDuration == 0) {
        sb.append(String.format(Locale.US, "%s", size));
      } else {
        sb.append(
            String.format(Locale.US, "%s@(%.3f, %.1f)", size, Float.valueOf(f), Float.valueOf(f2)));
      }
      if (i != outputSizes.length - 1) {
        sb.append(", ");
      }
    }
    sb.append("]");
    return sb.toString();
  }

  public static String formatHighSpeedSizes(StreamConfigurationMap streamConfigurationMap) {
    Size[] highSpeedVideoSizes = streamConfigurationMap.getHighSpeedVideoSizes();
    StringBuilder sb = new StringBuilder("High Speed sizes: \n[");
    for (int i = 0; i < highSpeedVideoSizes.length; i++) {
      Size size = highSpeedVideoSizes[i];
      sb.append(
          String.format(
              Locale.US,
              "%s @(%s)",
              size,
              Arrays.toString(streamConfigurationMap.getHighSpeedVideoFpsRangesFor(size))));
      if (i != highSpeedVideoSizes.length - 1) {
        sb.append(", ");
      }
    }
    sb.append("]");
    return sb.toString();
  }

  public static String getFormatName(int i) {
    return getValueFromMap(sImageFormats, Integer.valueOf(i));
  }

  public static String getValueFromMap(SparseArray<String> sparseArray, Integer num) {
    if (num == null) {
      return "";
    }
    String str = sparseArray.get(num.intValue());
    return str != null ? str + "(" + String.valueOf(num) + ")" : String.valueOf(num);
  }

  public static String buildExtraDetails(CameraCharacteristics c) {
    StringBuilder sb = new StringBuilder();

    // Aperture
    try {
      float[] apertures = c.get(CameraCharacteristics.LENS_INFO_AVAILABLE_APERTURES);
      if (apertures != null && apertures.length > 0) {
        sb.append("Aperture = ").append(apertures[0]).append("\n");
      }
    } catch (Exception ignored) {
    }

    // Sensor size
    SizeF sensorSize = null;
    try {
      sensorSize = c.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE);
      if (sensorSize != null) {
        sb.append("SensorSize = ")
            .append(String.format("%.3fx%.3f", sensorSize.getWidth(), sensorSize.getHeight()))
            .append("\n");
      }
    } catch (Exception ignored) {
    }

    // Pixel array & pixel size
    try {
      android.util.Size pixelArray = c.get(CameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE);
      if (pixelArray != null) {
        sb.append("PixelArray = ")
            .append(pixelArray.getWidth())
            .append("x")
            .append(pixelArray.getHeight())
            .append("\n");

        if (sensorSize != null) {

          double pixelSize = calculatePixelSize(pixelArray.getWidth(), sensorSize.getWidth());
          sb.append("PixelSize = ").append(String.format("%.2fµm", pixelSize)).append("\n");
        }
      }
    } catch (Exception ignored) {
    }

    // Angle of View
    try {
      float[] focalLengths = c.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS);
      android.util.Size pixelArraytwo = c.get(CameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE);
      if (focalLengths != null
          && focalLengths.length > 0
          && sensorSize != null
          && pixelArraytwo != null) {
        float focal = focalLengths[0];

        double fov = calculateAngleOfView(focal, sensorSize, pixelArraytwo);
        sb.append("AngleOfView(Diagonal) = ").append(String.format("%.0f°", fov)).append("\n");
      }
    } catch (Exception ignored) {
    }

    // Flash
    try {
      Boolean flash = c.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
      sb.append("FlashSupported = ").append(flash != null && flash).append("\n");
    } catch (Exception ignored) {
    }

    // HW Level
    try {
      Integer hwLevel = c.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
      if (hwLevel != null) {
        sb.append("SupportedHardwareLevel = ")
            .append(hwLevel)
            .append("(")
            .append(getHardwareLevelName(hwLevel).toUpperCase())
            .append(")")
            .append("\n");
      }
    } catch (Exception ignored) {
    }

    return sb.toString();
  }

  public static String getHardwareLevelName(int level) {
    switch (level) {
      case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED:
        return "Limited";

      case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL:
        return "Full";

      case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY:
        return "Legacy";

      case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_3:
        return "Level_3";

      case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_EXTERNAL:
        return "External";

      default:
        return "Unknown";
    }
  }

  public static boolean isLogicalCameraId(CameraCharacteristics currentCameraCharacteristics) {
    return (currentCameraCharacteristics == null
            || currentCameraCharacteristics.getPhysicalCameraIds().isEmpty())
        ? false
        : true;
  }

  public static String DetectPhysicalLens(CameraCharacteristics cameraCharacteristics) {
    if (cameraCharacteristics.getPhysicalCameraIds().isEmpty()) {
      return "";
    }
    return cameraCharacteristics.getPhysicalCameraIds().toString().replace(", ", " + ");
  }

  public static <T> T[] concatenate(T[] tArr, T[] tArr2) {
    // Arrays.copyOf creates a new array of the same type as tArr
    // with the combined length of both arrays.
    T[] tArr3 = Arrays.copyOf(tArr, tArr.length + tArr2.length);

    // We only need to copy the second array,
    // as copyOf already filled the first part.
    System.arraycopy(tArr2, 0, tArr3, tArr.length, tArr2.length);

    return tArr3;
  }

  public static float calculatePixelSize(int pixelArrayWidth, float sensorWidth) {
    return (sensorWidth / ((float) pixelArrayWidth)) * 1000.0f;
  }

  public static Double calculateAngleOfView(
      float focalLength, SizeF sensorSize, Size pixelArraySize) {
    float pixelSize = calculatePixelSize(pixelArraySize.getWidth(), sensorSize.getWidth());
    return Math.toDegrees(
        Math.atan(
                Math.sqrt(
                        Math.pow(sensorSize.getWidth() * pixelSize, 2.0d)
                            + Math.pow(sensorSize.getHeight() * pixelSize, 2.0d))
                    / ((double) (2.0f * focalLength)))
            * 2.0d);
  }

  public static float calculate35mmeqv(float focalLength, SizeF sensorSize) {
    return (36.0f / sensorSize.getWidth()) * focalLength;
  }

  public static Size[] PreventEmptyOutputSize(
      StreamConfigurationMap streamConfigurationMap, int i) {
    Size[] outputSizes = null;
    try {
      if (streamConfigurationMap != null) {
        outputSizes = streamConfigurationMap.getOutputSizes(i);
      }
    } catch (Throwable e) {
      // ignore but prevent crash
    }
    return outputSizes;
  }

  public static String getCheckedRawSize(CameraCharacteristics cameraCharacteristics) {
    StringBuilder sb = new StringBuilder();
    try {
      StreamConfigurationMap map =
          cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

      // Raw16
      try {
        Size[] raw16 = PreventEmptyOutputSize(map, 32);
        sb.append("\nRawSupport = Raw16 ");
        sb.append((raw16 == null || raw16.length == 0) ? "✘" : "✓");
      } catch (Throwable e) {
        sb.append("\nRawSupport = Raw16 Error");
      }

      // Raw10
      try {
        Size[] raw10 = PreventEmptyOutputSize(map, 37);
        sb.append(", Raw10 ");
        sb.append((raw10 == null || raw10.length == 0) ? "✘" : "✓");
      } catch (Throwable e) {
        sb.append(", Raw10 Error");
      }

      // RawPrivate
      try {
        Size[] rawPrivate = PreventEmptyOutputSize(map, 36);
        sb.append(", RawPrivate ");
        sb.append((rawPrivate == null || rawPrivate.length == 0) ? "✘" : "✓");
      } catch (Throwable e) {
        sb.append(", RawPrivate Error");
      }

    } catch (Throwable e) {
      sb.append("\nError checking raw sizes");
    }
    return sb.toString();
  }

  public static String getMoreInfos(CameraCharacteristics cameraCharacteristics) {
    StringBuilder sb = new StringBuilder();

    // Sensitivity Range
    try {
      Range<Integer> sensitivityRange =
          cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE);
      if (sensitivityRange != null) {
        sb.append("SensitivityRange = ")
            .append(sensitivityRange.getLower())
            .append(" - ")
            .append(sensitivityRange.getUpper());
      }
    } catch (Throwable e) {
      sb.append("SensitivityRange = Error");
    }

    // Exposure Time Range
    try {
      Range<Long> exposureRange =
          cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE);
      if (exposureRange != null) {
        sb.append("\nExposureTimeRange = ")
            .append(String.format("%.2f", (float) exposureRange.getLower() / 1_000_000.0f))
            .append(" - ")
            .append(String.format("%.2f", (float) exposureRange.getUpper() / 1_000_000.0f));
      }
    } catch (Throwable e) {
      sb.append("\nExposureTimeRange = Error");
    }

    // Raw checks
    sb.append(getCheckedRawSize(cameraCharacteristics));

    // Output info
    sb.append(getNonNullSize(cameraCharacteristics));

    return sb.toString();
  }

  public static String getNonNullSize(CameraCharacteristics cameraCharacteristics) {
    StringBuilder sb = new StringBuilder();
    try {
      StreamConfigurationMap map =
          cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

      if (map != null) {
        int[] outputFormats = null;
        try {
          outputFormats = map.getOutputFormats();
        } catch (Throwable e) {
          sb.append("\nOutputFormats = Error");
        }

        if (outputFormats != null) {
          for (int format : outputFormats) {
            sb.append(getOutputFormateName(format));
            try {
              Size[] sizes = PreventEmptyOutputSize(map, format);
              if (sizes != null) {
                sb.append(BuildPropHelper.objectToString(sizes));
              } else {
                sb.append("null");
              }
            } catch (Throwable e) {
              sb.append("Error");
            }
          }
        } else {
          sb.append("\nOutput Format is totally null");
        }
      } else {
        sb.append("\nStreamConfigurationMap is null");
      }
    } catch (Throwable e) {
      sb.append("\nError reading output sizes");
    }
    return sb.toString();
  }

  public static String getOutputFormateName(int i) {
    switch (i) {
      case 32:
        return "\nRaw16(" + i + ") = ";
      case 34:
        return "\nPrivate(" + i + ") = ";
      case 35:
        return "\nYuv420-888(" + i + ") = ";
      case 36:
        return "\nRawPrivate(" + i + ") = ";
      case 37:
        return "\nRaw10(" + i + ") = ";
      case 256:
        return "\nJpeg(" + i + ") = ";
      default:
        return "\n" + i + " = ";
    }
  }
}
