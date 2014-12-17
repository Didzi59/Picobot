/************** IFactory.java **************/
package picobot.interfaces.core;

/** Responsible for creating new objects.
 * It's always better to rely on factories rather than on 
 * explicit constructors (see Josh Bloch's Effective Java).
 * 
 * Example usage:
 * class MyFactory implements IFactory {
 *   public IMapBuilder createMapBuilder() {
 *     return new MyMapBuilder();
 *   }
 * }
 * 
 * IMapBuilder mb = new MyFactory().createMapBuilder();
 * 
 * The factory is used in the public tests (see {@link Bootstrap}, 
 * {@link PublicTestPicobot}).
 * 
 * Note that there is no method createMap and createRule, 
 * because it is the responsibility of {@link IMapBuilder} 
 * and {@link IRuleBuilder} to create them.
 */
public interface IFactory {
  /** @return an instance of IMapBuilder */
  public IMapBuilder createMapBuilder();
    
  /** @return an instance of IPicobot */
  public IPicobot createPicobot();
  
  /** @return an instance of IRuleBuilder */
  public IRuleBuilder createRuleBuilder();
  
  /** @return an instance of ISimulator */
  public ISimulator createSimulator();
    
} // end interface IFactory

