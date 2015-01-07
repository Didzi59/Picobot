package opl.iagl

import opl.iagl.EnvInfo.ENVINFO
import opl.iagl.Move.MOVE

/**
 * Created by jimipepper on 12/23/14.
 */

object Move extends Enumeration {
  type MOVE = Value
  val NORTH = Value("N")
  val EAST = Value("E")
  val WEST = Value("W")
  val SOUTH = Value("S")
  val STAY = Value("x")
}

object EnvInfo extends Enumeration {
  type ENVINFO = Value
  val NORTH = Value("N")
  val EAST = Value("E")
  val WEST = Value("W")
  val SOUTH = Value("S")
  val ANY = Value("*")
}

case class Surroundings(north: ENVINFO, east: ENVINFO, west: ENVINFO, south: ENVINFO) {
  override def toString() : String = {
    north.toString + east.toString + west.toString + south.toString
  }
}
case class Action(nextState : Int, move : MOVE) {
  override def toString() : String = {
    val direction = move match {
      case Move.NORTH => "N"
      case Move.EAST => "E"
      case Move.WEST => "W"
      case Move.SOUTH => "S"
      case Move.STAY => "X"
    }

    nextState +" "+ direction
  }
}
case class Rule(currentState : Int, environment: Surroundings, next: Action) {
  override def toString() : String = {
    currentState +" "+ environment.toString +" -> "+ next.toString
  }
}

package object alloy {

}
