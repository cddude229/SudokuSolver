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
  def getPossibleValues(col: Int, row: Int): Set[R]

  /**
    * Get the possibilities for a cell
    * @param cellCoordinates
    * @return
    */
  def getPossibleValues(cellCoordinates: CellCoordinates): Set[R] = getPossibleValues(cellCoordinates.columnIndex, cellCoordinates.rowIndex)

  /**
    * Remove a set of possibilities from a cell
    * @param col
    * @param row
    * @param values
    * @return If anything was removed, returns true
    */
  def removePossibleValues(col: Int, row: Int, values: Set[R]): Boolean

  /**
    * Remove a set of possibilities from a cell
    * @param cellCoordinates
    * @param values
    * @return If anything was removed, returns true
    */
  def removePossibleValues(cellCoordinates: CellCoordinates, values: Set[R]): Boolean = {
    removePossibleValues(cellCoordinates.columnIndex, cellCoordinates.rowIndex, values)
  }

  /**
    * Set the values for a cell and automatically removes the value from all items in the row, column, and box.
    * Will still remove possibilities if value was already set
    * @param col
    * @param row
    * @param value
    */
  def setValueAndRemovePossibleValue(col: Int, row: Int, value: R): Unit = {
    setCellValue(col, row, value)
    removePossibleValueFromColumn(col, value)
    removePossibleValueFromRow(row, value)
    val coords = boxCoordsContainingCell(col, row)
    removePossibleValueFromBox(coords, value)
    removePossibleValues(col, row, getPossibleValues(col, row) - value) // Remove all but current value from this cell
  }

  /**
    * Set the values for a cell and automatically removes the value from all items in the row, column, and box.
    * Will still remove possibilities if value was already set
    * @param cellCoordinates
    * @param value
    */
  def setValueAndRemovePossibleValue(cellCoordinates: CellCoordinates, value: R): Unit = {
    setValueAndRemovePossibleValue(cellCoordinates.columnIndex, cellCoordinates.rowIndex, value)
  }

  /**
    * Remove a single possibility from a cell
    * @param col
    * @param row
    * @param value
    */
  def removePossibleValue(col: Int, row: Int, value: R) = removePossibleValues(col, row, Set(value))

  /**
    * Remove a single possibility from a cell
    * @param cellCoordinates
    * @param value
    */
  def removePossibleValue(cellCoordinates: CellCoordinates, value: R) = removePossibleValues(cellCoordinates.columnIndex, cellCoordinates.rowIndex, Set(value))

  /**
    * Remove a possibility from every cell in a row
    * @param row
    * @param value
    */
  def removePossibleValueFromRow(row: Int, value: R) = mapCellsInRow(row)(removePossibleValue(_, value))

  /**
    * Remove a possibility from every cell in a column
    * @param col
    * @param value
    */
  def removePossibleValueFromColumn(col: Int, value: R) = mapCellsInColumn(col)(removePossibleValue(_, value))

  /**
    * Remove a possibility from every cell in a box
    * @param boxCol
    * @param boxRow
    * @param value
    */
  def removePossibleValueFromBox(boxCol: Int, boxRow: Int, value: R): Unit = mapCellsInBox(boxCol, boxRow)(removePossibleValue(_, value))

  /**
    * Removes a possibility from every cell in a box
    * @param boxCoords Coordinate tuple of (col, row)
    * @param value
    */
  def removePossibleValueFromBox(boxCoords: (Int, Int), value: R): Unit = removePossibleValueFromBox(boxCoords._1, boxCoords._2, value)

  /**
    * Is this cell's value already determined?
    * @param col
    * @param row
    * @return
    */
  def isDetermined(col: Int, row: Int): Boolean = getCellValue(col, row) != emptyCellValue

  /**
    * Determine if the puzzle is solved
    * @return
    */
  def isSolved(): Boolean

  /**
    * Determine if the current sudoku is correct
    * @return
    */
  def isCorrect(): Boolean

  /**
    * The number of possibilities still remaining in the puzzle. 0 = solved.
    * @return
    */
  def solvedScore(): Int = {
    mapAllCells {
      cellCoordinates =>
        if (getCellValue(cellCoordinates) == emptyCellValue) {
          getPossibleValues(cellCoordinates).size
        } else {
          0
        }
    }.flatten.sum
  }

  override def toString(): String = {
    if (isSolved()) {
      super.toString()
    } else {
      def check(set: Set[R], i: Int): String = {
        if (set.asInstanceOf[Set[Int]].contains(i)) {
          i.toString
        } else {
          " "
        }
      }
      var totalSet = ""
      val outerList = 0 until outerDimension
      // Note: This doesn't scale to more than 3 items
      outerList.foreach {
        row => {
          var row1 = List[String]()
          var row2 = List[String]()
          var row3 = List[String]()
          outerList.foreach {
            col => {
              val set = getPossibleValues(col, row)
              if (set.size > 1) {
                val one = check(set, 1)
                val two = check(set, 2)
                val three = check(set, 3)
                val four = check(set, 4)
                val five = check(set, 5)
                val six = check(set, 6)
                val seven = check(set, 7)
                val eight = check(set, 8)
                val nine = check(set, 9)

                row1 :+= s"$one $two $three"
                row2 :+= s"$four $five $six"
                row3 :+= s"$seven $eight $nine"
              } else {
                val value = getCellValue(col, row).toString
                row1 :+= s"     "
                row2 :+= s"  $value  "
                row3 :+= s"     "
              }
            }
          }

          totalSet += row1.mkString("    ") + "\n"
          totalSet += row2.mkString("    ") + "\n"
          totalSet += row3.mkString("    ") + "\n\n\n"
        }

      }
      totalSet
    }
  }
}
