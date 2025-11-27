package com.comp2042.board;

import com.comp2042.model.ClearRow;
import com.comp2042.model.Score;
import com.comp2042.view.ViewData;

/**
 * Interface defining the contract for game board operations in the Tetris game.
 * <p>
 * This interface is part of the Model layer in the MVC architecture. It defines
 * the core game logic operations including brick movement, rotation, spawning,
 * and row clearing. Implementations manage the game state including the board
 * matrix, current brick position, and score.
 * </p>
 * <p>
 * Coordinate system: x = column, y = row. The board matrix is indexed as
 * matrix[row][col] = matrix[y][x].
 * </p>
 *
 * @author TetrisJFX Team
 * @version 1.0
 */
public interface Board {

    /**
     * Attempts to move the current brick down by one row.
     *
     * @return true if the brick was successfully moved down, false if movement
     *         was blocked by collision or boundary
     */
    boolean moveBrickDown();

    /**
     * Attempts to move the current brick left by one column.
     *
     * @return true if the brick was successfully moved left, false if movement
     *         was blocked by collision or boundary
     */
    boolean moveBrickLeft();

    /**
     * Attempts to move the current brick right by one column.
     *
     * @return true if the brick was successfully moved right, false if movement
     *         was blocked by collision or boundary
     */
    boolean moveBrickRight();

    /**
     * Attempts to rotate the current brick 90 degrees clockwise.
     *
     * @return true if the rotation was successful, false if rotation was blocked
     *         by collision or boundary
     */
    boolean rotateLeftBrick();

    /**
     * Creates a new brick at the top center of the board.
     * <p>
     * The brick spawns at row 0 (top) and is centered horizontally. If the
     * spawn position results in an immediate collision, the game is over.
     * </p>
     *
     * @return true if the new brick collides immediately (game over condition),
     *         false if the brick was successfully spawned
     */
    boolean createNewBrick();

    /**
     * Returns the current state of the game board matrix.
     * <p>
     * The matrix is indexed as matrix[row][col]. Non-zero values represent
     * placed blocks, while zero represents empty cells.
     * </p>
     *
     * @return a 2D array representing the board state, where matrix[row][col]
     *         contains the cell value
     */
    int[][] getBoardMatrix();

    /**
     * Returns the current view data for rendering the game state.
     * <p>
     * This includes the current falling brick shape, its position, and the
     * next brick preview.
     * </p>
     *
     * @return ViewData object containing brick shape, position, and next brick
     *         information
     */
    ViewData getViewData();

    /**
     * Merges the current falling brick into the board background.
     * <p>
     * This method is called when a brick can no longer move down. The brick's
     * cells are permanently added to the board matrix at its current position.
     * </p>
     */
    void mergeBrickToBackground();

    /**
     * Clears all completed rows from the board and collapses remaining rows.
     * <p>
     * A row is considered complete when all cells in that row are non-zero.
     * Cleared rows are removed and remaining rows fall down to fill the gaps.
     * </p>
     *
     * @return ClearRow object containing the number of lines removed, updated
     *         board matrix, and score bonus
     */
    ClearRow clearRows();

    /**
     * Returns the score object for this game board.
     *
     * @return Score object that manages the game score
     */
    Score getScore();

    /**
     * Resets the game board to initial state.
     * <p>
     * Clears the board matrix, resets the score to zero, and spawns a new
     * brick to start a new game.
     * </p>
     */
    void newGame();
}
