package picobot.tests;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses( { 
  PublicTests.class,
  PrivateTests.class,
})
public class Main  {
  public final static String _FACTORY = "picobot.implementation.Factory";
  @BeforeClass
  public static void init() {
    System.setProperty("factory", _FACTORY);
  }
}
