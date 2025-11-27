package com.comp2042.model;

import com.comp2042.logic.MatrixOperations;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines the J-shaped tetromino (also known as the "J-piece").
 * <p>
 * This brick has 4 rotation states. The brick uses color value 2.
 * </p>
 * <p>
 * Shape matrices are indexed as shape[row][col]. Non-zero values (2) represent
 * filled cells.
 * </p>
 *
 * @author TetrisJFX Team
 * @version 1.0
 */
final class JBrick implements Brick {

    private final List<int[][]> brickMatrix = new ArrayList<>();

    /**
     * Constructs a new J-shaped brick with all rotation variants.
     * <p>
     * Initializes the brick with 4 rotation states.
     * </p>
     */
    public JBrick() {
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {2, 2, 2, 0},
                {0, 0, 2, 0},
                {0, 0, 0, 0}
        });
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {0, 2, 2, 0},
                {0, 2, 0, 0},
                {0, 2, 0, 0}
        });
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {0, 2, 0, 0},
                {0, 2, 2, 2},
                {0, 0, 0, 0}
        });
        brickMatrix.add(new int[][]{
                {0, 0, 2, 0},
                {0, 0, 2, 0},
                {0, 2, 2, 0},
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
