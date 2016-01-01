package com.dessonville.sudoku.solver.patterns

import com.dessonville.sudoku.representation.SudokuGuesser
import com.dessonville.sudoku.solver.ReducingPattern

/**
  * If there is only one possible value for the cell, then that should be the value.
  */
class OnePossibilityToValue[Value] extends ReducingPattern[Value] {
  def reduce(guesser: SudokuGuesser[Value]): Boolean = {
    var reduction = false
    guesser.getAllCells.flatten.foreach {
      cellCoordinates => {
        val possibilities: Set[Value] = guesser.getPossibleValues(cellCoordinates)
        if (possibilities.size == 1) {
          val value: Value = possibilities.head
          guesser.setValueAndRemovePossibleValue(cellCoordinates, value) // Re-setting the value will remove it from later rows
          reduction = true
        }
      }
    }
    reduction
  }
}
