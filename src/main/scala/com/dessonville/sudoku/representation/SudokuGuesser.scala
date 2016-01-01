package com.dessonville.sudoku.representation

/**
  * Typically used to wrap a sudoku.  Used to record known information while analyzing a sudoku
  */
trait SudokuGuesser[Value] extends Sudoku[Value] {
  /**
    * Get the possibilities for a cell
    * @param cellCoordinates
    * @return
    */
  def getPossibleValues(cellCoordinates: CellCoordinates): Set[Value]

  /**
    * Remove a set of possibilities from a cell
    * @param cellCoordinates
    * @param values
    * @return If anything was removed, returns true
    */
  def removePossibleValues(cellCoordinates: CellCoordinates, values: Set[Value]): Boolean

  /**
    * Set the values for a cell and automatically removes the value from all items in the row, column, and box.
    * Will still remove possibilities if value was already set
    * @param cellCoordinates
    * @param value
    */
  def setValueAndRemovePossibleValue(cellCoordinates: CellCoordinates, value: Value): Unit = {
    val col = cellCoordinates.columnIndex
    val row = cellCoordinates.rowIndex

    setCellValue(cellCoordinates, value)
    removePossibleValueFromColumn(col, value)
    removePossibleValueFromRow(row, value)
    val coords = boxCoordsContainingCell(cellCoordinates)
    removePossibleValueFromBox(coords, value)
    removePossibleValues(cellCoordinates, getPossibleValues(cellCoordinates) - value) // Remove all but current value from this cell
  }

  /**
    * Remove a single possibility from a cell
    * @param cellCoordinates
    * @param value
    */
  def removePossibleValue(cellCoordinates: CellCoordinates, value: Value): Unit = removePossibleValues(cellCoordinates, Set(value))

  /**
    * Remove a possibility from every cell in a row
    * @param row
    * @param value
    */
  def removePossibleValueFromRow(row: Int, value: Value): Unit = getCellsInRow(row).foreach(removePossibleValue(_, value))

  /**
    * Remove a possibility from every cell in a column
    * @param col
    * @param value
    */
  def removePossibleValueFromColumn(col: Int, value: Value): Unit = getCellsInColumn(col).foreach(removePossibleValue(_, value))

  /**
    * Remove a possibility from every cell in a box
    * @param boxCol
    * @param boxRow
    * @param value
    */
  def removePossibleValueFromBox(boxCol: Int, boxRow: Int, value: Value): Unit = getCellsInBox(boxCol, boxRow).foreach(removePossibleValue(_, value))

  /**
    * Removes a possibility from every cell in a box
    * @param boxCoords Coordinate tuple of (col, row)
    * @param value
    */
  def removePossibleValueFromBox(boxCoords: (Int, Int), value: Value): Unit = removePossibleValueFromBox(boxCoords._1, boxCoords._2, value)

  /**
    * Is this cell's value already determined?
    * @param cellCoordinates
    * @return
    */
  def isDetermined(cellCoordinates: CellCoordinates): Boolean = getCellValue(cellCoordinates) != emptyCellValue

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
    getAllCells.flatten.map {
      cellCoordinates =>
        if (getCellValue(cellCoordinates) == emptyCellValue) {
          getPossibleValues(cellCoordinates).size
        } else {
          0
        }
    }.sum
  }

  override def toString(): String = {
    if (isSolved()) {
      super.toString()
    } else {
      def check(set: Set[Value], i: Int): String = {
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
              val coords = coordsToCellCoords(col, row)
              val set = getPossibleValues(coords)
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
                val value = getCellValue(coords).toString
                row1 :+= s"     "
                row2 :+= s"  $value  "
                row3 :+= s"     "
              }
            }
          }

          val divider = "=====" * 9 + "======" * 8
          totalSet += row1.mkString("  ||  ") + "\n"
          totalSet += row2.mkString("  ||  ") + "\n"
          totalSet += row3.mkString("  ||  ") + s"\n$divider\n"
        }

      }
      totalSet
    }
  }
}
