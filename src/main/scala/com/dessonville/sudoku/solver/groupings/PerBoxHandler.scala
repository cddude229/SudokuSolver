package com.dessonville.sudoku.solver.groupings

import com.dessonville.sudoku.representation.SudokuGuesser
import com.dessonville.sudoku.solver.PerGroupingHandler

trait PerBoxHandler[R] extends PerGroupingHandler[R] {

  override protected def loadUsedItemsInGrouping(guesser: SudokuGuesser[R], id: Int): Iterable[R] = guesser.getBox(id)

  override protected def forCellsInGrouping(guesser: SudokuGuesser[R], id: Int)(func: (Int, Int) => Unit): Unit = {
    guesser.forCellsInBox(id)(func)
  }

}
