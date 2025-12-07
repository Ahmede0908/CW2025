package com.comp2042.model;

import com.comp2042.logic.MatrixOperations;

/**
 * Immutable data container representing the result of row clearing operations.
 * <p>
 * This class is part of the Model layer in the MVC architecture. It stores
 * information about completed rows that were cleared from the board, including
 * the number of lines removed, the updated board matrix after clearing, and
 * the score bonus calculated based on the number of cleared rows.
 * </p>
 * <p>
 * Score bonus is calculated in SimpleBoard.clearRows() using classic Tetris rules:
 * - 1 line: 40 × (level + 1)
 * - 2 lines: 100 × (level + 1)
 * - 3 lines: 300 × (level + 1)
 * - 4 lines: 1200 × (level + 1)
 * </p>
 * <p>
 * All data returned by getter methods are defensive copies to maintain
 * immutability.
 * </p>
 *
 * @author TetrisJFX Team
 * @version 1.0
 */
public final class ClearRow {

    private final int linesRemoved;
    private final int[][] newMatrix;
    private final int scoreBonus;

    /**
     * Constructs a new ClearRow object with the specified clearing results.
     *
     * @param linesRemoved the number of completed rows that were cleared
     * @param newMatrix    the updated board matrix after row clearing
     *                     (matrix[row][col])
     * @param scoreBonus   the score bonus calculated from cleared rows
     */
    public ClearRow(int linesRemoved, int[][] newMatrix, int scoreBonus) {
        this.linesRemoved = linesRemoved;
        this.newMatrix = newMatrix;
        this.scoreBonus = scoreBonus;
    }

    /**
     * Returns the number of completed rows that were cleared.
     *
     * @return the number of lines removed (0 if no rows were cleared)
     */
    public int getLinesRemoved() {
        return linesRemoved;
    }

    /**
     * Returns a defensive copy of the updated board matrix after row clearing.
     * <p>
     * The matrix is indexed as matrix[row][col]. Cleared rows have been
     * removed and remaining rows have been collapsed downward.
     * </p>
     *
     * @return a deep copy of the updated board matrix
     */
    public int[][] getNewMatrix() {
        return MatrixOperations.copy(newMatrix);
    }

    /**
     * Returns the score bonus (legacy field, now always 0).
     * <p>
     * Score is now calculated in SimpleBoard.clearRows() using classic Tetris rules
     * based on the current level. This field is kept for backward compatibility
     * but should not be used for scoring calculations.
     * </p>
     *
     * @return 0 (score is calculated elsewhere)
     */
    public int getScoreBonus() {
        return scoreBonus;
    }
}
