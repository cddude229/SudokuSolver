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
   * Set the value for a given spot
   * @param col
   * @param row
   * @param value
   */
  def setValue(col: Int, row: Int, value: R): Unit


  def getMissingItemsInRow(row: Int) = determineMissing(getRow(row))
  def getMissingItemsInColumn(col: Int) = determineMissing(getColumn(col))
  def getMissingItemsInBox(col: Int, row: Int) = determineMissing(getBox(col, row).reduce(_ ++ _))

  final protected def determineMissing(items: Iterable[R]): Set[R] = allowedItems -- items.toSet
  final protected def lowBoxIndex(rowOrCol: Int): Int = rowOrCol * innerDimension
  final protected def highBoxIndex(rowOrCol: Int): Int = lowBoxIndex(rowOrCol + 1) - 1 // Always one less than it
}
