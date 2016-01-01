package com.dessonville.sudoku.solver.patterns

import com.dessonville.sudoku.representation.SudokuGuesser

class OnlyValueInRow[R] extends OnlyValueInGrouping[R] {

  override protected def loadGrouping(guesser: SudokuGuesser[R], id: Int): Iterable[R] = guesser.getRow(id)

  override protected def forCellsInGrouping(guesser: SudokuGuesser[R], id: Int)(func: (Int, Int) => Unit): Unit = {
    guesser.forCellsInRow(id)(func)
  }
}
