/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.heigit.bigspatialdata.oshdb.osm;

import java.sql.DriverManager;
import java.sql.SQLException;

import org.heigit.bigspatialdata.oshdb.util.TagTranslator;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Moritz Schott <m.schott@stud.uni-heidelberg.de>
 */
public class OSMMemberTest {

  public OSMMemberTest() {
  }

  @Test
  public void testGetId() {
    OSMMember instance = new OSMMember(1L, OSMType.WAY, 1);
    long expResult = 1L;
    long result = instance.getId();
    assertEquals(expResult, result);
  }

  @Test
  public void testGetType() {
    OSMMember instance = new OSMMember(1L, OSMType.WAY, 1);
    OSMType expResult = OSMType.WAY;
    OSMType result = instance.getType();
    assertEquals(expResult, result);
  }

  @Test
  public void testGetRoleId() {
    OSMMember instance = new OSMMember(1L, OSMType.WAY, 1);
    int expResult = 1;
    int result = instance.getRoleId();
    assertEquals(expResult, result);
  }

  @Test
  public void testGetData() {
    // getEntity (explicit null)
    OSMMember instance = new OSMMember(1L, OSMType.WAY, 1, null);
    Object expResult = null;
    Object result = instance.getEntity();
    assertEquals(expResult, result);
  }

  @Test
  public void testGetDataII() {
    // getEntity (implicit null)
    OSMMember instance = new OSMMember(1L, OSMType.WAY, 1);
    Object expResult = null;
    Object result = instance.getEntity();
    assertEquals(expResult, result);
  }

  @Test
  public void testToString() {
    OSMMember instance = new OSMMember(1L, OSMType.WAY, 1);
    String expResult = "T:WAY ID:1 R:1";
    String result = instance.toString();
    assertEquals(expResult, result);
  }

  @Test
  public void testToString_TragTranslator() throws SQLException, ClassNotFoundException {
    OSMMember instance = new OSMMember(2L, OSMType.WAY, 0);
    String expResult = "T:Way ID:2 R:outer";
    String result = instance.toString(new TagTranslator(DriverManager.getConnection("jdbc:h2:./src/test/resources/keytables", "sa", "")));
    assertEquals(expResult, result);
  }

}