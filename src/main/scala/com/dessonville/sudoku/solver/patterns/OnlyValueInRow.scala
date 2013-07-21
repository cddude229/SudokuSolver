package com.dessonville.sudoku.solver.patterns

import com.dessonville.sudoku.solver.ReducingPattern
import com.dessonville.sudoku.representation.SudokuGuesser
import java.util.concurrent.atomic.AtomicInteger

class OnlyValueInRow[R] extends ReducingPattern[R] {
  def reduce(guesser: SudokuGuesser[R]): Boolean = {
    var reduction = false

    guesser.forAllIndices (
      rowIdx => {
        val remaining = guesser.allowedItems -- guesser.getRow(rowIdx).toSet
        val rowMap = Map[R, AtomicInteger](
          remaining.map(_ -> new AtomicInteger()).toArray: _*
        )

        // TODO: Optimize this to store references and then work based off that instead of counting AtomicInteger
        // Figure out if there's only one occurrence
        guesser.forCellsInRow(rowIdx){
          (col, rowIdx) => {
            if(!guesser.isDetermined(col, rowIdx)){
              guesser.getPossibilities(col, rowIdx).foreach {
                possibility => {
                  rowMap.get(possibility).get.incrementAndGet()
                }
              }
            }
          }
        }

        // Find one occurrences and assign if that's the case
        val listR = rowMap.toList.filter(_._2.get() == 1).map(_._1)
        if(listR.size > 0){
          reduction = true
          listR.foreach {
            item => {
              guesser.forCellsInRow(rowIdx){
                (col, rowIdx) => {
                  if(guesser.getPossibilities(col, rowIdx).contains(item)){
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
