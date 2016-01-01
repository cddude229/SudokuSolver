package com.dessonville.sudoku.solver.patterns

import com.dessonville.sudoku.representation.SudokuGuesser
import com.dessonville.sudoku.solver.ReducingPattern

class OnePossibilityToValue[R] extends ReducingPattern[R] {
  def reduce(guesser: SudokuGuesser[R]): Boolean = {
    var reduction = false
    guesser.forAllCells {
      (col, row) => {
        val possibilities: Set[R] = guesser.getPossibilities(col, row)
        if (possibilities.size == 1) {
          val value: R = possibilities.head
          guesser.setValueAndRemovePossibilities(col, row, value) // Re-setting the value will remove it from later rows
          reduction = true
        }
      }
    }
    reduction
  }
}
