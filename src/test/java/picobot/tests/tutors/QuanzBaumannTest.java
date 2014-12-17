package picobot.tests.tutors;

import java.io.File;
import java.util.List;

import junit.framework.TestCase;
import picobot.interfaces.core.IMap;
import picobot.interfaces.core.IMapBuilder;
import picobot.interfaces.core.IRule;
import picobot.tests.Bootstrap;

public class QuanzBaumannTest extends TestCase {


  public static IRule createTestRule() {
    IRule rule = Bootstrap.f().createRuleBuilder()
        .parseRule("0 N*** -> S 0");
    return rule;
  }

  // wrong destination
  public void testA() {
    String srule = "A x*** -> N 0  ";
    try {
    Bootstrap.f().createRuleBuilder().parseRule(srule);
    fail();     
    } catch (Exception e) {    }
  }

  // space
  public void testB() {
    String srule = "  0 x*** -> N 0  ";

    IRule rule = Bootstrap.f().createRuleBuilder().parseRule(srule);
    assertEquals("0 x*** -> N 0", rule.toString()); 
  }

  // wrong condition
  public void testC() {
    String srule = "0 x**N -> N 0  ";
    try {
    Bootstrap.f().createRuleBuilder().parseRule(srule);
    fail();     
    } catch (Exception e) {    }
  }

  // comment
  public void testD() {
    String srule = "# if there's nothing to the N, go N" + "\n"
        + "# 0 x*** -> N 0";

    List<IRule> rules = Bootstrap.f().createRuleBuilder().parseRules(srule);
    assertEquals(0, rules.size()); 
  }

  // wrong file
  public void testE() {
    boolean fail = true;
    File file = new File("foo.bar");
    try {
      @SuppressWarnings("unused")
      List<IRule> lrules = Bootstrap.f().createRuleBuilder()
          .parseRules(file);

    } catch (RuntimeException e) {
      fail = false;
    }
    assertFalse(fail);
  }
  
  // wrong target
  public void testF() {
    String srule = "0 x*** -> N c";
    try {
    Bootstrap.f().createRuleBuilder().parseRule(srule);
    fail();     
    } catch (Exception e) {    }  
  }
  
  // wrong target
  public void testG() {
    String srule = "A x*** -> * 0";

    try {
      Bootstrap.f().createRuleBuilder().parseRule(srule);
      fail();     
      } catch (Exception e) {    }  
  }
  
  public static IMap  createMap() {
    IMapBuilder mb = Bootstrap.f().createMapBuilder();
    mb.createEmptySquareMap(5);
    return mb.getCurrentMap();
  }

  
  /** Creates a new map and tests the output representation */

  public void testH() {
    IMapBuilder mb = Bootstrap.f().createMapBuilder();
    String map = "#####\n"
          +"##  #\n"
          +"#   #\n"
          +"#   #\n"
          +"#####\n";
    mb.parseMap(map);

    assertEquals( mb.getCurrentMap().toString(), map);
  }
  
  // wrong content
  public void testI() {
      boolean fail = true;
      IMapBuilder mb = Bootstrap.f().createMapBuilder();
      String map = "#####\n"
            +"#Er #\n"
            +"#   #\n"
            +"#   #\n"
            +"#####\n";
      
      try {
       mb.parseMap(map);
    } catch (RuntimeException e) {
      fail=false;
    }
      assertFalse(fail);
    }
  
  // wrong file
  public void testJ() {
      boolean fail = true;
      IMapBuilder mb = Bootstrap.f().createMapBuilder();
      File file = new File("foo.bar");
      
      try {
       mb.parseMap(file);
    } catch (RuntimeException e) {
      fail=false;
    }
      assertFalse(fail);
    }
  
  

  /** placeWall illegal argument */
  public void testK() {
  boolean fail = true;
    
    IMapBuilder mb = Bootstrap.f().createMapBuilder();
    mb.createEmptySquareMap(5);

    
    try {
      mb.placeWall(-1,-1);
  } catch (Exception e) {
    fail = false;
  }
    assertFalse(fail);
    
  }
  
}
