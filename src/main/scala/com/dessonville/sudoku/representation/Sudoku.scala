package com.dessonville.sudoku.representation

import scala.collection.mutable

/*
NOTES
1) Columns and rows are 0 based, 0-8
2) A value is 1-9.  0 means it's an unknown value
3) Boxes are based 0-2 in row and column
*/

/**
  * Base design for any implementation of a sudoku, including ones that might be faster to load or analyze
  */
trait Sudoku[Value] {

  def allowedCellValues: Set[Value]

  def emptyCellValue: Value

  def innerDimension: Int

  def outerDimension: Int

  require(!allowedCellValues.contains(emptyCellValue), "Empty cell value can not be in the allowed cell values")
  require(allowedCellValues.size == outerDimension, "You must have enough allowed cell values to fill every spot")

  /**
    * Gets all the ints in the current row, left to right
    * @param row
    * @return
    */
  def getValuesInRow(row: Int): Iterable[Value]

  /**
    * Gets all the ints in the current row, top to bottom
    * @param col
    * @return
    */
  def getValuesInColumn(col: Int): Iterable[Value]

  /**
    * Gets all the ints in the current box, left to right, top to bottom
    * @param boxColumn
    * @param boxRow
    * @return
    */
  def getValuesInBox(boxColumn: Int, boxRow: Int): Iterable[Iterable[Value]]

  /**
    * Get the items in a box, in order, using the box index instead of (col, row)
    * @param boxIndex
    * @return
    */
  def getValuesInBox(boxIndex: Int): Iterable[Value] = {
    val (col, row) = getBoxCoordsFromBoxIndex(boxIndex)
    getValuesInBox(col, row).flatten
  }

  /**
    * Set the value for a given cell
    * @param colIndex
    * @param rowIndex
    * @param value
    */
  def setCellValue(colIndex: Int, rowIndex: Int, value: Value): Unit

  /**
    * Get the value for a given cell
    * @param colIndex
    * @param rowIndex
    * @return
    */
  def getCellValue(colIndex: Int, rowIndex: Int): Value

  /**
    * Set the value for a given cell
    * @param cellCoordinates
    * @param value
    */
  def setCellValue(cellCoordinates: CellCoordinates, value: Value): Unit = setCellValue(cellCoordinates.columnIndex, cellCoordinates.rowIndex, value)

  /**
    * Get the value for a given cell* @param cellCoordinates
    * @return
    */
  def getCellValue(cellCoordinates: CellCoordinates): Value = getCellValue(cellCoordinates.columnIndex, cellCoordinates.rowIndex)


  def getMissingItemsInRow(rowIndex: Int) = determineMissingValues(getValuesInRow(rowIndex))

  def getMissingItemsInColumn(colIndex: Int) = determineMissingValues(getValuesInColumn(colIndex))

  def getMissingItemsInBox(colIndex: Int, rowIndex: Int) = determineMissingValues(getValuesInBox(colIndex, rowIndex).reduce(_ ++ _))

  final protected def determineMissingValues(values: Iterable[Value]): Set[Value] = allowedCellValues -- values.toSet

  final protected def lowerBoxIndex(rowOrColIndex: Int): Int = rowOrColIndex * innerDimension

  final protected def higherBoxIndex(rowOrColIndex: Int): Int = lowerBoxIndex(rowOrColIndex + 1) - 1 // Always one less than it

  /**
    * Gives a cell's coordinates, determine it's containing box's coordinates
    * @param cellColIndex
    * @param cellRowIndex
    * @return
    */
  final protected def boxCoordsContainingCell(cellColIndex: Int, cellRowIndex: Int): (Int, Int) = {
    (cellColIndex / innerDimension, cellRowIndex / innerDimension)
  }

  /**
    * Lets you iterate from 0 to outerDimension easily
    */
  final def mapAllIndices[T](func: Int => T): Iterable[T] = (0 until outerDimension).map(func)

  final protected def mapColumnAndRowRange[T](columnRange: Iterable[Int], rowRange: Iterable[Int])(func: CellCoordinates => T): Iterable[Iterable[T]] = {
    columnRange.map {
      col => rowRange.map {
        row => func(ColumnRowIndexBasedCoordinates(col, row, innerDimension, outerDimension))
      }
    }
  }

  final def mapAllCells[T](func: CellCoordinates => T): Iterable[Iterable[T]] = {
    mapColumnAndRowRange(0 until outerDimension, 0 until outerDimension)(func)
  }

  final def mapCellsInRow[T](row: Int)(func: CellCoordinates => T): Iterable[T] = mapAllIndices {
    columnIndex => func(ColumnRowIndexBasedCoordinates(columnIndex, row, innerDimension, outerDimension))
  }

  final def mapCellsInColumn[T](col: Int)(func: CellCoordinates => T): Iterable[T] = mapAllIndices {
    rowIndex => func(ColumnRowIndexBasedCoordinates(col, rowIndex, innerDimension, outerDimension))
  }

  final def mapCellsInBox[T](boxCol: Int, boxRow: Int)(func: CellCoordinates => T): Iterable[Iterable[T]] = {
    def lowToHigh(i: Int) = lowerBoxIndex(i) to higherBoxIndex(i)

    mapColumnAndRowRange(lowToHigh(boxCol), lowToHigh(boxRow))(func)
  }

  final def mapCellsInBox[T](boxIdx: Int)(func: CellCoordinates => T): Iterable[Iterable[T]] = {
    val (col, row) = getBoxCoordsFromBoxIndex(boxIdx)
    mapCellsInBox(col, row)(func)
  }

  override def toString(): String = (0 until outerDimension).map(getValuesInRow(_).mkString(",")).mkString("\n")

  private def getBoxCoordsFromBoxIndex(index: Int): (Int, Int) = {
    val col = index % (outerDimension / innerDimension)
    val row = index / (outerDimension / innerDimension)
    (col, row)
  }

  final def getCellsInBox(boxIdx: Int): Iterable[(Int, Int)] = {
    mapCellsInBox(boxIdx) {
      cellCoordinates =>
        (cellCoordinates.columnIndex, cellCoordinates.rowIndex)
    }.flatten
  }

  final def getCellsInColumn(colIdx: Int): Iterable[(Int, Int)] = {
    mapCellsInColumn(colIdx) {
      cellCoordinates =>
        (cellCoordinates.columnIndex, cellCoordinates.rowIndex)
    }
  }

  final def getCellsInRow(rowIdx: Int): Iterable[(Int, Int)] = {
    mapCellsInRow(rowIdx) {
      cellCoordinates =>
        (cellCoordinates.columnIndex, cellCoordinates.rowIndex)
    }
  }

  final def getColumnsContainingCells(cells: (Int, Int)*): Iterable[Iterable[(Int, Int)]] = {
    val colIndices = cells.map(_._1).toSet
    (0 until outerDimension).filter(colIndices.contains).map(getCellsInColumn)
  }

  final def getRowsContainingCells(cells: (Int, Int)*): Iterable[Iterable[(Int, Int)]] = {
    val rowIndices = cells.map(_._2).toSet
    (0 until outerDimension).filter(rowIndices.contains).map(getCellsInRow)
  }
}
