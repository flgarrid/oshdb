package org.heigit.bigspatialdata.oshdb.util.geometry.osmhistorytestdata;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.Polygonal;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import java.util.prefs.NodeChangeEvent;
import org.heigit.bigspatialdata.oshdb.osm.OSMEntity;
import org.heigit.bigspatialdata.oshdb.util.OSHDBTimestamp;
import org.heigit.bigspatialdata.oshdb.util.geometry.OSHDBGeometryBuilder;
import org.heigit.bigspatialdata.oshdb.util.geometry.helpers.OSMXmlReaderTagInterpreter;
import org.heigit.bigspatialdata.oshdb.util.geometry.helpers.TimestampParser;
import org.heigit.bigspatialdata.oshdb.util.tagInterpreter.TagInterpreter;
import org.heigit.bigspatialdata.oshdb.util.test.OSMXmlReader;
import org.junit.Test;

public class OSHDBGeometryBuilderTestOsmHistoryTestDataRelationNotMultipolygon {
  private final OSMXmlReader testData = new OSMXmlReader();
  TagInterpreter areaDecider;
  private final double DELTA = 1E-6;

  public OSHDBGeometryBuilderTestOsmHistoryTestDataRelationNotMultipolygon() {
    testData.add("./src/test/resources/different-timestamps/type-not-multipolygon.osm");
    areaDecider = new OSMXmlReaderTagInterpreter(testData);
  }

  @Test
  public void test1() throws ParseException {
    // relation getting more ways, one disappears, last version not valid
    OSMEntity entity = testData.relations().get(500L).get(0);
    OSHDBTimestamp timestamp = entity.getTimestamp();
    Geometry result = OSHDBGeometryBuilder.getGeometry(entity, timestamp, areaDecider);
    assertTrue(result instanceof MultiPolygon);
    assertTrue(result.isValid());
    assertEquals(9, result.getCoordinates().length);
    Geometry expectedPolygon = (new WKTReader()).read(
        "MULTIPOLYGON(((7.31 1.01,7.34 1.01,7.34 1.05, 7.31 1.01)),"
            + "((7.34 1.05, 7.32 1.05, 7.32 1.04, 7.33 1.04, 7.34 1.05)))"
    );
    Geometry intersection = result.intersection(expectedPolygon);
    assertEquals(expectedPolygon.getArea(), intersection.getArea(), DELTA);
    // second version
    OSMEntity entity1 = testData.relations().get(500L).get(1);
    OSHDBTimestamp timestamp1 = entity1.getTimestamp();
    Geometry result1 = OSHDBGeometryBuilder.getGeometry(entity1, timestamp1, areaDecider);
    assertTrue(result1 instanceof MultiPolygon);
    assertTrue(result1.isValid());
    assertEquals(14, result1.getCoordinates().length);
    Geometry expectedPolygon1 = (new WKTReader()).read(
        "MULTIPOLYGON(((7.31 1.01,7.34 1.01,7.34 1.05, 7.31 1.01)),"
            + "((7.34 1.05, 7.32 1.05, 7.32 1.04, 7.33 1.04, 7.34 1.05)),"
            + "(( 7.32 1.05,7.32 1.07,7.31 1.07,7.31 1.05,7.32 1.05)))"
    );
    Geometry intersection1 = result1.intersection(expectedPolygon1);
    assertEquals(expectedPolygon1.getArea(), intersection1.getArea(), DELTA);
    // third version
    OSMEntity entity2 = testData.relations().get(500L).get(2);
    OSHDBTimestamp timestamp2 = entity2.getTimestamp();
    try {
      Geometry result2 = OSHDBGeometryBuilder.getGeometry(entity1, timestamp2, areaDecider);
      assertTrue(result2 instanceof GeometryCollection || result2 instanceof Polygonal);
      assertTrue(result2.getNumGeometries() == 3);
    }
    catch(Exception e){
      e.printStackTrace();
      fail("Should not have thrown any exception");
    }
  }

