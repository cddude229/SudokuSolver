package com.dessonville.sudoku.representation

trait SudokuBuilder[R] {
  def addRow(row: Iterable[R])

  def finish(): Sudoku[R]
}
