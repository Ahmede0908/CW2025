package com.comp2042.model;

import com.comp2042.view.ViewData;

/**
 * Immutable data container representing the result of a hard drop operation.
 * <p>
 * This class is part of the Model layer in the MVC architecture. It stores
 * information about a hard drop operation, including the final view data
 * after the brick is locked, the row clearing result (if any), and the
 * number of rows the brick dropped.
 * </p>
 * <p>
 * All data returned by getter methods are defensive copies to maintain
 * immutability where applicable.
 * </p>
 *
 * @author TetrisJFX Team
 * @version 1.0
 */
public final class HardDropResult {

    private final ViewData viewData;
    private final ClearRow clearRow;
    private final int rowsDropped;
    private final boolean gameOver;

    /**
     * Constructs a new HardDropResult object with the specified drop results.
     *
     * @param viewData    the ViewData after the brick is locked and new brick spawned
     * @param clearRow    the ClearRow result from row clearing (can be null if no rows cleared)
     * @param rowsDropped the number of rows the brick dropped during the hard drop
     * @param gameOver    true if the game is over after hard drop (new brick collides at spawn)
     */
    public HardDropResult(ViewData viewData, ClearRow clearRow, int rowsDropped, boolean gameOver) {
        this.viewData = viewData;
        this.clearRow = clearRow;
        this.rowsDropped = rowsDropped;
        this.gameOver = gameOver;
    }

    /**
     * Returns the ViewData after the hard drop is complete.
     * <p>
     * This contains the state of the new brick that was spawned after the
     * hard drop, along with updated score information.
     * </p>
     *
     * @return the ViewData after hard drop completion
     */
    public ViewData getViewData() {
        return viewData;
    }

    /**
     * Returns the ClearRow result from row clearing after the hard drop.
     * <p>
     * This will be null if no rows were cleared. Otherwise, it contains
     * information about the cleared rows and score bonus.
     * </p>
     *
     * @return the ClearRow result, or null if no rows were cleared
     */
    public ClearRow getClearRow() {
        return clearRow;
    }

    /**
     * Returns the number of rows the brick dropped during the hard drop.
     * <p>
     * This is the distance from the brick's original position to its final
     * locked position. Used for calculating hard drop bonus points.
     * </p>
     *
     * @return the number of rows dropped (always >= 0)
     */
    public int getRowsDropped() {
        return rowsDropped;
    }

    /**
     * Returns whether the game is over after the hard drop.
     * <p>
     * The game is over if the new brick spawned after the hard drop collides
     * immediately at the spawn position (top of the board).
     * </p>
     *
     * @return true if the game is over, false otherwise
     */
    public boolean isGameOver() {
        return gameOver;
    }
}

