package com.dessonville.sudoku.representation

import com.dessonville.SudokuTestBase
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class CellIndexBasedCoordinatesTest extends SudokuTestBase {
  "Cell Index-Based Cell Coordinates" should {
    "calculate indices correctly from cellIndex (for 9x9 grid)" in {
      // Start by testing the four corners
      CellIndexBasedCoordinates(0, 3, 9).asTuple should be(0, 0, 0) // uppermost left
      CellIndexBasedCoordinates(8, 3, 9).asTuple should be(8, 0, 2) // uppermost right
      CellIndexBasedCoordinates(72, 3, 9).asTuple should be(0, 8, 6) // lowermost left
      CellIndexBasedCoordinates(80, 3, 9).asTuple should be(8, 8, 8) // lowermost right

      // Test one from the 5 remaining boxes
      CellIndexBasedCoordinates(13, 3, 9).asTuple should be(4, 1, 1)
      CellIndexBasedCoordinates(47, 3, 9).asTuple should be(2, 5, 3)
      CellIndexBasedCoordinates(30, 3, 9).asTuple should be(3, 3, 4)
      CellIndexBasedCoordinates(35, 3, 9).asTuple should be(8, 3, 5)
      CellIndexBasedCoordinates(59, 3, 9).asTuple should be(5, 6, 7)
    }
  }
}
