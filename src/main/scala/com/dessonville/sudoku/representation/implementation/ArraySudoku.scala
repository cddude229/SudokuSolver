package com.dessonville.sudoku.representation.implementation

import com.dessonville.sudoku.representation.{SudokuBuilder, Sudoku}

class ArraySudoku[R] private[implementation] (private val grid: Array[Array[R]], val innerDimension: Int,
                                              val outerDimension: Int, val allowedItems: Set[R],
                                              val emptyItem: R) extends Sudoku[R] {
  require(grid.size == outerDimension, "Please make sure you have enough rows.")
  require(grid.forall(_.size == outerDimension), s"All of your rows must have $outerDimension items.")
  require(grid.forall(_.forall(item => allowedItems.contains(item) || item == emptyItem)), "All values must be in allowedItems or emptyItem")

  def getRow(row: Int): Iterable[R] = grid(row)

  def getColumn(col: Int): Iterable[R] = grid.map(row => row(col))

  def getBox(col: Int, row: Int): Iterable[Iterable[R]] = {
    grid.slice(lowBoxIndex(row), highBoxIndex(row)+1).map {
      _.slice(lowBoxIndex(col), highBoxIndex(col) + 1).toIterable
    }
  }

  def setValue(col: Int, row: Int, value: R){
    grid(row)(col) = value
  }
}
