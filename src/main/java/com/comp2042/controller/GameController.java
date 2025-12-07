package com.comp2042.controller;

import com.comp2042.board.Board;
import com.comp2042.board.SimpleBoard;
import com.comp2042.model.ClearRow;
import com.comp2042.model.HardDropResult;
import com.comp2042.view.DownData;
import com.comp2042.view.GuiController;
import com.comp2042.view.ViewData;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

/**
 * Main game controller connecting Model (Board) and View (GuiController).
 */
public class GameController implements InputEventListener {

    private Board board = new SimpleBoard(10, 25);
    private final GuiController viewGuiController;

    private Timeline timeline;  // Auto-fall loop

    public GameController(GuiController c) {
        this.viewGuiController = c;

        board.createNewBrick();

        // Connect the GUI to this controller
        viewGuiController.setEventListener(this);

        // Render initial grid + first piece
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());
        viewGuiController.refreshGameBackground(board.getBoardMatrix());

        // Score + level binding
        viewGuiController.bindScore(board.getScore().scoreProperty());
        viewGuiController.bindLevel(board.getScore().levelProperty(), board.getScore());

        startAutoFall();
    }

    /**
     * Starts automatic piece falling using a Timeline.
     */
    private void startAutoFall() {
        if (timeline != null) timeline.stop();

        timeline = new Timeline(new KeyFrame(
                Duration.millis(400),
                e -> handleAutoDrop()
        ));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void handleAutoDrop() {
        DownData data = onDownEvent(new MoveEvent(EventType.DOWN, EventSource.THREAD));
        viewGuiController.refreshBrick(data.getViewData());
    }

    // ---------------- Movement Handlers ----------------

    /**
     * Detects which rows in the board are full (ready to be cleared).
     * 
     * @param boardMatrix the board matrix to check (board[row][col])
     * @return list of row indices that are full (0-based)
     */
    private List<Integer> detectFullRows(int[][] boardMatrix) {
        List<Integer> fullRows = new ArrayList<>();
        if (boardMatrix == null || boardMatrix.length == 0) {
            return fullRows;
        }
        
        int rows = boardMatrix.length;
        int cols = boardMatrix[0].length;
        
        for (int row = 0; row < rows; row++) {
            boolean isFull = true;
            for (int col = 0; col < cols; col++) {
                if (boardMatrix[row][col] == 0) {
                    isFull = false;
                    break;
                }
            }
            if (isFull) {
                fullRows.add(row);
            }
        }
        
        return fullRows;
    }

    @Override
    public DownData onDownEvent(MoveEvent event) {
        boolean canMove = board.moveBrickDown();
        ClearRow clearRow = null;

        if (!canMove) {
            board.mergeBrickToBackground();
            
            // Detect full rows before clearing for animation
            int[][] boardMatrix = board.getBoardMatrix();
            List<Integer> fullRows = detectFullRows(boardMatrix);
            
            // Animate line clear if rows will be cleared
            if (!fullRows.isEmpty()) {
                // Flash the rows white immediately
                viewGuiController.animateLineClear(fullRows);
                
                // Clear rows immediately (game logic continues)
                clearRow = board.clearRows();
                
                if (board.createNewBrick()) {
                    viewGuiController.gameOver();
                    timeline.stop();
                }
                
                // Delay refresh to show the flash for ~150ms
                Timeline refreshTimeline = new Timeline(new KeyFrame(
                    Duration.millis(150),
                    e -> viewGuiController.refreshGameBackground(board.getBoardMatrix())
                ));
                refreshTimeline.setCycleCount(1);
                refreshTimeline.play();
            } else {
                // No rows to clear, proceed normally
                clearRow = board.clearRows();
                
                if (board.createNewBrick()) {
                    viewGuiController.gameOver();
                    timeline.stop();
                }
                
                viewGuiController.refreshGameBackground(board.getBoardMatrix());
            }
        } else {
            if (event.getEventSource() == EventSource.USER) {
                // Tetris Guideline: Soft drop awards +1 point per cell moved down manually
                board.getScore().addSoftDropPoints(1);
            }
        }

        return new DownData(clearRow, board.getViewData());
    }

    @Override
    public ViewData onLeftEvent(MoveEvent event) {
        board.moveBrickLeft();
        return board.getViewData();
    }

    @Override
    public ViewData onRightEvent(MoveEvent event) {
        board.moveBrickRight();
        return board.getViewData();
    }

    @Override
    public ViewData onRotateEvent(MoveEvent event) {
        board.rotateLeftBrick();
        return board.getViewData();
    }

    // ---------------- Hard Drop ----------------

    public HardDropResult onHardDropEvent() {
        if (board instanceof SimpleBoard sb) {
            // For hard drop, detect full rows before the drop
            // We need to check the board after brick would be merged but before clearing
            // This is complex, so we'll do a simpler approach: check board state before drop
            int[][] boardBeforeDrop = board.getBoardMatrix();
            
            HardDropResult result = sb.hardDrop();
            
            // If rows were cleared, we can't easily animate them since hardDrop() clears internally
            // For now, hard drop clears are so fast that animation may not be necessary
            // But we'll still refresh the background normally
            viewGuiController.refreshGameBackground(board.getBoardMatrix());

            if (result.isGameOver()) {
                viewGuiController.gameOver();
                timeline.stop();
            }

            return result;
        }

        return new HardDropResult(board.getViewData(), null, 0, false);
    }

    // ---------------- Game Reset ----------------

    @Override
    public void createNewGame() {
        board.newGame();
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());

        startAutoFall();
    }
}

