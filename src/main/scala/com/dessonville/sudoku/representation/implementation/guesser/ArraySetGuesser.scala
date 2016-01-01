package com.dessonville.sudoku.representation.implementation.guesser

import com.dessonville.sudoku.representation.{CellCoordinates, Sudoku}

class ArraySetGuesser[Value](private val wrapped: Sudoku[Value]) extends WrappedSudokuGuesser[Value](wrapped) {
  private[this] val grid: Array[Array[Set[Value]]] = Array.ofDim[Set[Value]](outerDimension, outerDimension)

  // Populate all items by default
  wrapped.getAllCells.flatten.foreach {
    cellCoordinates => {
      val col = cellCoordinates.columnIndex
      val row = cellCoordinates.rowIndex
      grid(col)(row) = if (isDetermined(cellCoordinates)) {
        Set[Value](getCellValue(cellCoordinates))
      } else {
        allowedCellValues
      }
    }
  }

  def getPossibleValues(cellCoordinates: CellCoordinates): Set[Value] = {
    grid(cellCoordinates.columnIndex)(cellCoordinates.rowIndex)
  }

  def removePossibleValues(cellCoordinates: CellCoordinates, values: Set[Value]): Boolean = {
    val col = cellCoordinates.columnIndex
    val row = cellCoordinates.rowIndex

    val original = grid(col)(row)
    grid(col)(row) = grid(col)(row) -- values
    grid(col)(row) != original
  }

  private[this] var solved = false

  def isSolved(): Boolean = {
    solved = solved || grid.forall(row => row.forall(col => col.isEmpty))
    solved
  }

  def isCorrect(): Boolean = {
    var ret = false
    if (isSolved()) {
      ret = true

      mapAllIndices {
        idx => {
          ret = ret && completedSet(getValuesInRow(idx)) && completedSet(getValuesInColumn(idx)) && completedSet(getValuesInBox(idx).flatten)
        }
      }
    }

    ret
  }

  private def completedSet(currentItems: Iterable[Value]): Boolean = {
    currentItems.toSet.filter(_ != emptyCellValue) == allowedCellValues
  }
}
