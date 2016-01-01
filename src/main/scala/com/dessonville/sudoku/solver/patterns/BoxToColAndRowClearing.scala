package com.dessonville.sudoku.solver.patterns

import com.dessonville.sudoku.representation.SudokuGuesser
import com.dessonville.sudoku.solver.ReducingPattern

/**
  * If the only set of a value is in a single row or column inside a box, remove it from all other cells in the row/column
  */
class BoxToColAndRowClearing[R] extends ReducingPattern[R] {
  type Coords = (Int, Int)

  override def reduce(guesser: SudokuGuesser[R]): Boolean = {
    var reduction = false

    guesser.forAllIndices {
      boxIndex =>
        val cellsInBox = guesser.getCellsInBox(boxIndex)
        val colsToReduce: Seq[Seq[Coords]] = guesser.getColumnsContainingCells(cellsInBox: _*)
        val rowsToReduce: Seq[Seq[Coords]] = guesser.getRowsContainingCells(cellsInBox: _*)
        reduction = reduceLines(guesser, cellsInBox, colsToReduce ++ rowsToReduce) || reduction
    }

    reduction
  }

  private def reduceLines(guesser: SudokuGuesser[R], cellsInBox: Seq[Coords], linesToReduce: Seq[Seq[Coords]]): Boolean = {
    val cellsContainingValue: Map[R, Set[Coords]] = cellsInBox.flatMap {
      cellCoords =>
        guesser.getPossibleValues(cellCoords._1, cellCoords._2).map {
          value => value -> cellCoords
        }
    }.groupBy(_._1).mapValues(_.map(_._2).toSet)

    var reduction = false

    linesToReduce.foreach {
      cellsInLine =>
        val cellsInBoxInLine = cellsInBox.filter(cellsInLine.contains)
        val cellsInLineNotInBox = cellsInLine.filterNot(cellsInBox.contains)

        val valuesInLineInBox: Set[R] = cellsInBoxInLine.flatMap(coords => guesser.getPossibleValues(coords._1, coords._2)).toSet

        // If the value in the box-line are only present in that line in the box, then we will remove it from all other cells not in the box-line
        val valuesToRemove = valuesInLineInBox.filter {
          value =>
            cellsContainingValue.get(value).get.forall(cellsInBoxInLine.contains)
        }

        cellsInLineNotInBox.foreach {
          cellCoords =>
            reduction = guesser.removePossibleValues(cellCoords._1, cellCoords._2, valuesToRemove)
        }
    }

    reduction
  }
}
