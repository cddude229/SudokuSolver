package com.dessonville.sudoku.representation

/**
 * Typically used to wrap a sudoku.  Used to record known information while analyzing a sudoku
 */
trait SudokuGuesser[R] extends Sudoku[R] {
  /**
   * Get the possibilities for a cell
   * @param col
   * @param row
   * @return
   */
  def getPossibilities(col: Int, row: Int): Set[R]

  /**
   * Remove a set of possibilities from a cell
   * @param col
   * @param row
   * @param values
   */
  def removePossibilities(col: Int, row: Int, values: Set[R]): Unit

  /**
   * Set the values for a cell and automatically removes the value from all items in the row, column, and box
   * @param col
   * @param row
   * @param value
   */
  def setValueAndRemovePossibilities(col: Int, row: Int, value: R): Unit

  /**
   * Remove a single possibility from a cell
   * @param col
   * @param row
   * @param value
   */
  def removePossibility(col: Int, row: Int, value: R) = removePossibilities(col, row, Set(value))

  def removePossibilityFromRow(row: Int, value: R) = forCellsInRow(row)(removePossibility(_, _, value))
  def removePossibilityFromCol(col: Int, value: R) = forCellsInColumn(col)(removePossibility(_, _, value))
  def removePossibilityFromBox(boxCol: Int, boxRow: Int, value: R) = forCellsInBox(boxCol, boxRow)(removePossibility(_, _, value))

  /**
   * Is this cell's value already determined?
   * @param col
   * @param row
   * @return
   */
  def isDetermined(col: Int, row: Int): Boolean = getValue(col, row) != emptyItem
}
