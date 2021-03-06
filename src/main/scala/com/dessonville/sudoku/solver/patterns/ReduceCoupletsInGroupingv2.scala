package com.dessonville.sudoku.solver.patterns

import com.dessonville.sudoku.representation.{CellCoordinates, SudokuGuesser}
import com.dessonville.sudoku.solver.groupings.{PerBoxHandler, PerColumnHandler, PerRowHandler}
import com.dessonville.sudoku.solver.{PerGroupingHandler, ReducingPattern}

/**
  * This improves on V1 by fixing the following condition:  The couplets (1,3), (1,3,7), and (1,3,7) would not be
  * grouped as a couplet, because it required an exact match.
  *
  * If N cells in a grouping contain only N values, then remove those values from all other cells
  *
  * NOTE: Like v1, this is still vulnerable to extra values in the cell.
  */
abstract class ReduceCoupletsInGroupingV2[Value](N: Int) extends ReducingPattern[Value] with PerGroupingHandler[Value] {
  override def reduce(guesser: SudokuGuesser[Value]): Unit = {
    guesser.getAllIndices.foreach {
      groupingIndex =>
        handleGrouping(guesser, cellsInGrouping(guesser, groupingIndex))
    }
  }

  private def handleGrouping(guesser: SudokuGuesser[Value], cells: Iterable[CellCoordinates]): Unit = {
    val unsolvedCells = cells.filterNot(guesser.isDetermined)
    val combinations = unsolvedCells.toSeq.combinations(N)

    combinations.foreach {
      cellsInCombination =>
        // If there are only N values in the set of N cells, then we can remove them from all other cells
        val allValues = cellsInCombination.flatMap(guesser.getPossibleValues).toSet

        if (allValues.size == N) {
          val remainingCells = unsolvedCells.filterNot(cellsInCombination.contains)
          remainingCells.foreach {
            cell =>
              val previousValues = guesser.getPossibleValues(cell)
              if (guesser.removePossibleValues(cell, allValues)) {
                println(s"${this.getClass.getSimpleName}($N) - Removed $allValues from $cell (which has $previousValues)")
              }
          }
        }

    }
  }
}

class ReduceCoupletsInBoxV2[Value](N: Int) extends ReduceCoupletsInGroupingV2[Value](N) with PerBoxHandler[Value]

class ReduceCoupletsInColumnV2[Value](N: Int) extends ReduceCoupletsInGroupingV2[Value](N) with PerColumnHandler[Value]

class ReduceCoupletsInRowV2[Value](N: Int) extends ReduceCoupletsInGroupingV2[Value](N) with PerRowHandler[Value]
