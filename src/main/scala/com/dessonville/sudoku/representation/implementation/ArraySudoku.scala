package com.dessonville.sudoku.representation.implementation

import com.dessonville.sudoku.representation.{CellCoordinates, Sudoku, SudokuBuilder}

import scala.reflect.ClassTag

class ArraySudoku[Value:ClassTag] private[implementation](private val grid: Array[Array[Value]], val innerDimension: Int,
                                                 val outerDimension: Int, val allowedCellValues: Set[Value],
                                                 val emptyCellValue: Value) extends Sudoku[Value] {
  require(grid.size == outerDimension, "Please make sure you have enough rows.")
  require(grid.forall(_.size == outerDimension), s"All of your rows must have $outerDimension items.")
  require(grid.forall(_.forall(item => allowedCellValues.contains(item) || item == emptyCellValue)), "All values must be in allowedItems or emptyItem")

  def getValuesInRow(row: Int): Iterable[Value] = grid(row)

  def getValuesInColumn(col: Int): Iterable[Value] = grid.map(row => row.apply(col))

  def getValuesInBox(col: Int, row: Int): Iterable[Iterable[Value]] = {
    grid.slice(lowerBoxIndex(row), higherBoxIndex(row) + 1).map {
      _.slice(lowerBoxIndex(col), higherBoxIndex(col) + 1).toIterable
    }
  }

  def setCellValue(cellCoordinates: CellCoordinates, value: Value): Unit = {
    grid(cellCoordinates.rowIndex)(cellCoordinates.columnIndex) = value
  }

  def getCellValue(cellCoordinates: CellCoordinates): Value = {
    grid(cellCoordinates.rowIndex)(cellCoordinates.columnIndex)
  }
}


object Array9x9Sudoku {
  val innerDimension: Int = 3
  val outerDimension: Int = 9
  val allowedItems: Set[Int] = Set(1, 2, 3, 4, 5, 6, 7, 8, 9)
  val emptyItem: Int = 0

  def builder: SudokuBuilder[Int] = new SudokuBuilder[Int] {
    private var map = Array[Array[Int]]()

    def addRow(row: Iterable[Int]): Unit = {
      map :+= row.toArray
    }

    def finish(): Sudoku[Int] = {
      new ArraySudoku[Int](map, innerDimension, outerDimension, allowedItems, emptyItem)
    }
  }
}
