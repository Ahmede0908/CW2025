package com.comp2042.model;

import com.comp2042.logic.MatrixOperations;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines the Z-shaped tetromino (also known as the "Z-piece").
 * <p>
 * This brick has 2 rotation states. The brick uses color value 7.
 * </p>
 * <p>
 * Shape matrices are indexed as shape[row][col]. Non-zero values (7) represent
 * filled cells.
 * </p>
 *
 * @author TetrisJFX Team
 * @version 1.0
 */
final class ZBrick implements Brick {

    private final List<int[][]> brickMatrix = new ArrayList<>();

    /**
     * Constructs a new Z-shaped brick with all rotation variants.
     * <p>
     * Initializes the brick with 2 rotation states.
     * </p>
     */
    public ZBrick() {
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {7, 7, 0, 0},
                {0, 7, 7, 0},
                {0, 0, 0, 0}
        });
        brickMatrix.add(new int[][]{
                {0, 7, 0, 0},
                {7, 7, 0, 0},
                {7, 0, 0, 0},
                {0, 0, 0, 0}
        });
    }

    /**
     * Returns a deep copy of all rotation variants for this brick.
     *
     * @return a list containing deep copies of all rotation state matrices
     */
    @Override
    public List<int[][]> getShapeMatrix() {
        return MatrixOperations.deepCopyList(brickMatrix);
    }
}
