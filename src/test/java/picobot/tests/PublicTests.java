package picobot.tests;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import picobot.tests.students.PublicTestMapBuilder;
import picobot.tests.students.PublicTestPicobot;
import picobot.tests.students.PublicTestRuleBuilder;
import picobot.tests.students.PublicTestSimulator;

@RunWith(Suite.class)
@SuiteClasses( { 
  // the public/student tests
  PublicTestMapBuilder.class,
  PublicTestRuleBuilder.class,
  PublicTestPicobot.class,
  PublicTestSimulator.class
})
public class PublicTests {
  @BeforeClass
  public static void init() {
    System.setProperty("factory", Main._FACTORY);
  }

}
