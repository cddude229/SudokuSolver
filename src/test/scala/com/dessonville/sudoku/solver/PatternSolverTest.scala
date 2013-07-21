package com.dessonville.sudoku.solver

import com.dessonville.sudoku.representation.{SudokuGuesser, Sudoku, SudokuBuilder}
import com.dessonville.sudoku.representation.implementation.Array9x9Sudoku
import com.dessonville.sudoku.representation.implementation.guesser.ArraySetGuesser
import org.scalatest.junit.JUnitSuite
import org.junit.Test

class PatternSolverTest extends JUnitSuite {
  @Test def sudoku1(){
    type R = Int
    val builder: SudokuBuilder[R] = Array9x9Sudoku.builder

    builder.addRow(Array(3,7,9,0,0,0,0,1,4))
    builder.addRow(Array(0,6,0,0,1,0,0,7,0))
    builder.addRow(Array(0,8,0,0,0,9,0,0,5))
    builder.addRow(Array(4,3,5,0,0,7,0,0,0))
    builder.addRow(Array(0,9,0,0,4,0,0,2,0))
    builder.addRow(Array(0,0,0,8,0,0,4,3,6))
    builder.addRow(Array(9,0,0,7,0,0,0,8,0))
    builder.addRow(Array(0,4,0,0,8,0,0,5,0))
    builder.addRow(Array(8,5,0,0,0,0,2,4,9))

    val sudoku: Sudoku[R] = builder.finish()

    val guesser: SudokuGuesser[R] = new ArraySetGuesser[R](sudoku)

    PatternSolver.solve(guesser)

    assert(guesser.isSolved())
  }

}
