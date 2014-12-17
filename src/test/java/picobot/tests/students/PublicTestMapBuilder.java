package picobot.tests.students;

import junit.framework.TestCase;
import picobot.interfaces.core.IMapBuilder;
import picobot.tests.Bootstrap;

/** This class tests the essential functionalities of IMapBuilder */
public class PublicTestMapBuilder extends TestCase {

  /** This method tests a programmatically built map */
  public void testA() {
    
    IMapBuilder mb = Bootstrap.f().createMapBuilder();
    
    mb.placeWall(0,0);
    mb.placeWall(1,0);
    mb.placeWall(2,0);
    mb.placeWall(0,1);
    mb.placeWall(2,1);
    mb.placeWall(0,2);
    mb.placeWall(1,2);
    mb.placeWall(2,2);
    
    System.out.println(mb.getCurrentMap().toString());

    assertEquals(
        3, mb.getCurrentMap().getHeight());

    assertEquals(
        3, mb.getCurrentMap().getWidth());

    assertEquals(
        8, mb.getCurrentMap().getNumberOfWalls());

  }

  /** This method tests a map parser */
  public void testB() {

     // creating a map
    IMapBuilder mb = Bootstrap.f().createMapBuilder();
    mb.placeWall(0,0);
    mb.placeWall(1,0);
    mb.placeWall(2,0);
    mb.placeWall(0,1);
    mb.placeWall(2,1);
    mb.placeWall(0,2);
    mb.placeWall(1,2);
    mb.placeWall(2,2);

    // getting its string representation
    String mapString1 = mb.getCurrentMap().toString();
    
    // parsing the result of toString
    mb.parseMap(mapString1);
    
    // getting the new map representation after parsing
    String mapString2 = mb.getCurrentMap().toString();
    
    // the result of to String must be compatible with parseMap
    assertEquals(mapString1, mapString2);
  }

}
