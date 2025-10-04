package com.shamim.camerainfo;

import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ImageReader;
import android.media.MediaCodec;
import android.media.MediaRecorder;
import android.os.Build;
import android.renderscript.Allocation;
import android.util.Range;
import android.util.Size;
import android.view.SurfaceHolder;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class CameraResolationFormatter {

  private static final Map<Integer, String> FORMAT_NAME_MAP = new LinkedHashMap<>();

  static {
    // ImageFormat constants
    FORMAT_NAME_MAP.put(ImageFormat.RAW_SENSOR, "RAW_SENSOR");
    FORMAT_NAME_MAP.put(ImageFormat.RAW_PRIVATE, "RAW_PRIVATE");
    FORMAT_NAME_MAP.put(ImageFormat.RAW10, "RAW10");
    FORMAT_NAME_MAP.put(ImageFormat.RAW12, "RAW12");
    FORMAT_NAME_MAP.put(ImageFormat.JPEG, "JPEG");
    FORMAT_NAME_MAP.put(ImageFormat.NV16, "NV16");
    FORMAT_NAME_MAP.put(ImageFormat.NV21, "NV21");
    FORMAT_NAME_MAP.put(ImageFormat.YUV_420_888, "YUV_420_888");
    FORMAT_NAME_MAP.put(ImageFormat.YUV_422_888, "YUV_422_888");
    FORMAT_NAME_MAP.put(ImageFormat.YUV_444_888, "YUV_444_888");
    FORMAT_NAME_MAP.put(ImageFormat.FLEX_RGB_888, "FLEX_RGB_888");
    FORMAT_NAME_MAP.put(ImageFormat.FLEX_RGBA_8888, "FLEX_RGBA_8888");
    FORMAT_NAME_MAP.put(ImageFormat.YUY2, "YUY2");
    FORMAT_NAME_MAP.put(ImageFormat.Y8, "Y8");
    FORMAT_NAME_MAP.put(ImageFormat.DEPTH16, "DEPTH16");
    FORMAT_NAME_MAP.put(ImageFormat.DEPTH_POINT_CLOUD, "DEPTH_POINT_CLOUD");
    FORMAT_NAME_MAP.put(ImageFormat.PRIVATE, "PRIVATE");
    // PixelFormat constants
    FORMAT_NAME_MAP.put(PixelFormat.RGBA_8888, "RGBA_8888");
    FORMAT_NAME_MAP.put(PixelFormat.RGBX_8888, "RGBX_8888");
    FORMAT_NAME_MAP.put(PixelFormat.RGB_888, "RGB_888");
    FORMAT_NAME_MAP.put(PixelFormat.RGB_565, "RGB_565");
  }

  private static final Map<Integer, String> FORMAT_DESCRIPTION_MAP = new LinkedHashMap<>();

  static {
    FORMAT_DESCRIPTION_MAP.put(ImageFormat.RAW_SENSOR, "Raw sensor data");
    FORMAT_DESCRIPTION_MAP.put(ImageFormat.RAW_PRIVATE, "Opaque raw sensor data");
    FORMAT_DESCRIPTION_MAP.put(ImageFormat.RAW10, "10-bit raw sensor data");
    FORMAT_DESCRIPTION_MAP.put(ImageFormat.RAW12, "12-bit raw sensor data");
    FORMAT_DESCRIPTION_MAP.put(ImageFormat.JPEG, "Compressed JPEG image");
    FORMAT_DESCRIPTION_MAP.put(ImageFormat.NV16, "YUV 4:2:2 semi-planar format");
    FORMAT_DESCRIPTION_MAP.put(ImageFormat.NV21, "YUV 4:2:0 semi-planar format");
    FORMAT_DESCRIPTION_MAP.put(ImageFormat.YUV_420_888, "Flexible YUV 4:2:0 format");
    FORMAT_DESCRIPTION_MAP.put(ImageFormat.YUV_422_888, "Flexible YUV 4:2:2 format");
    FORMAT_DESCRIPTION_MAP.put(ImageFormat.YUV_444_888, "Flexible YUV 4:4:4 format");
    FORMAT_DESCRIPTION_MAP.put(ImageFormat.FLEX_RGB_888, "Flexible RGB 888 format");
    FORMAT_DESCRIPTION_MAP.put(ImageFormat.FLEX_RGBA_8888, "Flexible RGBA 8888 format");
    FORMAT_DESCRIPTION_MAP.put(ImageFormat.YUY2, "YUV 4:2:2 interleaved format");
    FORMAT_DESCRIPTION_MAP.put(ImageFormat.Y8, "8-bit grayscale format");
    FORMAT_DESCRIPTION_MAP.put(ImageFormat.DEPTH16, "16-bit depth data");
    FORMAT_DESCRIPTION_MAP.put(ImageFormat.DEPTH_POINT_CLOUD, "Depth point cloud data");
    FORMAT_DESCRIPTION_MAP.put(ImageFormat.PRIVATE, "Opaque private format");
    FORMAT_DESCRIPTION_MAP.put(PixelFormat.RGBA_8888, "32-bit RGBA format");
    FORMAT_DESCRIPTION_MAP.put(PixelFormat.RGBX_8888, "32-bit RGBX format");
    FORMAT_DESCRIPTION_MAP.put(PixelFormat.RGB_888, "24-bit RGB format");
    FORMAT_DESCRIPTION_MAP.put(PixelFormat.RGB_565, "16-bit RGB format");
  }

  public static String formatSizes(StreamConfigurationMap streamConfigurationMap, int format) {
    if (streamConfigurationMap == null) return "No stream configuration map available";

    Size[] outputSizes = streamConfigurationMap.getOutputSizes(format);
    Size[] highResSizes =
        Build.VERSION.SDK_INT >= 23
            ? streamConfigurationMap.getHighResolutionOutputSizes(format)
            : null;
    if (highResSizes != null && highResSizes.length > 0) {
      outputSizes = concatenate(outputSizes, highResSizes);
    }
    if (outputSizes == null || outputSizes.length == 0) {
      return String.format(Locale.US, "%s sizes: []", getFormatName(format));
    }

    StringBuilder sb =
        new StringBuilder(String.format(Locale.US, "%s sizes:\n[", getFormatName(format)));
    for (int i = 0; i < outputSizes.length; i++) {
      Size size = outputSizes[i];
      long minFrameDuration = streamConfigurationMap.getOutputMinFrameDuration(format, size);
      float minFrameMs = minFrameDuration / 1000000.0f;
      long stallDuration = streamConfigurationMap.getOutputStallDuration(format, size);
      float stallMs = stallDuration / 1000000.0f;
      sb.append(
          String.format(
              Locale.US,
              "%s (Min frame duration: %.3f ms, Stall duration: %.1f ms)",
              size,
              minFrameMs,
              stallMs));
      if (i < outputSizes.length - 1) {
        sb.append(", ");
      }
    }
    sb.append("]");
    return sb.toString();
  }

  public static String formatSizes(StreamConfigurationMap streamConfigurationMap, Class<?> cls) {
    if (streamConfigurationMap == null || !streamConfigurationMap.isOutputSupportedFor(cls)) {
      return String.format(Locale.US, "%s sizes: []", cls.getSimpleName());
    }

    Size[] outputSizes = streamConfigurationMap.getOutputSizes(cls);
    if (outputSizes == null || outputSizes.length == 0) {
      return String.format(Locale.US, "%s sizes: []", cls.getSimpleName());
    }

    StringBuilder sb =
        new StringBuilder(String.format(Locale.US, "%s sizes:\n[", cls.getSimpleName()));
    for (int i = 0; i < outputSizes.length; i++) {
      Size size = outputSizes[i];
      long minFrameDuration = streamConfigurationMap.getOutputMinFrameDuration(cls, size);
      float minFrameMs = minFrameDuration / 1000000.0f;
      long stallDuration = streamConfigurationMap.getOutputStallDuration(cls, size);
      float stallMs = stallDuration / 1000000.0f;
      sb.append(
          String.format(
              Locale.US,
              "%s (Min frame duration: %.3f ms, Stall duration: %.1f ms)",
              size,
              minFrameMs,
              stallMs));
      if (i < outputSizes.length - 1) {
        sb.append(", ");
      }
    }
    sb.append("]");
    return sb.toString();
  }

  public static String formatHighSpeedSizes(StreamConfigurationMap streamConfigurationMap) {
    if (streamConfigurationMap == null) return "High Speed sizes: []";

    Size[] highSpeedSizes = streamConfigurationMap.getHighSpeedVideoSizes();
    if (highSpeedSizes == null || highSpeedSizes.length == 0) {
      return "High Speed sizes: []";
    }

    StringBuilder sb = new StringBuilder("High Speed sizes:\n[");
    for (int i = 0; i < highSpeedSizes.length; i++) {
      Size size = highSpeedSizes[i];
      Range<Integer>[] fpsRanges = streamConfigurationMap.getHighSpeedVideoFpsRangesFor(size);
      String fpsString = formatFpsRanges(fpsRanges);
      sb.append(String.format(Locale.US, "%s (FPS ranges: %s)", size, fpsString));
      if (i < highSpeedSizes.length - 1) {
        sb.append(", ");
      }
    }
    sb.append("]");
    return sb.toString();
  }

  public static String formatOutputSizes(CameraCharacteristics cameraCharacteristics) {
    if (cameraCharacteristics == null) return "No camera characteristics available";

    StringBuilder sb = new StringBuilder();
    StreamConfigurationMap streamConfigurationMap =
        cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
    if (streamConfigurationMap == null) return "No stream configuration map available";

    int[] outputFormats = streamConfigurationMap.getOutputFormats();
    if (outputFormats == null || outputFormats.length == 0) {
      sb.append("Output formats: []");
    } else {
      sb.append("Output formats: [");
      for (int i = 0; i < outputFormats.length; i++) {
        sb.append(
            String.format(
                Locale.US,
                "%s (%s)",
                getFormatName(outputFormats[i]),
                getFormatDescription(outputFormats[i])));
        if (i < outputFormats.length - 1) {
          sb.append(", ");
        }
      }
      sb.append("]\n\n");
      for (int format : outputFormats) {
        sb.append(formatSizes(streamConfigurationMap, format)).append("\n\n");
      }
    }

    Class<?>[] outputClasses =
        new Class<?>[] {
          ImageReader.class,
          SurfaceTexture.class,
          Allocation.class,
          SurfaceHolder.class,
          MediaCodec.class,
          MediaRecorder.class
        };
    for (Class<?> cls : outputClasses) {
      if (streamConfigurationMap.isOutputSupportedFor(cls)) {
        sb.append(formatSizes(streamConfigurationMap, cls)).append("\n\n");
      }
    }

    sb.append(formatHighSpeedSizes(streamConfigurationMap));
    return sb.toString();
  }

  private static String getFormatName(int format) {
    return FORMAT_NAME_MAP.getOrDefault(format, String.format("UNKNOWN_0x%X", format))
        + "("
        + String.valueOf(format)
        + ")";
  }

  private static String getFormatDescription(int format) {
    return FORMAT_DESCRIPTION_MAP.getOrDefault(format, "Unknown format");
  }

  private static String formatFpsRanges(Range<Integer>[] fpsRanges) {
    if (fpsRanges == null || fpsRanges.length == 0) return "None";
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < fpsRanges.length; i++) {
      Range<Integer> range = fpsRanges[i];
      if (range.getLower().equals(range.getUpper())) {
        sb.append(range.getLower());
      } else {
        sb.append(String.format("%d-%d", range.getLower(), range.getUpper()));
      }
      if (i < fpsRanges.length - 1) {
        sb.append(", ");
      }
    }
    return sb.toString();
  }

  private static Size[] concatenate(Size[] a, Size[] b) {
    if (a == null) return b;
    if (b == null) return a;
    Size[] result = new Size[a.length + b.length];
    System.arraycopy(a, 0, result, 0, a.length);
    System.arraycopy(b, 0, result, a.length, b.length);
    return result;
  }
}
