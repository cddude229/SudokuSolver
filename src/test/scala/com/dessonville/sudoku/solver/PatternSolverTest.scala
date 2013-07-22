package com.dessonville.sudoku.solver

import com.dessonville.sudoku.representation.{SudokuGuesser, Sudoku, SudokuBuilder}
import com.dessonville.sudoku.representation.implementation.Array9x9Sudoku
import com.dessonville.sudoku.representation.implementation.guesser.ArraySetGuesser
import org.scalatest.junit.JUnitSuite
import org.junit.Test
import java.io.File

class PatternSolverTest extends JUnitSuite {
  val filePath = "./src/test/resources/%s"

  private def runAgainstFiles9x9(files: Array[String], solved: Boolean, correct: Boolean){
    type R = Int

    files.foreach {
      file => {
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

        println(s"$file:")
        println(guesser.toString())
        println("\n\n\n")

        assert(guesser.isSolved() == solved, s"Checking $file solved: expected $solved but got ${guesser.isSolved()}")
        assert(guesser.isCorrect() == correct, s"Checking $file correctness: expected $correct but got ${guesser.isCorrect()}")

      }
    }
  }

  @Test def sudoku9x9success(){
    val files = Array(
      "easy/sudoku1.txt",
      "easy/sudoku2.txt",
      "easy/sudoku3.txt",
      "easy/sudoku4.txt",
      "easy/sudoku5.txt",
      "easy/sudoku6.txt",
      "easy/sudoku7.txt",

      "medium/sudoku1.txt",
      "medium/sudoku2.txt",
      "medium/sudoku3.txt",
      "medium/sudoku4.txt"
    )

    runAgainstFiles9x9(files, solved=true, correct=true)
  }

  @Test def sudoku9x9fail(){
    val unsolvableFiles = Array(
      "fail/blank.txt"
    )

    val solvedButWrongFiles = Array(
      "fail/all-1.txt",
      "fail/one-row-duplication.txt",
      "fail/one-column-duplication.txt",
      "fail/one-box-duplication.txt"
    )

    runAgainstFiles9x9(unsolvableFiles, solved=false, correct=false)
    runAgainstFiles9x9(solvedButWrongFiles, solved=true, correct=false)

  }
}
