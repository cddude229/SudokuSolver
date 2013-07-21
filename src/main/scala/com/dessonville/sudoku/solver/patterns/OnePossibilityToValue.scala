package com.dessonville.sudoku.solver.patterns

import com.dessonville.sudoku.solver.ReducingPattern
import com.dessonville.sudoku.representation.SudokuGuesser

class OnePossibilityToValue[R] extends ReducingPattern[R] {
  def reduce(guesser: SudokuGuesser[R]): Boolean = {
    var reduction = false
    guesser.forAllCells {
      (col, row) => {
        if(!guesser.isDetermined(col, row)){
          val possibilities: Set[R] = guesser.getPossibilities(col, row)
          if(possibilities.size == 1){
            val value: R = possibilities.head
            guesser.setValueAndRemovePossibilities(col, row, value) // Re-setting the value will remove it
            reduction = true
          }
        }
      }
    }
    reduction
  }
}
