package com.shamim.camerainfo;

import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureResult;
import java.util.*;
import java.util.stream.Collectors;

public class CaptureResultFormatter {

  public static String formatResultKeys(CameraCharacteristics cameraCharacteristics) {
    if (cameraCharacteristics == null) return "No info";

    StringBuilder sb = new StringBuilder("Result keys:\n\n");
    List<CaptureResult.Key<?>> keys = cameraCharacteristics.getAvailableCaptureResultKeys();
    if (keys == null) return "No result keys available";

    for (CaptureResult.Key<?> key : keys) {
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
        return "Chromatic aberration correction mode applied";
      case "android.colorCorrection.gains":
        return "Color correction gains applied to each channel";
      case "android.colorCorrection.mode":
        return "Color correction processing mode used";
      case "android.colorCorrection.transform":
        return "Color correction transform matrix applied";
      case "android.colorCorrection.colorTemperature":
        return "Color temperature applied in Kelvin";

        // -------------------- CONTROL --------------------
      case "android.control.aeAntibandingMode":
        return "Auto-exposure antibanding mode used";
      case "android.control.aeExposureCompensation":
        return "Exposure compensation value applied in EV steps";
      case "android.control.aeLock":
        return "Auto-exposure lock state";
      case "android.control.aeMode":
        return "Auto-exposure operating mode used";
      case "android.control.aePrecaptureTrigger":
        return "Auto-exposure precapture trigger state";
      case "android.control.aeRegions":
        return "Regions used for auto-exposure metering";
      case "android.control.aeState":
        return "Current state of auto-exposure system";
      case "android.control.aeTargetFpsRange":
        return "Target FPS range used for auto-exposure";
      case "android.control.afMode":
        return "Auto-focus operating mode used";
      case "android.control.afRegions":
        return "Regions used for auto-focus";
      case "android.control.afState":
        return "Current state of auto-focus system";
      case "android.control.afTrigger":
        return "Auto-focus trigger state";
      case "android.control.autoframing":
        return "Auto-framing mode used";
      case "android.control.autoframingState":
        return "Current state of auto-framing system";
      case "android.control.awbLock":
        return "Auto-white-balance lock state";
      case "android.control.awbMode":
        return "Auto-white-balance operating mode used";
      case "android.control.awbRegions":
        return "Regions used for auto-white-balance";
      case "android.control.awbState":
        return "Current state of auto-white-balance system";
      case "android.control.captureIntent":
        return "Intended use of the capture";
      case "android.control.effectMode":
        return "Special effect mode applied";
      case "android.control.mode":
        return "Overall control mode used";
      case "android.control.postRawSensitivityBoost":
        return "Post-RAW sensitivity boost applied in percentage";
      case "android.control.sceneMode":
        return "Scene mode applied";
      case "android.control.settingsOverride":
        return "Override applied for specific control settings";
      case "android.control.videoStabilizationMode":
        return "Video stabilization mode applied";
      case "android.control.zoomRatio":
        return "Zoom ratio applied";

        // -------------------- DISTORTION CORRECTION --------------------
      case "android.distortionCorrection.mode":
        return "Distortion correction mode applied";

        // -------------------- EDGE --------------------
      case "android.edge.mode":
        return "Edge enhancement mode applied";

        // -------------------- FLASH --------------------
      case "android.flash.firingPower":
        return "Flash firing power level used";
      case "android.flash.firingTime":
        return "Flash firing time in nanoseconds";
      case "android.flash.mode":
        return "Flash operating mode used";
      case "android.flash.state":
        return "Current state of the flash unit";
      case "android.flash.singleStrength":
        return "Flash strength used for single mode";
      case "android.flash.torchStrength":
        return "Torch strength level used";

        // -------------------- HOT PIXEL --------------------
      case "android.hotPixel.mode":
        return "Hot pixel correction mode applied";

        // -------------------- JPEG --------------------
      case "android.jpeg.gpsCoordinates":
        return "GPS coordinates in JPEG EXIF";
      case "android.jpeg.gpsProcessingMethod":
        return "GPS processing method in JPEG EXIF";
      case "android.jpeg.gpsTimestamp":
        return "GPS timestamp in JPEG EXIF";
      case "android.jpeg.orientation":
        return "JPEG image orientation in degrees";
      case "android.jpeg.quality":
        return "JPEG compression quality applied";
      case "android.jpeg.thumbnailQuality":
        return "JPEG thumbnail compression quality applied";
      case "android.jpeg.thumbnailSize":
        return "JPEG thumbnail size applied";

        // -------------------- LENS --------------------
      case "android.lens.aperture":
        return "Lens aperture used (f-number)";
      case "android.lens.filterDensity":
        return "Lens filter density applied";
      case "android.lens.focalLength":
        return "Lens focal length used in millimeters";
      case "android.lens.focusDistance":
        return "Lens focus distance used in diopters";
      case "android.lens.focusRange":
        return "Range of focus distances supported";
      case "android.lens.opticalStabilizationMode":
        return "Optical image stabilization mode used";
      case "android.lens.state":
        return "Current state of the lens system";

        // -------------------- NOISE REDUCTION --------------------
      case "android.noiseReduction.mode":
        return "Noise reduction mode applied";

        // -------------------- SENSOR --------------------
      case "android.sensor.dynamicBlackLevel":
        return "Dynamic black level values applied";
      case "android.sensor.exposureTime":
        return "Exposure time used in nanoseconds";
      case "android.sensor.frameDuration":
        return "Frame duration used in nanoseconds";
      case "android.sensor.greenSplit":
        return "Green channel split due to lighting";
      case "android.sensor.neutralColorPoint":
        return "Neutral color point for white balance";
      case "android.sensor.noiseProfile":
        return "Noise profile for the sensor";
      case "android.sensor.rollingShutterSkew":
        return "Rolling shutter skew in nanoseconds";
      case "android.sensor.sensitivity":
        return "Sensor sensitivity used (ISO)";
      case "android.sensor.testPatternData":
        return "Test pattern data used by sensor";
      case "android.sensor.testPatternMode":
        return "Test pattern mode used by sensor";
      case "android.sensor.timestamp":
        return "Timestamp of the frame in nanoseconds";

        // -------------------- SHADING --------------------
      case "android.shading.mode":
        return "Lens shading correction mode applied";

        // -------------------- STATISTICS --------------------
      case "android.statistics.faceDetectMode":
        return "Face detection mode applied";
      case "android.statistics.faces":
        return "Detected faces in the frame";
      case "android.statistics.hotPixelMap":
        return "Hot pixel map data";
      case "android.statistics.hotPixelMapMode":
        return "Hot pixel map output mode";
      case "android.statistics.lensShadingCorrectionMap":
        return "Lens shading correction map applied";
      case "android.statistics.lensShadingMapMode":
        return "Lens shading map output mode";
      case "android.statistics.oisSamples":
        return "Optical image stabilization sample data";
      case "android.statistics.predictedColorGains":
        return "Predicted color gains for white balance";
      case "android.statistics.predictedColorTransform":
        return "Predicted color transform matrix";
      case "android.statistics.sceneFlicker":
        return "Detected scene flicker frequency";

        // -------------------- TONEMAP --------------------
      case "android.tonemap.curveBlue":
        return "Tonemap curve applied for blue channel";
      case "android.tonemap.curveGreen":
        return "Tonemap curve applied for green channel";
      case "android.tonemap.curveRed":
        return "Tonemap curve applied for red channel";
      case "android.tonemap.mode":
        return "Tonemap processing mode applied";

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
      case "android.control.aeState":
        return mapArrayValues(
            Map.of(
                0,
                "INACTIVE",
                1,
                "SEARCHING",
                2,
                "CONVERGED",
                3,
                "LOCKED",
                4,
                "FLASH_REQUIRED",
                5,
                "PRECAPTURE"));
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
      case "android.control.afState":
        return mapArrayValues(
            Map.of(
                0,
                "INACTIVE",
                1,
                "PASSIVE_SCAN",
                2,
                "PASSIVE_FOCUSED",
                3,
                "ACTIVE_SCAN",
                4,
                "FOCUSED_LOCKED",
                5,
                "NOT_FOCUSED_LOCKED",
                6,
                "PASSIVE_UNFOCUSED"));
      case "android.control.afTrigger":
        return mapArrayValues(Map.of(0, "IDLE", 1, "START", 2, "CANCEL"));
      case "android.control.autoframing":
        return mapArrayValues(Map.of(0, "OFF", 1, "ON"));
      case "android.control.autoframingState":
        return mapArrayValues(Map.of(0, "INACTIVE", 1, "CONVERGED", 2, "FRAMING"));
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
      case "android.control.awbState":
        return mapArrayValues(Map.of(0, "INACTIVE", 1, "SEARCHING", 2, "CONVERGED", 3, "LOCKED"));
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
      case "android.flash.state":
        return mapArrayValues(
            Map.of(0, "UNAVAILABLE", 1, "CHARGING", 2, "READY", 3, "FIRED", 4, "PARTIAL"));

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
      case "android.lens.state":
        return mapArrayValues(Map.of(0, "STATIONARY", 1, "MOVING"));

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
      case "android.statistics.sceneFlicker":
        return mapArrayValues(Map.of(0, "NONE", 1, "50HZ", 2, "60HZ"));

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
}
