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
 * The score bonus formula is: 50 * (linesRemoved)^2
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
     * Returns the score bonus calculated from the cleared rows.
     * <p>
     * The bonus is calculated as 50 * (linesRemoved)^2. For example:
     * <ul>
     *   <li>1 line: 50 points</li>
     *   <li>2 lines: 200 points</li>
     *   <li>3 lines: 450 points</li>
     *   <li>4 lines: 800 points</li>
     * </ul>
     * </p>
     *
     * @return the score bonus to add to the player's score
     */
    public int getScoreBonus() {
        return scoreBonus;
    }
}
