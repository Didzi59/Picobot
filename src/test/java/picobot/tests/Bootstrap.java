package picobot.tests;

import picobot.interfaces.core.IFactory;

/** Responsible for creating a factory object using the singleton pattern.
 * The object is created with reflection, so as to have no compile-time
 * dependency.
 * 
 * Usage: 
 * <pre>
 * IMapBuilder mb = Bootstrap.f().createMapBuilder();
 * </pre>
 */
public class Bootstrap {
  private static IFactory _INSTANCE = null;
  public synchronized static IFactory  f() {
    try {
    if (_INSTANCE == null ) {
         String factoryClass = System.getProperty("factory");

         // default test factory
         if (factoryClass == null) {
           factoryClass = "picobot.implementation.Factory";
         }
        _INSTANCE = ((IFactory)Class.forName(factoryClass).newInstance());
    }
    return _INSTANCE;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }  
  }
}
