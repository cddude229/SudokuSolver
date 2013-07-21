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
  final protected def forAllIndices(func: (Int) => ()) = (0 until outerDimension).foreach(func)
  final protected def forCellsInRow(row: Int)(func: (Int, Int) => ()) = forAllIndices(func(_, row))
  final protected def forCellsInColumn(col: Int)(func: (Int, Int) => ()) = forAllIndices(func(col, _))
  final protected def forCellsInBox(boxCol: Int, boxRow: Int)(func: (Int, Int) => ()) = {
    def lowToHigh(i: Int) = lowBoxIndex(i) to highBoxIndex(i)

    // TODO: I'm sure there's a more scala way to do this.
    // Probably some way similar to zip() creating tuples that you can iterate over
    lowToHigh(boxCol).foreach {
      col =>
        lowToHigh(boxRow).foreach {
          row =>
            func(col, row)
        }
    }
  }
}
