package com.dessonville.sudoku.solver.patterns

import com.dessonville.sudoku.representation.{CellCoordinates, SudokuGuesser}
import com.dessonville.sudoku.solver.ReducingPattern

/**
  * If the only location of a value in a row/column is in a single box, then remove it from everywhere else in that box.
  */
class ColAndRowToBoxClearing[Value] extends ReducingPattern[Value] {
  override def reduce(guesser: SudokuGuesser[Value]): Unit = {
    guesser.getAllIndices.foreach {
      lineIndex =>
        reduceLine(guesser, guesser.getCellsInRow(lineIndex))
        reduceLine(guesser, guesser.getCellsInColumn(lineIndex))
    }
  }

  private def reduceLine(guesser: SudokuGuesser[Value], lineToReduce: Iterable[CellCoordinates]): Unit = {
    // Figure out the cells in the line in which the Value is present
    val valueToCells: Map[Value, Iterable[CellCoordinates]] = guesser.allowedCellValues.map {
      allowedValue =>
        allowedValue -> lineToReduce.filter {
          cellCoordinates => guesser.getPossibleValues(cellCoordinates).contains(allowedValue)
        }
    }.toMap

    // Find the values that are only in a single box, so that we can strip them from the other parts of the box
    val valuesInOnlyOneBox: Map[Value, Iterable[CellCoordinates]] = valueToCells.filter {
      case (value, cellsWithValue) =>
        cellsWithValue.map(_.boxIndex).toSet.size == 1
    }

    // Now, remove the values from the other cells in the boxes
    valuesInOnlyOneBox.foreach {
      case (value, cellsWithValue) =>
        val boxIndex = cellsWithValue.head.boxIndex
        val cellsInBox = guesser.getCellsInBox(boxIndex)
        val cellsToRemoveFrom = cellsInBox.filterNot(cellsWithValue.toSeq.contains)
        cellsToRemoveFrom.foreach {
          cellToRemove =>
            guesser.removePossibleValue(cellToRemove, value)
        }
    }
  }
}
