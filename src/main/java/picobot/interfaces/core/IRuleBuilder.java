/************** IRuleBuilder.java **************/
package picobot.interfaces.core;

import java.io.File;
import java.util.List;

/** Responsible for creating rules (instances of IRule). */
public interface IRuleBuilder {

  /** Builds a rule from its textual ASCII representation.
   * It is expected that the ruleString is a correct rule as specified
   * in the requirements.
   * Especially, comments are not allowed.
   * 
   * @param ruleString is the textual ASCII representation of one rule
   * @return a new  IRule instance
   */
  public IRule parseRule(String ruleString);

  /** Builds a list of rules from a source text.
   * The source can contain comments, and blank lines.
   * There is at most one rule per line (separated by the line break of 
   * the underlying OS)
   * 
   * @param srule specifies a set of rules.
   * @return a list of IRule instances
   */
  public List<IRule> parseRules(String srule);
  
  /** Parses the content of a file as rules.
   * Must be semantically equivalent to {@link parseRules(String)}.
   * 
   * See {@link IMapBuilder} for the exception strategy.
   * 
   * @param ruleFile must be an existing file containing rules that comply
   * with the specified format.
   * @return a list of IRule instances
   */
  public List<IRule> parseRules(File ruleFile);

} // end interface IRuleBuilder
