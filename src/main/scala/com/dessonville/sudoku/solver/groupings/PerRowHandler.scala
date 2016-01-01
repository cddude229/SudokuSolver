package com.dessonville.sudoku.solver.groupings

import com.dessonville.sudoku.representation.{CellCoordinates, SudokuGuesser}
import com.dessonville.sudoku.solver.PerGroupingHandler

trait PerRowHandler[R] extends PerGroupingHandler[R] {

  override protected def loadUsedItemsInGrouping(guesser: SudokuGuesser[R], id: Int): Iterable[R] = guesser.getValuesInRow(id)

  override protected def forCellsInGrouping(guesser: SudokuGuesser[R], id: Int)(func: CellCoordinates => Unit): Unit = {
    guesser.mapCellsInRow(id)(func)
  }

}
