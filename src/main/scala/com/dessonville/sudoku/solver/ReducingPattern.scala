package com.dessonville.sudoku.solver

import com.dessonville.sudoku.representation.SudokuGuesser

/**
  * Patterns that are used in analysis of a SudokuGuesser
  */
trait ReducingPattern[R] {
  /**
    * Given the guesser, attempt to reduce it to a simpler problem
    * @param guesser
    * @return True if any reduction was performed, false otherwise
    */
  def reduce(guesser: SudokuGuesser[R]): Boolean
}
