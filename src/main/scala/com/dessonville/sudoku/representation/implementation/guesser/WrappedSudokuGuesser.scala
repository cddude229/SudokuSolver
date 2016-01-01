package com.dessonville.sudoku.representation.implementation.guesser

import com.dessonville.sudoku.representation.{Sudoku, SudokuGuesser}

abstract class WrappedSudokuGuesser[R](private val wrapped: Sudoku[R]) extends SudokuGuesser[R] {
  def allowedCellValues: Set[R] = wrapped.allowedCellValues

  def emptyCellValue: R = wrapped.emptyCellValue

  def innerDimension: Int = wrapped.innerDimension

  def outerDimension: Int = wrapped.outerDimension

  def getValuesInRow(row: Int): Iterable[R] = wrapped.getValuesInRow(row)

  def getValuesInColumn(col: Int): Iterable[R] = wrapped.getValuesInColumn(col)

  def getValuesInBox(col: Int, row: Int): Iterable[Iterable[R]] = wrapped.getValuesInBox(col, row)

  def setCellValue(col: Int, row: Int, value: R) = wrapped.setCellValue(col, row, value)

  def getCellValue(col: Int, row: Int): R = wrapped.getCellValue(col, row)
}
