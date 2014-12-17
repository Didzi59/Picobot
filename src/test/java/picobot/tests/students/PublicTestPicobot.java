package picobot.tests.students;

import java.util.Arrays;

import junit.framework.TestCase;
import picobot.interfaces.core.IPicobot;
import picobot.interfaces.core.IRule;
import picobot.tests.Bootstrap;

public class PublicTestPicobot extends TestCase {

  /** Creates a simple picobot and tests the result of toString() */
  public void testA() {
    IPicobot p = Bootstrap.f().createPicobot();
    assertEquals(0, p.getXCoordinate());
    assertEquals(0, p.getYCoordinate());
    assertEquals(0, p.getState());
    assertEquals(0, p.getRules().size());
  }
  
  /** Applies a given rule.
   *  (This test does not require a map.)
   */
  public void testB() {
    IPicobot p = Bootstrap.f().createPicobot();
    IRule r = Bootstrap.f().createRuleBuilder().parseRule("0 N*** -> S 0");
    p.loadRules((Arrays.asList((new IRule[] {r}))));
    p.apply(r);
    assertEquals(0, p.getXCoordinate());
    assertEquals(-1, p.getYCoordinate());
    assertEquals(0, p.getState());
    assertEquals(1, p.getRules().size());
  }
}
