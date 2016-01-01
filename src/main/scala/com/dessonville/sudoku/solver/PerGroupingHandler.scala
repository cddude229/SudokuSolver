package com.dessonville.sudoku.solver

import com.dessonville.sudoku.representation.SudokuGuesser

/**
  * There is a common patterns that iterates on a pre-group (row, cell, box,) so this is an attempt to
  * extract this pattern.
  */
trait PerGroupingHandler[R] {
  protected def loadGrouping(guesser: SudokuGuesser[R], id: Int): Iterable[R]

  protected def forCellsInGrouping(guesser: SudokuGuesser[R], id: Int)(func: (Int, Int) => Unit): Unit

}
