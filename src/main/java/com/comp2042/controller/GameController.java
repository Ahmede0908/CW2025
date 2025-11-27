package com.comp2042.controller;

import com.comp2042.board.Board;
import com.comp2042.board.SimpleBoard;
import com.comp2042.model.ClearRow;
import com.comp2042.model.HardDropResult;
import com.comp2042.view.DownData;
import com.comp2042.view.GuiController;
import com.comp2042.view.ViewData;

/**
 * Controller class that coordinates game logic and user input in the MVC
 * architecture.
 * <p>
 * This class acts as the Controller layer, connecting the Model (Board) and
 * View (GuiController). It implements InputEventListener to handle user input
 * events and translates them into board operations. It also manages the game
 * flow including brick movement, rotation, merging, row clearing, and game
 * over detection.
 * </p>
 * <p>
 * Responsibilities:
 * <ul>
 *   <li>Handle user input events (left, right, down, rotate)</li>
 *   <li>Coordinate board operations with view updates</li>
 *   <li>Manage game state transitions (brick merging, row clearing, game over)</li>
 *   <li>Update score based on user actions and cleared rows</li>
 * </ul>
 * </p>
 *
 * @author TetrisJFX Team
 * @version 1.0
 */
public class GameController implements InputEventListener {

    private Board board = new SimpleBoard(10, 25);

    private final GuiController viewGuiController;

    /**
     * Constructs a new GameController and initializes the game.
     * <p>
     * Creates a new board, spawns the first brick, sets up the event listener
     * connection, initializes the game view, and binds the score property.
     * </p>
     *
     * @param c the GuiController to coordinate with
     */
    public GameController(GuiController c) {
        viewGuiController = c;
        board.createNewBrick();
        viewGuiController.setEventListener(this);
        viewGuiController.initGameView(board.getBoardMatrix(),
                board.getViewData());
        viewGuiController.bindScore(board.getScore().scoreProperty());
    }

    /**
     * Handles the down movement event.
     * <p>
     * Attempts to move the brick down. If movement fails, merges the brick
     * into the board, clears completed rows, updates the score, spawns a new
     * brick, and checks for game over. If movement succeeds and was triggered
     * by the user, adds 1 point to the score.
     * </p>
     *
     * @param event the move event containing event type and source
     * @return DownData containing the cleared row information and updated
     *         view data
     */
    @Override
    public DownData onDownEvent(MoveEvent event) {
        boolean canMove = board.moveBrickDown();
        ClearRow clearRow = null;
        if (!canMove) {
            board.mergeBrickToBackground();
            clearRow = board.clearRows();
            // Score is now updated automatically in SimpleBoard.clearRows()
            if (board.createNewBrick()) {
                viewGuiController.gameOver();
            }

            viewGuiController.refreshGameBackground(board.getBoardMatrix());

        } else {
            if (event.getEventSource() == EventSource.USER) {
                board.getScore().addScore(1);
            }
        }
        return new DownData(clearRow, board.getViewData());
    }

    /**
     * Handles the left movement event.
     * <p>
     * Attempts to move the brick left and returns the updated view data.
     * </p>
     *
     * @param event the move event containing event type and source
     * @return ViewData containing the current brick state and position
     */
    @Override
    public ViewData onLeftEvent(MoveEvent event) {
        board.moveBrickLeft();
        return board.getViewData();
    }

    /**
     * Handles the right movement event.
     * <p>
     * Attempts to move the brick right and returns the updated view data.
     * </p>
     *
     * @param event the move event containing event type and source
     * @return ViewData containing the current brick state and position
     */
    @Override
    public ViewData onRightEvent(MoveEvent event) {
        board.moveBrickRight();
        return board.getViewData();
    }

    /**
     * Handles the rotation event.
     * <p>
     * Attempts to rotate the brick 90 degrees clockwise and returns the
     * updated view data.
     * </p>
     *
     * @param event the move event containing event type and source
     * @return ViewData containing the current brick state and position
     */
    @Override
    public ViewData onRotateEvent(MoveEvent event) {
        board.rotateLeftBrick();
        return board.getViewData();
    }

    /**
     * Handles the hard drop event.
     * <p>
     * Performs an instant hard drop of the current brick to the lowest possible
     * valid position. The brick is locked immediately, rows are cleared if
     * applicable, and a new brick is spawned. Hard drop bonus points are
     * calculated and added to the score.
     * </p>
     *
     * @return HardDropResult containing the final ViewData, ClearRow result,
     *         number of rows dropped, and game over status
     */
    public HardDropResult onHardDropEvent() {
        if (board instanceof SimpleBoard) {
            HardDropResult result = ((SimpleBoard) board).hardDrop();
            
            // Refresh the board background
            viewGuiController.refreshGameBackground(board.getBoardMatrix());
            
            // Check for game over (hardDrop() already checked this via createNewBrick())
            if (result.isGameOver()) {
                viewGuiController.gameOver();
            }
            
            return result;
        }
        // Fallback if board is not SimpleBoard (shouldn't happen)
        return new HardDropResult(board.getViewData(), null, 0, false);
    }

    /**
     * Creates a new game by resetting the board and refreshing the view.
     * <p>
     * Resets the board state and updates the view to reflect the new game
     * state. Also refreshes the scoreboard to show reset scores.
     * </p>
     */
    @Override
    public void createNewGame() {
        board.newGame();
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
        // Refresh scoreboard with reset scores
        viewGuiController.refreshScoreboard(board.getViewData());
    }
}
