/************** IRule.java **************/
package picobot.interfaces.core;

/** Responsible for representing a rule that define picobot movements.
 * 
 * This interface represents immutable rules, i.e. there are only methods 
 * to get some information. 
 * 
 * Rules are meant to be created using a {@link IRuleBuilder}.
 */
public interface IRule {

  /** @return the condition at a given place. In {WALL, FREE, ANY}
   * @param place must be in {NORTH, SOUTH, EAST, WEST}.
   */
  public String getConditionAt(String place);

  /** @return the target direction. In {NORTH, SOUTH, EAST, WEST, STAY}. 
   */
  public String getDestination();

  /** @return the picobot state that is required for the picobot 
   * to apply this rule. */
  public int getRequiredState();
  
  /** @return the target state of the rule. */
  public int getTargetState();

  /** @return a string representation of the rule as specified in
   *  the requirements */
  public String toString();

} // end interface IRule


