package com.comp2042.view;

import com.comp2042.controller.EventSource;
import com.comp2042.controller.EventType;
import com.comp2042.controller.InputEventListener;
import com.comp2042.controller.MoveEvent;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
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
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.net.URL;
import java.util.ResourceBundle;

public class GuiController implements Initializable {

    private static final int BRICK_SIZE = 20;

    @FXML
    private BorderPane gameBoard;

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

    // Board dimensions for fullscreen centering
    private int numberOfColumns;
    private int numberOfRows;
    private double boardPixelWidth;
    private double boardPixelHeight;
    private ViewData lastViewData;

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
        numberOfRows = boardMatrix.length;  // rows (y dimension)
        numberOfColumns = boardMatrix[0].length;  // cols (x dimension)
        
        // Calculate board pixel dimensions
        boardPixelWidth = numberOfColumns * BRICK_SIZE;
        boardPixelHeight = numberOfRows * BRICK_SIZE;
        
        displayMatrix = new Rectangle[numberOfRows][numberOfColumns];
        
        // Loop: y (row) as outer, x (col) as inner
        // Map board[y][x] → GridPane column=x row=y
        for (int y = 0; y < numberOfRows; y++) {
            for (int x = 0; x < numberOfColumns; x++) {
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
        
        // Store ViewData for centering updates
        lastViewData = brick;
        
        // Set brick panel position relative to gamePanel
        updateBrickPanelPosition(brick);
        
        // Center the board after initialization
        Platform.runLater(() -> {
            if (gameBoard.getScene() != null) {
                javafx.stage.Window window = gameBoard.getScene().getWindow();
                if (window instanceof Stage) {
                    centerBoardInWindow((Stage) window);
                }
            }
        });
        
        timeLine = new Timeline(new KeyFrame(
                Duration.millis(400),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();
    }

    /**
     * Sets up window and fullscreen centering listeners for the stage.
     * Centers the board in windowed mode and when fullscreen mode is activated.
     */
    public void setupFullscreenCentering(Stage stage) {
        Scene scene = stage.getScene();
        if (scene == null) return;
        
        // Listen to fullscreen property changes
        stage.fullScreenProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    // Fullscreen activated - center the board
                    Platform.runLater(() -> centerBoardForFullscreen(stage));
                } else {
                    // Fullscreen deactivated - center in windowed mode
                    Platform.runLater(() -> centerBoardInWindow(stage));
                }
            }
        });
        
        // Listen to scene size changes to keep board centered
        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                Platform.runLater(() -> {
                    if (stage.isFullScreen()) {
                        centerBoardForFullscreen(stage);
                    } else {
                        centerBoardInWindow(stage);
                    }
                });
            }
        });
        
        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                Platform.runLater(() -> {
                    if (stage.isFullScreen()) {
                        centerBoardForFullscreen(stage);
                    } else {
                        centerBoardInWindow(stage);
                    }
                });
            }
        });
        
        // Initial centering in windowed mode
        Platform.runLater(() -> centerBoardInWindow(stage));
    }

    /**
     * Centers the board horizontally and vertically in windowed mode.
     */
    private void centerBoardInWindow(Stage stage) {
        Scene scene = stage.getScene();
        if (scene == null || boardPixelWidth == 0 || boardPixelHeight == 0 || gameBoard == null) return;
        
        double sceneWidth = scene.getWidth();
        double sceneHeight = scene.getHeight();
        
        // Calculate center position for the BorderPane (gameBoard)
        // Account for BorderPane's border width (12px from CSS)
        double borderWidth = 12.0;
        double totalWidth = boardPixelWidth + (borderWidth * 2);
        double totalHeight = boardPixelHeight + (borderWidth * 2);
        
        double centerX = (sceneWidth - totalWidth) / 2.0;
        double centerY = (sceneHeight - totalHeight) / 2.0;
        
        // Position the gameBoard (BorderPane container)
        gameBoard.setLayoutX(centerX);
        gameBoard.setLayoutY(centerY);
        
        // Center the game over panel
        centerGameOverPanel(scene);
        
        // Update brick panel position
        if (lastViewData != null) {
            updateBrickPanelPosition(lastViewData);
        }
    }

    /**
     * Centers the board horizontally and vertically in fullscreen mode.
     */
    private void centerBoardForFullscreen(Stage stage) {
        Scene scene = stage.getScene();
        if (scene == null || boardPixelWidth == 0 || boardPixelHeight == 0 || gameBoard == null) return;
        
        double sceneWidth = scene.getWidth();
        double sceneHeight = scene.getHeight();
        
        // Calculate center position for the BorderPane (gameBoard)
        // Account for BorderPane's border width (12px from CSS)
        double borderWidth = 12.0;
        double totalWidth = boardPixelWidth + (borderWidth * 2);
        double totalHeight = boardPixelHeight + (borderWidth * 2);
        
        double centerX = (sceneWidth - totalWidth) / 2.0;
        double centerY = (sceneHeight - totalHeight) / 2.0;
        
        // Position the gameBoard (BorderPane container)
        gameBoard.setLayoutX(centerX);
        gameBoard.setLayoutY(centerY);
        
        // Center the game over panel
        centerGameOverPanel(scene);
        
        // Update brick panel position
        if (lastViewData != null) {
            updateBrickPanelPosition(lastViewData);
        }
    }

    /**
     * Centers the game over panel horizontally and vertically.
     */
    private void centerGameOverPanel(Scene scene) {
        if (scene == null || groupNotification == null || gameOverPanel == null) return;
        
        double sceneWidth = scene.getWidth();
        double sceneHeight = scene.getHeight();
        
        // Get the preferred size of the game over panel
        double panelWidth = gameOverPanel.prefWidth(-1);
        double panelHeight = gameOverPanel.prefHeight(-1);
        
        // If preferred size is not available, use bounds
        if (panelWidth <= 0 || panelHeight <= 0) {
            javafx.geometry.Bounds bounds = gameOverPanel.getBoundsInLocal();
            panelWidth = bounds.getWidth();
            panelHeight = bounds.getHeight();
        }
        
        // If still not available, use default size
        if (panelWidth <= 0 || panelHeight <= 0) {
            panelWidth = 200; // Default width
            panelHeight = 50;  // Default height
        }
        
        // Calculate center position
        double centerX = (sceneWidth - panelWidth) / 2.0;
        double centerY = (sceneHeight - panelHeight) / 2.0;
        
        // Position the groupNotification (which contains the gameOverPanel)
        groupNotification.setLayoutX(centerX);
        groupNotification.setLayoutY(centerY);
    }

    /**
     * Updates the brick panel position relative to gameBoard (BorderPane).
     * Coordinate system: x = column, y = row
     * brickPanel must be positioned INSIDE gameBoard
     */
    private void updateBrickPanelPosition(ViewData brick) {
        // x = column, y = row
        // Position brickPanel relative to gameBoard's position
        // Account for BorderPane's border width (12px from CSS)
        double borderWidth = 12.0;
        double layoutX = gameBoard.getLayoutX() + borderWidth + brick.getxPosition() * BRICK_SIZE;
        double layoutY = gameBoard.getLayoutY() + borderWidth + brick.getyPosition() * BRICK_SIZE;
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
            // Store ViewData for fullscreen updates
            lastViewData = brick;
            
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
        
        // Center the game over panel when it's shown
        if (gameBoard.getScene() != null) {
            centerGameOverPanel(gameBoard.getScene());
        }
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