  @Test
  public void test2() throws ParseException {
    // relation  visible tag changed
    OSMEntity entity = testData.relations().get(501L).get(0);
    OSHDBTimestamp timestamp = entity.getTimestamp();
    Geometry result = OSHDBGeometryBuilder.getGeometry(entity, timestamp, areaDecider);
    assertTrue(result instanceof MultiPolygon);
    assertTrue(result.isValid());
    assertEquals(10, result.getCoordinates().length);
    Geometry expectedPolygon = (new WKTReader()).read(
        "MULTIPOLYGON(((7.35 1.01, 7.34 1.01,7.34 1.02,7.35 1.02, 7.35 1.01)),"
            + "((7.33 1.04,7.33 1.03, 7.31 1.02, 7.31 1.04, 7.33 1.04)))"
    );
    Geometry intersection = result.intersection(expectedPolygon);
    assertEquals(expectedPolygon.getArea(), intersection.getArea(), DELTA);
    // second version
    OSMEntity entity1 = testData.relations().get(501L).get(1);
    OSHDBTimestamp timestamp1 = entity1.getTimestamp();
    Geometry result1 = OSHDBGeometryBuilder.getGeometry(entity1, timestamp1, areaDecider);
    assertTrue(result1.isEmpty());
    // third version
    OSMEntity entity2 = testData.relations().get(501L).get(2);
    OSHDBTimestamp timestamp2 = entity2.getTimestamp();
    Geometry result2 = OSHDBGeometryBuilder.getGeometry(entity2, timestamp2, areaDecider);
    assertTrue(result2 instanceof MultiPolygon);
    assertTrue(result2.isValid());
    assertEquals(10, result2.getCoordinates().length);
    Geometry expectedPolygon2 = (new WKTReader()).read(
        "MULTIPOLYGON(((7.35 1.01, 7.34 1.01,7.34 1.02,7.35 1.02, 7.35 1.01)),"
            + "((7.33 1.04,7.33 1.03, 7.31 1.02, 7.31 1.04, 7.33 1.04)))"
    );
    Geometry intersection2 = result2.intersection(expectedPolygon2);
    assertEquals(expectedPolygon2.getArea(), intersection2.getArea(), DELTA);
  }

  @Test
  public void test3() {
    // relation with two ways, both missing
    OSMEntity entity = testData.relations().get(502L).get(0);
    Geometry result = null;
    try {
      OSHDBTimestamp timestamp = entity.getTimestamp();
      result = OSHDBGeometryBuilder.getGeometry(entity, timestamp, areaDecider);
    }
    catch(Exception e){
      e.printStackTrace();
      fail("Should not have thrown any exception");
    }
  }

  @Test
  public void test4() throws ParseException {
    // relation tags changing
    OSMEntity entity = testData.relations().get(503L).get(0);
    OSHDBTimestamp timestamp = entity.getTimestamp();
    Geometry result = OSHDBGeometryBuilder.getGeometry(entity, timestamp, areaDecider);
    assertTrue(result instanceof Polygon);
    assertTrue(result.isValid());
    assertEquals(5, result.getCoordinates().length);
    Geometry expectedPolygon = (new WKTReader()).read(
        "MULTIPOLYGON((( 7.33 1.05,7.33 1.06,7.32 1.06,7.32 1.05,7.33 1.05)))"
    );
    Geometry intersection = result.intersection(expectedPolygon);
    assertEquals(expectedPolygon.getArea(), intersection.getArea(), DELTA);
    // second version
    OSMEntity entity1 = testData.relations().get(503L).get(1);
    OSHDBTimestamp timestamp1 = entity1.getTimestamp();
    Geometry result1 = OSHDBGeometryBuilder.getGeometry(entity1, timestamp1, areaDecider);
    assertTrue(result1 instanceof Polygon);
    assertTrue(result1.isValid());
    assertEquals(5, result1.getCoordinates().length);
    Geometry expectedPolygon1 = (new WKTReader()).read(
        "MULTIPOLYGON((( 7.33 1.05,7.33 1.06,7.32 1.06,7.32 1.05,7.33 1.05)))"
    );
    Geometry intersection1 = result1.intersection(expectedPolygon1);
    assertEquals(expectedPolygon1.getArea(), intersection1.getArea(), DELTA);
    // third version
    OSMEntity entity2 = testData.relations().get(503L).get(2);
    OSHDBTimestamp timestamp2 = entity2.getTimestamp();
    Geometry result2 = OSHDBGeometryBuilder.getGeometry(entity2, timestamp2, areaDecider);
    assertTrue(result2 instanceof Polygon);
    assertTrue(result2.isValid());
    assertEquals(5, result2.getCoordinates().length);
    Geometry expectedPolygon2 = (new WKTReader()).read(
        "MULTIPOLYGON((( 7.33 1.05,7.33 1.06,7.32 1.06,7.32 1.05,7.33 1.05)))"
    );
    Geometry intersection2 = result2.intersection(expectedPolygon2);
    assertEquals(expectedPolygon2.getArea(), intersection2.getArea(), DELTA);
  }

