package opl.iagl.alloy

// java
import java.io.File

// intern project dependencies
import opl.iagl.Rule
import opl.iagl.alloy.picobot.{SimulationPicobotException, OracleSimulator}
import opl.iagl.alloy.util.{Export, Extractor, Position}

// alloy
import edu.mit.csail.sdg.alloy4compiler.ast.Command
import edu.mit.csail.sdg.alloy4compiler.parser.{CompUtil, CompModule}
import edu.mit.csail.sdg.alloy4compiler.translator.{TranslateAlloyToKodkod, A4Options, A4Solution}

// scala
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global // for concurrency with future - do not remove

/**
 * Refers to an Alloy model
 *
 * @param name is the Alloy model filename without its extension
 * @param data is the loaded model by Alloy class CompUtil
 */
case class Model(name : String, data : CompModule)

/**
 * Refers to a map file
 *
 * @param path is the map file's absolute path
 * @param positions is list of all non-wall cells
 */
case class Map(path : String, positions : List[Position])

/**
 * Is a data containing the result of a Picobot simulation
 *
 * @param rules is the list of rules tested in a simulation
 * @param position is starting cell position used in a simulation
 * @param nbCellCrossed is number of crossed cells that Picobot did in a simulation
 */
case class Result(rules : List[Rule], position: Position, nbCellCrossed : Int)

/**
 * Is the application's main entry point
 *
 * @note It requires an integer parameter to specify how much sets of rules it needs to generate with Alloy
 *       To launch it, use this following sbt command :
 *       {{{
 *         sbt "run [required-parameter]"
 *       }}}
 *
 * @author Romain Philippon
 */
object Main extends App {
  /* CHECK PARAMETER */
  if (args.length != 1 || !args(0).forall(_.isDigit)) {
    Console.err.println(Console.RED + "Expected an integer value that represents the number of expected results for each tested alloy model" + Console.RESET)
    System.exit(1)
  }

  /* DEFAULT VARIABLES */
  val resourceFolderPath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator
  val numberExpectedResult = args(0).toInt

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

  /* LOAD PICOBOT MAPS */
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

  /* FOR EACH PICOBOT RULE MODEL : CONSTRUCT A SIMULATION */
  val futures = models.map { model =>
    /* LOAD ALLOY */
    val cmd: Command = model.data.getAllCommands.get(0)
    val solutions: A4Solution = TranslateAlloyToKodkod.execute_command(
      null,
      model.data.getAllReachableSigs,
      cmd,
      new A4Options
    )

    createSimulations(model, solutions, numberExpectedResult)
  }.flatten

  /* LAUNCH SIMULATION*/
  Await.ready(Future.sequence(futures), Duration.Inf)

  /**
   * Is a helper function that builds a filename for the temporary file containing alloy Picobot rules
   *
   * @param filename Is a part of the filename
   * @return a complete filename which concatenates filename variable with -picobot-rules
   */
  private def rulesFilename(filename: String): String = {
    val RULES_FILENAME = "-picobot-rules.txt"

    filename + RULES_FILENAME
  }

  /**
   * Is a helper function that runs an alloy instance to get the n first generated solutions.
   * It throws an exception if the model name is unknown by this function
   *
   * @param solutions Is the alloy instance where extracts alloy solutions
   * @param n Specifies how much solutions the function must return
   * @param modelName Is the picobot alloy model filename to tell which parser the function has to use
   * @return for each solution, a list of rules
   */
  private def getFirstNSolutions(solutions : A4Solution, n : Int, modelName: String) : List[List[Rule]] = {
    var i = 0
    var list : List[List[Rule]] = Nil
    var solution = solutions


    while (solution.satisfiable && i < n) {
      i += 1

      /* PARSE ALLOY RULE OUTPUT */
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

  /**
   * Is a helper function that creates simulations by generated futures which have an onSuccess implementation
   * that generates result files
   *
   * @param model Is the alloy model used for the simulation
   * @param solutions Is the list of sets of rules the simulation that need to be tested
   * @param nbSolutionToExplore Is the number of sets of rules the simulation needs to test
   * @return a list of future ready to launch simulations on the model passed in parameter
   */
  private def createSimulations(model: Model, solutions: A4Solution, nbSolutionToExplore: Int): List[Future[(String, Int, List[Result])]] = {
    val rules = getFirstNSolutions(solutions, nbSolutionToExplore, model.name)

    maps.map { map =>
      /* INSTANTIATE A SIMULATION */
      val f = Future[(String, Int, List[Result])] {
        var results: List[Result] = Nil
        val filename = model.name + "-" + map.path.substring(map.path.lastIndexOf('/') + 1).dropRight(4)

        /* LAUNCH A SIMULATION FOR EACH SET OF RULES */
        rules.foreach { rules =>
          for (position <- map.positions) {
            lazy val simulator = new OracleSimulator(
              map.path,
              saveRules(rulesFilename(filename), rules).getAbsolutePath,
              position,
              1000
            )

            /* SUCCEEDED SIMULATION */
            try {
              simulator.run
              results = results.:+(Result(rules, position, map.positions.size))
            }
            /* FAILED SIMULATION */
            catch {
              case spe: SimulationPicobotException => results = results.:+(Result(rules, position, spe.getNbCrossedCells))
            }
          }
        }

        /* DATA REQUIRED WHEN SIMULATION IS OVER */
        (filename, map.positions.size, results)
      }

      /* WHEN SIMULATION IS OVER */
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