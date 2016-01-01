package com.dessonville.sudoku.representation

import com.dessonville.SudokuTestBase
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ColumnRowIndexBasedCoordinatesTest extends SudokuTestBase {
  "Column/Row Index-Based Cell Coordinates" should {
    "calculate indices correctly from column/row index (for 9x9 grid)" in {
      ColumnRowIndexBasedCoordinates(0, 0, 3, 9).cellIndex should be(0)
      ColumnRowIndexBasedCoordinates(1, 0, 3, 9).cellIndex should be(1)
      ColumnRowIndexBasedCoordinates(0, 1, 3, 9).cellIndex should be(9)
    }
  }
}
