package com.shamim.camerainfo;

import android.media.CamcorderProfile;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CamcorderProfileLogger {
  public static List<String> getAvailableCamcorderProfiles(int i) {
    ArrayList arrayList = new ArrayList();
    for (int i2 : getAllQualityValues()) {
      try {
        if (CamcorderProfile.hasProfile(i, i2)) {
          CamcorderProfile camcorderProfile = CamcorderProfile.get(i, i2);
          StringBuilder sb = new StringBuilder();
          sb.append("Quality: ").append(getQualityName(i2)).append(" (").append(i2).append(")\n");
          try {
            sb.append("Video Frame Width: ").append(camcorderProfile.videoFrameWidth).append("\n");
          } catch (Exception e) {
            sb.append("Video Frame Width: Error - ").append(e.getMessage()).append("\n");
          }
          try {
            sb.append("Video Frame Height: ")
                .append(camcorderProfile.videoFrameHeight)
                .append("\n");
          } catch (Exception e2) {
            sb.append("Video Frame Height: Error - ").append(e2.getMessage()).append("\n");
          }
          try {
            sb.append("Video Bit Rate: ").append(camcorderProfile.videoBitRate).append("\n");
          } catch (Exception e3) {
            sb.append("Video Bit Rate: Error - ").append(e3.getMessage()).append("\n");
          }
          try {
            sb.append("Video Frame Rate: ").append(camcorderProfile.videoFrameRate).append("\n");
          } catch (Exception e4) {
            sb.append("Video Frame Rate: Error - ").append(e4.getMessage()).append("\n");
          }
          try {
            sb.append("Video Codec: ").append(camcorderProfile.videoCodec).append("\n");
          } catch (Exception e5) {
            sb.append("Video Codec: Error - ").append(e5.getMessage()).append("\n");
          }
          sb.append("Video Codec Profile: ").append(-1).append("\n");
          sb.append("Video Codec Level: ").append(-1).append("\n");
          try {
            sb.append("Audio Bit Rate: ").append(camcorderProfile.audioBitRate).append("\n");
          } catch (Exception e6) {
            sb.append("Audio Bit Rate: Error - ").append(e6.getMessage()).append("\n");
          }
          try {
            sb.append("Audio Channels: ").append(camcorderProfile.audioChannels).append("\n");
          } catch (Exception e7) {
            sb.append("Audio Channels: Error - ").append(e7.getMessage()).append("\n");
          }
          try {
            sb.append("Audio Codec: ").append(camcorderProfile.audioCodec).append("\n");
          } catch (Exception e8) {
            sb.append("Audio Codec: Error - ").append(e8.getMessage()).append("\n");
          }
          try {
            sb.append("Audio Sample Rate: ").append(camcorderProfile.audioSampleRate).append("\n");
          } catch (Exception e9) {
            sb.append("Audio Sample Rate: Error - ").append(e9.getMessage()).append("\n");
          }
          try {
            sb.append("File Format: ").append(camcorderProfile.fileFormat).append("\n");
          } catch (Exception e10) {
            sb.append("File Format: Error - ").append(e10.getMessage()).append("\n");
          }
          arrayList.add(sb.toString());
        } else {
          arrayList.add(
              "No CamcorderProfile available for quality: "
                  + getQualityName(i2)
                  + " ("
                  + i2
                  + ")\n");
        }
      } catch (Exception e11) {
        arrayList.add(
            "Error accessing profile for quality: "
                + getQualityName(i2)
                + " ("
                + i2
                + ") - "
                + e11.getMessage());
      }
    }
    return arrayList;
  }

  private static int[] getAllQualityValues() {
    ArrayList arrayList = new ArrayList();
    for (Field field : CamcorderProfile.class.getFields()) {
      if (field.getName().startsWith("QUALITY_") && Modifier.isStatic(field.getModifiers())) {
        try {
          arrayList.add((Integer) field.get(null));
        } catch (IllegalAccessException e) {
        }
      }
    }
    int[] iArr = new int[arrayList.size()];
    for (int i = 0; i < arrayList.size(); i++) {
      iArr[i] = ((Integer) arrayList.get(i)).intValue();
    }
    return iArr;
  }

  private static String getQualityName(int i) {
    for (Field field : CamcorderProfile.class.getFields()) {
      try {
        if (Modifier.isStatic(field.getModifiers())
            && field.getType() == Integer.TYPE
            && ((Integer) field.get(null)).intValue() == i) {
          return field.getName();
        }
      } catch (IllegalAccessException e) {
      }
    }
    return "UNKNOWN_QUALITY";
  }

  public static String getCamcorderLog(int i) {
    StringBuilder sb = new StringBuilder();
    Iterator<String> it = getAvailableCamcorderProfiles(i).iterator();
    while (it.hasNext()) {
      sb.append(it.next());
      sb.append("");
    }
    return sb.toString();
  }
}
