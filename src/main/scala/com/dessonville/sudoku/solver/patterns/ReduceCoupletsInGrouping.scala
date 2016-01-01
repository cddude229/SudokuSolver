package com.dessonville.sudoku.solver.patterns

import java.util.concurrent.atomic.AtomicInteger

import com.dessonville.sudoku.representation.SudokuGuesser
import com.dessonville.sudoku.solver.groupings.{PerBoxHandler, PerColumnHandler, PerRowHandler}
import com.dessonville.sudoku.solver.{PerGroupingHandler, ReducingPattern}

import scala.collection.mutable

/**
  * If N cells in a grouping have the exact same N values, then remove those values from all other cells
  *
  * NOTE: There could be improvements to this where the same values are only present in certain squares, despite other
  * values being present.  i.e. (3,5) is only present in two cells BUT one cell is (3,4,5), so this doesn't work for all cases
  *
  * TODO: Once the NOTE above is solved, then the OnlyValue case should be the N=1 case of this code.
  */
abstract class ReduceCoupletsInGrouping[R](N: Int) extends ReducingPattern[R] with PerGroupingHandler[R] {
  override def reduce(guesser: SudokuGuesser[R]): Boolean = {
    var reduction = false

    guesser.mapAllIndices(
      groupingId => {
        val possibilitiesInGroupMap = mutable.Map[Set[R], AtomicInteger]()

        // Iterate over each cell, and record the recordings of those items.
        forCellsInGrouping(guesser, groupingId) {
          (colIdx, rowIdx) => {
            if (!guesser.isDetermined(colIdx, rowIdx)) {
              val totalPossibilities = guesser.getPossibleValues(colIdx, rowIdx)
              if (totalPossibilities.size == N) {
                possibilitiesInGroupMap.getOrElseUpdate(totalPossibilities, new AtomicInteger()).incrementAndGet()
              }
            }
          }
        }

        // Find one occurrences and assign if that's the case
        val listOfSizeNItems = possibilitiesInGroupMap.toList.filter(_._2.get() == N).map(_._1)
        if (listOfSizeNItems.nonEmpty) {
          listOfSizeNItems.foreach {
            possibilitiesSet => {
              forCellsInGrouping(guesser, groupingId) {
                (colIdx, rowIdx) => {
                  if (guesser.getPossibleValues(colIdx, rowIdx) != possibilitiesSet) {
                    reduction = guesser.removePossibleValues(colIdx, rowIdx, possibilitiesSet) || reduction
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

class ReduceCoupletsInBox[R](N: Int) extends ReduceCoupletsInGrouping[R](N) with PerBoxHandler[R]

class ReduceCoupletsInColumn[R](N: Int) extends ReduceCoupletsInGrouping[R](N) with PerColumnHandler[R]

class ReduceCoupletsInRow[R](N: Int) extends ReduceCoupletsInGrouping[R](N) with PerRowHandler[R]
