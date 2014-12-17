package picobot.implementation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import picobot.interfaces.core.IRule;
import picobot.interfaces.core.IRuleBuilder;

public class RuleBuilder implements IRuleBuilder {

  /** must remains package-visible
   * From outside, please use the factory  
   */
  public RuleBuilder() {
  }
  
  public IRule parseRule(String ruleString) {
    // Requirement in the minimal level
    //return parseRuleSimple(ruleString);

    // Requirement in the extension level
    return parseRuleExtensionLevel(ruleString);
  }
  
  /** Requirement in the extension level: This uses a regular expression, 
   * but there are plenty of other correct solutions.
   */
  public IRule parseRuleExtensionLevel(String ruleString) {
    //Pattern syntax = Pattern.compile("^(\\d+).*$");
    Pattern syntax = Pattern.compile("^\\s*(\\d+)\\s*([N*x])([E*x])([W*x])([S*x])\\s*->\\s*([NEWSX])\\s*(\\d+)\\s*$");
    Matcher matcher = syntax.matcher(ruleString);
    matcher.matches();
    
    Rule result = new Rule();
    
    result.setInputState(Integer.parseInt(matcher.group(1)));

    result.addCondition(symbolToKind(matcher.group(2).charAt(0)), "NORTH");
    result.addCondition(symbolToKind(matcher.group(3).charAt(0)), "EAST");
    result.addCondition(symbolToKind(matcher.group(4).charAt(0)), "WEST");
    result.addCondition(symbolToKind(matcher.group(5).charAt(0)), "SOUTH");

    // 6,7,8,9 (space)->(space)
    
    result.setDestination(symbolToDirection(matcher.group(6).charAt(0)));
    // 11 (space)
    result.setTargetState(Integer.parseInt(matcher.group(7)));

    return result;

  }

  /** Parses rules using a "static" grammar: characters are expected to be always
   * at the same position (e.g. the condition on the state at position 0)
   */
  public IRule parseRuleSimple(String ruleString) {

    Rule result = new Rule();
    
    result.setInputState(Integer.parseInt(ruleString.substring(0, 1)));

    result.addCondition(symbolToKind(ruleString.charAt(2)), "NORTH");
    result.addCondition(symbolToKind(ruleString.charAt(3)), "EAST");
    result.addCondition(symbolToKind(ruleString.charAt(4)), "WEST");
    result.addCondition(symbolToKind(ruleString.charAt(5)), "SOUTH");

    // 6,7,8,9 (space)->(space)
    
    result.setDestination(symbolToDirection(ruleString.charAt(10)));
    // 11 (space)
    result.setTargetState(Integer.parseInt(Character.toString(ruleString.charAt(12))));

    return result;
  }
  
  public String symbolToKind(char c) {
    switch (c) {
    case 'N': return "WALL";
    case 'E': return "WALL";
    case 'W': return "WALL";
    case 'S': return "WALL";
    case 'x': return "FREE";
    case '*': return "ANY";
    default: throw new SimulationException("Parsing error at line "+currentLineNumber+": symbol \""+c+"\" is not valid at this position");
    }
  }

  public String symbolToDirection(char c) {
    switch (c) {
    case 'N': return "NORTH";
    case 'E': return "EAST";
    case 'W': return "WEST";
    case 'S': return "SOUTH";
    case 'X': return "ANY";
    default: throw new SimulationException("Parsing error at line "+currentLineNumber+": symbol \""+c+"\" is not valid at this position");
    }
  }

  private int currentLineNumber;

  public List<IRule> parseRules(String srules) {
    try {

      // using a BufferedReader is a way to have an OS independent way
      // to detect line breaks.
      BufferedReader fileReader = new BufferedReader(new StringReader(srules));

      List<IRule> rules = new ArrayList<IRule>();
      currentLineNumber=1;
      String line = fileReader.readLine();
      while (line != null) {
        // trimming
        line = line.trim();

        // removing the comments
        if (line.contains("#")) {
          // where does the comment starts
          int pos = line.indexOf("#");
          line = line.substring(0, pos);
        }

        // do we still have something?
        if (line.length() > 0) {
          // then it must be a rule
          rules.add(parseRule(line));
        }

        line = fileReader.readLine();
        currentLineNumber++;

      }

      return rules;
    
    } catch (IOException e) { // for the method call to readLine
      throw new SimulationException(e);
    }
  }

  public List<IRule> parseRules(File mapFile) {
    try {
      BufferedReader fileReader = new BufferedReader(new FileReader(mapFile));
      Writer sw = new StringWriter();
      String s = fileReader.readLine(); 
      while (s != null) {
        // Warning the readLine removes the line break :-)
        sw.write(s+"\n");
        s = fileReader.readLine();      
      }
      return parseRules(sw.toString());
    } catch (IOException e) {
      throw new SimulationException(e);    
    }
  }

}
