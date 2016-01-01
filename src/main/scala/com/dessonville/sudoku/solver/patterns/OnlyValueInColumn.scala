package com.dessonville.sudoku.solver.patterns

import com.dessonville.sudoku.representation.SudokuGuesser

class OnlyValueInColumn[R] extends OnlyValueInSet[R] {

  override protected def loadItem(guesser: SudokuGuesser[R], id: Int): Iterable[R] = guesser.getColumn(id)

  override protected def forCellsInItem(guesser: SudokuGuesser[R], id: Int)(func: (Int, Int) => Unit): Unit = {
    guesser.forCellsInColumn(id)(func)
  }
}
