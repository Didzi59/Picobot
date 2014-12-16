package picobot.tests.tutors;

import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import picobot.interfaces.core.IMap;
import picobot.interfaces.core.IMapBuilder;
import picobot.interfaces.core.IPicobot;
import picobot.interfaces.core.IRule;
import picobot.interfaces.core.IRuleBuilder;
import picobot.interfaces.core.ISimulator;
import picobot.tests.Bootstrap;


public class LoewTest extends TestCase {   
  
  /**
   * Helper Function: count Walls of a Map
   * @param map The map which is being loaded
   * @return number of Walls of a Map
   */
  public int countWalls(IMap map){
    int returnValue = 0;
      int height = map.getHeight();
      int width = map.getWidth();
      
      for (int i = 0; i < height; i++){
        for(int j = 0; j < width; j++){
          if(map.getCellKind(j, i) == "WALL") returnValue++;
        }
        
      }
    return returnValue;
  }
  
  /**
   * Testing Map Parser
   */
  public void testA(){
    //initiating variables
    String map = 
            "########\n"
           +"#      #\n"
           +"#      #\n"
           +"#      #\n"
           +"########\n";
    
    ISimulator simulator = Bootstrap.f().createSimulator();
    IMapBuilder mapBuilder = Bootstrap.f().createMapBuilder();
    
    //parsing and loading our map
    mapBuilder.parseMap(map);  
      simulator.loadMap(mapBuilder.getCurrentMap());
      IMap loadedMap = simulator.getMap();  
      
      //test if walls correctly placed
      assertEquals("WALL", loadedMap.getCellKind(0, 0));
      assertEquals("WALL", loadedMap.getCellKind(loadedMap.getWidth() -1, loadedMap.getHeight() -1));
      assertEquals("FREE", loadedMap.getCellKind(1, 1));
      assertEquals("FREE", loadedMap.getCellKind(loadedMap.getWidth() -2, loadedMap.getHeight() -2));
  }
  

  /**
   * Testing Map Parser II
   */
  public void testB(){
    //initiating variables
    String map = 
            "########\n"
           +"#    ###\n"
           +"####   #\n"
           +"#      #\n"
           +"########\n";
    
    ISimulator simulator = Bootstrap.f().createSimulator();
    IMapBuilder mapBuilder = Bootstrap.f().createMapBuilder();
    
    //parsing and loading our map
    mapBuilder.parseMap(map);  
      simulator.loadMap(mapBuilder.getCurrentMap());
      IMap loadedMap = simulator.getMap();  
      
      //test if walls correctly placed
      assertEquals("WALL", loadedMap.getCellKind(0, 0));
      assertEquals("WALL", loadedMap.getCellKind(loadedMap.getWidth() -1, loadedMap.getHeight() -1));
      assertEquals("FREE", loadedMap.getCellKind(1, 1));
      assertEquals("WALL", loadedMap.getCellKind(loadedMap.getWidth() -2, loadedMap.getHeight() -2));
      assertEquals("WALL", loadedMap.getCellKind(2, 2));
      assertEquals("FREE", loadedMap.getCellKind(3, 3));
  
  }
  
  /**
   * Adding walls fixes not surrounded
   */
  public void testC(){
    //initiating variables
    String map = 
            "####### \n"
           +"#      #\n"
           +"#      #\n"
           +"#       \n"
           +"########\n";
    
    ISimulator simulator = Bootstrap.f().createSimulator();
    IMapBuilder mapBuilder = Bootstrap.f().createMapBuilder();
    
    //parsing and loading our map
    mapBuilder.parseMap(map);  
      simulator.loadMap(mapBuilder.getCurrentMap());
      IMap loadedMap = simulator.getMap();  
      
      //Should not be surrounded
      assertFalse(loadedMap.isCompletelySurrounded());
      
      //Adding walls to positions not having a wall which caused the map not to be surrounded
      mapBuilder.placeWall(7, 4);
      mapBuilder.placeWall(7, 1);     
      loadedMap = simulator.getMap();     
      assertTrue(loadedMap.isCompletelySurrounded());
  } 
  
  /**
   * Adding (not all) walls fixes not surrounded
   */
  public void testD(){
    //initiating variables
    String map = 
            "####### \n"
           +"#      #\n"
           +"       #\n"
           +"#       \n"
           +"## #####\n";
    
    ISimulator simulator = Bootstrap.f().createSimulator();
    IMapBuilder mapBuilder = Bootstrap.f().createMapBuilder();
    
    //parsing and loading our map
    mapBuilder.parseMap(map);  
      simulator.loadMap(mapBuilder.getCurrentMap());
      IMap loadedMap = simulator.getMap();  
      
      //Should not be surrounded
      assertFalse(loadedMap.isCompletelySurrounded());
      
      //Adding walls to positions not having a wall which caused the map not to be surrounded
      mapBuilder.placeWall(7, 4);
      mapBuilder.placeWall(7, 1);     
      loadedMap = simulator.getMap();     
      assertFalse(loadedMap.isCompletelySurrounded());
  } 
  
