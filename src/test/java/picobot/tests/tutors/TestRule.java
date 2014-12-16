package picobot.tests.tutors;

import java.util.List;

import junit.framework.TestCase;
import picobot.interfaces.core.IRule;
import picobot.tests.Bootstrap;

public class TestRule extends TestCase {
 
  public static IRule createTestRule() {
    IRule rule = Bootstrap.f().createRuleBuilder().parseRule("0 N*** -> S 0");
    return rule;
  }
  
  public void testA() {
    assertEquals("0 N*** -> S 0",createTestRule().toString());
  }
  
  public void testC() {
		String srule = 
			  "0 x*** -> N 0  ";

		IRule rule = Bootstrap.f().createRuleBuilder().parseRule(srule); 
	    assertEquals("0 x*** -> N 0",rule.toString()); //returns only first rule
  }
  
  public void testD() {
		String srule = 
			  "# if there's nothing to the N, go N"+"\n"+
		      "0 x*** -> N 0";

		List<IRule> rules = Bootstrap.f().createRuleBuilder().parseRules(srule); 
	    assertEquals("0 x*** -> N 0",rules.get(0).toString()); //returns only first rule
  }
  
  
  public void testB() {
		String rules = 
			  "0 x*** -> N 0   # if there's nothing to the N, go N"+"\n"+
			  "0 N*** -> X 1   # if N is blocked, switch to state 1"+"\n"+
			  "1 ***x -> S 1   # if there's nothing to the S, go S"+"\n"+
			  "1 *x*S -> E 0   # otherwise, switch to state 0"+"\n"+

			  "1 *E*S -> W 2"+"\n"+

			  "2 x*** -> N 2   # if there's nothing to the N, go N"+"\n"+
			  "2 N*** -> X 3   # if N is blocked, switch to state 3"+"\n"+

			  "3 ***x -> S 3   # if there's nothing to the S, go S"+"\n"+
			  "3 *x*S -> W 2   # otherwise, switch to state 2";

		List<IRule> lrules = Bootstrap.f().createRuleBuilder().parseRules(rules);
	  assertEquals("0 x*** -> N 0",lrules.get(0).toString()); 
	  assertEquals("3 *x*S -> W 2",lrules.get(8).toString()); 
  }

  
}
