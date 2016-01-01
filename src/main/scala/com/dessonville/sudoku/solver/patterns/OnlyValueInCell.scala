package com.dessonville.sudoku.solver.patterns

import java.util.concurrent.atomic.AtomicInteger

import com.dessonville.sudoku.representation.SudokuGuesser
import com.dessonville.sudoku.solver.ReducingPattern

class OnlyValueInCell[R] extends ReducingPattern[R] {
  def reduce(guesser: SudokuGuesser[R]): Boolean = {
    var reduction = false

    guesser.forAllIndices(
      colIdx => {
        val remaining = guesser.allowedItems -- guesser.getColumn(colIdx).toSet
        val colMap = Map[R, AtomicInteger](
          remaining.map(_ -> new AtomicInteger()).toArray: _*
        )

        // TODO: Optimize this to store references and then work based off that instead of counting AtomicInteger
        // Figure out if there's only one occurrence
        guesser.forCellsInColumn(colIdx) {
          (colIdx, row) => {
            if (!guesser.isDetermined(colIdx, row)) {
              guesser.getPossibilities(colIdx, row).foreach {
                possibility => {
                  colMap.get(possibility).get.incrementAndGet()
                }
              }
            }
          }
        }

        // Find one occurrences and assign if that's the case
        val listR = colMap.toList.filter(_._2.get() == 1).map(_._1)
        if (listR.size > 0) {
          reduction = true
          listR.foreach {
            item => {
              guesser.forCellsInColumn(colIdx) {
                (colIdx, row) => {
                  if (guesser.getPossibilities(colIdx, row).contains(item)) {
                    guesser.setValueAndRemovePossibilities(colIdx, row, item)
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
