package opl.iagl.alloy.util

import opl.iagl._
import opl.iagl.{Surroundings, Action, Rule}

import scala.io.Source

case class Position(x : Int, y : Int)

/**
 * Extracts free positions from a Picobot map
 *
 * @author Romain Philippon
 */
object Extractor {
  val WALL_CELL = '#'

  def positions(mapFilePath : String) : List[Position] = {
    var y = 0
    var list : List[Position] = Nil

    Source.fromFile(mapFilePath).getLines.foreach{ line =>
      for((char, i) <- line.toCharArray.zipWithIndex) {
        if(char != WALL_CELL) list = Position(i, y) :: list
      }

      y += 1
    }

    list
  }

  def rules(rulesFilePath : String) : List[Rule] = {
    def buildRule(array : Array[String]) : Rule = {
      Rule (
        array(0).toInt,
        Surroundings(
          EnvInfo.get(array(1)(0).toString),
          EnvInfo.get(array(1)(1).toString),
          EnvInfo.get(array(1)(2).toString),
          EnvInfo.get(array(1)(3).toString)),
        Action(
          array(2).toInt,
          Move.get(array(3)))
      )
    }

    val ruleRegex = "^(\\d)\\s+([Nx*][Ex*][Wx*][Sx*])\\s+->\\s+([NEWSX])\\s+(\\d)$".r
    // no checking on white space at the beginning and the end -> trim function used
    var list : List[Rule] = Nil

    Source.fromFile(rulesFilePath).getLines.foreach{ line =>
      if(!line.trim.isEmpty) {
        list = list.:+(buildRule(ruleRegex.findAllIn(line).toArray))
      }
    }

    list
  }
}
