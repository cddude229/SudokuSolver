package com.dessonville.sudoku.solver

import java.io.File

import com.dessonville.sudoku.representation.implementation.Array9x9Sudoku
import com.dessonville.sudoku.representation.implementation.guesser.ArraySetGuesser
import com.dessonville.sudoku.representation.{Sudoku, SudokuBuilder, SudokuGuesser}
import org.junit.runner.RunWith
import org.scalatest.WordSpec
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.Matchers
import org.springframework.core.io.support.PathMatchingResourcePatternResolver

@RunWith(classOf[JUnitRunner])
class PatternSolverTest extends WordSpec with Matchers {
  val filePath = "./src/test/resources/%s"

  val solvableFileFolders = Array (
    "easy", "medium", "hard", "euler"
  )

  val solvableFiles: Array[String] = solvableFileFolders.flatMap {
    fileFolder =>
      new PathMatchingResourcePatternResolver().getResources(s"$fileFolder/*.txt").map {
        resource => s"$fileFolder/${resource.getFilename}"
      }
  }

  val unsolvableFiles = Array(
    "fail/blank.txt"
  )

  val solvableButWrongFiles = Array(
    "fail/all-1.txt",
    "fail/one-row-duplication.txt",
    "fail/one-column-duplication.txt",
    "fail/one-box-duplication.txt"
  )

  "PatternSolver" should {
    solvableFiles.foreach {
      solvableFile =>
        s"successfully solve $solvableFile" in {
          runAgainstFile9x9(solvableFile, solved = true, correct = true)
        }
    }

    unsolvableFiles.foreach {
      unsolvableFile =>
        s"fail to solve $unsolvableFile" in {
          runAgainstFile9x9(unsolvableFile, solved = false, correct = false)
        }
    }

    solvableButWrongFiles.foreach {
      solvableButWrongFile =>
        s"solve, but not produce correct results of $solvableButWrongFile" in {
          runAgainstFile9x9(solvableButWrongFile, solved = true, correct = false)
        }
    }
  }

  private def runAgainstFile9x9(file: String, solved: Boolean, correct: Boolean) {
    type R = Int

    val theFile = new File(filePath.format(file))
    assert(theFile.exists, s"Couldn't find file: $file")

    val builder: SudokuBuilder[R] = Array9x9Sudoku.builder

    scala.io.Source.fromFile(theFile).getLines().foreach {
      line => {
        builder.addRow(line.split("").filter(_.length > 0).map(_.toInt))
      }
    }

    val sudoku: Sudoku[R] = builder.finish()
    val guesser: SudokuGuesser[R] = new ArraySetGuesser[R](sudoku)

    PatternSolver.solve(guesser)

    println(s"$file (score: ${guesser.solvedScore()}):")
    println(guesser.toString())
    println("\n\n\n")

    assert(guesser.isSolved() == solved, s"Checking $file solved: expected $solved but got ${guesser.isSolved()}")
    assert(guesser.isCorrect() == correct, s"Checking $file correctness: expected $correct but got ${guesser.isCorrect()}")

    if (solved) {
      assert(guesser.solvedScore() == 0, s"Checking $file score: expected 0 but got ${guesser.solvedScore()}")
    } else {
      assert(guesser.solvedScore() != 0, s"Checking $file score: expected not 0, but got 0")
    }
  }
}
