package com.dessonville.sudoku.solver.groupings

import com.dessonville.sudoku.representation.{CellCoordinates, SudokuGuesser}
import com.dessonville.sudoku.solver.PerGroupingHandler

trait PerRowHandler[Value] extends PerGroupingHandler[Value] {

  override protected def loadUsedItemsInGrouping(guesser: SudokuGuesser[Value], id: Int): Iterable[Value] = guesser.getValuesInRow(id)

  override protected def forCellsInGrouping(guesser: SudokuGuesser[Value], id: Int)(func: CellCoordinates => Unit): Unit = {
    guesser.getCellsInRow(id).foreach(func)
  }

}
