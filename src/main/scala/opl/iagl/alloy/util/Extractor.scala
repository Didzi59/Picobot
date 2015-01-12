package opl.iagl.alloy.util

import opl.iagl._
import opl.iagl.{Surroundings, Action, Rule}

import scala.io.Source

/**
 * Describes a position in 2D plane
 * @param x is the x-coordinate position
 * @param y is the y-coordinate position
 */
case class Position(x : Int, y : Int)

/**
 * Provides several function to extract information from simple formatted text file
 *
 * @author Romain Philippon
 */
object Extractor {
  /**
   * Is the character that represents a wall in a Picobot map
   */
  private val WALL_CELL = '#'

  /**
   * Gets positions of non-wall cells from a Picobot map
   * @param mapFilePath is the absolute path of a Picobot map
   * @return a list of all non-wall cell positions
   */
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

  /**
   * Gets Picobot rules from a file
   * @param rulesFilePath is the absolute path of a file containing Picobot rules
   * @return the list of rules containing in the file
   */
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
