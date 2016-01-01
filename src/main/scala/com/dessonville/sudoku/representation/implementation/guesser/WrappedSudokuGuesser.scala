package com.dessonville.sudoku.representation.implementation.guesser

import com.dessonville.sudoku.representation.{CellCoordinates, Sudoku, SudokuGuesser}

abstract class WrappedSudokuGuesser[Value](private val wrapped: Sudoku[Value]) extends SudokuGuesser[Value] {
  def allowedCellValues: Set[Value] = wrapped.allowedCellValues

  def emptyCellValue: Value = wrapped.emptyCellValue

  def innerDimension: Int = wrapped.innerDimension

  def outerDimension: Int = wrapped.outerDimension

  def getValuesInRow(row: Int): Iterable[Value] = wrapped.getValuesInRow(row)

  def getValuesInColumn(col: Int): Iterable[Value] = wrapped.getValuesInColumn(col)

  def getValuesInBox(col: Int, row: Int): Iterable[Iterable[Value]] = wrapped.getValuesInBox(col, row)

  def setCellValue(cellCoordinates: CellCoordinates, value: Value) = wrapped.setCellValue(cellCoordinates, value)

  def getCellValue(cellCoordinates: CellCoordinates): Value = wrapped.getCellValue(cellCoordinates)
}
