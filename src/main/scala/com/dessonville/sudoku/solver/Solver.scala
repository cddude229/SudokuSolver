package com.dessonville.sudoku.solver

import com.dessonville.sudoku.representation.SudokuGuesser
import com.dessonville.sudoku.solver.patterns._

trait Solver {
  def solve[R](guesser: SudokuGuesser[R])
}

object PatternSolver extends Solver {
  def solve[R](guesser: SudokuGuesser[R]) {
    val patterns = Array[ReducingPattern[R]](
      new OnePossibilityToValue[R],

      new OnlyValueInRow[R],
      new OnlyValueInColumn[R],
      new OnlyValueInBox[R],

      new ReduceCoupletsInBox[R](2),
      new ReduceCoupletsInColumn[R](2),
      new ReduceCoupletsInRow[R](2),

      new ReduceCoupletsInBox[R](3),
      new ReduceCoupletsInColumn[R](3),
      new ReduceCoupletsInRow[R](3),

      new BoxToColAndRowClearing[R]
    )

    // Iterate over the patterns, reset to first pattern until we're done
    var idx = 0
    while (!guesser.isSolved() && idx < patterns.length) {
      val result = patterns(idx).reduce(guesser)
      if (result) {
        idx = 0
      } else {
        idx += 1
      }
    }
  }
}
