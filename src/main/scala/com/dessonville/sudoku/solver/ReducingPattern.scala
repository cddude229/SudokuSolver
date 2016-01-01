package com.dessonville.sudoku.solver

import com.dessonville.sudoku.representation.SudokuGuesser

/**
  * Patterns that are used in analysis of a SudokuGuesser
  */
trait ReducingPattern[Value] {
  /**
    * Given the guesser, attempt to reduce it to a simpler problem
    * @param guesser
    */
  def reduce(guesser: SudokuGuesser[Value]): Unit
}
