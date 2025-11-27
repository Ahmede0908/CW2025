package com.comp2042.model;

import com.comp2042.logic.MatrixOperations;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines the L-shaped tetromino (also known as the "L-piece").
 * <p>
 * This brick has 4 rotation states. The brick uses color value 3.
 * </p>
 * <p>
 * Shape matrices are indexed as shape[row][col]. Non-zero values (3) represent
 * filled cells.
 * </p>
 *
 * @author TetrisJFX Team
 * @version 1.0
 */
final class LBrick implements Brick {

    private final List<int[][]> brickMatrix = new ArrayList<>();

    /**
     * Constructs a new L-shaped brick with all rotation variants.
     * <p>
     * Initializes the brick with 4 rotation states.
     * </p>
     */
    public LBrick() {
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {0, 3, 3, 3},
                {0, 3, 0, 0},
                {0, 0, 0, 0}
        });
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {0, 3, 3, 0},
                {0, 0, 3, 0},
                {0, 0, 3, 0}
        });
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {0, 0, 3, 0},
                {3, 3, 3, 0},
                {0, 0, 0, 0}
        });
        brickMatrix.add(new int[][]{
                {0, 3, 0, 0},
                {0, 3, 0, 0},
                {0, 3, 3, 0},
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
