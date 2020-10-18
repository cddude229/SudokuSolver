package com.dessonville.sudoku.solver.patterns

import java.util.concurrent.atomic.AtomicInteger

import com.dessonville.sudoku.representation.SudokuGuesser
import com.dessonville.sudoku.solver.groupings.{PerBoxHandler, PerColumnHandler, PerRowHandler}
import com.dessonville.sudoku.solver.{PerGroupingHandler, ReducingPattern}

/**
  * There are groupings (row, column, box) where if a single cell is the only cell that could possibly containing the
  * value, then we should set the value of that cell to that sole possibility.
  */
abstract class OnlyValueInGrouping[Value] extends ReducingPattern[Value] with PerGroupingHandler[Value] {
  def reduce(guesser: SudokuGuesser[Value]): Unit = {
    guesser.getAllIndices.foreach(
      groupingId => {
        val remaining = guesser.allowedCellValues -- loadUsedItemsInGrouping(guesser, groupingId).toSet
        val possibilitiesInGroupMap = Map[Value, AtomicInteger](
          remaining.map(_ -> new AtomicInteger()).toSeq: _*
        )

        // TODO: Optimize this to store references and then work based off that instead of counting AtomicInteger
        // Figure out if there's only one occurrence
        cellsInGrouping(guesser, groupingId).foreach {
          cellCoordinates => {
            if (!guesser.isDetermined(cellCoordinates)) {
              guesser.getPossibleValues(cellCoordinates).foreach {
                possibility => {
                  possibilitiesInGroupMap.get(possibility).get.incrementAndGet()
                }
              }
            }
          }
        }

        // Find one occurrences and assign if that's the case
        val listOfSizeOneItems = possibilitiesInGroupMap.toList.filter(_._2.get() == 1).map(_._1)
        if (listOfSizeOneItems.nonEmpty) {
          listOfSizeOneItems.foreach {
            item => {
              cellsInGrouping(guesser, groupingId).foreach {
                cellCoordinates => {
                  if (guesser.getPossibleValues(cellCoordinates).contains(item)) {
                    guesser.setValueAndRemovePossibleValue(cellCoordinates, item)
                  }
                }
              }
            }
          }
        }
      }
    )
  }

}

class OnlyValueInBox[Value] extends OnlyValueInGrouping[Value] with PerBoxHandler[Value]

class OnlyValueInColumn[Value] extends OnlyValueInGrouping[Value] with PerColumnHandler[Value]

class OnlyValueInRow[Value] extends OnlyValueInGrouping[Value] with PerRowHandler[Value]
