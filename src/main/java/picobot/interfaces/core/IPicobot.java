/************** IPicobot.java **************/
package picobot.interfaces.core;

import java.util.List;

/** Responsible for representing a picobot.
 * 
 * The public methods on a picobot can be classified as:<ul>
 *     <li> introspection (getXCoordinate, getYCoordinate, getRules)
 *     <li> intercession (setInitialPosition) 
 *     <li> simulation (apply)
 *     <li> queries (getApplicableRule)
 *</ul>
 */
public interface IPicobot {
  /** @return the X coordinate of the position of the picobot */
  public int getXCoordinate();

  /** @return the Y coordinate of the position of the picobot */
  public int getYCoordinate();

  /** @return the internal state of the picobot */
  public int getState();

  /** @return the rules that are currently loaded in the picobot. */
  public List<IRule>  getRules();

  /** Sets the initial position of the picobot. 
   * 
   * @param xCoordinate is the x-axis coordinate at which to place the picobot
   * @param yCoordinate is the y-axis coordinate at which to place the picobot
   */
  public void setInitialPosition(int xCoordinate, int yCoordinate);

  /** Loads a set of rules in the picobot. The previous rules must 
   * be erased. 
   * 
   * @param rules is a list of rules to be loaded into the picobot.
   */
  public void loadRules(List<IRule> rules);
  
  /** Applies a rule to a picobot.
   * 
   * This method changes its internal 
   * state and its position (if the internal state of the picobot 
   * corresponds to the condition on the state of the rule).
   * 
   * @param rule is a rule to be executed by the picobot.
   */
  public void apply(IRule rule);

  /** Returns the first applicable rule, according to the internal state 
   * of the Picobot, the internal rules, and the given map. If no rule 
   * is applicable, throws a subtype of RuntimeException.
   * 
   * @param map is the map used to select the applicable rule.
   * @return a rule that is currently loaded in this picobot (in operation)
   */
  public IRule getApplicableRule(IMap map);

} // end interface IPicobot
