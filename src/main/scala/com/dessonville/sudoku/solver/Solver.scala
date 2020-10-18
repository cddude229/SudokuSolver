package com.dessonville.sudoku.solver

import com.dessonville.sudoku.representation.{CellCoordinates, SudokuGuesser}
import com.dessonville.sudoku.representation.implementation.guesser.WrappedSudokuGuesser
import com.dessonville.sudoku.solver.patterns._

trait Solver {
  def solve[Value](guesser: SudokuGuesser[Value]): Unit
}

object PatternSolver extends Solver {
  def solve[Value](guesser: SudokuGuesser[Value]): Unit = {
    val trackingGuesser = new ReductionTrackingSudokuGuesser[Value](guesser)
    val patterns = Array[ReducingPattern[Value]](
      new OnePossibilityToValue[Value],

      new OnlyValueInRow[Value],
      new OnlyValueInColumn[Value],
      new OnlyValueInBox[Value],

      new ReduceCoupletsInBox[Value](2),
      new ReduceCoupletsInColumn[Value](2),
      new ReduceCoupletsInRow[Value](2),

      new ReduceCoupletsInBox[Value](3),
      new ReduceCoupletsInColumn[Value](3),
      new ReduceCoupletsInRow[Value](3),

      new ReduceCoupletsInBox[Value](4),
      new ReduceCoupletsInColumn[Value](4),
      new ReduceCoupletsInRow[Value](4),

      new ReduceCoupletsInBox[Value](5),
      new ReduceCoupletsInColumn[Value](5),
      new ReduceCoupletsInRow[Value](5),

      new BoxToColAndRowClearing[Value],
      new ColAndRowToBoxClearing[Value],

      new ReduceCoupletsInBoxV2[Value](2),
      new ReduceCoupletsInColumnV2[Value](2),
      new ReduceCoupletsInRowV2[Value](2),

      new ReduceCoupletsInBoxV2[Value](3),
      new ReduceCoupletsInColumnV2[Value](3),
      new ReduceCoupletsInRowV2[Value](3),

      new ReduceCoupletsInBoxV2[Value](4),
      new ReduceCoupletsInColumnV2[Value](4),
      new ReduceCoupletsInRowV2[Value](4),

      new ReduceCoupletsInBoxV2[Value](5),
      new ReduceCoupletsInColumnV2[Value](5),
      new ReduceCoupletsInRowV2[Value](5)
    )

    // Iterate over the patterns, reset to first pattern until we're done
    var idx = 0
    while (!trackingGuesser.isSolved() && idx < patterns.length) {
      patterns(idx).reduce(trackingGuesser)

      if (trackingGuesser.getAndClearFlag()) {
        idx = 0
      } else {
        idx += 1
      }
    }
  }
}

/**
  * Tracks if any reductions made a change since last checked
  */
class ReductionTrackingSudokuGuesser[Value](wrapped: SudokuGuesser[Value]) extends WrappedSudokuGuesser[Value](wrapped) {
  private var reducedFlag = false

  def getAndClearFlag(): Boolean = {
    val ret = reducedFlag
    reducedFlag = false
    ret
  }

  override def removePossibleValues(cellCoordinates: CellCoordinates, values: Set[Value]): Boolean = {
    val reduced = wrapped.removePossibleValues(cellCoordinates, values)
    reducedFlag = reduced || reducedFlag
    reduced
  }

  override def getPossibleValues(cellCoordinates: CellCoordinates): Set[Value] = wrapped.getPossibleValues(cellCoordinates)
  override def isSolved(): Boolean = wrapped.isSolved()
  override def isCorrect(): Boolean = wrapped.isCorrect()
}
