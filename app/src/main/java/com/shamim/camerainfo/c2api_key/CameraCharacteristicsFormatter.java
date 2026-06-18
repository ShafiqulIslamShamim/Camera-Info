package com.shamim.camerainfo.c2api_key;

import android.graphics.Rect;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.params.BlackLevelPattern;
import android.util.Range;
import android.util.Rational;
import android.util.Size;
import android.util.SizeF;
import com.shamim.camerainfo.activity.*;
import com.shamim.camerainfo.exception_catcher.*;
import com.shamim.camerainfo.preference.*;
import com.shamim.camerainfo.recycle_view.*;
import com.shamim.camerainfo.update_checker.*;
import com.shamim.camerainfo.util.*;
import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.*;

public class CameraCharacteristicsFormatter {

  public static String formatCameraCharacteristics(CameraCharacteristics c) {
    if (c == null) return "No info";

    StringBuilder sb = new StringBuilder("Camera characteristics:\n\n");

    for (CameraCharacteristics.Key<?> key : c.getKeys()) {
      Object value = c.get(key);
      sb.append(key.getName()).append(":  ");

      if (value == null) {
        sb.append("null\n");
        continue;
      }

      sb.append(formatValue(key.getName(), value)).append("\n");
    }
    return sb.toString();
  }

