package com.comp2042.board.rotation;

/**
 * Rotation strategy that performs no rotation (returns the original matrix).
 * <p>
 * This class is part of the Model layer in the MVC architecture. It implements
 * the RotationStrategy interface using the Strategy design pattern. This
 * strategy is used for square bricks (like OBrick) that have only one shape
 * variant and therefore do not need rotation.
 * </p>
 * <p>
 * This strategy simply returns the input matrix unchanged, effectively
 * disabling rotation for bricks that use it.
 * </p>
 *
 * @author TetrisJFX Team
 * @version 1.0
 */
public class NoRotationStrategy implements RotationStrategy {

    /**
     * Returns the shape matrix unchanged (no rotation).
     * <p>
     * Square bricks do not rotate, so this method simply returns the input
     * matrix without modification.
     * </p>
     *
     * @param shapeMatrix the shape matrix (shape[row][col])
     * @return the same matrix unchanged
     */
    @Override
    public int[][] rotate(int[][] shapeMatrix) {
        // Square bricks do not rotate
        return shapeMatrix;
    }
}
