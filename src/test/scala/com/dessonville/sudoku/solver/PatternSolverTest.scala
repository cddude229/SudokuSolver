package com.dessonville.sudoku.solver

import com.dessonville.sudoku.representation.{SudokuGuesser, Sudoku, SudokuBuilder}
import com.dessonville.sudoku.representation.implementation.Array9x9Sudoku
import com.dessonville.sudoku.representation.implementation.guesser.ArraySetGuesser
import org.scalatest.junit.JUnitSuite
import org.junit.Test
import java.io.File

class PatternSolverTest extends JUnitSuite {
  val filePath = "./src/test/resources/%s"

  @Test def sudoku9x9(){
    val files = Array(
      "easy/sudoku1.txt",
      "easy/sudoku2.txt",
      "easy/sudoku3.txt",
      "easy/sudoku4.txt",
      "easy/sudoku5.txt",
      "easy/sudoku6.txt",
      "easy/sudoku7.txt"
    )

    type R = Int
    files.foreach {
      file => {
        val builder: SudokuBuilder[R] = Array9x9Sudoku.builder

        val theFile = new File(filePath.format(file))
        assert(theFile.exists, s"Couldn't find file: $file")

        scala.io.Source.fromFile(theFile).getLines().foreach {
          line => {
            builder.addRow(line.split("").filter(_.length > 0).map(_.toInt))
          }
        }

        val sudoku: Sudoku[R] = builder.finish()

        val guesser: SudokuGuesser[R] = new ArraySetGuesser[R](sudoku)

        PatternSolver.solve(guesser)

        assert(guesser.isSolved(), s"Failed to solve $file")
        assert(guesser.isCorrect(), s"Failed to correctly solve $file")
      }
    }
  }



}
