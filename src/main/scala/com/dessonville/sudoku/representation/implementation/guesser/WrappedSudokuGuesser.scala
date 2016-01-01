package com.dessonville.sudoku.representation.implementation.guesser

import com.dessonville.sudoku.representation.{CellCoordinates, Sudoku, SudokuGuesser}

abstract class WrappedSudokuGuesser[R](private val wrapped: Sudoku[R]) extends SudokuGuesser[R] {
  def allowedCellValues: Set[R] = wrapped.allowedCellValues

  def emptyCellValue: R = wrapped.emptyCellValue

  def innerDimension: Int = wrapped.innerDimension

  def outerDimension: Int = wrapped.outerDimension

  def getValuesInRow(row: Int): Iterable[R] = wrapped.getValuesInRow(row)

  def getValuesInColumn(col: Int): Iterable[R] = wrapped.getValuesInColumn(col)

  def getValuesInBox(col: Int, row: Int): Iterable[Iterable[R]] = wrapped.getValuesInBox(col, row)

  def setCellValue(cellCoordinates: CellCoordinates, value: R) = wrapped.setCellValue(cellCoordinates, value)

  def getCellValue(cellCoordinates: CellCoordinates): R = wrapped.getCellValue(cellCoordinates)
}