  /**
   * A 3 x 3 field should contain one Free field in the middle and
   *is a Maze. Removing a Wall should remove the completed surrounded
   *property
   *
   */
  public void testE(){
    ISimulator simulator = Bootstrap.f().createSimulator();
    IMapBuilder mapBuilder = Bootstrap.f().createMapBuilder();  
    mapBuilder.createEmptySquareMap(3);
      IMap m = mapBuilder.getCurrentMap();      
      simulator.loadMap(m);
      
      assertEquals("FREE", simulator.getMap().getCellKind(1, 1));
      assertTrue(simulator.getMap().isAConnectedMaze());
      mapBuilder.placeFreeCell(0, 0);
      assertFalse(simulator.getMap().isCompletelySurrounded());
  }
  
  /**
   * Numbers of walls in a loaded map should be correct
   */
  public void testF(){
    int size = 100;
    ISimulator simulator = Bootstrap.f().createSimulator();
    IMapBuilder mapBuilder = Bootstrap.f().createMapBuilder();
    
    //map with size x size Fields
    mapBuilder.createEmptySquareMap(size);    
      IMap map = mapBuilder.getCurrentMap();      
      simulator.loadMap(map);
      
      IMap loadedMap = simulator.getMap();
      
      //Testing of number of walls with mathematics formula
      assertEquals(4 * size -4 ,this.countWalls(loadedMap));
  }
  
  /**
   * Testing Picobot coordinates
   */
  public void testG(){
    int size = 1000;
    ISimulator simulator = Bootstrap.f().createSimulator();
    IMapBuilder mapBuilder = Bootstrap.f().createMapBuilder();  
    
    mapBuilder.createEmptySquareMap(size);  
      IMap map = mapBuilder.getCurrentMap();      
      simulator.loadMap(map);
      IPicobot picobot = Bootstrap.f().createPicobot();
      
      
      int height = mapBuilder.getCurrentMap().getHeight();
      int width = mapBuilder.getCurrentMap().getWidth();
      for (int i = 1; i < height - 1; i++){
        for(int j = 1; j < width - 1; j++){
            picobot.setInitialPosition(j, i);
            assertEquals(j, picobot.getXCoordinate());
            assertEquals(i, picobot.getYCoordinate());
        }
        
      }     
  }
  
  /**
   * Testing ruleParser
   */
  public void testH(){
    IRuleBuilder ruleBuilder = Bootstrap.f().createRuleBuilder();
      
      String[] ruleInput = new String[12];
      ruleInput[0]  = "0 N*** -> S 3";
      ruleInput[1]  = "1 *E** -> W 2";
      ruleInput[2]  = "2 **W* -> E 1";
      ruleInput[3]  = "3 ***S -> N 0";
      ruleInput[4]  = "4 Nxxx -> S 7";
      ruleInput[5]  = "5 xExx -> W 6";
      ruleInput[6]  = "6 xxWx -> E 5";
      ruleInput[7]  = "7 xxxS -> N 4";
      ruleInput[8]  = "8 x*** -> S 4";
      ruleInput[9]  = "9 *x** -> W 5";
      ruleInput[10] = "9 **x* -> E 3";
      ruleInput[11] = "9 ***x -> N 4";
      
      IRule[] ruleOutput = new IRule[12];
      ruleOutput[0]  = ruleBuilder.parseRule(ruleInput[0]);
      ruleOutput[1]  = ruleBuilder.parseRule(ruleInput[1]);
      ruleOutput[2]  = ruleBuilder.parseRule(ruleInput[2]);
      ruleOutput[3]  = ruleBuilder.parseRule(ruleInput[3]);
      ruleOutput[4]  = ruleBuilder.parseRule(ruleInput[4]);
      ruleOutput[5]  = ruleBuilder.parseRule(ruleInput[5]);
      ruleOutput[6]  = ruleBuilder.parseRule(ruleInput[6]);
      ruleOutput[7]  = ruleBuilder.parseRule(ruleInput[7]);
      ruleOutput[8]  = ruleBuilder.parseRule(ruleInput[8]);
      ruleOutput[9]  = ruleBuilder.parseRule(ruleInput[9]);
      ruleOutput[10] = ruleBuilder.parseRule(ruleInput[10]);
      ruleOutput[11] = ruleBuilder.parseRule(ruleInput[11]);
      
      String d[] = {"NORTH", "EAST", "WEST", "SOUTH"};
     for(int i = 0; i < ruleOutput.length; i++){       
       assertEquals(ruleInput[i], ruleOutput[i].toString());
       assertTrue(ruleOutput[i].getDestination() == d[0] ||
              ruleOutput[i].getDestination() == d[1] ||
              ruleOutput[i].getDestination() == d[2] ||
              ruleOutput[i].getDestination() == d[3]);
     }
  } 
  
