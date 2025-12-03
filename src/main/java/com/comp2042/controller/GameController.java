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
import javafx.util.Duration;

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

    @Override
    public DownData onDownEvent(MoveEvent event) {
        boolean canMove = board.moveBrickDown();
        ClearRow clearRow = null;

        if (!canMove) {
            board.mergeBrickToBackground();
            clearRow = board.clearRows();

            if (board.createNewBrick()) {
                viewGuiController.gameOver();
                timeline.stop();
            }

            viewGuiController.refreshGameBackground(board.getBoardMatrix());
        } else {
            if (event.getEventSource() == EventSource.USER) {
                board.getScore().addScore(1);
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
            HardDropResult result = sb.hardDrop();
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

