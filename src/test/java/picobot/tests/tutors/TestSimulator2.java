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


public class TestSimulator2 extends TestCase {
  
  /** Tests "isMissionCompleted" */
  public void testA() {
    ISimulator sim = Bootstrap.f().createSimulator();
    
    IMapBuilder mb = Bootstrap.f().createMapBuilder();
    String map = 
       "########\n"
      +"#      #\n"
      +"########\n";

    mb.parseMap(map);
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
    p.setInitialPosition(3, 1);
         
    // runs for n steps
    sim.run(20);
            
    assertEquals(true, sim.isMissionCompleted());

  }
}
