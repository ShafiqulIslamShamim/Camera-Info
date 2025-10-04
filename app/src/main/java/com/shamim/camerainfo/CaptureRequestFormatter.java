package com.shamim.camerainfo;

import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import java.util.*;
import java.util.stream.Collectors;

public class CaptureRequestFormatter {

  public static String formatRequestKeys(CameraCharacteristics cameraCharacteristics) {
    if (cameraCharacteristics == null) return "No info";

    StringBuilder sb = new StringBuilder("Request keys:\n\n");
    List<CaptureRequest.Key<?>> keys = cameraCharacteristics.getAvailableCaptureRequestKeys();
    if (keys == null) return "No request keys available";

    for (CaptureRequest.Key<?> key : keys) {
      sb.append(formatKey(key.getName())).append("\n");
    }
    return sb.toString();
  }

  private static String formatKey(String name) {
    String description = getDescription(name);
    String values = getAvailableValues(name);
    return String.format(Locale.US, "%s [%s, %s]", name, description, values);
  }

  private static String getDescription(String name) {
    switch (name) {
        // -------------------- COLOR CORRECTION --------------------
      case "android.colorCorrection.aberrationMode":
        return "Chromatic aberration correction mode";
      case "android.colorCorrection.gains":
        return "Color correction gains for each color channel";
      case "android.colorCorrection.mode":
        return "Color correction processing mode";
      case "android.colorCorrection.transform":
        return "Color correction transform matrix";
      case "android.colorCorrection.colorTemperature":
        return "Color temperature in Kelvin";

        // -------------------- CONTROL --------------------
      case "android.control.aeAntibandingMode":
        return "Auto-exposure antibanding mode";
      case "android.control.aeExposureCompensation":
        return "Exposure compensation value in EV steps";
      case "android.control.aeLock":
        return "Auto-exposure lock state";
      case "android.control.aeMode":
        return "Auto-exposure operating mode";
      case "android.control.aePrecaptureTrigger":
        return "Trigger for auto-exposure precapture metering";
      case "android.control.aeRegions":
        return "Regions for auto-exposure metering";
      case "android.control.aeTargetFpsRange":
        return "Target FPS range for auto-exposure";
      case "android.control.afMode":
        return "Auto-focus operating mode";
      case "android.control.afRegions":
        return "Regions for auto-focus";
      case "android.control.afTrigger":
        return "Trigger for auto-focus";
      case "android.control.autoframing":
        return "Auto-framing mode";
      case "android.control.awbLock":
        return "Auto-white-balance lock state";
      case "android.control.awbMode":
        return "Auto-white-balance operating mode";
      case "android.control.awbRegions":
        return "Regions for auto-white-balance";
      case "android.control.captureIntent":
        return "Intended use of the capture request";
      case "android.control.effectMode":
        return "Special effect mode for image processing";
      case "android.control.enableZsl":
        return "Enable zero shutter lag";
      case "android.control.mode":
        return "Overall control mode for the camera";
      case "android.control.postRawSensitivityBoost":
        return "Post-RAW sensitivity boost in percentage";
      case "android.control.sceneMode":
        return "Scene mode for specific shooting conditions";
      case "android.control.settingsOverride":
        return "Override for specific control settings";
      case "android.control.videoStabilizationMode":
        return "Video stabilization mode";
      case "android.control.zoomRatio":
        return "Zoom ratio for the capture";

        // -------------------- DISTORTION CORRECTION --------------------
      case "android.distortionCorrection.mode":
        return "Distortion correction mode";

        // -------------------- EDGE --------------------
      case "android.edge.mode":
        return "Edge enhancement processing mode";

        // -------------------- FLASH --------------------
      case "android.flash.firingPower":
        return "Flash firing power level";
      case "android.flash.firingTime":
        return "Flash firing time in nanoseconds";
      case "android.flash.mode":
        return "Flash operating mode";
      case "android.flash.singleStrength":
        return "Flash strength for single mode";
      case "android.flash.torchStrength":
        return "Torch strength level";

        // -------------------- HOT PIXEL --------------------
      case "android.hotPixel.mode":
        return "Hot pixel correction mode";

        // -------------------- JPEG --------------------
      case "android.jpeg.gpsCoordinates":
        return "GPS coordinates for JPEG EXIF";
      case "android.jpeg.gpsProcessingMethod":
        return "GPS processing method for JPEG EXIF";
      case "android.jpeg.gpsTimestamp":
        return "GPS timestamp for JPEG EXIF";
      case "android.jpeg.orientation":
        return "JPEG image orientation in degrees";
      case "android.jpeg.quality":
        return "JPEG compression quality";
      case "android.jpeg.thumbnailQuality":
        return "JPEG thumbnail compression quality";
      case "android.jpeg.thumbnailSize":
        return "JPEG thumbnail size";

        // -------------------- LENS --------------------
      case "android.lens.aperture":
        return "Lens aperture (f-number)";
      case "android.lens.filterDensity":
        return "Lens filter density";
      case "android.lens.focalLength":
        return "Lens focal length in millimeters";
      case "android.lens.focusDistance":
        return "Lens focus distance in diopters";
      case "android.lens.opticalStabilizationMode":
        return "Optical image stabilization mode";

        // -------------------- NOISE REDUCTION --------------------
      case "android.noiseReduction.mode":
        return "Noise reduction processing mode";

        // -------------------- SENSOR --------------------
      case "android.sensor.exposureTime":
        return "Exposure time in nanoseconds";
      case "android.sensor.frameDuration":
        return "Frame duration in nanoseconds";
      case "android.sensor.sensitivity":
        return "Sensor sensitivity (ISO)";
      case "android.sensor.testPatternData":
        return "Test pattern data for sensor";
      case "android.sensor.testPatternMode":
        return "Test pattern mode for sensor";
      case "android.sensor.rollingShutterSkew":
        return "Rolling shutter skew in nanoseconds";
      case "android.sensor.timestamp":
        return "Timestamp of the frame in nanoseconds";

        // -------------------- SHADING --------------------
      case "android.shading.mode":
        return "Lens shading correction mode";

        // -------------------- STATISTICS --------------------
      case "android.statistics.faceDetectMode":
        return "Face detection mode";
      case "android.statistics.hotPixelMapMode":
        return "Hot pixel map output mode";
      case "android.statistics.lensShadingMapMode":
        return "Lens shading map output mode";

        // -------------------- TONEMAP --------------------
      case "android.tonemap.curveBlue":
        return "Tonemap curve for blue channel";
      case "android.tonemap.curveGreen":
        return "Tonemap curve for green channel";
      case "android.tonemap.curveRed":
        return "Tonemap curve for red channel";
      case "android.tonemap.mode":
        return "Tonemap processing mode";

      default:
        return "Unknown key";
    }
  }