  @Test
  public void test5() throws ParseException {
    // relation, way 109 -inner- and 110 -outer- ways changed node refs
    OSMEntity entity = testData.relations().get(504L).get(0);
    OSHDBTimestamp timestamp = entity.getTimestamp();
    Geometry result = OSHDBGeometryBuilder.getGeometry(entity, timestamp, areaDecider);
    assertTrue(result instanceof Polygon);
    assertTrue(result.isValid());
    assertEquals(10, result.getCoordinates().length);
    Geometry expectedPolygon = (new WKTReader()).read(
        "MULTIPOLYGON((( 7.24 1.04, 7.24 1.07, 7.30 1.07, 7.30 1.04, 7.24 1.04),"
            + "(7.26 1.055, 7.265 1.06, 7.28 1.06,7.265 1.065, 7.26 1.055)))"
    );
    Geometry intersection = result.intersection(expectedPolygon);
    assertEquals(expectedPolygon.getArea(), intersection.getArea(), DELTA);
    // second version
    OSMEntity entity1 = testData.relations().get(504L).get(1);
    OSHDBTimestamp timestamp1 = entity1.getTimestamp();
    Geometry result1 = OSHDBGeometryBuilder.getGeometry(entity1, timestamp1, areaDecider);
    assertTrue(result1 instanceof Polygon);
    assertTrue(result1.isValid());
    assertEquals(10, result1.getCoordinates().length);
    Geometry expectedPolygon1 = (new WKTReader()).read(
        "MULTIPOLYGON((( 7.24 1.04, 7.24 1.07, 7.30 1.07, 7.30 1.04, 7.24 1.04),"
            + "( 7.26 1.05,7.265 1.06, 7.28 1.06, 7.265 1.05,7.26 1.05)))"
    );
    Geometry intersection1 = result1.intersection(expectedPolygon1);
    assertEquals(expectedPolygon1.getArea(), intersection1.getArea(), DELTA);
    // version in between
    OSMEntity entity_between = testData.relations().get(504L).get(0);
    OSHDBTimestamp timestamp_between =  TimestampParser.toOSHDBTimestamp("2012-02-01T00:00:00Z");
    Geometry result_between = OSHDBGeometryBuilder.getGeometry(entity_between, timestamp_between, areaDecider);
    assertTrue(result_between instanceof Polygon);
    assertTrue(result_between.isValid());
    assertEquals(10, result_between.getCoordinates().length);
    Geometry expectedPolygon_between = (new WKTReader()).read(
        "MULTIPOLYGON(((7.24 1.04, 7.24 1.07, 7.31 1.07, 7.31 1.04 , 7.24 1.04),"
            + "(7.26 1.055, 7.265 1.06, 7.28 1.06,7.265 1.065, 7.26 1.055)))"
    );
    Geometry intersection_between = result_between.intersection(expectedPolygon_between);
    assertEquals(expectedPolygon_between.getArea(), intersection_between.getArea(), DELTA);
  }

