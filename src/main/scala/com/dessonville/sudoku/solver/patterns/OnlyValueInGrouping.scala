package com.dessonville.sudoku.solver.patterns

import java.util.concurrent.atomic.AtomicInteger

import com.dessonville.sudoku.representation.SudokuGuesser
import com.dessonville.sudoku.solver.groupings.{PerBoxHandler, PerColumnHandler, PerRowHandler}
import com.dessonville.sudoku.solver.{PerGroupingHandler, ReducingPattern}

/**
  * There are groupings (row, column, box) where if a single cell is the only cell that could possibly containing the
  * value, then we should set the value of that cell to that sole possibility.
  */
abstract class OnlyValueInGrouping[R] extends ReducingPattern[R] with PerGroupingHandler[R] {
  def reduce(guesser: SudokuGuesser[R]): Boolean = {
    var reduction = false

    guesser.mapAllIndices(
      groupingId => {
        val remaining = guesser.allowedCellValues -- loadUsedItemsInGrouping(guesser, groupingId).toSet
        val possibilitiesInGroupMap = Map[R, AtomicInteger](
          remaining.map(_ -> new AtomicInteger()).toArray: _*
        )

        // TODO: Optimize this to store references and then work based off that instead of counting AtomicInteger
        // Figure out if there's only one occurrence
        forCellsInGrouping(guesser, groupingId) {
          (colIdx, rowIdx) => {
            if (!guesser.isDetermined(colIdx, rowIdx)) {
              guesser.getPossibleValues(colIdx, rowIdx).foreach {
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
          reduction = true
          listOfSizeOneItems.foreach {
            item => {
              forCellsInGrouping(guesser, groupingId) {
                (colIdx, rowIdx) => {
                  if (guesser.getPossibleValues(colIdx, rowIdx).contains(item)) {
                    guesser.setValueAndRemovePossibleValue(colIdx, rowIdx, item)
                  }
                }
              }
            }
          }
        }
      }
    )

    reduction
  }

}

class OnlyValueInBox[R] extends OnlyValueInGrouping[R] with PerBoxHandler[R]

class OnlyValueInColumn[R] extends OnlyValueInGrouping[R] with PerColumnHandler[R]

class OnlyValueInRow[R] extends OnlyValueInGrouping[R] with PerRowHandler[R]
