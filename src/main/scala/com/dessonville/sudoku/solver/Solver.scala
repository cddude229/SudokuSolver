package com.dessonville.sudoku.solver

import com.dessonville.sudoku.representation.SudokuGuesser
import com.dessonville.sudoku.solver.patterns._

trait Solver {
  def solve[Value](guesser: SudokuGuesser[Value])
}

object PatternSolver extends Solver {
  def solve[Value](guesser: SudokuGuesser[Value]) {
    val patterns = Array[ReducingPattern[Value]](
      new OnePossibilityToValue[Value],

      new OnlyValueInRow[Value],
      new OnlyValueInColumn[Value],
      new OnlyValueInBox[Value],

      new ReduceCoupletsInBox[Value](2),
      new ReduceCoupletsInColumn[Value](2),
      new ReduceCoupletsInRow[Value](2),

      new ReduceCoupletsInBox[Value](3),
      new ReduceCoupletsInColumn[Value](3),
      new ReduceCoupletsInRow[Value](3),

      new BoxToColAndRowClearing[Value],
      new ColAndRowToBoxClearing[Value]
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