  @Test
  public void test6() throws ParseException {
    // relation, way 112  changed node coordinates
    OSMEntity entity = testData.relations().get(505L).get(0);
    OSHDBTimestamp timestamp = entity.getTimestamp();
    Geometry result = OSHDBGeometryBuilder.getGeometry(entity, timestamp, areaDecider);
    assertTrue(result instanceof Polygon);
    assertTrue(result.isValid());
    assertEquals(5, result.getCoordinates().length);
    Geometry expectedPolygon = (new WKTReader()).read(
        "MULTIPOLYGON(((7.24 1.048, 7.245 1.072, 7.305 1.078, 7.303 1.042 , 7.24 1.048)))"
    );
    Geometry intersection = result.intersection(expectedPolygon);
    assertEquals(expectedPolygon.getArea(), intersection.getArea(), DELTA);
    // version after
    OSMEntity entity_after = testData.relations().get(505L).get(0);
    OSHDBTimestamp timestamp_after =  TimestampParser.toOSHDBTimestamp("2012-02-01T00:00:00Z");
    Geometry result_after = OSHDBGeometryBuilder.getGeometry(entity_after, timestamp_after, areaDecider);
    assertTrue(result_after instanceof Polygon);
    assertTrue(result_after.isValid());
    assertEquals(5, result_after.getCoordinates().length);
    Geometry expectedPolygon_after = (new WKTReader()).read(
        "MULTIPOLYGON(((7.24 1.042, 7.242 1.07, 7.305 1.07, 7.295 1.039 , 7.24 1.042)))"
    );
    Geometry intersection_after = result_after.intersection(expectedPolygon_after);
    assertEquals(expectedPolygon_after.getArea(), intersection_after.getArea(), DELTA);
  }

  @Test
  public void test7() throws ParseException {
    // relation, with node members, nodes changed coordinates
    OSMEntity entity = testData.relations().get(506L).get(0);
    OSHDBTimestamp timestamp = entity.getTimestamp();
    Geometry result = OSHDBGeometryBuilder.getGeometry(entity, timestamp, areaDecider);
    assertTrue(result instanceof Polygon);
    assertTrue(result.isValid());
    assertEquals(4, result.getCoordinates().length);
    Geometry expectedPolygon = (new WKTReader()).read(
        "MULTIPOLYGON(((7.24 1.048, 7.245 1.072,  7.303 1.042 , 7.24 1.048)))"
    );
    Geometry intersection = result.intersection(expectedPolygon);
    assertEquals(expectedPolygon.getArea(), intersection.getArea(), DELTA);
    // version after
    OSMEntity entity_after = testData.relations().get(506L).get(0);
    OSHDBTimestamp timestamp_after =  TimestampParser.toOSHDBTimestamp("2012-02-01T00:00:00Z");
    Geometry result_after = OSHDBGeometryBuilder.getGeometry(entity_after, timestamp_after, areaDecider);
    assertTrue(result_after instanceof Polygon);
    assertTrue(result_after.isValid());
    assertEquals(4, result_after.getCoordinates().length);
    Geometry expectedPolygon_after = (new WKTReader()).read(
        "MULTIPOLYGON(((7.24 1.042, 7.242 1.07, 7.295 1.039 , 7.24 1.042)))"
    );
    Geometry intersection_after = result_after.intersection(expectedPolygon_after);
    assertEquals(expectedPolygon_after.getArea(), intersection_after.getArea(), DELTA);
  }

