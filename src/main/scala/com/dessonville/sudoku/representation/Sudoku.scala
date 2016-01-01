package com.dessonville.sudoku.representation

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
    * Gets all the values in the given row, left to right.  Will return emptyCellValue for unknown cells
    * @param row
    * @return
    */
  def getValuesInRow(row: Int): Iterable[Value]

  /**
    * Gets all the values in the given row, top to bottom.  Will return emptyCellValue for unknown cells
    * @param col
    * @return
    */
  def getValuesInColumn(col: Int): Iterable[Value]

  /**
    * Gets all the values in the given box, left to right, top to bottom.  Will return emptyCellValue for unknown cells
    * @param boxColumn
    * @param boxRow
    * @return
    */
  def getValuesInBox(boxColumn: Int, boxRow: Int): Iterable[Iterable[Value]]

  /**
    * Gets all the values in the given box, left to right, top to bottom.  Will return emptyCellValue for unknown cells
    * @param boxIndex
    * @return
    */
  def getValuesInBox(boxIndex: Int): Iterable[Iterable[Value]] = {
    val (col, row) = getBoxCoordsFromBoxIndex(boxIndex)
    getValuesInBox(col, row)
  }

  /**
    * Set the value for a given cell
    * @param cellCoordinates
    * @param value
    */
  def setCellValue(cellCoordinates: CellCoordinates, value: Value): Unit

  /**
    * Get the value for a given cell
    * @param cellCoordinates
    * @return
    */
  def getCellValue(cellCoordinates: CellCoordinates): Value


  def getMissingItemsInRow(rowIndex: Int) = determineMissingValues(getValuesInRow(rowIndex))

  def getMissingItemsInColumn(colIndex: Int) = determineMissingValues(getValuesInColumn(colIndex))

  def getMissingItemsInBox(colIndex: Int, rowIndex: Int) = determineMissingValues(getValuesInBox(colIndex, rowIndex).reduce(_ ++ _))

  final protected def determineMissingValues(values: Iterable[Value]): Set[Value] = allowedCellValues -- values.toSet

  final protected def lowerBoxIndex(rowOrColIndex: Int): Int = rowOrColIndex * innerDimension

  final protected def higherBoxIndex(rowOrColIndex: Int): Int = lowerBoxIndex(rowOrColIndex + 1) - 1 // Always one less than it

  /**
    * Gives a cell's coordinates, determine it's containing box's coordinates
    * @param cellCoordinates
    * @return
    */
  final protected def boxCoordsContainingCell(cellCoordinates: CellCoordinates): (Int, Int) = {
    (cellCoordinates.columnIndex / innerDimension, cellCoordinates.rowIndex / innerDimension)
  }

  /**
    * Lets you iterate from 0 to outerDimension easily
    */
  final def getAllIndices: Iterable[Int] = 0 until outerDimension

  final protected def mapColumnAndRowRange[T](columnRange: Iterable[Int], rowRange: Iterable[Int])(func: CellCoordinates => T): Iterable[Iterable[T]] = {
    columnRange.map {
      col => rowRange.map {
        row => func(ColumnRowIndexBasedCoordinates(col, row, innerDimension, outerDimension))
      }
    }
  }

  override def toString(): String = (0 until outerDimension).map(getValuesInRow(_).mkString(",")).mkString("\n")

  private def getBoxCoordsFromBoxIndex(index: Int): (Int, Int) = {
    val col = index % (outerDimension / innerDimension)
    val row = index / (outerDimension / innerDimension)
    (col, row)
  }

  final def getAllCells: Iterable[Iterable[CellCoordinates]] = {
    mapColumnAndRowRange(0 until outerDimension, 0 until outerDimension)(cellCoordinates => cellCoordinates)
  }

  final def getCellsInBox(boxCol: Int, boxRow: Int): Iterable[CellCoordinates] = {
    def lowToHigh(i: Int) = lowerBoxIndex(i) to higherBoxIndex(i)

    mapColumnAndRowRange(lowToHigh(boxCol), lowToHigh(boxRow))(cellCoordinates => cellCoordinates).flatten
  }

  final def getCellsInBox(boxIdx: Int): Iterable[CellCoordinates] = {
    val (col, row) = getBoxCoordsFromBoxIndex(boxIdx)
    getCellsInBox(col, row)
  }

  final def getCellsInColumn(colIdx: Int): Iterable[CellCoordinates] = {
    getAllIndices.map {
      rowIdx => coordsToCellCoords(colIdx, rowIdx)
    }
  }

  final def getCellsInRow(rowIdx: Int): Iterable[CellCoordinates] = {
    getAllIndices.map {
      colIdx => coordsToCellCoords(colIdx, rowIdx)
    }
  }

  final def getColumnsContainingCells(cells: CellCoordinates*): Iterable[Iterable[CellCoordinates]] = {
    val colIndices = cells.map(_.columnIndex).toSet
    (0 until outerDimension).filter(colIndices.contains).map(getCellsInColumn)
  }

  final def getRowsContainingCells(cells: CellCoordinates*): Iterable[Iterable[CellCoordinates]] = {
    val rowIndices = cells.map(_.rowIndex).toSet
    (0 until outerDimension).filter(rowIndices.contains).map(getCellsInRow)
  }

  final def coordsToCellCoords(col: Int, row: Int): CellCoordinates = {
    ColumnRowIndexBasedCoordinates(col, row, innerDimension, outerDimension)
  }
}
