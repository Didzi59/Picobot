package picobot.tests.tutors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;
import picobot.interfaces.core.IMap;
import picobot.interfaces.core.IMapBuilder;
import picobot.interfaces.core.IPicobot;
import picobot.interfaces.core.IRule;
import picobot.interfaces.core.IRuleBuilder;
import picobot.interfaces.core.ISimulator;
import picobot.tests.Bootstrap;


/** check at runtime the validity of rules, like going inside a wall! */
public class TestSimulator extends TestCase {
  
  
  /** Test for the connected maze task. */
  public void testA() {
    ISimulator sim = Bootstrap.f().createSimulator();
    
    IMapBuilder mb = Bootstrap.f().createMapBuilder();
    mb.parseMap(
        "#########\n"+
        "#### ####\n"+
        "#### ####\n"+
        "##     ##\n"+
        "#### ####\n"+
        "#### ####\n"+
        "#########\n");
    IMap m = mb.getCurrentMap();
    //System.out.println(m.toString());
    //System.out.println("foo");
    
    sim.loadMap(m);
    
    IPicobot p = Bootstrap.f().createPicobot();
    List<IRule> rules = new ArrayList<IRule>();

    IRuleBuilder rb = Bootstrap.f().createRuleBuilder();    
    String rulesDefinition =     "## Going towards north \n"+
    "0 xE** -> N 0 \n"+
    "0 *x** -> E 1  # free to the east\n"+
    "0 NEx* -> W 3\n"+
    "0 NEW* -> X 2 # deadlock\n"+
    
    "## Going towards east \n"+
    "1 *x*S -> E 1 # \n"+
    "1 ***x -> S 2 #  \n"+
    "1 xE*S -> N 0\n"+
    "1 NExS -> X 3 # deadlock\n"+
    
    "## Going towards south \n"+
    "2 **Wx -> S 2 \n"+
    "2 **x* -> W 3 #  \n"+
    "2 *xWS -> E 1\n"+
    "2 xEWS -> X 0 # deadlock\n"+
    
    "## Going towards west \n"+
    "3 N*x* -> W 3 # \n"+
    "3 x*** -> N 0 \n"+
    "3 N*Wx -> S 2\n"+
    "3 NxWS -> X 1 # deadlock\n";

    System.out.println(rulesDefinition);
    for(IRule r : rb.parseRules(rulesDefinition)) {
      rules.add(r);
    }
    
    // loading the rules in the robot
    p.loadRules(rules);
    
    // loading the picobot in the simulator
    sim.loadPicobot(p);
    
    // setting the picobot position
    p.setInitialPosition(4,1);

    List<String> trace = new ArrayList<String>();
    for (int i=0; i<=22; i++) {
      String traceItem = p.getXCoordinate()+","+p.getYCoordinate();
      System.out.print("\""+traceItem+"\", ");
      trace.add(traceItem);
      sim.step();
    }

    System.out.println(trace);
    String[] expected = {
        "4,1", "4,2", "4,3", "5,3", "6,3", "6,3", "5,3", "4,3", "4,4", "4,5", "4,5", "4,4", "4,3", "3,3", "2,3", "2,3", "3,3", "4,3", "4,2", "4,1", "4,1", "4,2", "4,3"
    };

    assertTrue(Arrays.equals(expected, trace.toArray()));

    assertTrue(sim.isMissionCompleted());
    
    String  map2_mission2_maze = 
    "#########################\n"+
    "#  # #   #   #   #   ## #\n"+
    "## # # # # # # #   #    #\n"+
    "## # ### # # # ######## #\n"+
    "#  #     # # #   #    # #\n"+
    "# ### #### # # # # ##   #\n"+
    "#          # # # # ######\n"+
    "############ ### #  #   #\n"+
    "#  #   #   #   # ## # ###\n"+
    "## # #   # # ###  # #   #\n"+
    "## # ##### # # # ## # # #\n"+
    "## #     #   #    # ### #\n"+
    "## ##### ## ##### #     #\n"+
    "#      # ##     # ### # #\n"+
    "# ###### #  ### #   # # #\n"+
    "#        # ## # ### # # #\n"+
    "########## #  #   # # # #\n"+
    "#    #   ### #### # # # #\n"+
    "# ## # #        # # ### #\n"+
    "#  # # ##### ## # # # # #\n"+
    "# ## # # #   #  #   #   #\n"+
    "# #  # #   ### ####### ##\n"+
    "# #### # #   # #     # ##\n"+
    "#      # ### #   ###   ##\n"+
    "#########################\n";
    
    // laoding map2
    mb.parseMap(map2_mission2_maze);
    IMap map2 = mb.getCurrentMap();
    
    sim.loadMap(map2);
    sim.reset();

    assertFalse(sim.isMissionCompleted());

    // running for a long time
    sim.run(10000);
    
    assertTrue(sim.isMissionCompleted());

  }

  public void testB() {

    ISimulator sim = Bootstrap.f().createSimulator();
    
    IMapBuilder mb = Bootstrap.f().createMapBuilder();
    mb.parseMap(
        "#########\n"+
        "#### ####\n"+
        "#### ####\n"+
        "##     ##\n"+
        "#### ####\n"+
        "#### ####\n"+
        "#########\n");
    IMap m = mb.getCurrentMap();
    
    sim.loadMap(m);
    
    IPicobot p = Bootstrap.f().createPicobot();
    
    sim.loadPicobot(p);
    try {
      // we want to be sure that this exception is thrown
      sim.step();
      fail();
    } catch (RuntimeException e) {
    }
  }
}