  @Test
  public void test8() {
    // relation, not valid, should be a not empty geometryCollection
    // https://gitlab.gistools.geog.uni-heidelberg.de/giscience/big-data/ohsome/oshdb/issues/143
    OSMEntity entity = testData.relations().get(507L).get(0);
    try {
      OSHDBTimestamp timestamp = entity.getTimestamp();
      Geometry result = OSHDBGeometryBuilder.getGeometry(entity, timestamp, areaDecider);
      assertTrue(result instanceof GeometryCollection);
      assertTrue(result.getNumGeometries() == 6);
      assertFalse(result instanceof MultiPolygon);
    }
    catch(Exception e){
      e.printStackTrace();
      fail("Should not have thrown any exception");
    }
  }

  @Test
  public void test9() {
    // relation with two ways, all nodes not existing
    OSMEntity entity = testData.relations().get(508L).get(0);
    Geometry result = null;
    try {
      OSHDBTimestamp timestamp = entity.getTimestamp();
      result = OSHDBGeometryBuilder.getGeometry(entity, timestamp, areaDecider);
    }
    catch(Exception e){
      e.printStackTrace();
      fail("Should not have thrown any exception");
    }
  }

  @Test
  public void test10() throws ParseException {
    // relation, way member: node 52 changes visible tag
    OSMEntity entity = testData.relations().get(509L).get(0);
    // timestamp where node 52 visible is false
    OSHDBTimestamp timestamp = entity.getTimestamp();
    Geometry result = OSHDBGeometryBuilder.getGeometry(entity, timestamp, areaDecider);
    assertTrue(result instanceof Polygon);
    assertTrue(result.isValid());
    assertEquals(4, result.getCoordinates().length);
    Geometry expectedPolygon = (new WKTReader()).read(
        "MULTIPOLYGON(((7.303 1.042, 7.32 1.07, 7.32 1.04,7.303 1.042)))"
    );
    Geometry intersection = result.intersection(expectedPolygon);
    assertEquals(expectedPolygon.getArea(), intersection.getArea(), DELTA);
    // version after
    OSMEntity entity_after = testData.relations().get(509L).get(0);
    // timestamp where node 52 visible is true
    OSHDBTimestamp timestamp_after =  TimestampParser.toOSHDBTimestamp("2014-02-01T00:00:00Z");
    Geometry result_after = OSHDBGeometryBuilder.getGeometry(entity_after, timestamp_after, areaDecider);
    assertTrue(result_after instanceof Polygon);
    assertTrue(result_after.isValid());
    assertEquals(5, result_after.getCoordinates().length);
    Geometry expectedPolygon_after = (new WKTReader()).read(
        "MULTIPOLYGON(((7.303 1.042, 7.31 1.06, 7.32 1.07, 7.32 1.04, 7.303 1.042)))"
    );
    Geometry intersection_after = result_after.intersection(expectedPolygon_after);
    assertEquals(expectedPolygon_after.getArea(), intersection_after.getArea(), DELTA);
  }

  @Test
  public void test11() throws ParseException {
    // relation, way member: node 53 changes tags, 51 changes coordinates
    OSMEntity entity = testData.relations().get(510L).get(0);
    OSHDBTimestamp timestamp = entity.getTimestamp();
    Geometry result = OSHDBGeometryBuilder.getGeometry(entity, timestamp, areaDecider);
    assertTrue(result instanceof Polygon);
    assertTrue(result.isValid());
    assertEquals(4, result.getCoordinates().length);
    Geometry expectedPolygon = (new WKTReader()).read(
        "MULTIPOLYGON(((7.303 1.042,1.43 1.24,7.32 1.04,7.303 1.042)))"
    );
    Geometry intersection = result.intersection(expectedPolygon);
    assertEquals(expectedPolygon.getArea(), intersection.getArea(), DELTA);
    // version after
    OSMEntity entity_after = testData.relations().get(510L).get(0);
    OSHDBTimestamp timestamp_after =  TimestampParser.toOSHDBTimestamp("2014-02-01T00:00:00Z");
    Geometry result_after = OSHDBGeometryBuilder.getGeometry(entity_after, timestamp_after, areaDecider);
    assertTrue(result_after instanceof Polygon);
    assertTrue(result_after.isValid());
    assertEquals(4, result_after.getCoordinates().length);
    Geometry expectedPolygon_after = (new WKTReader()).read(
        "MULTIPOLYGON(((7.295 1.039, 1.43 1.24, 7.32 1.04, 7.295 1.039)))"
    );
    Geometry intersection_after = result_after.intersection(expectedPolygon_after);
    assertEquals(expectedPolygon_after.getArea(), intersection_after.getArea(), DELTA);
  }

