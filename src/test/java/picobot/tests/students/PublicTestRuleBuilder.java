package picobot.tests.students;

import picobot.interfaces.core.IRule;
import picobot.interfaces.core.IRuleBuilder;
import picobot.tests.Bootstrap;
import junit.framework.TestCase;

public class PublicTestRuleBuilder extends TestCase {

  /** Parses a valid rule */
  public void testA() {
    IRuleBuilder rb = Bootstrap.f().createRuleBuilder();
    IRule rule = rb.parseRule("0 N*x* -> S 8");
    assertEquals("WALL",rule.getConditionAt("NORTH"));
    assertEquals("ANY",rule.getConditionAt("EAST"));
    assertEquals("FREE",rule.getConditionAt("WEST"));
    assertEquals("ANY",rule.getConditionAt("SOUTH"));
    assertEquals(0, rule.getRequiredState());
    assertEquals(8, rule.getTargetState()); 
    assertEquals("SOUTH", rule.getDestination()); 
  }

}
