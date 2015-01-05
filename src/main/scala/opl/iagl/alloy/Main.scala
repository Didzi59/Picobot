package opl.iagl.alloy

import java.io.{PrintWriter, File}

import edu.mit.csail.sdg.alloy4compiler.ast.Command
import edu.mit.csail.sdg.alloy4compiler.parser.{CompUtil, CompModule}
import edu.mit.csail.sdg.alloy4compiler.translator.{TranslateAlloyToKodkod, A4Options, A4Solution}
import opl.iagl._
import picobot.implementation.CommandLineSimulator

import scala.concurrent.Future

/**
 * Is the application's main entry point
 * @author Romain Philippon
 */
object Main extends App {
  val SEPARATOR_CHAR = '='
  lazy val picobotModelFilePath : String = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "Picobot.als"
  lazy val model : CompModule = CompUtil.parseEverything_fromFile(null, null, picobotModelFilePath)

  val cmd : Command = model.getAllCommands.get(0)
  var itSolutions : A4Solution = TranslateAlloyToKodkod.execute_command(
    null,
    model.getAllReachableSigs,
    cmd,
    new A4Options()
  )

  //val pathInstanceAlloy = System.getProperty("java.io.tmpdir") + File.separator + "temp_alloy_instance.xml"

  while(itSolutions.satisfiable()) {
    val solution = itSolutions.toString.replace("$", "")
    val lines = solution.split("\n").drop(7).reverse.drop(13).reverse // delete useless line

    /* GET ACTIONS */
    val actionsStr = extract(lines(9), SEPARATOR_CHAR) // action name
    val nextStateStr = extract(lines(10), SEPARATOR_CHAR) // next state
    val moveStr = extract(lines(11), SEPARATOR_CHAR) // move

    val nextStates = splitIntoMap(id)(nextStateStr, ", ", "->")
    val moves = splitIntoMapOfChar(moveStr, ", ", "->")

    val actions = actionsStr.split(", ").map { action =>
      val nextState = nextStates.get(action) match {
        case Some(move) => move.toInt
        case None => throw new Exception("exception")
      }

      val build = moves.get(action) match {
        case Some('X') => Action(nextState, Move.STAY)
        case Some('N') => Action(nextState, Move.NORTH)
        case Some('E') => Action(nextState, Move.EAST)
        case Some('S') => Action(nextState, Move.SOUTH)
        case Some('W') => Action(nextState, Move.WEST)
        case _ => throw new Exception("exception")
      }

      (action, build)
    }.toMap

    /* GET SURROUNDINGS */
    val northStr = extract(lines(5), SEPARATOR_CHAR)
    val eastStr = extract(lines(6), SEPARATOR_CHAR)
    val westStr = extract(lines(7), SEPARATOR_CHAR)
    val southStr = extract(lines(8), SEPARATOR_CHAR)
    val surroundingStr = extract(lines(4), SEPARATOR_CHAR)

    val northSurroundings = splitIntoMap(substr)(northStr, ", ", "->")
    val eastSurroundings = splitIntoMap(substr)(eastStr, ", ", "->")
    val westSurroundings = splitIntoMap(substr)(westStr, ", ", "->")
    val southSurroundings = splitIntoMap(substr)(southStr, ", ", "->")

    val surroundings = surroundingStr.split(", ").map{ surroundingName =>
      buildSurrounding(surroundingName, northSurroundings, eastSurroundings, westSurroundings, southSurroundings)
    }.toMap

    /* GET RULES */
    val ruleStr = extract(lines(0), SEPARATOR_CHAR) // rule name
    val curStateStr = extract(lines(1), SEPARATOR_CHAR) // current_state
    val linkRuleSurr = extract(lines(2), SEPARATOR_CHAR) // associated rules and surrounding
    val linkRuleAction = extract(lines(3), SEPARATOR_CHAR) // associated rules and actions

    val curStates = splitIntoMapOfInt(curStateStr, ", ", "->")
    val environment = splitIntoMap(id)(linkRuleSurr, ", ", "->")
    val next = splitIntoMap(id)(linkRuleAction, ", ", "->")

    /* BUILD PICOBOT RULES */
    val rules = ruleStr.split(", ").map { ruleName =>
      val currentState = curStates.get(ruleName) match {
        case Some(stateFound) => stateFound
        case _ => throw new Exception("exception")
      }

      val currentSurrounding = environment.get(ruleName) match {
        case Some(surroundingName) => surroundings.get(surroundingName) match {
          case Some(surrounding) => surrounding
          case _ => throw new Exception("exception")
        }
        case _ => throw new Exception("exception")
      }

      val currentAction = next.get(ruleName) match {
        case Some(actionName) => actions.get(actionName) match {
          case Some(action) => action
          case _ => throw new Exception("exception")
        }
        case _ => throw new Exception("exception")
      }

      Rule(currentState, currentSurrounding, currentAction)
    }

    /* DEBUG */
    for(line <- lines) {
      println(line)
    }

    for(rule <- rules) {
      println(rule)
    }

    Future {
      val picobotRuleFile = new File(System.getProperty("java.io.tmpdir") + File.separator + "picobot-rules.txt")

      if (picobotRuleFile.exists) {
        picobotRuleFile.delete
      }
      val writer = new PrintWriter(picobotRuleFile)

      for (rule <- rules) {
        writer.write(rule.toString + "\n\n")
      }
      writer.close()

      println()

      // map 1
      //lazy val simulator = CommandLineSimulator.main(Array())
    }

    /* GET NEXT RULE */
    itSolutions = itSolutions.next()
    Thread.sleep(1000)
  }

