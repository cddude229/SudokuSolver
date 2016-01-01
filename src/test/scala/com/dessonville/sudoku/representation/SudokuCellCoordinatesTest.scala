package com.dessonville.sudoku.representation

import com.dessonville.SudokuTestBase
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class SudokuCellCoordinatesTest extends SudokuTestBase {
  "Sudoku Cell Coordinates" should {
    "calculate indices correctly from cellIndex (for 9x9 grid)" in {
      // Start by testing the four corners
      SudokuCellCoordinates(0, 3, 9).asTuple should be(0, 0, 0) // uppermost left
      SudokuCellCoordinates(8, 3, 9).asTuple should be(8, 0, 2) // uppermost right
      SudokuCellCoordinates(72, 3, 9).asTuple should be(0, 8, 6) // lowermost left
      SudokuCellCoordinates(80, 3, 9).asTuple should be(8, 8, 8) // lowermost right

      // Test one from the 5 remaining boxes
      SudokuCellCoordinates(13, 3, 9).asTuple should be(4, 1, 1)
      SudokuCellCoordinates(47, 3, 9).asTuple should be(2, 5, 3)
      SudokuCellCoordinates(30, 3, 9).asTuple should be(3, 3, 4)
      SudokuCellCoordinates(35, 3, 9).asTuple should be(8, 3, 5)
      SudokuCellCoordinates(59, 3, 9).asTuple should be(5, 6, 7)
    }
  }
}
