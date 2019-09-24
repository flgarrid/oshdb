package org.heigit.bigspatialdata.oshdb.util.geometry.helpers;

import org.heigit.bigspatialdata.oshdb.OSHDB;
import org.heigit.bigspatialdata.oshdb.util.OSHDBTimestamp;
import org.heigit.bigspatialdata.oshdb.util.time.ISODateTimeParser;

public class TimestampParser {
  public static OSHDBTimestamp toOSHDBTimestamp(String timeString) {
    try {
      return OSHDB.timestamp(
          ISODateTimeParser.parseISODateTime(timeString).toEpochSecond()
      );
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
}
