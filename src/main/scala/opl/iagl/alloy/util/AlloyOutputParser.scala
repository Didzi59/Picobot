package opl.iagl.alloy.util

/**
 * Provides a set of helper functions in order to parse an alloy output
 *
 * @see [opl.iagl.alloy.parseRegularAlloyModel]
 *      [opl.iagl.alloy.parseStarAlloyModel]
 *
 * @author Romain Philippon
 */
object AlloyOutputParser {
  /**
   * Extracts specific values from an alloy output
   * @param line is the line where the information will be extracted
   * @param startCharacter is the character that separates an alloy variable from its value
   * @return only extracted values
   */
  def extract(line : String, startCharacter : Char) : String = {
    val tempString = line.substring(line.indexOf(startCharacter) + 1) // substring index between [1 ; n]
    tempString.substring(1, tempString.length - 1)
  }

  /**
   * Splits extracted informations to get alloy sub-variables and their value
   * @param appliedFunction is the function applied on alloy sub-variable values to format it in a better way
   * @param string is information which need to be splitted
   * @param separatorLine is the character that separates each alloy sub-variable information
   * @param separatorAssignation is the character that separates an alloy sub-variable from its value
   * @return the corresponding Map instance where the key is the alloy sub-variable name and the value is the corresponding value of the alloy sub-variable
   */
  def splitIntoMap(appliedFunction : String => String)(string : String, separatorLine : String, separatorAssignation : String)  : Map[String, String] = {
    string.split(separatorLine).map{ splitPart =>
      val assignation = splitPart.split(separatorAssignation)
      (assignation(0), appliedFunction(assignation(1)))
    }.toMap
  }

  /**
   * Splits extracted informations to get alloy sub-variables and their value in Integer format
   * @param string is information which need to be splitted
   * @param separatorLine is the character that separates each alloy sub-variable information
   * @param separatorAssignation is the character that separates an alloy sub-variable from its value
   * @return the corresponding Map instance where the key is the alloy sub-variable name and the value is the corresponding value of the alloy sub-variable
   */
  def splitIntoMapOfInt(string : String, separatorLine : String, separatorAssignation : String)  : Map[String, Int] = {
    string.split(separatorLine).map{ splitPart =>
      val assignation = splitPart.split(separatorAssignation)
      (assignation(0), intoInt(assignation(1)))
    }.toMap
  }

  /**
   * Splits extracted informations to get alloy sub-variables and their value in Character format
   * @param string is information which need to be splitted
   * @param separatorLine is the character that separates each alloy sub-variable information
   * @param separatorAssignation is the character that separates an alloy sub-variable from its value
   * @return the corresponding Map instance where the key is the alloy sub-variable name and the value is the corresponding value of the alloy sub-variable
   */
  def splitIntoMapOfChar(string : String, separatorLine : String, separatorAssignation : String)  : Map[String, Char] = {
    string.split(separatorLine).map{ splitPart =>
      val assignation = splitPart.split(separatorAssignation)
      (assignation(0), getDirectionChar(assignation(1)))
    }.toMap
  }

  /**
   * Extracts a string to get it without its first and last character
   *
   * @see [opl.iagl.alloy.AlloyOutputParser.splitIntoMap]
   *
   * @param string is the string which the first and last character will be removed
   * @return the same string passed in parameter with its first and last character removed
   */
  def substr(string : String) : String = {
    string.substring(0, 2) // only keep d[Letter]
  }

  /**
   * Extracts a string representation of a MOVE value
   *
   * @see [opl.iagl.alloy.AlloyOutputParser.splitIntoMap]
   *      [opl.iagl.Move]
   *
   * @param string is the string where the function will extract the MOVE information
   * @return a string with only character which is a string representation of a MOVE value
   */
  def getEnv(string : String) : String = {
    val length = string.length
    string.substring(0, length - 1)
  }

  /**
   * Extracts a string representation of an EnvInfo value
   *
   * @see [opl.iagl.alloy.AlloyOutputParser.splitIntoMap]
   *      [opl.iagl.EnInfo]
   *
   * @param string is the string where the function will extract the EnvInfo information
   * @return a string with only character which is a string representation of a ENVINFO value
   */
  def getDirectionChar(string : String) : Char = {
    string(0)
  }

  def id(string : String) : String = { string }

  def intoInt(string : String) : Int = { string.toInt }
}
