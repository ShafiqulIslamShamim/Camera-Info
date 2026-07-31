/*
 * Copyright (c) 2026 Shafiqul Islam Shamim
 * GitHub: https://github.com/ShafiqulIslamShamim/Camera-Info
 *
 * All Rights Reserved.
 *
 * This source code is made publicly available solely for viewing, collaboration,
 * educational reference, and submitting pull requests to the official repository.
 *
 * No permission is granted to copy, modify, redistribute, sublicense, or use
 * this source code, in whole or in part, for personal, commercial, or any other
 * purpose without the prior written permission of the copyright holder.
 */
package com.shamim.camerainfo.util;

import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;

public class GlobalWinterSystem {

  /**
   * Checks if it is currently winter based on the device's current date and time zone.
   *
   * @return true if the current date falls within winter for the resolved hemisphere, false
   *     otherwise.
   */
  public static boolean isWinterNow() {
    // ১. ডিভাইসের বর্তমান টাইম জোন আইডি এবং তারিখ নেওয়া
    String zoneId = ZoneId.systemDefault().getId();
    LocalDate today = LocalDate.now();
    Month month = today.getMonth();
    int day = today.getDayOfMonth();

    // ২. টাইম জোন দিয়ে গোলার্ধ নির্ণয় করা
    boolean southern = isSouthernHemisphere(zoneId);

    if (southern) {
      // দক্ষিণ গোলার্ধ: জুন ২১ থেকে সেপ্টেম্বর ২০ পর্যন্ত শীত
      if (month == Month.JUNE) return day >= 21;
      if (month == Month.JULY || month == Month.AUGUST) return true;
      if (month == Month.SEPTEMBER) return day <= 20;
    } else {
      // উত্তর গোলার্ধ: ডিসেম্বর ২১ থেকে মার্চ ২০ পর্যন্ত শীত
      if (month == Month.DECEMBER) return true;
      if (month == Month.JANUARY || month == Month.FEBRUARY) return true;
      //  if (month == Month.MARCH) return day <= 20;
    }
    return false;
  }

  /**
   * Determines if the given time zone ID belongs to the Southern Hemisphere.
   *
   * @param zoneId The system default time zone ID.
   * @return true if the time zone ID matches any of the pre-defined southern hemisphere regions,
   *     false otherwise.
   */
  private static boolean isSouthernHemisphere(String zoneId) {
    // দক্ষিণ গোলার্ধের প্রধান টাইম জোন আইডি প্রিফিক্স এবং অঞ্চলসমূহ
    String[] southernRegions = {
      "Antarctica",
      "Australia",
      "Africa/Johannesburg",
      "Africa/Windhoek",
      "Africa/Maputo",
      "Africa/Harare",
      "Africa/Luanda",
      "Africa/Lusaka",
      "America/Argentina",
      "America/Buenos_Aires",
      "America/Santiago",
      "America/Montevideo",
      "America/La_Paz",
      "America/Asuncion",
      "America/Lima",
      "Brazil/East",
      "Brazil/West",
      "Pacific/Auckland",
      "Pacific/Fiji",
      "Pacific/Port_Moresby"
    };

    for (String region : southernRegions) {
      if (zoneId.contains(region)) {
        return true;
      }
    }
    return false;
  }
}
