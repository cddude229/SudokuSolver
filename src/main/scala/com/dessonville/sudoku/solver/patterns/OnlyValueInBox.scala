package com.dessonville.sudoku.solver.patterns

import com.dessonville.sudoku.representation.SudokuGuesser

class OnlyValueInBox[R] extends OnlyValueInGrouping[R] {

  override protected def loadGrouping(guesser: SudokuGuesser[R], id: Int): Iterable[R] = guesser.getBox(id)

  override protected def forCellsInGrouping(guesser: SudokuGuesser[R], id: Int)(func: (Int, Int) => Unit): Unit = {
    guesser.forCellsInBox(id)(func)
  }
}