  def extract(line : String, startCharacter : Char) : String = {
    val tempString = line.substring(line.indexOf(startCharacter) + 1) // substring index between [1 ; n]
    tempString.substring(1, tempString.length - 1)
  }

  def splitIntoMap(appliedFunction : String => String)(string : String, separatorLine : String, separatorAssignation : String)  : Map[String, String] = {
    string.split(separatorLine).map{ splitPart =>
      val assignation = splitPart.split(separatorAssignation)
      (assignation(0), appliedFunction(assignation(1)))
    }.toMap
  }

  def splitIntoMapOfInt(string : String, separatorLine : String, separatorAssignation : String)  : Map[String, Int] = {
    string.split(separatorLine).map{ splitPart =>
      val assignation = splitPart.split(separatorAssignation)
      (assignation(0), intoInt(assignation(1)))
    }.toMap
  }

  def splitIntoMapOfChar(string : String, separatorLine : String, separatorAssignation : String)  : Map[String, Char] = {
    string.split(separatorLine).map{ splitPart =>
      val assignation = splitPart.split(separatorAssignation)
      (assignation(0), getDirectionChar(assignation(1)))
    }.toMap
  }

  def substr(string : String ) : String = {
    string.substring(0, 2) // only keep d[Letter]
  }

  def getDirectionChar(string : String) : Char = {
    string(0)
  }

  def id(string : String) : String = { string }

  def intoInt(string : String) : Int = { string.toInt }

  def buildSurrounding(surroundingName: String, north: Map[String, String], east: Map[String, Any], west: Map[String, String], south: Map[String, String]) : Tuple2[String, Surroundings] = {
    val northDirection = north.get(surroundingName) match {
      case Some("dN") => EnvInfo.NORTH
      case Some("dX") => EnvInfo.ANY
      case _ => throw new Exception("exception")
    }

    val eastDirection = east.get(surroundingName) match {
      case Some("dE") => EnvInfo.EAST
      case Some("dX") => EnvInfo.ANY
      case _ => throw new Exception("exception")
    }

    val westDirection = west.get(surroundingName) match {
      case Some("dW") => EnvInfo.WEST
      case Some("dX") => EnvInfo.ANY
      case _ => throw new Exception("exception")
    }

    val southDirection = south.get(surroundingName) match {
      case Some("dS") => EnvInfo.SOUTH
      case Some("dX") => EnvInfo.ANY
      case _ => throw new Exception("exception")
    }

    (surroundingName, Surroundings(northDirection, eastDirection, westDirection, southDirection))
  }
}