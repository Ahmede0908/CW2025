package com.comp2042.model;

import com.comp2042.logic.MatrixOperations;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines the O-shaped tetromino (also known as the "O-piece" or "square piece").
 * <p>
 * This brick has only 1 rotation state (it is a square, so rotation has no
 * visual effect). The brick uses color value 4.
 * </p>
 * <p>
 * Shape matrices are indexed as shape[row][col]. Non-zero values (4) represent
 * filled cells.
 * </p>
 * <p>
 * Note: This brick uses NoRotationStrategy in the rotation system since it
 * has only one shape variant.
 * </p>
 *
 * @author TetrisJFX Team
 * @version 1.0
 */
final class OBrick implements Brick {

    private final List<int[][]> brickMatrix = new ArrayList<>();

    /**
     * Constructs a new O-shaped brick.
     * <p>
     * Initializes the brick with 1 rotation state (square shape).
     * </p>
     */
    public OBrick() {
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {0, 4, 4, 0},
                {0, 4, 4, 0},
                {0, 0, 0, 0}
        });
    }

    /**
     * Returns a deep copy of all rotation variants for this brick.
     * <p>
     * For O-shaped bricks, this always returns a list with a single element
     * (the square shape).
     * </p>
     *
     * @return a list containing a deep copy of the square shape matrix
     */
    @Override
    public List<int[][]> getShapeMatrix() {
        return MatrixOperations.deepCopyList(brickMatrix);
    }
}
