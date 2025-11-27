package com.comp2042.model;

import java.util.List;

/**
 * Interface defining the contract for Tetris brick (tetromino) shapes.
 * <p>
 * This interface is part of the Model layer in the MVC architecture. It
 * defines the structure for all brick types in the game. Each brick
 * implementation provides a list of shape matrices representing all possible
 * rotation states of that brick.
 * </p>
 * <p>
 * <strong>Shape Matrix Format:</strong>
 * <ul>
 *   <li>Each shape is represented as a 2D integer array: shape[row][col]</li>
 *   <li>Non-zero values represent filled cells (the value indicates the color)</li>
 *   <li>Zero values represent empty cells</li>
 *   <li>The list contains all rotation variants of the brick</li>
 * </ul>
 * </p>
 *
 * @author TetrisJFX Team
 * @version 1.0
 */
public interface Brick {

    /**
     * Returns a list of all rotation variants for this brick.
     * <p>
     * Each element in the list is a 2D array representing one rotation state
     * of the brick. The matrices are indexed as shape[row][col]. The list
     * should contain deep copies to prevent external modification.
     * </p>
     *
     * @return a list of 2D integer arrays, each representing a rotation state
     *         of the brick (shape[row][col])
     */
    List<int[][]> getShapeMatrix();
}
