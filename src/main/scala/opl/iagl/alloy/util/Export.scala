package opl.iagl.alloy.util

import java.io.{PrintWriter, File}
import java.nio.file.{Path, Files, Paths}

import opl.iagl.Rule
import opl.iagl.alloy.Result

/**
 * @author Romain Philippon
 */
object Export {
  private val FOLDER_RESULT_PATH = System.getProperty("user.home") + File.separator + "picobot-alloy-res"
  private val EXTENSION_FILE = ".csv"

  private def getFolderResult : Path = {
    var folderResFilePath = Paths.get(FOLDER_RESULT_PATH)

    if(Files.notExists(folderResFilePath)) {
      folderResFilePath = Files.createDirectory(Paths.get(FOLDER_RESULT_PATH))
    }

    folderResFilePath
  }

  def saveCoveringRate(filename : String, nbCells : Int, results : List[Result]) : String = {
    val folderResFile = getFolderResult
    val resFilePath = Paths.get(folderResFile.toString + File.separator + filename + "__covering-rate" + EXTENSION_FILE)

    Files.deleteIfExists(resFilePath)
    Files.createFile(resFilePath)

    val writer = new PrintWriter(resFilePath.toFile)

    writer.write("Rules, Covering Rate\n")

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

  def saveAverageCrossedCell(filename : String, nbCells : Int, results : List[Result]) : String = {
    val folderResFile = getFolderResult
    val resFilePath = Paths.get(folderResFile.toString + File.separator + filename + "__average-crossed-cells" + EXTENSION_FILE)

    Files.deleteIfExists(resFilePath)
    Files.createFile(resFilePath)

    val writer = new PrintWriter(resFilePath.toFile)
    writer.write("Rules, Total cells in map, Average crossed cell\n")

    results.groupBy(_.rules).map { case (rules : List[Rule], results : List[Result]) =>
      (
        rules,
        (results.map(res => res.nbCellCrossed.toDouble).foldLeft(0.0)(_ + _) / results.size.toDouble).toInt
        )
    }.foreach { case (rules: List[Rule], averageCells: Int) =>
      writer.write(rules.map(rule => rule +" ; ").mkString.dropRight(2))
      writer.write(", "+ averageCells +"\n")
    }

    writer.close()

    resFilePath.toFile.getName
  }

  def saveNbPassedAndFailedPosition(filename : String, nbCells : Int, results : List[Result]) : String = {
    val folderResFile = getFolderResult
    val resFilePath = Paths.get(folderResFile.toString + File.separator + filename + "__nb-passed-failed-positions" + EXTENSION_FILE)

    Files.deleteIfExists(resFilePath)
    Files.createFile(resFilePath)

    val writer = new PrintWriter(resFilePath.toFile)
    writer.write("Rules, Total cells in map, Number of position where Picobot crossed all cells, Number of positions where Picobot failed\n")

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