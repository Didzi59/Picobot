package picobot.tests.students;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import picobot.interfaces.core.IMap;
import picobot.interfaces.core.IMapBuilder;
import picobot.interfaces.core.IPicobot;
import picobot.interfaces.core.IRule;
import picobot.interfaces.core.IRuleBuilder;
import picobot.interfaces.core.ISimulator;
import picobot.tests.Bootstrap;

public class PublicTestSimulator extends TestCase {
  
  /** This tests checks whether the implementation is capable
   * of storing the past of the simulation, i.e. whether we can get the
   * traversed cells from it.
   */
  public void testA() {
    ISimulator sim = Bootstrap.f().createSimulator();
    
    IMapBuilder mb = Bootstrap.f().createMapBuilder();
    mb.createEmptySquareMap(5);
    IMap m = mb.getCurrentMap();
    
    sim.loadMap(m);
    
    IPicobot p = Bootstrap.f().createPicobot();
    List<IRule> rules = new ArrayList<IRule>();

    IRuleBuilder rb = Bootstrap.f().createRuleBuilder();    
    rules.add(rb.parseRule("0 *x** -> E 0"));
    rules.add(rb.parseRule("0 *E** -> W 1"));
    rules.add(rb.parseRule("1 **x* -> W 1"));
    rules.add(rb.parseRule("1 **W* -> E 0"));
    
    // loading the rules in the robot
    p.loadRules(rules);
    
    // loading the picobot in the simulator
    sim.loadPicobot(p);
    
    // setting the picobot at 1,1
    p.setInitialPosition(1, 1);
     
    // runs the simulation for n steps
    sim.run(10);
        
    Set<int[]> result = sim.getTraversedCells();
    System.out.println(result);
    
    Set<int[]> expectedTraversedCells = new HashSet<int[]>();
    
    int[] pos1 = {1,1};
    expectedTraversedCells.add(pos1);
    int[] pos2 = {2,1};
    expectedTraversedCells.add(pos2);
    int[] pos3 = {3,1};    
    expectedTraversedCells.add(pos3);
    
    // Wednesday, February 23 2011
    // this test is changed to allow different assumptions that are not specified
    // see https://moodle.informatik.tu-darmstadt.de/mod/forum/discuss.php?d=7184
    // It is changed in a **backward compatible way**: the code that previously 
    // passed this test is still correct.
    
    // Note that the following permits that the set contains several times 
    // the same position (since Java does not handle equals with arrays of int
    // in the regular way)
    
    // expectedTraversedCells \in result
    for (int[] item : expectedTraversedCells) {
      boolean found = false;
      for (int[] resultItem : result) {
        if (Arrays.equals(resultItem, item)) found=true;
      }
      assertTrue(found);
    }
    
    // result \in expectedTraversedCells 
    int numberOfNotExpectedPosition = 0;
    for (int[] item : result) {
      boolean found = false;
      for (int[] resultItem : expectedTraversedCells) {
        if (Arrays.equals(resultItem, item)) found=true;
      }
      if (!found) { numberOfNotExpectedPosition++; }
    }
    
    // if you assume that loadPicobot adds a random position in the traversed cells
    // then numberOfNotExpectedPosition=1 and it's still OK
    if (numberOfNotExpectedPosition>1) { fail(); }      
    
  }

  /** Testing reset */
  public void testB() {
    
    ISimulator sim = Bootstrap.f().createSimulator();
    
    IMapBuilder mb = Bootstrap.f().createMapBuilder();
    mb.createEmptySquareMap(5);
    IMap m = mb.getCurrentMap();
    
    sim.loadMap(m);
    
    IPicobot p = Bootstrap.f().createPicobot();
    List<IRule> rules = new ArrayList<IRule>();

    IRuleBuilder rb = Bootstrap.f().createRuleBuilder();    
    rules.add(rb.parseRule("0 *x** -> E 0"));
    rules.add(rb.parseRule("0 *E** -> X 0"));
    
    p.loadRules(rules);
    
    sim.loadPicobot(p);
        
    sim.run(1);

    sim.reset();
    
    // The following has changed on Feb. 15
    // previous version: assertEquals(0, sim.getTraversedCells().size());
    // the semantics of getTraversedCells() is also clarified in its Javadoc documentation
    // This change will be announced on the forum at the official start date 
    // of the project
    assertEquals(1, sim.getTraversedCells().size());
    int[] pos = sim.getTraversedCells().toArray(new int[0][0])[0];
    assertEquals(pos[0], p.getXCoordinate());
    assertEquals(pos[1], p.getYCoordinate());
    
    assertEquals("FREE", m.getCellKind(p.getXCoordinate(), p.getYCoordinate()));
    
  }

  
  /** A complete simulation 
   * with a set of rules to navigate east-west-east infinitely.
   * */
  public void testC() {
    ISimulator sim = Bootstrap.f().createSimulator();
    
    IMapBuilder mb = Bootstrap.f().createMapBuilder();
    mb.createEmptySquareMap(5);
    IMap m = mb.getCurrentMap();
    
    
    IPicobot p = Bootstrap.f().createPicobot();
    List<IRule> rules = new ArrayList<IRule>();

    IRuleBuilder rb = Bootstrap.f().createRuleBuilder();    
    rules.add(rb.parseRule("0 *x** -> E 0"));
    rules.add(rb.parseRule("0 *E** -> W 1"));
    rules.add(rb.parseRule("1 **x* -> W 1"));
    rules.add(rb.parseRule("1 **W* -> E 0"));

    // loading the rules in the robot
    p.loadRules(rules);
    
    // loading the picobot in the simulator
    sim.loadPicobot(p);
    
    sim.loadMap(m);

    
    // setting the picobot at 1,1
    p.setInitialPosition(1, 1);

    List<String> trace = new ArrayList<String>();
    for (int i=0; i<=10; i++) {
      String traceItem = p.getXCoordinate()+","+p.getYCoordinate();
      trace.add(traceItem);
      sim.step();
    }

    //System.out.println(trace);
    String[] expected = {
        "1,1", "2,1", "3,1", "2,1", "1,1", "2,1", "3,1", "2,1", "1,1", "2,1", "3,1"
    };

    assertTrue(Arrays.equals(expected, trace.toArray()));
    
  }

}
