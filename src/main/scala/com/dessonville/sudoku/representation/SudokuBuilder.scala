package com.dessonville.sudoku.representation

trait SudokuBuilder[Value] {
  def addRow(row: Iterable[Value]): Unit

  def finish(): Sudoku[Value]
}
