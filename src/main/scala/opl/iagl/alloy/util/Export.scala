package opl.iagl.alloy.util

import java.io.{PrintWriter, File}
import java.nio.file.{Path, Files, Paths}

import opl.iagl.Rule
import opl.iagl.alloy.Result

/**
 * Gathers a set of helper function that save simulation's results in CVS format
 *
 * @author Romain Philippon
 */
object Export {
  /**
   * Is the absolute path of the folder result where result files will be save. The path corresponds to the system home folder with a new directory named picobot-alloy-res
   */
  private val FOLDER_RESULT_PATH = System.getProperty("user.home") + File.separator + "picobot-alloy-res"
  /**
   * Is the result file's extension
    */
  private val EXTENSION_FILE = ".csv"

  /**
   * Gives the path of folder where result files will be saved
   *
   * @note If the folder does not exist, this function creates it.
   *
   * @return the path of the folder where result files will be saved
   */
  private def getFolderResult : Path = {
    var folderResFilePath = Paths.get(FOLDER_RESULT_PATH)

    if(Files.notExists(folderResFilePath)) {
      folderResFilePath = Files.createDirectory(Paths.get(FOLDER_RESULT_PATH))
    }

    folderResFilePath
  }

  /**
   * Saves results for the covering rate for each set of rules
   * @param filename is the result filename without its extension
   * @param nbCells is the number of non-wall cells in the map where simulation took place
   * @param results is the list of result provided after the simulation
   * @return the filename with the extension of the result file
   */
  def saveCoveringRate(filename : String, nbCells : Int, results : List[Result]) : String = {
    /* INITIALIZE FOLDER AND RESULT FILE */
    val folderResFile = getFolderResult
    val resFilePath = Paths.get(folderResFile.toString + File.separator + filename + "__covering-rate" + EXTENSION_FILE)

    Files.deleteIfExists(resFilePath)
    Files.createFile(resFilePath)

    val writer = new PrintWriter(resFilePath.toFile)

    writer.write("Rules, Covering Rate\n") // head table

    /* WRITE RESULTS */
    results.groupBy(_.rules).map { case (rules : List[Rule], results : List[Result]) =>
      (
        rules,
        results.map(res => res.nbCellCrossed.toDouble / nbCells.toDouble).foldLeft(0.0)(_ + _) / results.size.toDouble
      )
    }.foreach { case (rules: List[Rule], coveringRate: Double) =>
      writer.write(rules.map(rule => rule +" ; ").mkString.dropRight(2))
      writer.write(", "+ coveringRate +"\n")
    }

    writer.close()

    resFilePath.toFile.getName
  }

  /**
   * Saves results for the average crossed cells for each set of rules
   * @param filename is the result filename without its extension
   * @param nbCells is the number of non-wall cells in the map where simulation took place
   * @param results is the list of result provided after the simulation
   * @return the filename with the extension of the result file
   */
  def saveAverageCrossedCell(filename : String, nbCells : Int, results : List[Result]) : String = {
    /* INITIALIZE FOLDER AND RESULT FILE */
    val folderResFile = getFolderResult
    val resFilePath = Paths.get(folderResFile.toString + File.separator + filename + "__average-crossed-cells" + EXTENSION_FILE)

    Files.deleteIfExists(resFilePath)
    Files.createFile(resFilePath)

    val writer = new PrintWriter(resFilePath.toFile)
    writer.write("Rules, Total cells in map, Average crossed cell\n") // head table

    /* WRITE RESULTS */
    results.groupBy(_.rules).map { case (rules : List[Rule], results : List[Result]) =>
      (
        rules,
        (results.map(res => res.nbCellCrossed.toDouble).foldLeft(0.0)(_ + _) / results.size.toDouble).toInt
        )
    }.foreach { case (rules: List[Rule], averageCells: Int) =>
      writer.write(rules.map(rule => rule +" ; ").mkString.dropRight(2))
      writer.write(", "+ nbCells +", "+ averageCells +"\n")
    }

    writer.close()

    resFilePath.toFile.getName
  }

  /**
   * Saves results for passed and failed positions for each set of rules
   * @param filename is the result filename without its extension
   * @param nbCells is the number of non-wall cells in the map where simulation took place
   * @param results is the list of result provided after the simulation
   * @return the filename with the extension of the result file
   */
  def saveNbPassedAndFailedPosition(filename : String, nbCells : Int, results : List[Result]) : String = {
    /* INITIALIZE FOLDER AND RESULT FILE */
    val folderResFile = getFolderResult
    val resFilePath = Paths.get(folderResFile.toString + File.separator + filename + "__nb-passed-failed-positions" + EXTENSION_FILE)

    Files.deleteIfExists(resFilePath)
    Files.createFile(resFilePath)

    val writer = new PrintWriter(resFilePath.toFile)
    writer.write("Rules, Total cells in map, Number of position where Picobot crossed all cells, Number of positions where Picobot failed\n") // head table

    /* WRITE RESULTS */
    results.groupBy(_.rules).map { case (rules : List[Rule], results : List[Result]) =>
      val passedRules = results.filter(result => result.nbCellCrossed == nbCells)
      val failedRules = results diff passedRules

      (
        rules,
        nbCells,
        (passedRules.size, failedRules.size)
        )
    }.foreach { case (rules: List[Rule], nbCells : Int, (nbPassedPosition : Int, nbFailedPosition : Int)) =>
      writer.write(rules.map(rule => rule +" ; ").mkString.dropRight(2))
      writer.write(", "+ nbCells + ", "+ nbPassedPosition +", "+ nbFailedPosition +"\n")
    }

    writer.close()

    resFilePath.toFile.getName
  }
}