  /**
   * Simulation test with map and corresponding rules
   */
  public void testI(){
    //size of auto generated map
    int size = 20;
    IPicobot picobot = Bootstrap.f().createPicobot();
    IMapBuilder mapBuilder = Bootstrap.f().createMapBuilder();
    mapBuilder.createEmptySquareMap(size);
    IRuleBuilder ruleBuilder = Bootstrap.f().createRuleBuilder();
    
    //rules working for this map.
    List<IRule> rule = new ArrayList<IRule>();
    rule.add(ruleBuilder.parseRule("0 x*** -> N 0")); 
    rule.add(ruleBuilder.parseRule("0 Nx** -> E 1"));
    rule.add(ruleBuilder.parseRule("0 NEx* -> W 1"));
    rule.add(ruleBuilder.parseRule("1 *x** -> E 1"));
    rule.add(ruleBuilder.parseRule("1 *Ex* -> W 2"));   
    rule.add(ruleBuilder.parseRule("2 **x* -> W 2"));   
    rule.add(ruleBuilder.parseRule("2 **Wx -> S 1"));
    rule.add(ruleBuilder.parseRule("2 **WS -> X 2"));
    
    ISimulator simulator = Bootstrap.f().createSimulator();
    simulator.loadMap(mapBuilder.getCurrentMap());
    picobot.loadRules(rule);
    simulator.loadPicobot(picobot);
    simulator.run(size * 100);
    
    //after run the number of traverse cell should fit this mathematics formula
    //In detail: Number of fields minus the number of walls
    assertEquals((size * size) - (4 * size - 4) , simulator.getTraversedCells().size());
    
    //Mission should be completed
    assertTrue(simulator.isMissionCompleted());
    
    //After Reset, Mission should not be completed
    simulator.reset();
    assertFalse(simulator.isMissionCompleted());
  }
  
  /**
   * Simulation test with another map and corresponding rules
   */
  public void testJ(){
    //size of auto generated map
    int size = 20;
    IPicobot picobot = Bootstrap.f().createPicobot();
    IMapBuilder mapBuilder = Bootstrap.f().createMapBuilder();
    String map = "#########################\n"
      +"#  # #   #   #   #   ## #\n"
      +"## # # # # # # #   #    #\n"
      +"## # ### # # # ######## #\n"
      +"#  #     # # #   #    # #\n"
      +"# ### #### # # # # ##   #\n"
      +"#          # # # # ######\n"
      +"############ ### #  #   #\n"
      +"#  #   #   #   # ## # ###\n"
      +"## # #   # # ###  # #   #\n"
      +"## # ##### # # # ## # # #\n"
      +"## #     #   #    # ### #\n"
      +"## ##### ## ##### #     #\n"
      +"#      # ##     # ### # #\n"
      +"# ###### #  ### #   # # #\n"
      +"#        # ## # ### # # #\n"
      +"########## #  #   # # # #\n"
      +"#    #   ### #### # # # #\n"
      +"# ## # #        # # ### #\n"
      +"#  # # ##### ## # # # # #\n"
      +"# ## # # #   #  #   #   #\n"
      +"# #  # #   ### ####### ##\n"
      +"# #### # #   # #     # ##\n"
      +"#      # ### #   ###   ##\n"
      +"#########################"; 
    mapBuilder.parseMap(map);
    IRuleBuilder ruleBuilder = Bootstrap.f().createRuleBuilder();
    
    //rules working for this map.
    List<IRule> rule = new ArrayList<IRule>();
    rule.add(ruleBuilder.parseRule("0 x*** -> N 0")); 
    rule.add(ruleBuilder.parseRule("0 xE** -> N 0")); 
    rule.add(ruleBuilder.parseRule("0 *x** -> E 1"));
    rule.add(ruleBuilder.parseRule("0 NEx* -> W 3"));
    rule.add(ruleBuilder.parseRule("0 NEW* -> X 2"));
    rule.add(ruleBuilder.parseRule("1 *x*S -> E 1"));
    rule.add(ruleBuilder.parseRule("1 ***x -> S 2"));
    rule.add(ruleBuilder.parseRule("1 xE*S -> N 0"));
    rule.add(ruleBuilder.parseRule("1 NExS -> X 3"));
    rule.add(ruleBuilder.parseRule("2 **Wx -> S 2")); 
    rule.add(ruleBuilder.parseRule("2 **x* -> W 3"));
    rule.add(ruleBuilder.parseRule("2 *xWS -> E 1"));
    rule.add(ruleBuilder.parseRule("2 xEWS -> X 0"));
    rule.add(ruleBuilder.parseRule("3 N*x* -> W 3"));
    rule.add(ruleBuilder.parseRule("3 x*** -> N 0"));
    rule.add(ruleBuilder.parseRule("3 N*Wx -> S 2"));
    rule.add(ruleBuilder.parseRule("3 NxWS -> X 1"));
    
    ISimulator simulator = Bootstrap.f().createSimulator();
    simulator.loadMap(mapBuilder.getCurrentMap());
    picobot.loadRules(rule);
    simulator.loadPicobot(picobot);
    simulator.run(size * 100);

    //Mission should not be completed
    assertFalse(simulator.isMissionCompleted());

  }
}