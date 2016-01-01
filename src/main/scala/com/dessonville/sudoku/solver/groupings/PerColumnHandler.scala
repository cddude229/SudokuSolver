package com.dessonville.sudoku.solver.groupings

import com.dessonville.sudoku.representation.{CellCoordinates, SudokuGuesser}
import com.dessonville.sudoku.solver.PerGroupingHandler

trait PerColumnHandler[R] extends PerGroupingHandler[R] {

  override protected def loadUsedItemsInGrouping(guesser: SudokuGuesser[R], id: Int): Iterable[R] = guesser.getValuesInColumn(id)

  override protected def forCellsInGrouping(guesser: SudokuGuesser[R], id: Int)(func: CellCoordinates => Unit): Unit = {
    guesser.mapCellsInColumn(id)(func)
  }

}
