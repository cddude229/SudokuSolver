package com.dessonville.sudoku.representation

/**
  * Used to identify a cell inside a Sudoku
  */
trait CellCoordinates {

  /**
    * Overall index of this cell.  Increases horizontally, then vertically:
    *
    * 00 01 02 03 04 05 06 07 08
    * 09 10 11 12 13 14 15 16 17
    * 18 19 20 21 22 23 24 25 26
    * 27 28 29 30 31 32 33 34 35
    * 36 37 38 39 40 41 42 43 44
    * 45 46 47 48 49 50 51 52 53
    * 54 55 56 57 58 59 60 61 62
    * 63 64 65 66 67 68 69 70 71
    * 72 73 74 75 76 77 78 79 80
    *
    * (NOTE: Those exact numbers are only accurate for 9x9 grids)
    * @return
    */
  def cellIndex: Int

  /**
    * Index of the column this cell belongs to.  Increases top to bottom.
    * @return
    */
  def columnIndex: Int

  /**
    * Index of the row this cell belongs to.  Increases left to right.
    * @return
    */
  def rowIndex: Int

  /**
    * Index of the box this cell belongs to.  Increases horizontally, then vertically:
    *
    * 0 1 2
    * 3 4 5
    * 6 7 8
    *
    * (NOTE: Those exact numbers are only accurate for 3x3 grids)
    * @return
    */
  def boxIndex: Int

  /**
    * Tuple of (columnIndex, rowIndex, boxIndex)
    * @return
    */
  def asTuple: (Int, Int, Int) = (columnIndex, rowIndex, boxIndex)

}

object CellCoordinates {
  def boxIndexFromColumnAndRow(columnIndex: Int, rowIndex: Int, boxWidth: Int, gridWidth: Int): Int = {
    (columnIndex / boxWidth) + ((rowIndex / boxWidth) * (gridWidth / boxWidth))
  }
}

case class CellIndexBasedCoordinates(override val cellIndex: Int, private val boxWidth: Int,
                                     private val gridWidth: Int) extends CellCoordinates {
  override val columnIndex = cellIndex % gridWidth

  override val rowIndex = cellIndex / gridWidth

  override val boxIndex = CellCoordinates.boxIndexFromColumnAndRow(columnIndex, rowIndex, boxWidth, gridWidth)
}

case class ColumnRowIndexBasedCoordinates(override val columnIndex: Int, override val rowIndex: Int,
                                          private val boxWidth: Int, private val gridWidth: Int)
  extends CellCoordinates {

  override val cellIndex: Int = columnIndex + (rowIndex * gridWidth)

  override val boxIndex: Int = CellCoordinates.boxIndexFromColumnAndRow(columnIndex, rowIndex, boxWidth, gridWidth)
}
