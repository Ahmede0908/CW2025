package com.comp2042.view;

import com.comp2042.controller.EventSource;
import com.comp2042.controller.EventType;
import com.comp2042.controller.InputEventListener;
import com.comp2042.controller.MoveEvent;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.effect.Reflection;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class GuiController implements Initializable {

    private static final int BRICK_SIZE = 20;

    @FXML
    private GridPane gamePanel;

    @FXML
    private Group groupNotification;

    @FXML
    private GridPane brickPanel;

    @FXML
    private GameOverPanel gameOverPanel;

    private Rectangle[][] displayMatrix;

    private InputEventListener eventListener;

    private Rectangle[][] rectangles;

    private Timeline timeLine;

    private final BooleanProperty isPause = new SimpleBooleanProperty();

    private final BooleanProperty isGameOver = new SimpleBooleanProperty();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Font.loadFont(getClass().getClassLoader().getResource("digital.ttf").toExternalForm(), 38);
        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();
        gamePanel.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (isPause.getValue() == Boolean.FALSE && isGameOver.getValue() == Boolean.FALSE) {
                    if (keyEvent.getCode() == KeyCode.LEFT || keyEvent.getCode() == KeyCode.A) {
                        refreshBrick(eventListener.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.RIGHT || keyEvent.getCode() == KeyCode.D) {
                        refreshBrick(eventListener.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.UP || keyEvent.getCode() == KeyCode.W) {
                        refreshBrick(eventListener.onRotateEvent(new MoveEvent(EventType.ROTATE, EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.DOWN || keyEvent.getCode() == KeyCode.S) {
                        moveDown(new MoveEvent(EventType.DOWN, EventSource.USER));
                        keyEvent.consume();
                    }
                }
                if (keyEvent.getCode() == KeyCode.N) {
                    newGame(null);
                }
            }
        });
        gameOverPanel.setVisible(false);

        final Reflection reflection = new Reflection();
        reflection.setFraction(0.8);
        reflection.setTopOpacity(0.9);
        reflection.setTopOffset(-12);
    }

    /**
     * Initializes the game view.
     * Coordinate system: x = column, y = row
     * Board matrix: board[row][col] = board[y][x]
     * GridPane: add(node, column, row) = add(node, x, y)
     */
    public void initGameView(int[][] boardMatrix, ViewData brick) {
        // Clear old nodes from gamePanel
        gamePanel.getChildren().clear();
        
        // Clear old nodes from brickPanel
        brickPanel.getChildren().clear();
        
        // boardMatrix is [rows][cols] = [y][x] in Java array notation
        // x = column, y = row
        int height = boardMatrix.length;  // rows (y dimension)
        int width = boardMatrix[0].length;  // cols (x dimension)
        displayMatrix = new Rectangle[height][width];
        
        // Loop: y (row) as outer, x (col) as inner
        // Map board[y][x] → GridPane column=x row=y
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                displayMatrix[y][x] = rectangle;
                // GridPane.add(node, column, row) = add(node, x, y)
                gamePanel.add(rectangle, x, y);
            }
        }

        // Initialize brick panel rectangles
        // brick.getBrickData() is [rows][cols] = [y][x]
        int brickHeight = brick.getBrickData().length;  // rows (y dimension)
        int brickWidth = brick.getBrickData()[0].length;  // cols (x dimension)
        rectangles = new Rectangle[brickHeight][brickWidth];
        
        // Loop: y (row) as outer, x (col) as inner
        // Map brick[y][x] → GridPane column=x row=y
        for (int y = 0; y < brickHeight; y++) {
            for (int x = 0; x < brickWidth; x++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(getFillColor(brick.getBrickData()[y][x]));
                rectangles[y][x] = rectangle;
                // GridPane.add(node, column, row) = add(node, x, y)
                brickPanel.add(rectangle, x, y);
            }
        }
        
        // Set brick panel position relative to gamePanel
        updateBrickPanelPosition(brick);
        
        timeLine = new Timeline(new KeyFrame(
                Duration.millis(400),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();
    }

    /**
     * Updates the brick panel position relative to gamePanel.
     * Coordinate system: x = column, y = row
     * brickPanel must be positioned INSIDE gamePanel
     */
    private void updateBrickPanelPosition(ViewData brick) {
        // x = column, y = row
        // Position brickPanel relative to gamePanel's position
        double layoutX = gamePanel.getLayoutX() + brick.getxPosition() * BRICK_SIZE;
        double layoutY = gamePanel.getLayoutY() + brick.getyPosition() * BRICK_SIZE;
        brickPanel.setLayoutX(layoutX);
        brickPanel.setLayoutY(layoutY);
    }

    private Paint getFillColor(int i) {
        Paint returnPaint;
        switch (i) {
            case 0:
                returnPaint = Color.TRANSPARENT;
                break;
            case 1:
                returnPaint = Color.AQUA;
                break;
            case 2:
                returnPaint = Color.BLUEVIOLET;
                break;
            case 3:
                returnPaint = Color.DARKGREEN;
                break;
            case 4:
                returnPaint = Color.YELLOW;
                break;
            case 5:
                returnPaint = Color.RED;
                break;
            case 6:
                returnPaint = Color.BEIGE;
                break;
            case 7:
                returnPaint = Color.BURLYWOOD;
                break;
            default:
                returnPaint = Color.WHITE;
                break;
        }
        return returnPaint;
    }


    /**
     * Refreshes the brick visual representation and position.
     * Coordinate system: x = column, y = row
     * Brick data: brick[y][x] (Java array notation: [row][col])
     */
    private void refreshBrick(ViewData brick) {
        if (isPause.getValue() == Boolean.FALSE) {
            // Update brick panel position using clean coordinate translation
            updateBrickPanelPosition(brick);
            
            // Update brick visual representation
            // brick.getBrickData() is [rows][cols] = [y][x]
            int brickHeight = brick.getBrickData().length;
            int brickWidth = brick.getBrickData()[0].length;
            // Loop: y (row) as outer, x (col) as inner
            for (int y = 0; y < brickHeight; y++) {
                for (int x = 0; x < brickWidth; x++) {
                    setRectangleData(brick.getBrickData()[y][x], rectangles[y][x]);
                }
            }
        }
    }

    /**
     * Refreshes the game background.
     * Coordinate system: x = column, y = row
     * Board matrix: board[y][x] (Java array notation: [row][col])
     */
    public void refreshGameBackground(int[][] board) {
        // board is [rows][cols] = [y][x]
        int height = board.length;  // rows (y dimension)
        int width = board[0].length;  // cols (x dimension)
        // Loop: y (row) as outer, x (col) as inner
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                setRectangleData(board[y][x], displayMatrix[y][x]);
            }
        }
    }

    private void setRectangleData(int color, Rectangle rectangle) {
        rectangle.setFill(getFillColor(color));
        rectangle.setArcHeight(9);
        rectangle.setArcWidth(9);
    }

    private void moveDown(MoveEvent event) {
        if (isPause.getValue() == Boolean.FALSE) {
            DownData downData = eventListener.onDownEvent(event);
            if (downData.getClearRow() != null && downData.getClearRow().getLinesRemoved() > 0) {
                NotificationPanel notificationPanel = new NotificationPanel("+" + downData.getClearRow().getScoreBonus());
                groupNotification.getChildren().add(notificationPanel);
                notificationPanel.showScore(groupNotification.getChildren());
            }
            refreshBrick(downData.getViewData());
        }
        gamePanel.requestFocus();
    }

    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public void bindScore(IntegerProperty integerProperty) {
    }

    public void gameOver() {
        timeLine.stop();
        gameOverPanel.setVisible(true);
        isGameOver.setValue(Boolean.TRUE);
    }

    public void newGame(ActionEvent actionEvent) {
        timeLine.stop();
        gameOverPanel.setVisible(false);
        eventListener.createNewGame();
        gamePanel.requestFocus();
        timeLine.play();
        isPause.setValue(Boolean.FALSE);
        isGameOver.setValue(Boolean.FALSE);
    }

    public void pauseGame(ActionEvent actionEvent) {
        gamePanel.requestFocus();
    }
}
