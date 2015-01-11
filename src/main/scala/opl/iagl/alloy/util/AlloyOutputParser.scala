package opl.iagl.alloy.util

/**
 * @author Romain Philippon
 */
object AlloyOutputParser {
  /* FUNCTIONS TO PARSE ALLOY OUTPUT */
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

  def substr(string : String) : String = {
    string.substring(0, 2) // only keep d[Letter]
  }

  def getEnv(string : String) : String = {
    val length = string.length
    string.substring(0, length - 1)
  }

  def getDirectionChar(string : String) : Char = {
    string(0)
  }

  def id(string : String) : String = { string }

  def intoInt(string : String) : Int = { string.toInt }
}
