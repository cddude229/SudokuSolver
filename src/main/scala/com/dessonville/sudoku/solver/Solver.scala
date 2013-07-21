package com.dessonville.sudoku.solver

import com.dessonville.sudoku.solver.patterns._
import com.dessonville.sudoku.representation.{SudokuBuilder, Sudoku, SudokuGuesser}
import com.dessonville.sudoku.representation.implementation.guesser.ArraySetGuesser
import com.dessonville.sudoku.representation.implementation.Array9x9Sudoku

object Solver extends App {
  // This is a temporary implementation until I can get a set of test code going

  type R = Int

  val builder: SudokuBuilder[R] = Array9x9Sudoku.builder

  builder.addRow(Array(0,2,3,4,5,6,7,8,9))
  builder.addRow(Array(4,5,6,7,8,9,1,2,3))
  builder.addRow(Array(7,8,9,1,2,3,4,5,6))

  builder.addRow(Array(2,3,4,5,6,7,8,9,1))
  builder.addRow(Array(5,6,7,8,0,1,2,3,4))
  builder.addRow(Array(8,9,1,2,3,4,5,6,7))

  builder.addRow(Array(3,4,5,6,7,8,9,1,2))
  builder.addRow(Array(6,7,8,9,1,2,3,4,5))
  builder.addRow(Array(9,1,2,3,4,5,6,7,8))

  val sudoku: Sudoku[R] = builder.finish()

  val guesser: SudokuGuesser[R] = new ArraySetGuesser[R](sudoku)

  val patterns = Array[ReducingPattern[R]](
    new OnePossibilityToValue[R],
    new OnlyValueInRow[R],
    new OnlyValueInCell[R]
  )

  // Iterate over the patterns, reset to first pattern until we're done
  var idx = 0
  while(!guesser.isSolved() && idx < patterns.length){
    val result = patterns(idx).reduce(guesser)
    if(result){
      idx = 0
    } else {
      idx += 1
    }
  }

  println(guesser.toString)
}
