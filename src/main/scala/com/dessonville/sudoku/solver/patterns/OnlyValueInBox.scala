package com.dessonville.sudoku.solver.patterns

import java.util.concurrent.atomic.AtomicInteger

import com.dessonville.sudoku.representation.SudokuGuesser
import com.dessonville.sudoku.solver.ReducingPattern

// TODO: The OnlyValueIn* are all duplicated patterns.  We should reduce them to a single class.
class OnlyValueInBox[R] extends ReducingPattern[R] {
  def reduce(guesser: SudokuGuesser[R]): Boolean = {
    var reduction = false

    guesser.forAllIndices(
      boxIdx => {
        val remaining = guesser.allowedItems -- guesser.getBox(boxIdx).toSet
        val boxMap = Map[R, AtomicInteger](
          remaining.map(_ -> new AtomicInteger()).toArray: _*
        )

        // TODO: Optimize this to store references and then work based off that instead of counting AtomicInteger
        // Figure out if there's only one occurrence
        guesser.forCellsInBox(boxIdx) {
          (col, rowIdx) => {
            if (!guesser.isDetermined(col, rowIdx)) {
              guesser.getPossibilities(col, rowIdx).foreach {
                possibility => {
                  boxMap.get(possibility).get.incrementAndGet()
                }
              }
            }
          }
        }

        // Find one occurrences and assign if that's the case
        val listR = boxMap.toList.filter(_._2.get() == 1).map(_._1)
        if (listR.size > 0) {
          reduction = true
          listR.foreach {
            item => {
              guesser.forCellsInBox(boxIdx) {
                (col, rowIdx) => {
                  if (guesser.getPossibilities(col, rowIdx).contains(item)) {
                    guesser.setValueAndRemovePossibilities(col, rowIdx, item)
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
