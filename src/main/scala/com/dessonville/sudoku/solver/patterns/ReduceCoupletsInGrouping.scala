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
abstract class ReduceCoupletsInGrouping[Value](N: Int) extends ReducingPattern[Value] with PerGroupingHandler[Value] {
  override def reduce(guesser: SudokuGuesser[Value]): Boolean = {
    var reduction = false

    guesser.getAllIndices.foreach(
      groupingId => {
        val possibilitiesInGroupMap = mutable.Map[Set[Value], AtomicInteger]()

        // Iterate over each cell, and record the possible values of those items.
        cellsInGrouping(guesser, groupingId).foreach {
          cellCoordinates => {
            if (!guesser.isDetermined(cellCoordinates)) {
              val possibleValues = guesser.getPossibleValues(cellCoordinates)
              if (possibleValues.size == N) {
                possibilitiesInGroupMap.getOrElseUpdate(possibleValues, new AtomicInteger()).incrementAndGet()
              }
            }
          }
        }

        // Find N occurrences and assign if that's the case
        val listOfSizeNItems = possibilitiesInGroupMap.toList.filter(_._2.get() == N).map(_._1)
        if (listOfSizeNItems.nonEmpty) {
          listOfSizeNItems.foreach {
            possibilitiesSet => {
              cellsInGrouping(guesser, groupingId).foreach {
                cellCoordinates => {
                  val cellPossibleValues = guesser.getPossibleValues(cellCoordinates)

                  if (cellPossibleValues != possibilitiesSet) {
                    if (guesser.removePossibleValues(cellCoordinates, possibilitiesSet)) {
                      println(s"${this.getClass.getSimpleName} - Removed $possibilitiesSet from $cellCoordinates (which has $cellPossibleValues)")
                      reduction = true
                    }
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

class ReduceCoupletsInBox[Value](N: Int) extends ReduceCoupletsInGrouping[Value](N) with PerBoxHandler[Value]

class ReduceCoupletsInColumn[Value](N: Int) extends ReduceCoupletsInGrouping[Value](N) with PerColumnHandler[Value]

class ReduceCoupletsInRow[Value](N: Int) extends ReduceCoupletsInGrouping[Value](N) with PerRowHandler[Value]