  private static String getAvailableValues(String name) {
    switch (name) {
        // -------------------- COLOR CORRECTION --------------------
      case "android.colorCorrection.aberrationMode":
        return mapArrayValues(Map.of(0, "OFF", 1, "FAST", 2, "HIGH_QUALITY"));
      case "android.colorCorrection.mode":
        return mapArrayValues(Map.of(0, "TRANSFORM_MATRIX", 1, "FAST", 2, "HIGH_QUALITY"));

        // -------------------- CONTROL --------------------
      case "android.control.aeAntibandingMode":
        return mapArrayValues(Map.of(0, "OFF", 1, "50HZ", 2, "60HZ", 3, "AUTO"));
      case "android.control.aeMode":
        return mapArrayValues(
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
      case "android.control.aePrecaptureTrigger":
        return mapArrayValues(Map.of(0, "IDLE", 1, "START", 2, "CANCEL"));
      case "android.control.afMode":
        return mapArrayValues(
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
      case "android.control.afTrigger":
        return mapArrayValues(Map.of(0, "IDLE", 1, "START", 2, "CANCEL"));
      case "android.control.autoframing":
        return mapArrayValues(Map.of(0, "OFF", 1, "ON"));
      case "android.control.awbMode":
        return mapArrayValues(
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
      case "android.control.captureIntent":
        Map<Integer, String> captureIntentMap = new LinkedHashMap<>();
        captureIntentMap.put(0, "CUSTOM");
        captureIntentMap.put(1, "PREVIEW");
        captureIntentMap.put(2, "STILL_CAPTURE");
        captureIntentMap.put(3, "VIDEO_RECORD");
        captureIntentMap.put(4, "VIDEO_SNAPSHOT");
        captureIntentMap.put(5, "ZERO_SHUTTER_LAG");
        captureIntentMap.put(6, "MANUAL");
        captureIntentMap.put(7, "MOTION_TRACKING");
        captureIntentMap.put(8, "PREVIEW_SECURE");
        return mapArrayValues(captureIntentMap);
      case "android.control.effectMode":
        Map<Integer, String> effectModeMap = new LinkedHashMap<>();
        effectModeMap.put(0, "OFF");
        effectModeMap.put(1, "MONO");
        effectModeMap.put(2, "NEGATIVE");
        effectModeMap.put(3, "SOLARIZE");
        effectModeMap.put(4, "SEPIA");
        effectModeMap.put(5, "POSTERIZE");
        effectModeMap.put(6, "WHITEBOARD");
        effectModeMap.put(7, "BLACKBOARD");
        effectModeMap.put(8, "AQUA");
        return mapArrayValues(effectModeMap);
      case "android.control.mode":
        return mapArrayValues(Map.of(0, "AUTO", 1, "USE_SCENE_MODE", 2, "OFF"));
      case "android.control.sceneMode":
        Map<Integer, String> sceneModeMap = new LinkedHashMap<>();
        sceneModeMap.put(0, "DISABLED");
        sceneModeMap.put(1, "FACE_PRIORITY");
        sceneModeMap.put(2, "ACTION");
        sceneModeMap.put(3, "PORTRAIT");
        sceneModeMap.put(4, "LANDSCAPE");
        sceneModeMap.put(5, "NIGHT");
        sceneModeMap.put(6, "NIGHT_PORTRAIT");
        sceneModeMap.put(7, "THEATRE");
        sceneModeMap.put(8, "BEACH");
        sceneModeMap.put(9, "SNOW");
        sceneModeMap.put(10, "SUNSET");
        sceneModeMap.put(12, "FIREWORKS");
        sceneModeMap.put(13, "SPORTS");
        sceneModeMap.put(14, "PARTY");
        sceneModeMap.put(15, "CANDLELIGHT");
        sceneModeMap.put(18, "BARCODE");
        return mapArrayValues(sceneModeMap);
      case "android.control.settingsOverride":
        return mapArrayValues(
            Map.of(0, "NONE", 1, "AE_MODE", 2, "AWB_MODE", 3, "AF_MODE", 4, "EFFECT_MODE"));
      case "android.control.videoStabilizationMode":
        return mapArrayValues(Map.of(0, "OFF", 1, "ON", 2, "PREVIEW_STABILIZATION"));

        // -------------------- DISTORTION CORRECTION --------------------
      case "android.distortionCorrection.mode":
        return mapArrayValues(Map.of(0, "NO", 1, "GEOMETRIC", 2, "GEOMETRIC_AND_CHROMATIC"));

        // -------------------- EDGE --------------------
      case "android.edge.mode":
        return mapArrayValues(
            Map.of(0, "OFF", 1, "FAST", 2, "HIGH_QUALITY", 3, "ZERO_SHUTTER_LAG"));

        // -------------------- FLASH --------------------
      case "android.flash.mode":
        return mapArrayValues(Map.of(0, "OFF", 1, "SINGLE", 2, "TORCH"));

        // -------------------- HOT PIXEL --------------------
      case "android.hotPixel.mode":
        return mapArrayValues(Map.of(0, "OFF", 1, "FAST", 2, "HIGH_QUALITY"));

        // -------------------- JPEG --------------------
      case "android.jpeg.orientation":
        return mapArrayValues(
            Map.of(0, "0_DEGREES", 90, "90_DEGREES", 180, "180_DEGREES", 270, "270_DEGREES"));

        // -------------------- LENS --------------------
      case "android.lens.opticalStabilizationMode":
        return mapArrayValues(Map.of(0, "OFF", 1, "ON"));

        // -------------------- NOISE REDUCTION --------------------
      case "android.noiseReduction.mode":
        return mapArrayValues(
            Map.of(0, "OFF", 1, "FAST", 2, "HIGH_QUALITY", 3, "MINIMAL", 4, "ZERO_SHUTTER_LAG"));

        // -------------------- SENSOR --------------------
      case "android.sensor.testPatternMode":
        return mapArrayValues(
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

        // -------------------- SHADING --------------------
      case "android.shading.mode":
        return mapArrayValues(Map.of(0, "OFF", 1, "FAST", 2, "HIGH_QUALITY"));

        // -------------------- STATISTICS --------------------
      case "android.statistics.faceDetectMode":
        return mapArrayValues(Map.of(0, "OFF", 1, "SIMPLE", 2, "FULL"));
      case "android.statistics.hotPixelMapMode":
        return mapArrayValues(Map.of(0, "OFF", 1, "ON"));
      case "android.statistics.lensShadingMapMode":
        return mapArrayValues(Map.of(0, "OFF", 1, "ON"));

        // -------------------- TONEMAP --------------------
      case "android.tonemap.mode":
        return mapArrayValues(Map.of(0, "CONTRAST_CURVE", 1, "FAST", 2, "HIGH_QUALITY"));

      default:
        return "Device-specific values";
    }
  }

  private static String mapArrayValues(Map<Integer, String> map) {
    List<String> values =
        map.entrySet().stream()
            .map(entry -> entry.getKey() + "(" + entry.getValue() + ")")
            .collect(Collectors.toList());
    return "Available values: {" + String.join(", ", values) + "}";
  }

  private static String mapArrayValues(int[] values, Map<Integer, String> map) {
    List<String> list = new ArrayList<>();
    for (int v : values) {
      list.add(v + "(" + map.getOrDefault(v, "UNKNOWN") + ")");
    }
    return "Available values: {" + String.join(", ", list) + "}";
  }
}