  // Main value formatter
  private static String formatValue(String name, Object value) {

    // -------------------- SCALER - MANDATORY STREAM COMBINATIONS --------------------
    if (name.equals("android.scaler.mandatoryStreamCombinations")
        || name.equals("android.scaler.mandatoryConcurrentStreamCombinations")
        || name.equals("android.scaler.mandatoryMaximumResolutionStreamCombinations")
        || name.equals("android.scaler.mandatoryPreviewStabilizationOutputStreamCombinations")
        || name.equals("android.scaler.mandatoryTenBitOutputStreamCombinations")
        || name.equals("android.scaler.mandatoryUseCaseStreamCombinations")) {

      return formatMandatoryStreamCombinations(value);
    }

    // -------------------- ANDROID AUTOMOTIVE --------------------
    if (name.equals("android.automotive.lens.facing") && value instanceof int[]) {
      Map<Integer, String> map = new LinkedHashMap<>();
      map.put(0, "EXTERIOR");
      map.put(1, "INTERIOR");
      map.put(2, "REARVIEW");
      return mapArray((int[]) value, map);
    }

    if (name.equals("android.automotive.location") && value instanceof Integer) {
      return mapOne((int) value, Map.of(0, "EXTERIOR", 1, "INTERIOR", 2, "REARVIEW"));
    }

    // -------------------- COLOR CORRECTION --------------------
    if (name.equals("android.colorCorrection.availableAberrationModes") && value instanceof int[]) {
      return mapArray((int[]) value, Map.of(0, "OFF", 1, "FAST", 2, "HIGH_QUALITY"));
    }

    if (name.equals("android.colorCorrection.availableModes") && value instanceof int[]) {
      return mapArray((int[]) value, Map.of(0, "TRANSFORM_MATRIX", 1, "FAST", 2, "HIGH_QUALITY"));
    }

    if (name.equals("android.colorCorrection.colorTemperatureRange") && value instanceof Range) {
      return value.toString() + " (Range of supported color temperatures in Kelvin)";
    }

    // -------------------- CONTROL / AE --------------------
    if (name.equals("android.control.aeAvailableAntibandingModes") && value instanceof int[]) {
      return mapArray((int[]) value, Map.of(0, "OFF", 1, "50HZ", 2, "60HZ", 3, "AUTO"));
    }

    if (name.equals("android.control.aeAvailableModes") && value instanceof int[]) {
      return mapArray(
          (int[]) value,
          Map.of(
              0,
              "OFF",
              1,
              "ON",
              2,
              "ON_AUTO_FLASH",
              3,
              "ON_ALWAYS_FLASH",
              4,
              "ON_AUTO_FLASH_REDEYE",
              5,
              "ON_EXTERNAL_FLASH"));
    }

    if (name.equals("android.control.aeAvailablePriorityModes") && value instanceof int[]) {
      return mapArray(
          (int[]) value,
          Map.of(0, "PRIORITY_AUTO", 1, "PRIORITY_LOW_POWER", 2, "PRIORITY_HIGH_QUALITY"));
    }

    if (name.equals("android.control.aeAvailableTargetFpsRanges") && value.getClass().isArray()) {
      @SuppressWarnings("unchecked")
      Range<Integer>[] ranges = (Range<Integer>[]) value;
      List<String> list = new ArrayList<>();
      for (Range<Integer> range : ranges) {
        list.add(String.format(Locale.US, "[%d, %d]", range.getLower(), range.getUpper()));
      }
      return "[" + String.join(", ", list) + "] (Supported FPS ranges)";
    }

    if (name.equals("android.control.aeCompensationRange") && value instanceof Range) {
      return value.toString() + " (Exposure compensation range in EV steps)";
    }

    if (name.equals("android.control.aeCompensationStep") && value instanceof Rational) {
      return value.toString() + " (Exposure compensation step size in EV units)";
    }

    if (name.equals("android.control.aeLockAvailable") && value instanceof Boolean) {
      return value.toString() + " (Whether AE lock is supported)";
    }

    if (name.equals("android.control.afAvailableModes") && value instanceof int[]) {
      return mapArray(
          (int[]) value,
          Map.of(
              0,
              "OFF",
              1,
              "AUTO",
              2,
              "MACRO",
              3,
              "CONTINUOUS_VIDEO",
              4,
              "CONTINUOUS_PICTURE",
              5,
              "EDOF"));
    }

    if (name.equals("android.control.autoframingAvailable") && value instanceof Boolean) {
      return value.toString() + " (Whether autoframing is supported)";
    }

    if (name.equals("android.control.availableEffects") && value instanceof int[]) {
      return mapArray(
          (int[]) value,
          Map.of(
              0,
              "OFF",
              1,
              "MONO",
              2,
              "NEGATIVE",
              3,
              "SOLARIZE",
              4,
              "SEPIA",
              5,
              "POSTERIZE",
              6,
              "WHITEBOARD",
              7,
              "BLACKBOARD",
              8,
              "AQUA"));
    }

    if (name.equals("android.control.availableExtendedSceneModeCapabilities")
        && value.getClass().isArray()) {
      return formatArray(value, "Extended scene mode capabilities");
    }

    if (name.equals("android.control.availableModes") && value instanceof int[]) {
      return mapArray((int[]) value, Map.of(0, "AUTO", 1, "USE_SCENE_MODE", 2, "OFF"));
    }

    if (name.equals("android.control.availableSceneModes") && value instanceof int[]) {
      Map<Integer, String> map = new LinkedHashMap<>();
      map.put(0, "DISABLED");
      map.put(1, "FACE_PRIORITY");
      map.put(2, "ACTION");
      map.put(3, "PORTRAIT");
      map.put(4, "LANDSCAPE");
      map.put(5, "NIGHT");
      map.put(6, "NIGHT_PORTRAIT");
      map.put(7, "THEATRE");
      map.put(8, "BEACH");
      map.put(9, "SNOW");
      map.put(10, "SUNSET");
      map.put(12, "FIREWORKS");
      map.put(13, "SPORTS");
      map.put(14, "PARTY");
      map.put(15, "CANDLELIGHT");
      map.put(18, "BARCODE");
      return mapArray((int[]) value, map);
    }

    if (name.equals("android.control.availableSettingsOverrides") && value instanceof int[]) {
      return mapArray(
          (int[]) value,
          Map.of(0, "NONE", 1, "AE_MODE", 2, "AWB_MODE", 3, "AF_MODE", 4, "EFFECT_MODE"));
    }

    if (name.equals("android.control.availableVideoStabilizationModes") && value instanceof int[]) {
      return mapArray((int[]) value, Map.of(0, "OFF", 1, "ON", 2, "PREVIEW_STABILIZATION"));
    }

    if (name.equals("android.control.awbAvailableModes") && value instanceof int[]) {
      return mapArray(
          (int[]) value,
          Map.of(
              0,
              "OFF",
              1,
              "AUTO",
              2,
              "INCANDESCENT",
              3,
              "FLUORESCENT",
              4,
              "WARM_FLUORESCENT",
              5,
              "DAYLIGHT",
              6,
              "CLOUDY_DAYLIGHT",
              7,
              "TWILIGHT",
              8,
              "SHADE"));
    }

    if (name.equals("android.control.awbLockAvailable") && value instanceof Boolean) {
      return value.toString() + " (Whether AWB lock is supported)";
    }

    if (name.equals("android.control.lowLightBoostInfoLuminanceRange") && value instanceof Range) {
      return value.toString() + " (Luminance range for low light boost in lux)";
    }

    if (name.equals("android.control.maxRegionsAe") && value instanceof Integer) {
      return value.toString() + " (Maximum number of auto-exposure regions supported)";
    }

    if (name.equals("android.control.maxRegionsAf") && value instanceof Integer) {
      return value.toString() + " (Maximum number of auto-focus regions supported)";
    }

    if (name.equals("android.control.maxRegionsAwb") && value instanceof Integer) {
      return value.toString() + " (Maximum number of auto-white-balance regions supported)";
    }

    if (name.equals("android.control.postRawSensitivityBoostRange") && value instanceof Range) {
      return value.toString() + " (Post-RAW sensitivity boost range in percentage)";
    }

    if (name.equals("android.control.zoomRatioRange") && value instanceof Range) {
      return value.toString() + " (Supported zoom ratio range)";
    }

    // -------------------- DEPTH --------------------
    if (name.equals("android.depth.depthIsExclusive") && value instanceof Boolean) {
      return value.toString() + " (Whether depth output is exclusive with color outputs)";
    }

    // -------------------- DISTORTION CORRECTION --------------------
    if (name.equals("android.distortionCorrection.availableModes") && value instanceof int[]) {
      return mapArray((int[]) value, Map.of(0, "NO", 1, "GEOMETRIC", 2, "GEOMETRIC_AND_CHROMATIC"));
    }

    // -------------------- EDGE --------------------
    if (name.equals("android.edge.availableEdgeModes") && value instanceof int[]) {
      return mapArray(
          (int[]) value, Map.of(0, "OFF", 1, "FAST", 2, "HIGH_QUALITY", 3, "ZERO_SHUTTER_LAG"));
    }

    // -------------------- FLASH --------------------
    if (name.equals("android.flash.info.available") && value instanceof Boolean) {
      return value.toString() + " (Whether the camera has a flash unit)";
    }

    if (name.equals("android.flash.infoStrengthDefaultLevel") && value instanceof Integer) {
      return value.toString() + " (Default flashlight strength level)";
    }

    if (name.equals("android.flash.infoStrengthMaximumLevel") && value instanceof Integer) {
      return value.toString() + " (Maximum flashlight strength level)";
    }

    if (name.equals("android.flash.singleStrengthDefaultLevel") && value instanceof Integer) {
      return value.toString() + " (Default flash strength for SINGLE mode)";
    }

    if (name.equals("android.flash.singleStrengthMaxLevel") && value instanceof Integer) {
      return value.toString() + " (Maximum flash strength for SINGLE mode)";
    }

    if (name.equals("android.flash.torchStrengthDefaultLevel") && value instanceof Integer) {
      return value.toString() + " (Default torch strength level)";
    }

    if (name.equals("android.flash.torchStrengthMaxLevel") && value instanceof Integer) {
      return value.toString() + " (Maximum torch strength level)";
    }

    // -------------------- HOT PIXEL --------------------
    if (name.equals("android.hotPixel.availableHotPixelModes") && value instanceof int[]) {
      return mapArray((int[]) value, Map.of(0, "OFF", 1, "FAST", 2, "HIGH_QUALITY"));
    }

    // -------------------- INFO --------------------
    if (name.equals("android.info.deviceStateSensorOrientationMap") && value != null) {
      return value.toString() + " (Device state sensor orientation map for foldable devices)";
    }

    if (name.equals("android.info.sessionConfigurationQueryVersion") && value instanceof Integer) {
      return value.toString() + " (Session configuration query API version)";
    }

    if (name.equals("android.info.supportedHardwareLevel") && value instanceof Integer) {
      return mapOne(
          (int) value, Map.of(0, "LIMITED", 1, "FULL", 2, "LEGACY", 3, "LEVEL_3", 4, "EXTERNAL"));
    }

    if (name.equals("android.info.version") && value instanceof String) {
      return "\"" + value.toString() + "\" (Manufacturer version info)";
    }

    // -------------------- JPEG --------------------
    if (name.equals("android.jpeg.availableThumbnailSizes") && value.getClass().isArray()) {
      Size[] sizes = (Size[]) value;
      List<String> list = new ArrayList<>();
      for (Size size : sizes) {
        list.add(size.getWidth() + "x" + size.getHeight());
      }
      return "[" + String.join(", ", list) + "] (Supported JPEG thumbnail sizes)";
    }

    // -------------------- LENS --------------------
    if (name.equals("android.lens.distortion") && value instanceof float[]) {
      return formatFloatArray((float[]) value) + " (Lens distortion correction coefficients)";
    }

    if (name.equals("android.lens.distortionMaximumResolution") && value instanceof float[]) {
      return formatFloatArray((float[]) value)
          + " (Lens distortion coefficients for max resolution)";
    }

    if (name.equals("android.lens.facing") && value instanceof Integer) {
      return mapOne((int) value, Map.of(0, "BACK", 1, "FRONT", 2, "EXTERNAL"));
    }

    if (name.equals("android.lens.info.availableApertures") && value instanceof float[]) {
      return formatFloatArray((float[]) value) + " (Supported aperture values)";
    }

    if (name.equals("android.lens.info.availableFilterDensities") && value instanceof float[]) {
      return formatFloatArray((float[]) value) + " (Supported filter density)";
    }

    if (name.equals("android.lens.info.availableFocalLengths") && value instanceof float[]) {
      return formatFloatArray((float[]) value) + " (Supported focal lengths in mm)";
    }

    if (name.equals("android.lens.info.availableOpticalStabilization") && value instanceof int[]) {
      return mapArray((int[]) value, Map.of(0, "OFF", 1, "ON"));
    }

    if (name.equals("android.lens.info.focusDistanceCalibration") && value instanceof Integer) {
      return mapOne((int) value, Map.of(0, "UNCALIBRATED", 1, "APPROXIMATE", 2, "CALIBRATED"));
    }

    if (name.equals("android.lens.info.hyperfocalDistance") && value instanceof Float) {
      return value.toString() + " (Hyperfocal distance in diopters)";
    }

    if (name.equals("android.lens.info.minimumFocusDistance") && value instanceof Float) {
      return value.toString() + " (Minimum focus distance in diopters)";
    }

    if (name.equals("android.lens.intrinsicCalibration") && value instanceof float[]) {
      return formatFloatArray((float[]) value) + " (Intrinsic calibration parameters)";
    }

    if (name.equals("android.lens.intrinsicCalibrationMaximumResolution")
        && value instanceof float[]) {
      return formatFloatArray((float[]) value) + " (Intrinsic calibration for max resolution)";
    }

    if (name.equals("android.lens.poseReference") && value instanceof Integer) {
      return mapOne(
          (int) value,
          Map.of(0, "OPTICAL_CENTER", 1, "SECONDARY_OPTICAL_CENTER", 2, "EXTERNAL_BODY_ORIGIN"));
    }

    if (name.equals("android.lens.poseRotation") && value instanceof float[]) {
      return formatFloatArray((float[]) value) + " (Pose rotation quaternion)";
    }

    if (name.equals("android.lens.poseTranslation") && value instanceof float[]) {
      return formatFloatArray((float[]) value) + " (Pose translation vector in meters)";
    }

    if (name.equals("android.lens.radialDistortion") && value instanceof float[]) {
      return formatFloatArray((float[]) value) + " (Deprecated radial distortion)";
    }

    // -------------------- LOGICAL MULTI CAMERA --------------------
    if (name.equals("android.logicalMultiCamera.sensorSyncType") && value instanceof Integer) {
      return mapOne((int) value, Map.of(0, "NONE", 1, "HARDWARE_SYNC", 2, "HARDWARE_SYNC_STRICT"));
    }

    // -------------------- NOISE REDUCTION --------------------
    if (name.equals("android.noiseReduction.availableNoiseReductionModes")
        && value instanceof int[]) {
      return mapArray(
          (int[]) value,
          Map.of(0, "OFF", 1, "FAST", 2, "HIGH_QUALITY", 3, "MINIMAL", 4, "ZERO_SHUTTER_LAG"));
    }

    // -------------------- REPROCESS --------------------
    if (name.equals("android.reprocess.maxCaptureStall") && value instanceof Integer) {
      return value.toString() + " (Maximum capture stall frames for reprocessing)";
    }

    // -------------------- REQUEST --------------------
    if (name.equals("android.request.availableCapabilities") && value instanceof int[]) {
      Map<Integer, String> map = new LinkedHashMap<>();
      map.put(0, "BACKWARD_COMPATIBLE");
      map.put(1, "MANUAL_SENSOR");
      map.put(2, "MANUAL_POST_PROCESSING");
      map.put(3, "RAW");
      map.put(4, "BURST_CAPTURE");
      map.put(5, "READ_SENSOR_SETTINGS");
      map.put(6, "YUV_REPROCESSING");
      map.put(7, "PRIVATE_REPROCESSING");
      map.put(8, "DEPTH_OUTPUT");
      map.put(9, "CONSTRAINED_HIGH_SPEED_VIDEO");
      map.put(10, "LOGICAL_MULTI_CAMERA");
      map.put(11, "DYNAMIC_RANGE_TEN_BIT");
      map.put(12, "DYNAMIC_RANGE_HDR10");
      return mapArray((int[]) value, map);
    }

    if (name.equals("android.request.availableColorSpaceProfiles") && value != null) {
      return value.toString() + " (Supported color space profiles)";
    }

    if (name.equals("android.request.availableDynamicRangeProfiles") && value != null) {
      return value.toString() + " (Supported dynamic range profiles)";
    }

    if (name.equals("android.request.maxNumInputStreams") && value instanceof Integer) {
      return value.toString() + " (Maximum input streams)";
    }

    if (name.equals("android.request.maxNumOutputProc") && value instanceof Integer) {
      return value.toString() + " (Maximum processed output streams)";
    }

    if (name.equals("android.request.maxNumOutputProcStalling") && value instanceof Integer) {
      return value.toString() + " (Maximum stalling processed output streams)";
    }

    if (name.equals("android.request.maxNumOutputRaw") && value instanceof Integer) {
      return value.toString() + " (Maximum RAW output streams)";
    }

    if (name.equals("android.request.partialResultCount") && value instanceof Integer) {
      return value.toString() + " (Partial result count)";
    }

    if (name.equals("android.request.pipelineMaxDepth") && value instanceof Integer) {
      return value.toString() + " (Pipeline max depth)";
    }

    if (name.equals("android.request.recommendedTenBitDynamicRangeProfile")
        && value instanceof Long) {
      return value.toString() + " (Recommended 10-bit dynamic range profile)";
    }

    // -------------------- SCALER --------------------
    if (name.equals("android.scaler.availableMaxDigitalZoom") && value instanceof Float) {
      return String.format(Locale.US, "%.1f", value) + "x (Maximum digital zoom)";
    }

    if (name.equals("android.scaler.availableRotateAndCropModes") && value instanceof int[]) {
      return mapArray(
          (int[]) value,
          Map.of(
              0,
              "NONE",
              1,
              "ROTATE_90",
              2,
              "ROTATE_180",
              3,
              "ROTATE_270",
              4,
              "FLIP_H",
              5,
              "FLIP_V"));
    }

    if (name.equals("android.scaler.availableStreamUseCases") && value instanceof long[]) {
      return formatLongArray((long[]) value) + " (Supported stream use cases)";
    }

    if (name.equals("android.scaler.croppingType") && value instanceof Integer) {
      return mapOne((int) value, Map.of(0, "CENTER_ONLY", 1, "FREEFORM"));
    }

    if (name.equals("android.scaler.defaultSecureImageSize") && value instanceof Size) {
      Size size = (Size) value;
      return size.getWidth() + "x" + size.getHeight() + " (Default secure image size)";
    }

    if (name.equals("android.scaler.mandatoryConcurrentStreamCombinations")
        && value.getClass().isArray()) {
      return formatArray(value, "Mandatory concurrent stream combinations");
    }

    if (name.equals("android.scaler.mandatoryMaximumResolutionStreamCombinations")
        && value.getClass().isArray()) {
      return formatArray(value, "Mandatory max resolution stream combinations");
    }

    if (name.equals("android.scaler.mandatoryPreviewStabilizationOutputStreamCombinations")
        && value.getClass().isArray()) {
      return formatArray(value, "Mandatory preview stabilization stream combinations");
    }

    if (name.equals("android.scaler.mandatoryStreamCombinations") && value.getClass().isArray()) {
      return formatArray(value, "Mandatory stream combinations");
    }

    if (name.equals("android.scaler.mandatoryTenBitOutputStreamCombinations")
        && value.getClass().isArray()) {
      return formatArray(value, "Mandatory 10-bit output stream combinations");
    }

    if (name.equals("android.scaler.mandatoryUseCaseStreamCombinations")
        && value.getClass().isArray()) {
      return formatArray(value, "Mandatory use case stream combinations");
    }

    if (name.equals("android.scaler.multiResolutionStreamConfigurationMap") && value != null) {
      return value.toString() + " (Multi-resolution stream config map)";
    }

    if (name.equals("android.scaler.streamConfigurationMap") && value != null) {
      return value.toString() + " (Stream configuration map)";
    }

    if (name.equals("android.scaler.streamConfigurationMapMaximumResolution") && value != null) {
      return value.toString() + " (Max resolution stream config map)";
    }

    // -------------------- SENSOR --------------------
    if (name.equals("android.sensor.availableTestPatternModes") && value instanceof int[]) {
      return mapArray(
          (int[]) value,
          Map.of(
              0,
              "OFF",
              1,
              "SOLID_COLOR",
              2,
              "COLOR_BARS",
              3,
              "COLOR_BARS_FADE_TO_GRAY",
              4,
              "PN9",
              5,
              "CUSTOM1"));
    }

    if (name.equals("android.sensor.blackLevelPattern") && value instanceof BlackLevelPattern) {
      return value.toString() + " (Black level pattern)";
    }

    if (name.equals("android.sensor.calibrationTransform1") && value != null) {
      return value.toString() + " (Calibration transform 1 matrix)";
    }

    if (name.equals("android.sensor.calibrationTransform2") && value != null) {
      return value.toString() + " (Calibration transform 2 matrix)";
    }

    if (name.equals("android.sensor.colorTransform1") && value != null) {
      return value.toString() + " (Color transform 1 matrix)";
    }

    if (name.equals("android.sensor.colorTransform2") && value != null) {
      return value.toString() + " (Color transform 2 matrix)";
    }

    if (name.equals("android.sensor.forwardMatrix1") && value != null) {
      return value.toString() + " (Forward matrix 1)";
    }

    if (name.equals("android.sensor.forwardMatrix2") && value != null) {
      return value.toString() + " (Forward matrix 2)";
    }

    if (name.equals("android.sensor.info.activeArraySize") && value instanceof Rect) {
      return value.toString() + " (Active array size)";
    }

    if (name.equals("android.sensor.info.activeArraySizeMaximumResolution")
        && value instanceof Rect) {
      return value.toString() + " (Active array size for max resolution)";
    }

    if (name.equals("android.sensor.info.binningFactor") && value instanceof Size) {
      Size size = (Size) value;
      return size.getWidth() + "x" + size.getHeight() + " (Binning factor)";
    }

    if (name.equals("android.sensor.info.colorFilterArrangement") && value instanceof Integer) {
      return mapOne((int) value, Map.of(0, "RGGB", 1, "GRBG", 2, "GBRG", 3, "BGGR", 4, "RGB"));
    }

    if (name.equals("android.sensor.info.exposureTimeRange") && value instanceof Range) {
      return value.toString() + " (Exposure time range in ns)";
    }

    if (name.equals("android.sensor.info.lensShadingApplied") && value instanceof Boolean) {
      return value.toString() + " (Lens shading applied to RAW)";
    }

    if (name.equals("android.sensor.info.maxFrameDuration") && value instanceof Long) {
      return value.toString() + " (Max frame duration in ns)";
    }

    if (name.equals("android.sensor.info.physicalSize") && value instanceof SizeF) {
      SizeF size = (SizeF) value;
      return String.format(Locale.US, "%.5fx%.5f mm", size.getWidth(), size.getHeight())
          + " (Physical sensor size)";
    }

    if (name.equals("android.sensor.info.pixelArraySize") && value instanceof Size) {
      Size size = (Size) value;
      return size.getWidth() + "x" + size.getHeight() + " (Pixel array size)";
    }

    if (name.equals("android.sensor.info.pixelArraySizeMaximumResolution")
        && value instanceof Size) {
      Size size = (Size) value;
      return size.getWidth() + "x" + size.getHeight() + " (Pixel array size for max resolution)";
    }

    if (name.equals("android.sensor.info.preCorrectionActiveArraySize") && value instanceof Rect) {
      return value.toString() + " (Pre-correction active array size)";
    }

    if (name.equals("android.sensor.info.preCorrectionActiveArraySizeMaximumResolution")
        && value instanceof Rect) {
      return value.toString() + " (Pre-correction active array size for max resolution)";
    }

    if (name.equals("android.sensor.info.sensitivityRange") && value instanceof Range) {
      return value.toString() + " (Sensitivity range (ISO))";
    }

    if (name.equals("android.sensor.info.timestampSource") && value instanceof Integer) {
      return mapOne((int) value, Map.of(0, "UNKNOWN", 1, "REALTIME"));
    }

    if (name.equals("android.sensor.info.whiteLevel") && value instanceof Integer) {
      return value.toString() + " (Max raw white level)";
    }

    if (name.equals("android.sensor.maxAnalogSensitivity") && value instanceof Integer) {
      return value.toString() + " (Max analog ISO)";
    }

    if (name.equals("android.sensor.opticalBlackRegions") && value.getClass().isArray()) {
      return formatRectArray((Rect[]) value) + " (Optical black regions)";
    }

    if (name.equals("android.sensor.orientation") && value instanceof Integer) {
      return mapOne(
          (int) value,
          Map.of(0, "0_DEGREES", 90, "90_DEGREES", 180, "180_DEGREES", 270, "270_DEGREES"));
    }

    if (name.equals("android.sensor.readoutTimestamp") && value instanceof Integer) {
      return mapOne((int) value, Map.of(0, "DISABLED", 1, "ENABLED"));
    }

    if (name.equals("android.sensor.referenceIlluminant1") && value instanceof Integer) {
      Map<Integer, String> map = new LinkedHashMap<>();
      map.put(0, "OTHER");
      map.put(1, "UNKNOWN");
      map.put(2, "DAYLIGHT");
      map.put(3, "FLUORESCENT");
      map.put(4, "TUNGSTEN");
      map.put(17, "D65");
      map.put(19, "D50");
      map.put(20, "D55");
      map.put(21, "D75");
      return mapOne((int) value, map);
    }

    if (name.equals("android.sensor.referenceIlluminant2") && value instanceof Integer) {
      Map<Integer, String> map = new LinkedHashMap<>();
      map.put(0, "OTHER");
      map.put(1, "UNKNOWN");
      map.put(2, "DAYLIGHT");
      map.put(3, "FLUORESCENT");
      map.put(4, "TUNGSTEN");
      map.put(17, "D65");
      map.put(19, "D50");
      map.put(20, "D55");
      map.put(21, "D75");
      return mapOne((int) value, map);
    }

    // -------------------- SHADING --------------------
    if (name.equals("android.shading.availableModes") && value instanceof int[]) {
      return mapArray((int[]) value, Map.of(0, "OFF", 1, "FAST", 2, "HIGH_QUALITY"));
    }

    // -------------------- STATISTICS --------------------
    if (name.equals("android.statistics.info.availableFaceDetectModes") && value instanceof int[]) {
      return mapArray((int[]) value, Map.of(0, "OFF", 1, "SIMPLE", 2, "FULL"));
    }

    if (name.equals("android.statistics.info.availableHotPixelMapModes")
        && value instanceof boolean[]) {
      boolean[] modes = (boolean[]) value;
      List<String> list = new ArrayList<>();
      for (boolean b : modes) list.add(b ? "ON" : "OFF");
      return "[" + String.join(", ", list) + "] (Hot pixel map modes)";
    }

    if (name.equals("android.statistics.info.availableLensShadingMapModes")
        && value instanceof int[]) {
      return mapArray((int[]) value, Map.of(0, "OFF", 1, "ON"));
    }

    if (name.equals("android.statistics.info.availableOisDataModes") && value instanceof int[]) {
      return mapArray((int[]) value, Map.of(0, "OFF", 1, "ON"));
    }

    if (name.equals("android.statistics.info.maxFaceCount") && value instanceof Integer) {
      return value.toString() + " (Max detectable faces)";
    }

    // -------------------- SYNC --------------------
    if (name.equals("android.sync.maxLatency") && value instanceof Integer) {
      return mapOne((int) value, Map.of(-1, "UNKNOWN", 0, "PER_FRAME_CONTROL"));
    }

    // -------------------- TONEMAP --------------------
    if (name.equals("android.tonemap.availableToneMapModes") && value instanceof int[]) {
      return mapArray((int[]) value, Map.of(0, "CONTRAST_CURVE", 1, "FAST", 2, "HIGH_QUALITY"));
    }

    if (name.equals("android.tonemap.maxCurvePoints") && value instanceof Integer) {
      return value.toString() + " (Max tonemap curve points)";
    }

    // -------------------- GENERIC HANDLING --------------------
    if (value.getClass().isArray()) {
      return formatGenericArray(value) + " (Array for " + name + ")";
    }

    if (value instanceof Range) {
      return value.toString() + " (Range for " + name + ")";
    }

    if (value instanceof Rect) {
      return value.toString() + " (Rect for " + name + ")";
    }

    if (value instanceof Size) {
      Size size = (Size) value;
      return size.getWidth() + "x" + size.getHeight() + " (Size for " + name + ")";
    }

    if (value instanceof SizeF) {
      SizeF size = (SizeF) value;
      return String.format(Locale.US, "%.5fx%.5f", size.getWidth(), size.getHeight())
          + " (SizeF for "
          + name
          + ")";
    }

    if (value instanceof Rational) {
      return value.toString() + " (Rational for " + name + ")";
    }

    if (value instanceof Boolean) {
      return value.toString() + " (Boolean for " + name + ")";
    }

    if (value instanceof Integer
        || value instanceof Byte
        || value instanceof Long
        || value instanceof Float) {
      return value.toString() + " (Numeric for " + name + ")";
    }

    return value.toString() + " (Value for " + name + ")";
  }

