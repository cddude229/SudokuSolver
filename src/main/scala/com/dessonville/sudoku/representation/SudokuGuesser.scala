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
    * @return If anything was removed, returns true
    */
  def removePossibilities(col: Int, row: Int, values: Set[R]): Boolean

  /**
    * Set the values for a cell and automatically removes the value from all items in the row, column, and box.
    * Will still remove possibilities if value was already set
    * @param col
    * @param row
    * @param value
    */
  def setValueAndRemovePossibilities(col: Int, row: Int, value: R): Unit = {
    setValue(col, row, value)
    removePossibilityFromCol(col, value)
    removePossibilityFromRow(row, value)
    val coords = getBoxCoords(col, row)
    removePossibilityFromBox(coords, value)
    removePossibilities(col, row, getPossibilities(col, row) - value) // Remove all but current value from this cell
  }

  /**
    * Remove a single possibility from a cell
    * @param col
    * @param row
    * @param value
    */
  def removePossibility(col: Int, row: Int, value: R) = removePossibilities(col, row, Set(value))

  /**
    * Remove a possibility from every cell in a row
    * @param row
    * @param value
    */
  def removePossibilityFromRow(row: Int, value: R) = forCellsInRow(row)(removePossibility(_, _, value))

  /**
    * Remove a possibility from every cell in a column
    * @param col
    * @param value
    */
  def removePossibilityFromCol(col: Int, value: R) = forCellsInColumn(col)(removePossibility(_, _, value))

  /**
    * Remove a possibility from every cell in a box
    * @param boxCol
    * @param boxRow
    * @param value
    */
  def removePossibilityFromBox(boxCol: Int, boxRow: Int, value: R): Unit = forCellsInBox(boxCol, boxRow)(removePossibility(_, _, value))

  /**
    * Removes a possibility from every cell in a box
    * @param boxCoords Coordinate tuple of (col, row)
    * @param value
    */
  def removePossibilityFromBox(boxCoords: (Int, Int), value: R): Unit = removePossibilityFromBox(boxCoords._1, boxCoords._2, value)

  /**
    * Is this cell's value already determined?
    * @param col
    * @param row
    * @return
    */
  def isDetermined(col: Int, row: Int): Boolean = getValue(col, row) != emptyItem

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
    var score = 0

    forAllCells {
      case (colIdx, rowIdx) =>
        if (getValue(colIdx, rowIdx) == emptyItem) {
          score += getPossibilities(colIdx, rowIdx).size
        }
    }

    score
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
      val squaresList = 0 until 3 // 0 to (outerDimension / innerDimension)
      val outerList = 0 until outerDimension
      // Note: This doesn't scale to more than 3 items
      outerList.foreach {
        row => {
          var row1 = List[String]()
          var row2 = List[String]()
          var row3 = List[String]()
          outerList.foreach {
            col => {
              val set = getPossibilities(col, row)
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
                val value = getValue(col, row).toString
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