  @Test
  public void test12() throws ParseException {
    // relation, way member: way 119 changes visible tag
    OSMEntity entity = testData.relations().get(511L).get(0);
    OSHDBTimestamp timestamp = entity.getTimestamp();
    Geometry result = OSHDBGeometryBuilder.getGeometry(entity, timestamp, areaDecider);
    assertTrue(result instanceof Polygon);
    assertTrue(result.isValid());
    assertEquals(4, result.getCoordinates().length);
    Geometry expectedPolygon = (new WKTReader()).read(
        "MULTIPOLYGON(((7.29 1.01, 7.29 1.05, 7.30 1.01, 7.29 1.01)))"
    );
    Geometry intersection = result.intersection(expectedPolygon);
    assertEquals(expectedPolygon.getArea(), intersection.getArea(), DELTA);
    // version after, visible false
    OSMEntity entity_after = testData.relations().get(511L).get(0);
    OSHDBTimestamp timestamp_after =  TimestampParser.toOSHDBTimestamp("2017-02-01T00:00:00Z");
    Geometry result_after = OSHDBGeometryBuilder.getGeometry(entity_after, timestamp_after, areaDecider);
    assertTrue(result_after.isEmpty());
  }

  @Test
  public void test13() throws ParseException {
    // relation, 2 way members making outer ring: way 120 changes visible tag later, 121 not
    OSMEntity entity = testData.relations().get(512L).get(0);
    OSHDBTimestamp timestamp = entity.getTimestamp();
    Geometry result = OSHDBGeometryBuilder.getGeometry(entity, timestamp, areaDecider);
    assertTrue(result instanceof Polygon);
    assertTrue(result.isValid());
    assertEquals(5, result.getCoordinates().length);
    Geometry expectedPolygon = (new WKTReader()).read(
        "MULTIPOLYGON((( 7.5 1.04, 7.5 1.6, 7.4 1.6, 7.4 1.04,7.5 1.04)))"
    );
    Geometry intersection = result.intersection(expectedPolygon);
    assertEquals(expectedPolygon.getArea(), intersection.getArea(), DELTA);
    // version after: way 120 does not exit any more
    OSMEntity entity_after = testData.relations().get(512L).get(0);
    OSHDBTimestamp timestamp_after =  TimestampParser.toOSHDBTimestamp("2018-02-01T00:00:00Z");
    Geometry result_after = OSHDBGeometryBuilder.getGeometry(entity_after, timestamp_after, areaDecider);
    assertTrue(result_after instanceof GeometryCollection);
    assertTrue(result_after.getNumGeometries() == 2);
  }

