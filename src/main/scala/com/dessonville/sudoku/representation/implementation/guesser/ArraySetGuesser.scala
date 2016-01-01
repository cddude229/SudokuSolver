package com.dessonville.sudoku.representation.implementation.guesser

import com.dessonville.sudoku.representation.Sudoku

class ArraySetGuesser[R](private val wrapped: Sudoku[R]) extends WrappedSudokuGuesser[R](wrapped) {
  private[this] val grid: Array[Array[Set[R]]] = Array.ofDim[Set[R]](outerDimension, outerDimension)

  // Populate all items by default
  wrapped.forAllCells {
    (col, row) => {
      grid(col)(row) = if (isDetermined(col, row)) {
        Set[R](getValue(col, row))
      } else {
        allowedItems
      }
    }
  }

  def getPossibilities(col: Int, row: Int): Set[R] = grid(col)(row)

  def removePossibilities(col: Int, row: Int, values: Set[R]) {
    grid(col)(row) = grid(col)(row) -- values
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

      forAllIndices {
        idx => {
          ret = ret && completedSet(getRow(idx)) && completedSet(getColumn(idx)) && completedSet(getBox(idx))
        }
      }
    }

    ret
  }

  private def completedSet(currentItems: Iterable[R]): Boolean = {
    currentItems.toSet.filter(_ != emptyItem) == allowedItems
  }
}
