package com.dessonville.sudoku.representation.implementation.guesser

import com.dessonville.sudoku.representation.{Sudoku, SudokuGuesser}

abstract class WrappedSudokuGuesser[R](private val wrapped: Sudoku[R]) extends SudokuGuesser[R] {
  def allowedItems: Set[R] = wrapped.allowedItems

  def emptyItem: R = wrapped.emptyItem

  def innerDimension: Int = wrapped.innerDimension

  def outerDimension: Int = wrapped.outerDimension

  def getRow(row: Int): Iterable[R] = wrapped.getRow(row)

  def getColumn(col: Int): Iterable[R] = wrapped.getColumn(col)

  def getBox(col: Int, row: Int): Iterable[Iterable[R]] = wrapped.getBox(col, row)

  def setValue(col: Int, row: Int, value: R) = wrapped.setValue(col, row, value)

  def getValue(col: Int, row: Int): R = wrapped.getValue(col, row)
}