  @Test
  public void test14() throws ParseException {
    // relation, way member: way 122 changes tags
    OSMEntity entity = testData.relations().get(513L).get(0);
    OSHDBTimestamp timestamp = entity.getTimestamp();
    Geometry result = OSHDBGeometryBuilder.getGeometry(entity, timestamp, areaDecider);
    assertTrue(result instanceof Polygon);
    assertTrue(result.isValid());
    assertEquals(5, result.getCoordinates().length);
    Geometry expectedPolygon = (new WKTReader()).read(
        "MULTIPOLYGON(((7.34 1.01, 7.34 1.05, 7.32 1.05, 7.32 1.04,7.34 1.01)))"
    );
    Geometry intersection = result.intersection(expectedPolygon);
    assertEquals(expectedPolygon.getArea(), intersection.getArea(), DELTA);
    // way first version
    OSMEntity entity1 = testData.relations().get(513L).get(0);
    OSHDBTimestamp timestamp1 =  TimestampParser.toOSHDBTimestamp("2009-02-01T00:00:00Z");
    Geometry result1 = OSHDBGeometryBuilder.getGeometry(entity1, timestamp1, areaDecider);
    assertTrue(result1 instanceof Polygon);
    assertTrue(result1.isValid());
    assertEquals(5, result1.getCoordinates().length);
    Geometry expectedPolygon1 = (new WKTReader()).read(
        "MULTIPOLYGON(((7.34 1.01, 7.34 1.05, 7.32 1.05, 7.32 1.04,7.34 1.01)))"
    );
    Geometry intersection1 = result1.intersection(expectedPolygon1);
    assertEquals(expectedPolygon1.getArea(), intersection1.getArea(), DELTA);
    // way second version
    OSMEntity entity2 = testData.relations().get(513L).get(0);
    OSHDBTimestamp timestamp2 =  TimestampParser.toOSHDBTimestamp("2012-02-01T00:00:00Z");
    Geometry result2 = OSHDBGeometryBuilder.getGeometry(entity2, timestamp2, areaDecider);
    assertTrue(result2 instanceof Polygon);
    assertTrue(result2.isValid());
    assertEquals(5, result2.getCoordinates().length);
    Geometry expectedPolygon2 = (new WKTReader()).read(
        "MULTIPOLYGON(((7.34 1.01, 7.34 1.05, 7.32 1.05, 7.32 1.04,7.34 1.01)))"
    );
    Geometry intersection2 = result2.intersection(expectedPolygon2);
    assertEquals(expectedPolygon2.getArea(), intersection2.getArea(), DELTA);
  }

  @Test
  public void test15() throws ParseException {
    // relation getting more ways, one disappears, last version not valid
    OSMEntity entity = testData.relations().get(514L).get(0);
    OSHDBTimestamp timestamp = entity.getTimestamp();
    try {
      Geometry result = OSHDBGeometryBuilder.getGeometry(entity, timestamp, areaDecider);
      assertTrue(result instanceof GeometryCollection);
      assertTrue(result.isValid());
      assertTrue(result.getNumGeometries() == 2);
      assertTrue(result.getGeometryN(0) instanceof LineString);
      assertTrue(result.getGeometryN(1) instanceof LineString);
    }
    catch(Exception e){
        e.printStackTrace();
        fail("Should not have thrown any exception");
      }
    // second version
    OSMEntity entity1 = testData.relations().get(514L).get(1);
    OSHDBTimestamp timestamp1 = entity1.getTimestamp();
    try {
      Geometry result1 = OSHDBGeometryBuilder.getGeometry(entity1, timestamp1, areaDecider);
      assertTrue(result1 instanceof GeometryCollection);
      assertTrue(result1.isValid());
      assertTrue(result1.getNumGeometries() == 1);
      assertTrue(result1.getGeometryN(0) instanceof LineString);
    }
    catch(Exception e){
      e.printStackTrace();
      fail("Should not have thrown any exception");
    }
  }

