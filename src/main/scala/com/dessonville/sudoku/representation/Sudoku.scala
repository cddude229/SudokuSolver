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
trait Sudoku[R] {
  def allowedItems: Set[R]
  def emptyItem: R
  def innerDimension: Int
  def outerDimension: Int

  require(!allowedItems.contains(emptyItem), "Empty square item can not be the allowed item")
  require(allowedItems.size == outerDimension, "You must have enough allowed items to fill every spot")

  /**
   * Gets all the ints in the current row, left to right
   * @param row
   * @return
   */
  def getRow(row: Int): Iterable[R]

  /**
   * Gets all the ints in the current row, top to bottom
   * @param col
   * @return
   */
  def getColumn(col: Int): Iterable[R]

  /**
   * Gets all the ints in the current box, left to right, top to bottom
   * @param col
   * @param row
   * @return
   */
  def getBox(col: Int, row: Int): Iterable[Iterable[R]]

  /**
   * Get the items in a box, in order, but without enforced grid structure
   * @param idx
   * @return
   */
  def getBox(idx: Int): Iterable[R] = {
    val col = idx % (outerDimension / innerDimension)
    val row = idx / (outerDimension / innerDimension)
    getBox(col, row).flatten
  }

  /**
   * Set the value for a given spot
   * @param col
   * @param row
   * @param value
   */
  def setValue(col: Int, row: Int, value: R): Unit

  /**
   * Get the value for a given spot
   * @param col
   * @param row
   * @return
   */
  def getValue(col: Int, row: Int): R


  def getMissingItemsInRow(row: Int) = determineMissing(getRow(row))
  def getMissingItemsInColumn(col: Int) = determineMissing(getColumn(col))
  def getMissingItemsInBox(col: Int, row: Int) = determineMissing(getBox(col, row).reduce(_ ++ _))

  final protected def determineMissing(items: Iterable[R]): Set[R] = allowedItems -- items.toSet
  final protected def lowBoxIndex(rowOrCol: Int): Int = rowOrCol * innerDimension
  final protected def highBoxIndex(rowOrCol: Int): Int = lowBoxIndex(rowOrCol + 1) - 1 // Always one less than it

  /**
   * Gives a cell's coordinates, determine it's containing box's coordinates
   * @param cellCol
   * @param cellRow
   * @return
   */
  final protected def getBoxCoords(cellCol: Int, cellRow: Int): (Int, Int) = {
    (cellCol / innerDimension, cellRow / innerDimension)
  }

  /**
   * Lets you iterate from 0 to outerDimension easily
   * @param func Func to run.  Must take int and return unit
   */
  final def forAllIndices(func: Int => Unit) = (0 until outerDimension).foreach(func)
  final protected def forColAndRowRange(colRange: Iterable[Int], rowRange: Iterable[Int])(func: (Int, Int) => Unit){
    colRange.foreach {
      col => rowRange.foreach(func(col, _))
    }
  }
  final def forAllCells(func: (Int, Int) => Unit){
    forColAndRowRange(0 until outerDimension, 0 until outerDimension)(func)
  }
  final def forCellsInRow(row: Int)(func: (Int, Int) => Unit) = forAllIndices(func(_, row))
  final def forCellsInColumn(col: Int)(func: (Int, Int) => Unit) = forAllIndices(func(col, _))
  final def forCellsInBox(boxCol: Int, boxRow: Int)(func: (Int, Int) => Unit) = {
    def lowToHigh(i: Int) = lowBoxIndex(i) to highBoxIndex(i)

    forColAndRowRange(lowToHigh(boxCol), lowToHigh(boxRow))(func)
  }

  override def toString(): String = (0 until outerDimension).map(getRow(_).mkString(",")).mkString("\n")
}
