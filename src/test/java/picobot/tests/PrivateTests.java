package picobot.tests;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import picobot.tests.tutors.LoewTest;
import picobot.tests.tutors.QuanzBaumannTest;
import picobot.tests.tutors.TestMapCreation;
import picobot.tests.tutors.TestPicobot;
import picobot.tests.tutors.TestRule;
import picobot.tests.tutors.TestSimulator;
import picobot.tests.tutors.TestSimulator2;

@RunWith(Suite.class)
@SuiteClasses( { 
  LoewTest.class,
  QuanzBaumannTest.class,
  TestPicobot.class, 
  TestMapCreation.class,
  TestRule.class,
  TestSimulator.class,
  TestSimulator2.class,
})
public class PrivateTests {
  @BeforeClass
  public static void init() {
    System.setProperty("factory", Main._FACTORY);
  }

}
