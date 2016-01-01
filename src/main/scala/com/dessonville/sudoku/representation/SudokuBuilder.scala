package com.dessonville.sudoku.representation

trait SudokuBuilder[Value] {
  def addRow(row: Iterable[Value])

  def finish(): Sudoku[Value]
}
