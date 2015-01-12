package opl.iagl

// java
import java.io.{PrintWriter, File}

// alloy
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution

// intern project dependencies
import opl.iagl.EnvInfo.ENVINFO
import opl.iagl.Move.MOVE
import opl.iagl.alloy.util.AlloyOutputParser

// scala
import scala.collection.immutable

/**
 * Enumerates possible move value that Picobot can execute
 */
object Move extends Enumeration {
  type MOVE = Value
  val NORTH = Value("N")
  val EAST = Value("E")
  val WEST = Value("W")
  val SOUTH = Value("S")
  val STAY = Value("x")

  /**
   * Gives a MOVE value from a character. It raises an exception if
   * direction parameter is wrong
   *
   * @param direction Is a character representing a MOVE value
   * @return the corresponding MOVE value from direction variable
   */
  def get(direction : String) : MOVE = {
    direction match {
      case "N" => NORTH
      case "E" => EAST
      case "W" => WEST
      case "S" => SOUTH
      case "x" => STAY
      case _ => throw new Exception("Can't find a MOVE value with "+ direction)
    }
  }
}

/**
 * Enumerates possible environment value where Picobot is located
 */
object EnvInfo extends Enumeration {
  type ENVINFO = Value
  val NORTH = Value("N")
  val EAST = Value("E")
  val WEST = Value("W")
  val SOUTH = Value("S")
  val NOTHING = Value("x")
  val ANY = Value("*")

  /**
   * Gives a ENVINFO value from a character. It raises an exception if
   * direction parameter is wrong
   *
   * @param direction Is a character representing a ENVINFO value
   * @return the corresponding ENVINFO value from direction variable
   */
  def get(direction : String) : ENVINFO = {
    direction match {
      case "N" => NORTH
      case "E" => EAST
      case "W" => WEST
      case "S" => SOUTH
      case "x" => NOTHING
      case "*" => ANY
      case _ => throw new Exception("Can't find an ENVINFO value with "+ direction)
    }
  }
}

/**
 * Describes a Picobot environment while crossing
 *
 * @see [opl.iagl.EnvInfo]
 *
 * @param north is the value information for the direction
 * @param east is the value information for the direction
 * @param west is the value information for the direction
 * @param south is the value information for the direction
 */
case class Surroundings(north: ENVINFO, east: ENVINFO, west: ENVINFO, south: ENVINFO) {
  override def toString : String = {
    north.toString + east.toString + west.toString + south.toString
  }
}

/**
 * Describes a Picobot action
 *
 * @see [opl.iagl.Move]
 *
 * @param nextState tells what is the next Picobot's intern state
 * @param move tells what move the Picobot has to perform
 */
case class Action(nextState : Int, move : MOVE) {
  override def toString : String = {
    val direction = move match {
      case Move.NORTH => "N"
      case Move.EAST => "E"
      case Move.WEST => "W"
      case Move.SOUTH => "S"
      case Move.STAY => "X"
    }

    direction +" "+ nextState
  }
}

/**
 * Describes a Picobot's rule
 *
 * @param currentState tells what is the Picobot's current intern state
 * @param environment tells what the current environment where the Picobot is located
 * @param next // tells, accordingly to the current current and the environment, the action that Picobot must perform
 */
case class Rule(currentState : Int, environment: Surroundings, next: Action) {
  override def toString : String = {
    currentState +" "+ environment.toString +" -> "+ next.toString
  }
}

/**
 * Contains default classes to generate Picobot Java rules and helper funtions to build rules
 * from an alloy result output
 *
 * @author Romain Philippon
 */