  private static String mapOne(int v, Map<Integer, String> map) {
    String label = map.getOrDefault(v, "UNKNOWN_" + v);
    return v + "(" + label + ")";
  }

  private static String mapArray(int[] vals, Map<Integer, String> map) {
    List<String> list = new ArrayList<>();
    for (int v : vals) {
      list.add(mapOne(v, map));
    }
    return "[" + String.join(", ", list) + "]";
  }

  private static String formatGenericArray(Object arr) {
    int len = Array.getLength(arr);
    StringBuilder sb = new StringBuilder("[");
    for (int i = 0; i < len; i++) {
      sb.append(Array.get(arr, i).toString());
      if (i < len - 1) sb.append(", ");
    }
    sb.append("]");
    return sb.toString();
  }

  private static String formatFloatArray(float[] arr) {
    StringBuilder sb = new StringBuilder("[");
    for (int i = 0; i < arr.length; i++) {
      sb.append(String.format(Locale.US, "%.4f", arr[i]));
      if (i < arr.length - 1) sb.append(", ");
    }
    sb.append("]");
    return sb.toString();
  }

  private static String formatLongArray(long[] arr) {
    StringBuilder sb = new StringBuilder("[");
    for (int i = 0; i < arr.length; i++) {
      sb.append(arr[i]);
      if (i < arr.length - 1) sb.append(", ");
    }
    sb.append("]");
    return sb.toString();
  }

  private static String formatRectArray(Rect[] arr) {
    StringBuilder sb = new StringBuilder("[");
    for (int i = 0; i < arr.length; i++) {
      sb.append(arr[i].toString());
      if (i < arr.length - 1) sb.append(", ");
    }
    sb.append("]");
    return sb.toString();
  }

  private static String formatArray(Object arr, String desc) {
    return formatGenericArray(arr) + " (" + desc + ")";
  }

  private static String formatMandatoryStreamCombinations(Object value) {
    if (value == null || !value.getClass().isArray()) return "null";

    int length = Array.getLength(value);
    if (length == 0) return "[]";

    return IntStream.range(0, length)
        .mapToObj(
            i -> {
              Object obj = Array.get(value, i);
              String desc = "Unknown combination";
              if (obj != null) {
                try {
                  var method = obj.getClass().getMethod("getDescription");
                  Object result = method.invoke(obj);
                  if (result != null) desc = result.toString();
                } catch (Exception ignored) {
                }
              }
              return String.format("[%2d] %s", i, desc);
            })
        .collect(Collectors.joining(", "));
  }
}
