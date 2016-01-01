package com.dessonville.sudoku.solver.patterns

import com.dessonville.sudoku.representation.SudokuGuesser

class OnlyValueInColumn[R] extends OnlyValueInGrouping[R] {

  override protected def loadGrouping(guesser: SudokuGuesser[R], id: Int): Iterable[R] = guesser.getColumn(id)

  override protected def forCellsInGrouping(guesser: SudokuGuesser[R], id: Int)(func: (Int, Int) => Unit): Unit = {
    guesser.forCellsInColumn(id)(func)
  }
}
