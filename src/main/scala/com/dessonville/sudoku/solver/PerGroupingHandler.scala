package com.dessonville.sudoku.solver

import com.dessonville.sudoku.representation.{CellCoordinates, SudokuGuesser}

/**
  * There is a common patterns that iterates on a pre-group (row, cell, box,) so this is an attempt to
  * extract this pattern.
  */
trait PerGroupingHandler[R] {
  /**
    * Loads the items that are already used in a grouping
    */
  protected def loadUsedItemsInGrouping(guesser: SudokuGuesser[R], id: Int): Iterable[R]

  /**
    * Apply a function to each cell in a grouping
    */
  protected def forCellsInGrouping(guesser: SudokuGuesser[R], id: Int)(func: CellCoordinates => Unit): Unit

}
