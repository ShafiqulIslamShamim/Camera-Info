package com.shamim.camerainfo.c2api_key;

import android.media.CamcorderProfile;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import com.shamim.camerainfo.activity.*;
import com.shamim.camerainfo.exception_catcher.*;
import com.shamim.camerainfo.preference.*;
import com.shamim.camerainfo.recycle_view.*;
import com.shamim.camerainfo.update_checker.*;
import com.shamim.camerainfo.util.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CamcorderProfileLogger {

  private static final MediaCodecList CODEC_LIST = new MediaCodecList(MediaCodecList.ALL_CODECS);

  public static List<String> getAvailableCamcorderProfiles(int cameraId) {
    List<String> profiles = new ArrayList<>();
    for (int quality : getAllQualityValues()) {
      try {
        if (CamcorderProfile.hasProfile(cameraId, quality)) {
          CamcorderProfile profile = CamcorderProfile.get(cameraId, quality);
          profiles.add(formatProfile(profile, quality));
        } else {
          profiles.add(
              String.format(
                  "No CamcorderProfile available for quality: %s (%d)\n",
                  getQualityName(quality), quality));
        }
      } catch (Exception e) {
        profiles.add(
            String.format(
                "Error accessing profile for quality: %s (%d) - %s\n",
                getQualityName(quality), quality, e.getMessage()));
      }
    }
    return profiles;
  }

  private static String formatProfile(CamcorderProfile profile, int quality) {
    StringBuilder sb = new StringBuilder();
    sb.append(
        String.format(
            "Quality: %s (%d) [Video recording quality level]\n",
            getQualityName(quality), quality));
    sb.append(
        formatField(
            "Video Frame Width", profile.videoFrameWidth, "Width of video frames in pixels"));
    sb.append(
        formatField(
            "Video Frame Height", profile.videoFrameHeight, "Height of video frames in pixels"));
    sb.append(
        formatField("Video Bit Rate", profile.videoBitRate, "Video bit rate in bits per second"));
    sb.append(
        formatField(
            "Video Frame Rate", profile.videoFrameRate, "Video frame rate in frames per second"));
    sb.append(
        formatField(
            "Video Codec",
            mapVideoCodec(profile.videoCodec),
            String.format("Video codec used (Raw value: %d)", profile.videoCodec)));
    sb.append(
        formatField(
            "Video Codec Profile",
            getVideoCodecProfile(profile.videoCodec),
            "Video codec profile"));
    sb.append(
        formatField(
            "Video Codec Level", getVideoCodecLevel(profile.videoCodec), "Video codec level"));
    sb.append(
        formatField("Audio Bit Rate", profile.audioBitRate, "Audio bit rate in bits per second"));
    sb.append(formatField("Audio Channels", profile.audioChannels, "Number of audio channels"));
    sb.append(
        formatField(
            "Audio Codec",
            mapAudioCodec(profile.audioCodec),
            String.format("Audio codec used (Raw value: %d)", profile.audioCodec)));
    sb.append(formatField("Audio Sample Rate", profile.audioSampleRate, "Audio sample rate in Hz"));
    sb.append(
        formatField(
            "File Format",
            mapFileFormat(profile.fileFormat),
            String.format("Output file format (Raw value: %d)", profile.fileFormat)));
    return sb.toString();
  }

  private static String formatField(String name, Object value, String description) {
    return String.format("%s: %s [%s]\n", name, value, description);
  }

  private static String mapVideoCodec(int codec) {
    Map<Integer, String> videoCodecMap = new LinkedHashMap<>();
    videoCodecMap.put(0, "DEFAULT");
    videoCodecMap.put(1, "H263");
    videoCodecMap.put(2, "H264");
    videoCodecMap.put(3, "MPEG_4_SP");
    videoCodecMap.put(4, "VP8");
    videoCodecMap.put(5, "HEVC");
    videoCodecMap.put(6, "VP9");
    videoCodecMap.put(7, "AV1");
    return videoCodecMap.getOrDefault(codec, "UNKNOWN_" + codec);
  }

  private static String mapAudioCodec(int codec) {
    Map<Integer, String> audioCodecMap = new LinkedHashMap<>();
    audioCodecMap.put(0, "DEFAULT");
    audioCodecMap.put(1, "AMR_NB");
    audioCodecMap.put(2, "AMR_WB");
    audioCodecMap.put(3, "AAC");
    audioCodecMap.put(4, "HE_AAC");
    audioCodecMap.put(5, "AAC_ELD");
    audioCodecMap.put(6, "VORBIS");
    return audioCodecMap.getOrDefault(codec, "UNKNOWN_" + codec);
  }

  private static String mapFileFormat(int format) {
    Map<Integer, String> fileFormatMap = new LinkedHashMap<>();
    fileFormatMap.put(0, "DEFAULT");
    fileFormatMap.put(1, "THREE_GPP");
    fileFormatMap.put(2, "MP4");
    fileFormatMap.put(4, "AMR_NB");
    fileFormatMap.put(5, "AMR_WB");
    fileFormatMap.put(6, "AAC");
    fileFormatMap.put(7, "WEBM");
    fileFormatMap.put(8, "MPEG_2_TS");
    fileFormatMap.put(9, "OGG");
    return fileFormatMap.getOrDefault(format, "UNKNOWN_" + format);
  }

  private static String getVideoCodecProfile(int videoCodec) {
    String mimeType = getMimeType(videoCodec);
    if (mimeType == null) return "Not supported";

    for (MediaCodecInfo info : CODEC_LIST.getCodecInfos()) {
      if (info.isEncoder()) {
        for (String type : info.getSupportedTypes()) {
          if (type.equals(mimeType)) {
            MediaCodecInfo.CodecCapabilities caps = info.getCapabilitiesForType(type);
            List<String> profiles = new ArrayList<>();
            for (MediaCodecInfo.CodecProfileLevel pl : caps.profileLevels) {
              profiles.add(mapCodecProfile(mimeType, pl.profile));
            }
            return profiles.isEmpty() ? "No profiles available" : String.join(", ", profiles);
          }
        }
      }
    }
    return "No encoder found";
  }

  private static String getVideoCodecLevel(int videoCodec) {
    String mimeType = getMimeType(videoCodec);
    if (mimeType == null) return "Not supported";

    for (MediaCodecInfo info : CODEC_LIST.getCodecInfos()) {
      if (info.isEncoder()) {
        for (String type : info.getSupportedTypes()) {
          if (type.equals(mimeType)) {
            MediaCodecInfo.CodecCapabilities caps = info.getCapabilitiesForType(type);
            List<String> levels = new ArrayList<>();
            for (MediaCodecInfo.CodecProfileLevel pl : caps.profileLevels) {
              levels.add(mapCodecLevel(mimeType, pl.level));
            }
            return levels.isEmpty() ? "No levels available" : String.join(", ", levels);
          }
        }
      }
    }
    return "No encoder found";
  }

  private static String getMimeType(int videoCodec) {
    Map<Integer, String> codecToMimeMap = new LinkedHashMap<>();
    codecToMimeMap.put(1, "video/3gpp"); // H263
    codecToMimeMap.put(2, "video/avc"); // H264
    codecToMimeMap.put(3, "video/mp4v-es"); // MPEG_4_SP
    codecToMimeMap.put(4, "video/x-vnd.on2.vp8"); // VP8
    codecToMimeMap.put(5, "video/hevc"); // HEVC
    codecToMimeMap.put(6, "video/x-vnd.on2.vp9"); // VP9
    codecToMimeMap.put(7, "video/av1"); // AV1
    return codecToMimeMap.get(videoCodec);
  }

  private static String mapCodecProfile(String mimeType, int profile) {
    Map<Integer, String> profileMap = new LinkedHashMap<>();
    if (mimeType.equals("video/avc")) { // H264
      profileMap.put(MediaCodecInfo.CodecProfileLevel.AVCProfileBaseline, "Baseline");
      profileMap.put(MediaCodecInfo.CodecProfileLevel.AVCProfileMain, "Main");
      profileMap.put(MediaCodecInfo.CodecProfileLevel.AVCProfileExtended, "Extended");
      profileMap.put(MediaCodecInfo.CodecProfileLevel.AVCProfileHigh, "High");
      profileMap.put(MediaCodecInfo.CodecProfileLevel.AVCProfileHigh10, "High10");
      profileMap.put(MediaCodecInfo.CodecProfileLevel.AVCProfileHigh422, "High422");
      profileMap.put(MediaCodecInfo.CodecProfileLevel.AVCProfileHigh444, "High444");
    } else if (mimeType.equals("video/hevc")) { // HEVC
      profileMap.put(MediaCodecInfo.CodecProfileLevel.HEVCProfileMain, "Main");
      profileMap.put(MediaCodecInfo.CodecProfileLevel.HEVCProfileMain10, "Main10");
      profileMap.put(MediaCodecInfo.CodecProfileLevel.HEVCProfileMain10HDR10, "Main10HDR10");
      profileMap.put(
          MediaCodecInfo.CodecProfileLevel.HEVCProfileMain10HDR10Plus, "Main10HDR10Plus");
      profileMap.put(MediaCodecInfo.CodecProfileLevel.HEVCProfileMainStill, "MainStill");
    } else if (mimeType.equals("video/3gpp")) { // H263
      profileMap.put(MediaCodecInfo.CodecProfileLevel.H263ProfileBaseline, "Baseline");
      profileMap.put(MediaCodecInfo.CodecProfileLevel.H263ProfileH320Coding, "H320Coding");
      profileMap.put(
          MediaCodecInfo.CodecProfileLevel.H263ProfileBackwardCompatible, "BackwardCompatible");
      profileMap.put(MediaCodecInfo.CodecProfileLevel.H263ProfileISWV2, "ISWV2");
      profileMap.put(MediaCodecInfo.CodecProfileLevel.H263ProfileISWV3, "ISWV3");
      profileMap.put(
          MediaCodecInfo.CodecProfileLevel.H263ProfileHighCompression, "HighCompression");
      profileMap.put(MediaCodecInfo.CodecProfileLevel.H263ProfileInternet, "Internet");
      profileMap.put(MediaCodecInfo.CodecProfileLevel.H263ProfileInterlace, "Interlace");
      profileMap.put(MediaCodecInfo.CodecProfileLevel.H263ProfileHighLatency, "HighLatency");
    } else if (mimeType.equals("video/mp4v-es")) { // MPEG_4_SP
      profileMap.put(MediaCodecInfo.CodecProfileLevel.MPEG4ProfileSimple, "Simple");
      profileMap.put(MediaCodecInfo.CodecProfileLevel.MPEG4ProfileAdvancedSimple, "AdvancedSimple");
      profileMap.put(MediaCodecInfo.CodecProfileLevel.MPEG4ProfileCore, "Core");
      profileMap.put(MediaCodecInfo.CodecProfileLevel.MPEG4ProfileMain, "Main");
    } else if (mimeType.equals("video/x-vnd.on2.vp8")) { // VP8
      profileMap.put(MediaCodecInfo.CodecProfileLevel.VP8ProfileMain, "Main");
    } else if (mimeType.equals("video/x-vnd.on2.vp9")) { // VP9
      profileMap.put(MediaCodecInfo.CodecProfileLevel.VP9Profile0, "Profile0");
      profileMap.put(MediaCodecInfo.CodecProfileLevel.VP9Profile1, "Profile1");
      profileMap.put(MediaCodecInfo.CodecProfileLevel.VP9Profile2, "Profile2");
      profileMap.put(MediaCodecInfo.CodecProfileLevel.VP9Profile3, "Profile3");
      profileMap.put(MediaCodecInfo.CodecProfileLevel.VP9Profile2HDR, "Profile2HDR");
      profileMap.put(MediaCodecInfo.CodecProfileLevel.VP9Profile3HDR, "Profile3HDR");
    } else if (mimeType.equals("video/av1")) { // AV1
      profileMap.put(MediaCodecInfo.CodecProfileLevel.AV1ProfileMain8, "Main8");
      profileMap.put(MediaCodecInfo.CodecProfileLevel.AV1ProfileMain10, "Main10");
      profileMap.put(MediaCodecInfo.CodecProfileLevel.AV1ProfileMain10HDR10, "Main10HDR10");
      profileMap.put(MediaCodecInfo.CodecProfileLevel.AV1ProfileMain10HDR10Plus, "Main10HDR10Plus");
    }
    return profileMap.getOrDefault(profile, "UNKNOWN_" + profile);
  }

  private static String mapCodecLevel(String mimeType, int level) {
    Map<Integer, String> levelMap = new LinkedHashMap<>();
    if (mimeType.equals("video/avc")) { // H264
      levelMap.put(MediaCodecInfo.CodecProfileLevel.AVCLevel1, "Level1");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.AVCLevel1b, "Level1b");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.AVCLevel11, "Level11");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.AVCLevel12, "Level12");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.AVCLevel13, "Level13");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.AVCLevel2, "Level2");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.AVCLevel21, "Level21");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.AVCLevel22, "Level22");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.AVCLevel3, "Level3");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.AVCLevel31, "Level31");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.AVCLevel32, "Level32");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.AVCLevel4, "Level4");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.AVCLevel41, "Level41");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.AVCLevel42, "Level42");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.AVCLevel5, "Level5");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.AVCLevel51, "Level51");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.AVCLevel52, "Level52");
    } else if (mimeType.equals("video/hevc")) { // HEVC
      levelMap.put(MediaCodecInfo.CodecProfileLevel.HEVCMainTierLevel1, "MainTierLevel1");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.HEVCHighTierLevel1, "HighTierLevel1");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.HEVCMainTierLevel2, "MainTierLevel2");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.HEVCHighTierLevel2, "HighTierLevel2");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.HEVCMainTierLevel21, "MainTierLevel21");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.HEVCHighTierLevel21, "HighTierLevel21");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.HEVCMainTierLevel3, "MainTierLevel3");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.HEVCHighTierLevel3, "HighTierLevel3");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.HEVCMainTierLevel31, "MainTierLevel31");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.HEVCHighTierLevel31, "HighTierLevel31");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.HEVCMainTierLevel4, "MainTierLevel4");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.HEVCHighTierLevel4, "HighTierLevel4");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.HEVCMainTierLevel41, "MainTierLevel41");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.HEVCHighTierLevel41, "HighTierLevel41");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.HEVCMainTierLevel5, "MainTierLevel5");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.HEVCHighTierLevel5, "HighTierLevel5");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.HEVCMainTierLevel51, "MainTierLevel51");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.HEVCHighTierLevel51, "HighTierLevel51");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.HEVCMainTierLevel52, "MainTierLevel52");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.HEVCHighTierLevel52, "HighTierLevel52");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.HEVCMainTierLevel6, "MainTierLevel6");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.HEVCHighTierLevel6, "HighTierLevel6");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.HEVCMainTierLevel61, "MainTierLevel61");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.HEVCHighTierLevel61, "HighTierLevel61");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.HEVCMainTierLevel62, "MainTierLevel62");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.HEVCHighTierLevel62, "HighTierLevel62");
    } else if (mimeType.equals("video/3gpp")) { // H263
      levelMap.put(MediaCodecInfo.CodecProfileLevel.H263Level10, "Level10");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.H263Level20, "Level20");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.H263Level30, "Level30");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.H263Level40, "Level40");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.H263Level45, "Level45");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.H263Level50, "Level50");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.H263Level60, "Level60");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.H263Level70, "Level70");
    } else if (mimeType.equals("video/mp4v-es")) { // MPEG_4_SP
      levelMap.put(MediaCodecInfo.CodecProfileLevel.MPEG4Level0, "Level0");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.MPEG4Level0b, "Level0b");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.MPEG4Level1, "Level1");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.MPEG4Level2, "Level2");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.MPEG4Level3, "Level3");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.MPEG4Level4, "Level4");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.MPEG4Level4a, "Level4a");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.MPEG4Level5, "Level5");
    } else if (mimeType.equals("video/x-vnd.on2.vp8")) { // VP8
      levelMap.put(MediaCodecInfo.CodecProfileLevel.VP8Level_Version0, "Version0");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.VP8Level_Version1, "Version1");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.VP8Level_Version2, "Version2");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.VP8Level_Version3, "Version3");
    } else if (mimeType.equals("video/x-vnd.on2.vp9")) { // VP9
      levelMap.put(MediaCodecInfo.CodecProfileLevel.VP9Level1, "Level1");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.VP9Level11, "Level11");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.VP9Level2, "Level2");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.VP9Level21, "Level21");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.VP9Level3, "Level3");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.VP9Level31, "Level31");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.VP9Level4, "Level4");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.VP9Level41, "Level41");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.VP9Level5, "Level5");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.VP9Level51, "Level51");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.VP9Level52, "Level52");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.VP9Level6, "Level6");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.VP9Level61, "Level61");
    } else if (mimeType.equals("video/av1")) { // AV1
      levelMap.put(MediaCodecInfo.CodecProfileLevel.AV1Level2, "Level2");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.AV1Level21, "Level21");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.AV1Level22, "Level22");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.AV1Level23, "Level23");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.AV1Level3, "Level3");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.AV1Level31, "Level31");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.AV1Level32, "Level32");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.AV1Level33, "Level33");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.AV1Level4, "Level4");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.AV1Level41, "Level41");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.AV1Level42, "Level42");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.AV1Level43, "Level43");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.AV1Level5, "Level5");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.AV1Level51, "Level51");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.AV1Level52, "Level52");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.AV1Level53, "Level53");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.AV1Level6, "Level6");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.AV1Level61, "Level61");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.AV1Level62, "Level62");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.AV1Level63, "Level63");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.AV1Level7, "Level7");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.AV1Level71, "Level71");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.AV1Level72, "Level72");
      levelMap.put(MediaCodecInfo.CodecProfileLevel.AV1Level73, "Level73");
    }
    return levelMap.getOrDefault(level, "UNKNOWN_" + level);
  }

  private static int[] getAllQualityValues() {
    List<Integer> qualities = new ArrayList<>();
    for (Field field : CamcorderProfile.class.getFields()) {
      if (field.getName().startsWith("QUALITY_") && Modifier.isStatic(field.getModifiers())) {
        try {
          qualities.add((Integer) field.get(null));
        } catch (IllegalAccessException ignored) {
        }
      }
    }
    int[] iArr = new int[qualities.size()];
    for (int i = 0; i < qualities.size(); i++) {
      iArr[i] = qualities.get(i);
    }
    return iArr;
  }

  private static String getQualityName(int quality) {
    for (Field field : CamcorderProfile.class.getFields()) {
      try {
        if (Modifier.isStatic(field.getModifiers())
            && field.getType() == Integer.TYPE
            && ((Integer) field.get(null)).intValue() == quality) {
          return field.getName();
        }
      } catch (IllegalAccessException ignored) {
      }
    }
    return "UNKNOWN_QUALITY";
  }

  public static String getCamcorderLog(int cameraId) {
    StringBuilder sb = new StringBuilder();
    for (String profile : getAvailableCamcorderProfiles(cameraId)) {
      sb.append(profile).append("\n");
    }
    return sb.toString();
  }
}