  @Test
  public void test16() throws ParseException {
    // relation, at the beginning one way, split up later into 2 ways
    OSMEntity entity = testData.relations().get(515L).get(0);
    OSHDBTimestamp timestamp = entity.getTimestamp();
    Geometry result = OSHDBGeometryBuilder.getGeometry(entity, timestamp, areaDecider);
    assertTrue(result instanceof Polygon);
    assertTrue(result.isValid());
    assertEquals(5, result.getCoordinates().length);
    Geometry expectedPolygon = (new WKTReader()).read(
        "MULTIPOLYGON((( 7.0 1.04, 7.0 1.6, 7.2 1.6, 7.2 1.04,7.0 1.04)))"
    );
    Geometry intersection = result.intersection(expectedPolygon);
    assertEquals(expectedPolygon.getArea(), intersection.getArea(), DELTA);
    // second version
    OSMEntity entity1 = testData.relations().get(515L).get(1);
    OSHDBTimestamp timestamp1 = entity1.getTimestamp();
    Geometry result1 = OSHDBGeometryBuilder.getGeometry(entity1, timestamp1, areaDecider);
    assertTrue(result1 instanceof Polygon);
    assertTrue(result1.isValid());
    assertEquals(5, result1.getCoordinates().length);
    Geometry expectedPolygon1 = (new WKTReader()).read(
        "MULTIPOLYGON((( 7.0 1.04, 7.0 1.6, 7.2 1.6, 7.2 1.04,7.0 1.04)))"
    );
    Geometry intersection1 = result1.intersection(expectedPolygon1);
    assertEquals(expectedPolygon1.getArea(), intersection1.getArea(), DELTA);
  }

  @Test
  public void test100() {
    // relation type restriction
    OSMEntity entity1 = testData.relations().get(710900L).get(0);
    OSHDBTimestamp timestamp1 = entity1.getTimestamp();
    try {
      Geometry result = OSHDBGeometryBuilder.getGeometry(entity1, timestamp1, areaDecider);
      assertTrue(result instanceof GeometryCollection );
      assertTrue(result.getNumGeometries() == 3);
      assertTrue(result.getGeometryN(0) instanceof LineString);
      assertTrue(result.getGeometryN(1) instanceof Point);
      assertTrue(result.getGeometryN(2) instanceof LineString);
    }
    catch(Exception e){
      e.printStackTrace();
      fail("Should not have thrown any exception");
    }
  }

  @Test
  public void test101() {
    // relation type associatedStreet
    OSMEntity entity1 = testData.relations().get(710901L).get(0);
    OSHDBTimestamp timestamp1 = entity1.getTimestamp();
    try {
      Geometry result = OSHDBGeometryBuilder.getGeometry(entity1, timestamp1, areaDecider);
      assertTrue(result instanceof GeometryCollection );
      assertTrue(result.getNumGeometries() == 3);
      assertTrue(result.getGeometryN(0) instanceof Point);
      assertTrue(result.getGeometryN(1) instanceof Point);
      assertTrue(result.getGeometryN(2) instanceof Point);
    }
    catch(Exception e){
      e.printStackTrace();
      fail("Should not have thrown any exception");
    }
  }

  @Test
  public void test102() {
    // relation type public_transport
    OSMEntity entity1 = testData.relations().get(710902L).get(0);
    OSHDBTimestamp timestamp1 = entity1.getTimestamp();
    try {
      Geometry result = OSHDBGeometryBuilder.getGeometry(entity1, timestamp1, areaDecider);
      assertTrue(result instanceof GeometryCollection );
      assertTrue(result.getNumGeometries() == 4);
      assertTrue(result.getGeometryN(0) instanceof LineString);
      assertTrue(result.getGeometryN(1) instanceof Point);
      assertTrue(result.getGeometryN(2) instanceof LineString);
      assertTrue(result.getGeometryN(3) instanceof Point);
    }
    catch(Exception e){
      e.printStackTrace();
      fail("Should not have thrown any exception");
    }
  }

  @Test
  public void test103() {
    // relation type building
    OSMEntity entity1 = testData.relations().get(710903L).get(0);
    OSHDBTimestamp timestamp1 = entity1.getTimestamp();
    try {
      Geometry result = OSHDBGeometryBuilder.getGeometry(entity1, timestamp1, areaDecider);
      assertTrue(result instanceof GeometryCollection );
      assertTrue(result.getNumGeometries() == 3);
      assertTrue(result.getGeometryN(0) instanceof LineString);
      assertTrue(result.getGeometryN(1) instanceof LineString);
      assertTrue(result.getGeometryN(2) instanceof LineString);
    }
    catch(Exception e){
      e.printStackTrace();
      fail("Should not have thrown any exception");
    }
  }

}