package object alloy {
  /**
   * Is a character that separated alloy variable from its value
   */
  private val SEPARATOR_CHAR = '='

  /**
   * Parses an alloy output from Picobot.als model
   *
   * @see [opl.iag.alloy.Main.getFirstNSolutions]
   *
   * @param itSolution is the alloy solution provided by Alloy
   * @return the corresponding list of rules that itSolution variable provided
   */
  def parseRegularAlloyModel(itSolution: A4Solution): List[Rule] = {
    val solution = itSolution.toString.replace("$", "")
    val lines = solution.split("\n").drop(7).reverse.drop(16).reverse // delete useless line

    /* DEBUG PART 1 /
    for(line <- lines) {
      println(line)
    }
    */

    /* GET ACTIONS */
    val actionsStr = AlloyOutputParser.extract(lines(9), SEPARATOR_CHAR) // action name
    val nextStateStr = AlloyOutputParser.extract(lines(10), SEPARATOR_CHAR) // next state
    val moveStr = AlloyOutputParser.extract(lines(11), SEPARATOR_CHAR) // move

    val nextStates = AlloyOutputParser.splitIntoMap(AlloyOutputParser.id)(nextStateStr, ", ", "->")
    val moves = AlloyOutputParser.splitIntoMapOfChar(moveStr, ", ", "->")

    val actions = actionsStr.split(", ").map { action =>
      val nextState = nextStates.get(action) match {
        case Some(move) => move.toInt
        case None => throw new Exception("No action were detected")
      }

      val build = moves.get(action) match {
        case Some('X') => Action(nextState, Move.STAY)
        case Some('N') => Action(nextState, Move.NORTH)
        case Some('E') => Action(nextState, Move.EAST)
        case Some('S') => Action(nextState, Move.SOUTH)
        case Some('W') => Action(nextState, Move.WEST)
        case _ => throw new Exception("No next move found")
      }

      (action, build)
    }.toMap

    /* GET SURROUNDINGS */
    val northStr = AlloyOutputParser.extract(lines(5), SEPARATOR_CHAR)
    val eastStr = AlloyOutputParser.extract(lines(6), SEPARATOR_CHAR)
    val westStr = AlloyOutputParser.extract(lines(7), SEPARATOR_CHAR)
    val southStr = AlloyOutputParser.extract(lines(8), SEPARATOR_CHAR)
    val surroundingStr = AlloyOutputParser.extract(lines(4), SEPARATOR_CHAR)

    val northSurroundings = AlloyOutputParser.splitIntoMap(AlloyOutputParser.getEnv)(northStr, ", ", "->")
    val eastSurroundings = AlloyOutputParser.splitIntoMap(AlloyOutputParser.getEnv)(eastStr, ", ", "->")
    val westSurroundings = AlloyOutputParser.splitIntoMap(AlloyOutputParser.getEnv)(westStr, ", ", "->")
    val southSurroundings = AlloyOutputParser.splitIntoMap(AlloyOutputParser.getEnv)(southStr, ", ", "->")

    val surroundings = surroundingStr.split(", ").map{ surroundingName =>
      buildSurroundingRegularModel(surroundingName, northSurroundings, eastSurroundings, westSurroundings, southSurroundings)
    }.toMap

    /* GET RULES */
    val ruleStr = AlloyOutputParser.extract(lines(0), SEPARATOR_CHAR) // rule name
    val curStateStr = AlloyOutputParser.extract(lines(1), SEPARATOR_CHAR) // current_state
    val linkRuleSurr = AlloyOutputParser.extract(lines(2), SEPARATOR_CHAR) // associated rules and surrounding
    val linkRuleAction = AlloyOutputParser.extract(lines(3), SEPARATOR_CHAR) // associated rules and actions

    val curStates = AlloyOutputParser.splitIntoMapOfInt(curStateStr, ", ", "->")
    val environment = AlloyOutputParser.splitIntoMap(AlloyOutputParser.id)(linkRuleSurr, ", ", "->")
    val next = AlloyOutputParser.splitIntoMap(AlloyOutputParser.id)(linkRuleAction, ", ", "->")

    /* BUILD PICOBOT RULES */
    val rules = ruleStr.split(", ").map { ruleName =>
      val currentState = curStates.get(ruleName) match {
        case Some(stateFound) => stateFound
        case _ => throw new Exception("Error when building rule, no current state found")
      }

      val currentSurrounding = environment.get(ruleName) match {
        case Some(surroundingName) => surroundings.get(surroundingName) match {
          case Some(surrounding) => surrounding
          case _ => throw new Exception("Error when building rule, no surrounding found named "+ surroundingName +" for that rule name "+ ruleName)
        }
        case _ => throw new Exception("Error when building rule, no surrounding found for that rule name "+ ruleName)
      }

      val currentAction = next.get(ruleName) match {
        case Some(actionName) => actions.get(actionName) match {
          case Some(action) => action
          case _ => throw new Exception("Error when building rule, no action found for rule "+ ruleName +" with this name"+ actionName)
        }
        case _ => throw new Exception("Error when building rule, no action found for rule "+ ruleName)
      }

      Rule(currentState, currentSurrounding, currentAction)
    }

    /* DEBUG PART2 /
    println("Number of rules : "+ rules.length)
    for(rule <- rules) {
      println(rule)
    }

    println()
    */

    rules.toList
  }

  /**
   * Parses an alloy output from StarPicobot.als model
   *
   * @see [opl.iag.alloy.Main.getFirstNSolutions]
   *
   * @param itSolution is the alloy solution provided by Alloy
   * @return the corresponding list of rules that itSolution variable provided
   */
  def parseStarAlloyModel(itSolution: A4Solution): List[Rule] = {
    val solution = itSolution.toString.replace("$", "")
    val lines = solution.split("\n").drop(7).reverse.drop(17).reverse // delete useless line

    /* DEBUG PART 1 /
    for(line <- lines) {
      println(line)
    }
    */

    /* GET ACTIONS */
    val actionsStr = AlloyOutputParser.extract(lines(9), SEPARATOR_CHAR) // action name
    val nextStateStr = AlloyOutputParser.extract(lines(10), SEPARATOR_CHAR) // next state
    val moveStr = AlloyOutputParser.extract(lines(11), SEPARATOR_CHAR) // move

    val nextStates = AlloyOutputParser.splitIntoMap(AlloyOutputParser.id)(nextStateStr, ", ", "->")
    val moves = AlloyOutputParser.splitIntoMapOfChar(moveStr, ", ", "->")

    val actions = actionsStr.split(", ").map { action =>
      val nextState = nextStates.get(action) match {
        case Some(move) => move.toInt
        case None => throw new Exception("No action were detected")
      }

      val build = moves.get(action) match {
        case Some('X') => Action(nextState, Move.STAY)
        case Some('N') => Action(nextState, Move.NORTH)
        case Some('E') => Action(nextState, Move.EAST)
        case Some('S') => Action(nextState, Move.SOUTH)
        case Some('W') => Action(nextState, Move.WEST)
        case _ => throw new Exception("No next move found")
      }

      (action, build)
    }.toMap

    /* GET SURROUNDINGS */
    val northStr = AlloyOutputParser.extract(lines(5), SEPARATOR_CHAR)
    val eastStr = AlloyOutputParser.extract(lines(6), SEPARATOR_CHAR)
    val westStr = AlloyOutputParser.extract(lines(7), SEPARATOR_CHAR)
    val southStr = AlloyOutputParser.extract(lines(8), SEPARATOR_CHAR)
    val surroundingStr = AlloyOutputParser.extract(lines(4), SEPARATOR_CHAR)

    val northSurroundings = AlloyOutputParser.splitIntoMap(AlloyOutputParser.getEnv)(northStr, ", ", "->")
    val eastSurroundings = AlloyOutputParser.splitIntoMap(AlloyOutputParser.getEnv)(eastStr, ", ", "->")
    val westSurroundings = AlloyOutputParser.splitIntoMap(AlloyOutputParser.getEnv)(westStr, ", ", "->")
    val southSurroundings = AlloyOutputParser.splitIntoMap(AlloyOutputParser.getEnv)(southStr, ", ", "->")

    val surroundings = surroundingStr.split(", ").map{ surroundingName =>
      buildSurroundingStarModel(surroundingName, northSurroundings, eastSurroundings, westSurroundings, southSurroundings)
    }.toMap

    /* GET RULES */
    val ruleStr = AlloyOutputParser.extract(lines(0), SEPARATOR_CHAR) // rule name
    val curStateStr = AlloyOutputParser.extract(lines(1), SEPARATOR_CHAR) // current_state
    val linkRuleSurr = AlloyOutputParser.extract(lines(2), SEPARATOR_CHAR) // associated rules and surrounding
    val linkRuleAction = AlloyOutputParser.extract(lines(3), SEPARATOR_CHAR) // associated rules and actions

    val curStates = AlloyOutputParser.splitIntoMapOfInt(curStateStr, ", ", "->")
    val environment = AlloyOutputParser.splitIntoMap(AlloyOutputParser.id)(linkRuleSurr, ", ", "->")
    val next = AlloyOutputParser.splitIntoMap(AlloyOutputParser.id)(linkRuleAction, ", ", "->")

    /* BUILD PICOBOT RULES */
    val rules = ruleStr.split(", ").map { ruleName =>
      val currentState = curStates.get(ruleName) match {
        case Some(stateFound) => stateFound
        case _ => throw new Exception("Error when building rule, no current state found")
      }

      val currentSurrounding = environment.get(ruleName) match {
        case Some(surroundingName) => surroundings.get(surroundingName) match {
          case Some(surrounding) => surrounding
          case _ => throw new Exception("Error when building rule, no surrounding found named "+ surroundingName +" for that rule name "+ ruleName)
        }
        case _ => throw new Exception("Error when building rule, no surrounding found for that rule name "+ ruleName)
      }

      val currentAction = next.get(ruleName) match {
        case Some(actionName) => actions.get(actionName) match {
          case Some(action) => action
          case _ => throw new Exception("Error when building rule, no action found for rule "+ ruleName +" with this name"+ actionName)
        }
        case _ => throw new Exception("Error when building rule, no action found for rule "+ ruleName)
      }

      Rule(currentState, currentSurrounding, currentAction)
    }

    /* DEBUG PART2 /
    println("Number of rules : "+ rules.length)
    for(rule <- rules) {
      println(rule)
    }

    println()
    */

    rules.toList
  }

  /**
   * Saves rules generated by Alloy in the temp system folder
   *
   * @param filename is the rules filename
   * @param rules is the list of rules which will be saved
   * @return the rule file with rules formatted for Picobot simulator
   */
  def saveRules(filename : String, rules : List[Rule]) : File = {
    /* SAVE GENERATED PICOBOT'S RULES  */
    val picobotRuleFile = new File(System.getProperty("java.io.tmpdir") + File.separator + filename)

    if (picobotRuleFile.exists) {
      picobotRuleFile.delete
    }

    val writer = new PrintWriter(picobotRuleFile)

    /* PRINT EACH RULE */
    for (rule <- rules) {
      writer.write(rule.toString + "\n\n")
    }

    writer.close()
    picobotRuleFile
  }

  /**
   * Is a helper function that builds an Surroundings instance in case where StarPicobot.als model
   * was used
   *
   * @see [opl.iagl.alloy.Surroundings]
   *
   * @param surroundingName is the surrroundings name from the alloy solution
   * @param north is the north direction value
   * @param east is the east direction value
   * @param west is the west direction value
   * @param south is the south direction value
   * @return
   */
  private def buildSurroundingStarModel(surroundingName: String, north: immutable.Map[String, String], east: immutable.Map[String, String], west: immutable.Map[String, String], south: immutable.Map[String, String]) : (String, Surroundings) = {
    val northDirection = north.get(surroundingName) match {
      case Some("True") => EnvInfo.NORTH
      case Some("False") => EnvInfo.NOTHING
      case Some("Star") => EnvInfo.ANY
      case _ => throw new Exception("Error when building surrounding north direction")
    }

    val eastDirection = east.get(surroundingName) match {
      case Some("True") => EnvInfo.EAST
      case Some("False") => EnvInfo.NOTHING
      case Some("Star") => EnvInfo.ANY
      case _ => throw new Exception("Error when building surrounding east direction")
    }

    val westDirection = west.get(surroundingName) match {
      case Some("True") => EnvInfo.WEST
      case Some("False") => EnvInfo.NOTHING
      case Some("Star") => EnvInfo.ANY
      case _ => throw new Exception("Error when building surrounding west direction")
    }

    val southDirection = south.get(surroundingName) match {
      case Some("True") => EnvInfo.SOUTH
      case Some("False") => EnvInfo.NOTHING
      case Some("Star") => EnvInfo.ANY
      case _ => throw new Exception("Error when building surrounding south direction")
    }

    (surroundingName, Surroundings(northDirection, eastDirection, westDirection, southDirection))
  }

  /**
   * Is a helper function that builds an Surroundings instance in case where Picobot.als model
   * was used
   *
   * @see [opl.iagl.alloy.Surroundings]
   *
   * @param surroundingName is the surrroundings name from the alloy solution
   * @param north is the north direction value
   * @param east is the east direction value
   * @param west is the west direction value
   * @param south is the south direction value
   * @return
   */
  private def buildSurroundingRegularModel(surroundingName: String, north: immutable.Map[String, String], east: immutable.Map[String, String], west: immutable.Map[String, String], south: immutable.Map[String, String]) : (String, Surroundings) = {
    val northDirection = north.get(surroundingName) match {
      case Some("True") => EnvInfo.NORTH
      case Some("False") => EnvInfo.ANY
      case _ => throw new Exception("Error when building surrounding north direction")
    }

    val eastDirection = east.get(surroundingName) match {
      case Some("True") => EnvInfo.EAST
      case Some("False") => EnvInfo.ANY
      case _ => throw new Exception("Error when building surrounding east direction")
    }

    val westDirection = west.get(surroundingName) match {
      case Some("True") => EnvInfo.WEST
      case Some("False") => EnvInfo.ANY
      case _ => throw new Exception("Error when building surrounding west direction")
    }

    val southDirection = south.get(surroundingName) match {
      case Some("True") => EnvInfo.SOUTH
      case Some("False") => EnvInfo.ANY
      case _ => throw new Exception("Error when building surrounding south direction")
    }

    (surroundingName, Surroundings(northDirection, eastDirection, westDirection, southDirection))
  }
}
