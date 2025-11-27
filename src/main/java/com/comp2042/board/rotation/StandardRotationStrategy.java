package com.comp2042.board.rotation;

import com.comp2042.logic.MatrixOperations;

/**
 * Standard rotation strategy that rotates shapes 90 degrees clockwise.
 * <p>
 * This class is part of the Model layer in the MVC architecture. It implements
 * the RotationStrategy interface using the Strategy design pattern. This
 * strategy is used for most brick types that support standard Tetris rotation.
 * </p>
 * <p>
 * The rotation is performed using MatrixOperations.rotate90(), which rotates
 * the matrix 90 degrees clockwise.
 * </p>
 *
 * @author TetrisJFX Team
 * @version 1.0
 */
public class StandardRotationStrategy implements RotationStrategy {

    /**
     * Rotates the shape matrix 90 degrees clockwise.
     * <p>
     * Delegates to MatrixOperations.rotate90() to perform the rotation.
     * Returns a new matrix; the input matrix is not modified.
     * </p>
     *
     * @param shapeMatrix the shape matrix to rotate (shape[row][col])
     * @return a new matrix rotated 90 degrees clockwise
     */
    @Override
    public int[][] rotate(int[][] shapeMatrix) {
        // Rotate the matrix using MatrixOperations
        return MatrixOperations.rotate90(shapeMatrix);
    }
}
