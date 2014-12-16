package picobot.tests.tutors;

import junit.framework.TestCase;
import picobot.interfaces.core.IMap;
import picobot.interfaces.core.IMapBuilder;
import picobot.tests.Bootstrap;


public class TestMapCreation extends TestCase {
  
  
  public static IMap  createMap() {
    IMapBuilder mb = Bootstrap.f().createMapBuilder();
    mb.createEmptySquareMap(5);
    return mb.getCurrentMap();
  }

  
  /** Creates a new map and tests the output representation */
  public void testA() {
    IMapBuilder mb = Bootstrap.f().createMapBuilder();
    mb.createEmptySquareMap(5);

    assertEquals( 
         "#####\n"
        +"#   #\n"
        +"#   #\n"
        +"#   #\n"
        +"#####\n",
        mb.getCurrentMap().toString().replaceAll("\r\n", "\n").replaceAll("\r", "\n") // expected
    );
  }
  
  

  /** adds a wall */
  public void testB() {
    IMapBuilder mb = Bootstrap.f().createMapBuilder();
    mb.createEmptySquareMap(5);

    mb.placeWall(2,3);
    
    assertEquals(
         "#####\n"
        +"# # #\n"
        +"#   #\n"
        +"#   #\n"
        +"#####\n",
        mb.getCurrentMap().toString().replaceAll("\r\n", "\n").replaceAll("\r", "\n"));
  }

  
  /** Tests isCompletelySurrounded */
  public void testC() {
    IMapBuilder mb = Bootstrap.f().createMapBuilder();
    mb.createEmptySquareMap(5);

    mb.parseMap(
        "#####\n");

     assertEquals( 
         true, mb.getCurrentMap().isCompletelySurrounded()
     );
  
     mb.parseMap(
           "#####\n"
          +"#   #\n"
          +"#   #\n"
          +"#   #\n"
          +"#####\n");
  
      assertEquals( 
          true, mb.getCurrentMap().isCompletelySurrounded()
      );
  
      mb.parseMap(
          "#####\n"
         +"#    \n"
         +"#   #\n"
         +"#   #\n"
         +"#####\n");
  
     assertEquals( 
         false, mb.getCurrentMap().isCompletelySurrounded()
     );
  
    
     mb.parseMap(
         "#### \n"
        +"#   #\n"
        +"#   #\n"
        +"#   #\n"
        +"#####\n");
  
    assertEquals( 
        false, mb.getCurrentMap().isCompletelySurrounded()
    );
  }
  
  
  public void testD() {
    IMapBuilder mb = Bootstrap.f().createMapBuilder();
    mb = Bootstrap.f().createMapBuilder();
    mb.parseMap(
        "#########\n"+
        "#### ####\n"+
        "#### ####\n"+
        "##     ##\n"+
        "#### ####\n"+
        "#### ####\n"+
        "#########\n");
    IMap m = mb.getCurrentMap();
    assertEquals( 
        true, m.isAConnectedMaze()
    );
  }

  public void testE() {
    IMapBuilder mb = Bootstrap.f().createMapBuilder();
    // this map is not a connected maze, there is a lonely cell on the top right hand side.
    mb.parseMap(
        "#########\n"+
        "#### ## #\n"+
        "#### ####\n"+
        "##     ##\n"+
        "#### ####\n"+
        "#### ####\n"+
        "#########\n");
    IMap m = mb.getCurrentMap();
    assertEquals( 
        false, m.isAConnectedMaze()
    );
  }

  public void testF() {
    IMapBuilder mb = Bootstrap.f().createMapBuilder();
    // this map is not a connected maze, there is a corridor of a size of two cells
    mb.parseMap(
        "#########\n"+
        "#### ####\n"+
        "#### ####\n"+
        "##     ##\n"+
        "####  ###\n"+
        "####  ###\n"+
        "#########\n");
    IMap m = mb.getCurrentMap();
    assertEquals( 
        false, m.isAConnectedMaze()
    );
  }

  /** Tests IMapBuilder.modify */
  public void testH() {
    IMapBuilder mb = Bootstrap.f().createMapBuilder();
    mb.createEmptySquareMap(5);
    IMap map = mb.getCurrentMap();
    
    mb.modify(map);
    
    mb.placeWall(2,3);
    mb.placeFreeCell(0, 0);
    
    IMap modifiedMap = mb.getCurrentMap();
    
    assertEquals(
         "#####\n"
        +"# # #\n"
        +"#   #\n"
        +"#   #\n"
        +" ####\n",
        modifiedMap.toString().replaceAll("\r\n", "\n").replaceAll("\r", "\n"));
  }

  public void testI() {
    IMapBuilder mb = Bootstrap.f().createMapBuilder();
    mb.parseMap(
        "###     \n"
       +"#    \n"
       +"#   #\n"
       +"#   #\n"
       +"#####\n");
    System.out.println(mb.getCurrentMap().getWidth());
    System.out.println(mb.getCurrentMap().getHeight());
    System.out.println(mb.getCurrentMap().toString());
  }

  
  
}
