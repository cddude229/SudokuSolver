package com.dessonville.sudoku.solver.patterns

import java.util.concurrent.atomic.AtomicInteger

import com.dessonville.sudoku.representation.SudokuGuesser
import com.dessonville.sudoku.solver.ReducingPattern

/**
  * @author Chris (chris@sumologic.com)
  */
abstract class OnlyValueInSet[R] extends ReducingPattern[R] {
  protected def loadItem(guesser: SudokuGuesser[R], id: Int): Iterable[R]

  protected def forCellsInItem(guesser: SudokuGuesser[R], id: Int)(func: (Int, Int) => Unit): Unit

  def reduce(guesser: SudokuGuesser[R]): Boolean = {
    var reduction = false

    guesser.forAllIndices(
      itemId => {
        val remaining = guesser.allowedItems -- loadItem(guesser, itemId).toSet
        val itemMap = Map[R, AtomicInteger](
          remaining.map(_ -> new AtomicInteger()).toArray: _*
        )

        // TODO: Optimize this to store references and then work based off that instead of counting AtomicInteger
        // Figure out if there's only one occurrence
        forCellsInItem(guesser, itemId) {
          (colIdx, rowIdx) => {
            if (!guesser.isDetermined(colIdx, rowIdx)) {
              guesser.getPossibilities(colIdx, rowIdx).foreach {
                possibility => {
                  itemMap.get(possibility).get.incrementAndGet()
                }
              }
            }
          }
        }

        // Find one occurrences and assign if that's the case
        val listR = itemMap.toList.filter(_._2.get() == 1).map(_._1)
        if (listR.nonEmpty) {
          reduction = true
          listR.foreach {
            item => {
              forCellsInItem(guesser, itemId) {
                (colIdx, rowIdx) => {
                  if (guesser.getPossibilities(colIdx, rowIdx).contains(item)) {
                    guesser.setValueAndRemovePossibilities(colIdx, rowIdx, item)
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
