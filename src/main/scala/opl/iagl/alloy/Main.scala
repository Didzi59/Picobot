package opl.iagl.alloy

// java
import java.io.File

import opl.iagl.Rule
import opl.iagl.alloy.picobot.{SimulationPicobotException, OracleSimulator}
import opl.iagl.alloy.util.{Export, Extractor, Position}

import scala.concurrent.duration.Duration

// alloy
import edu.mit.csail.sdg.alloy4compiler.ast.Command
import edu.mit.csail.sdg.alloy4compiler.parser.{CompUtil, CompModule}
import edu.mit.csail.sdg.alloy4compiler.translator.{TranslateAlloyToKodkod, A4Options, A4Solution}

// picobot simulator

// scala
import scala.concurrent.{Promise, Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global // for concurrency with future - do not remove

case class Model(name : String, data : CompModule)
case class Map(path : String, positions : List[Position])

case class Result(rules : List[Rule], position: Position, nbCellCrossed : Int)

/**
 * Is the application's main entry point
 * @author Romain Philippon
 */
object Main extends App {
  if (args.length == 1 || !args(1).forall(_.isDigit)) {
    Console.err.println(Console.RED + "Expected an integer value that represents the number of expected results for each tested alloy model" + Console.RESET)
    System.exit(1)
  }

  /* DEFAULT VARIABLES */
  val resourceFolderPath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator
  val numberExpectedResult = args(1).toInt

  /* LOAD ALLOY MODEL */
  lazy val picobotModelFilesPath: List[String] = List(
    resourceFolderPath + "alloy" + File.separator + "StarPicobot.als",
    resourceFolderPath + "alloy" + File.separator + "Picobot.als"
  )

  lazy val models = picobotModelFilesPath map { modelFile =>
    Model(
      modelFile.split('/').last.dropRight(4), // get alloy model filename without extension file
      CompUtil.parseEverything_fromFile(null, null, modelFile)
    )
  }

  /* VARIABLE FOR PICOBOT SIMULATION */
  val maps: List[Map] = List(
    Map(
      resourceFolderPath + "maps" + File.separator + "map1-mission1-empty.map",
      Extractor.positions(resourceFolderPath + "maps" + File.separator + "map1-mission1-empty.map")
    ),
    Map(
      resourceFolderPath + "maps" + File.separator + "map2-mission2-maze.map",
      Extractor.positions(resourceFolderPath + "maps" + File.separator + "map2-mission2-maze.map")
    )
  )

  /* FOR EACH PICOBOT RULE MODEL */
  val futures = models.map { model =>
    /* LOAD ALLOY */
    val cmd: Command = model.data.getAllCommands.get(0)
    val solutions: A4Solution = TranslateAlloyToKodkod.execute_command(
      null,
      model.data.getAllReachableSigs,
      cmd,
      new A4Options
    )

    launchSimulation(model, solutions, numberExpectedResult)
  }.flatten

  Await.ready(Future.sequence(futures), Duration.Inf)

  private def rulesFilename(filename: String): String = {
    val RULES_FILENAME = "-picobot-rules.txt"

    filename + RULES_FILENAME
  }

  private def getFirstNSolutions(solutions : A4Solution, n : Int, modelName: String) : List[List[Rule]] = {
    var i = 0
    var list : List[List[Rule]] = Nil
    var solution = solutions

    while (solution.satisfiable && i < n) {
      i += 1

      val rules = modelName match {
        case "StarPicobot" => parseStarAlloyModel(solution)
        case "Picobot" => parseRegularAlloyModel(solution)
        case _ => throw new Exception("Unknown model name : " + modelName)
      }

      list = list :+ rules
      solution = solution.next
    }

    list
  }

  private def launchSimulation(model: Model, solutions: A4Solution, nbSolutionToExplore: Int): List[Future[(String, Int, List[Result])]] = {
    val rules = getFirstNSolutions(solutions, nbSolutionToExplore, model.name)

    maps.map { map =>
      val f = Future[(String, Int, List[Result])] {
        var results: List[Result] = Nil
        val filename = model.name + "-" + map.path.substring(map.path.lastIndexOf('/') + 1).dropRight(4)

        rules.foreach { rules =>
          for (position <- map.positions) {
            lazy val simulator = new OracleSimulator(
              map.path,
              saveRules(rulesFilename(filename), rules).getAbsolutePath,
              position,
              1000
            )

            try {
              simulator.run
              results = results.:+(Result(rules, position, map.positions.size))
            }
            catch {
              case spe: SimulationPicobotException => results = results.:+(Result(rules, position, spe.getNbCrossedCells))
            }
          }
        }

        (filename, map.positions.size, results)
      }

      f onSuccess{
        case (filename: String, nbCells: Int, results: List[Result]) =>
          println(Console.GREEN + Export.saveCoveringRate(filename, nbCells, results)+ " saved"+ Console.RESET)
          println(Console.GREEN + Export.saveAverageCrossedCell(filename, nbCells, results)+ " saved"+ Console.RESET)
          println(Console.GREEN + Export.saveNbPassedAndFailedPosition(filename, nbCells, results)+ " saved"+ Console.RESET)
      }

      f
    }
  }
}