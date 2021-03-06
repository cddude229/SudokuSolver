package com.dessonville.sudoku.solver.patterns

import com.dessonville.sudoku.representation.{CellCoordinates, SudokuGuesser}
import com.dessonville.sudoku.solver.ReducingPattern

/**
  * If the only set of a value is in a single row or column inside a box, remove it from all other cells in the row/column
  */
class BoxToColAndRowClearing[Value] extends ReducingPattern[Value] {
  override def reduce(guesser: SudokuGuesser[Value]): Unit = {
    guesser.getAllIndices.foreach {
      boxIndex =>
        val cellsInBox = guesser.getCellsInBox(boxIndex).toSeq
        val colsToReduce = guesser.getColumnsContainingCells(cellsInBox: _*)
        val rowsToReduce = guesser.getRowsContainingCells(cellsInBox: _*)
        reduceLines(guesser, cellsInBox, colsToReduce ++ rowsToReduce)
    }
  }

  private def reduceLines(guesser: SudokuGuesser[Value], cellsInBox: Iterable[CellCoordinates], linesToReduce: Iterable[Iterable[CellCoordinates]]): Unit = {
    val cellsContainingValue: Map[Value, Set[CellCoordinates]] = cellsInBox.flatMap {
      cellCoords =>
        guesser.getPossibleValues(cellCoords).map {
          value => value -> cellCoords
        }
    }.groupBy(_._1).view.mapValues(_.map(_._2).toSet).toMap


    linesToReduce.foreach {
      cellsInLine =>
        // TOOD: .toSet.contains every time is not very efficient
        val cellsInBoxInLine = cellsInBox.filter(cellsInLine.toSet.contains)
        val cellsInLineNotInBox = cellsInLine.filterNot(cellsInBox.toSet.contains)

        val valuesInLineInBox: Set[Value] = cellsInBoxInLine.flatMap(coords => guesser.getPossibleValues(coords)).toSet

        // If the value in the box-line are only present in that line in the box, then we will remove it from all other cells not in the box-line
        val valuesToRemove = valuesInLineInBox.filter {
          value =>
            cellsContainingValue.get(value).get.forall(cellsInBoxInLine.toSet.contains)
        }

        cellsInLineNotInBox.foreach {
          cellCoords =>
            guesser.removePossibleValues(cellCoords, valuesToRemove)
        }
    }

  }
}
