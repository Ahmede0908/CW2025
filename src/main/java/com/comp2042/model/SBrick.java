package com.comp2042.model;

import com.comp2042.logic.MatrixOperations;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines the S-shaped tetromino (also known as the "S-piece").
 * <p>
 * This brick has 2 rotation states. The brick uses color value 5.
 * </p>
 * <p>
 * Shape matrices are indexed as shape[row][col]. Non-zero values (5) represent
 * filled cells.
 * </p>
 *
 * @author TetrisJFX Team
 * @version 1.0
 */
final class SBrick implements Brick {

    private final List<int[][]> brickMatrix = new ArrayList<>();

    /**
     * Constructs a new S-shaped brick with all rotation variants.
     * <p>
     * Initializes the brick with 2 rotation states.
     * </p>
     */
    public SBrick() {
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {0, 5, 5, 0},
                {5, 5, 0, 0},
                {0, 0, 0, 0}
        });
        brickMatrix.add(new int[][]{
                {5, 0, 0, 0},
                {5, 5, 0, 0},
                {0, 5, 0, 0},
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
