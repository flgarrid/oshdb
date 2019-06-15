package org.heigit.bigspatialdata.oshdb.util.update;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import org.heigit.bigspatialdata.oshdb.osm.OSMType;
import org.heigit.bigspatialdata.oshdb.util.TableNames;
import org.roaringbitmap.longlong.LongBitmapDataProvider;

public class UpdateDbHelper {

  private UpdateDbHelper() {
  }

  public static Map<OSMType, LongBitmapDataProvider> getBitMap(Connection dbBit) throws SQLException,
      IOException, ClassNotFoundException {
    Map<OSMType, LongBitmapDataProvider> map = new HashMap<>(3);
    for (OSMType type : OSMType.values()) {
      if (type != OSMType.UNKNOWN) {
        Statement retreave = dbBit.createStatement();
        String retrSQL = "SELECT bitmap FROM " + TableNames.forOSMType(type).get() + "_bitmap WHERE id=1;";
        ResultSet executeQuery = retreave.executeQuery(retrSQL);
        executeQuery.next();
        byte[] bytes = executeQuery.getBytes("bitmap");
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream input = new ObjectInputStream(bais);
        LongBitmapDataProvider r = (LongBitmapDataProvider) input.readObject();
        map.put(type, r);
      }
    }
    return map;
  }

}
