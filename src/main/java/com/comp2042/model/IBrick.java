package com.comp2042.model;

import com.comp2042.logic.MatrixOperations;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines the I-shaped tetromino (also known as the "I-piece" or "line piece").
 * <p>
 * This brick has 2 rotation states: horizontal (4 cells in a row) and
 * vertical (4 cells in a column). The brick uses color value 1.
 * </p>
 * <p>
 * Shape matrices are indexed as shape[row][col]. Non-zero values (1) represent
 * filled cells.
 * </p>
 *
 * @author TetrisJFX Team
 * @version 1.0
 */
final class IBrick implements Brick {

    private final List<int[][]> brickMatrix = new ArrayList<>();

    /**
     * Constructs a new I-shaped brick with all rotation variants.
     * <p>
     * Initializes the brick with 2 rotation states: horizontal and vertical.
     * </p>
     */
    public IBrick() {
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {1, 1, 1, 1},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        });
        brickMatrix.add(new int[][]{
                {0, 1, 0, 0},
                {0, 1, 0, 0},
                {0, 1, 0, 0},
                {0, 1, 0, 0}
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
