package com.comp2042.board.rotation;

/**
 * Strategy interface for brick rotation algorithms in the Strategy design pattern.
 * <p>
 * This interface is part of the Model layer in the MVC architecture. It
 * defines the contract for rotation strategies used by BrickRotator. Different
 * implementations can provide different rotation behaviors (e.g., standard
 * 90-degree rotation, no rotation for square bricks, or custom rotation rules).
 * </p>
 * <p>
 * The Strategy pattern allows the rotation algorithm to be selected at runtime
 * based on the brick type, providing flexibility and extensibility.
 * </p>
 *
 * @author TetrisJFX Team
 * @version 1.0
 */
public interface RotationStrategy {

    /**
     * Rotates a shape matrix according to the strategy's rotation algorithm.
     * <p>
     * The input matrix is indexed as shape[row][col]. The implementation
     * should return a new matrix (not modify the input) with the rotation
     * applied.
     * </p>
     *
     * @param shapeMatrix the shape matrix to rotate (shape[row][col])
     * @return a new matrix with the rotation applied, or the original matrix
     *         if no rotation is performed
     */
    int[][] rotate(int[][] shapeMatrix);
}
