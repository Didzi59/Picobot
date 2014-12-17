package picobot.tests.tutors;

import java.util.Arrays;

import junit.framework.TestCase;
import picobot.interfaces.core.IPicobot;
import picobot.interfaces.core.IRule;
import picobot.tests.Bootstrap;


public class TestPicobot extends TestCase {
  
  /** What happens if the picobot is not in the correct state? 
   * Nothing, the picobot stays unchanged,
   */
  public void testA() {
    IPicobot p = Bootstrap.f().createPicobot();
    IRule r = Bootstrap.f().createRuleBuilder().parseRule("1 N*** -> S 0");
    // moving to north
    p.apply(r);
    System.out.println(p.toString());
    assertEquals( p.getXCoordinate(), 0); 
    assertEquals( p.getYCoordinate(), 0); 
    assertEquals( p.getState(), 0); 
    assertEquals( p.getRules().size(), 0); 
  }

  public void testB() {
    IPicobot p = Bootstrap.f().createPicobot();
    IRule r = Bootstrap.f().createRuleBuilder().parseRule("1 N*** -> S 0");
    IRule[] l = {r};
    p.loadRules(Arrays.asList(l));
    assertEquals( p.getXCoordinate(), 0); 
    assertEquals( p.getYCoordinate(), 0); 
    assertEquals( p.getState(), 0); 
    assertEquals( p.getRules().size(), 1); 
  }

}
