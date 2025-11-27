package com.comp2042.view;

import com.comp2042.controller.EventSource;
import com.comp2042.controller.EventType;
import com.comp2042.controller.InputEventListener;
import com.comp2042.controller.MoveEvent;
import com.comp2042.model.HardDropResult;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the JavaFX GUI in the Tetris game.
 * <p>
 * This class is part of the View layer in the MVC architecture. It manages
 * all JavaFX UI components, handles user input events, coordinates rendering
 * of the game board and falling bricks, and manages visual effects such as
 * animations and notifications. It implements Initializable for FXML-based
 * initialization.
 * </p>
 * <p>
 * <strong>Coordinate System:</strong>
 * <ul>
 *   <li>x = column (horizontal position)</li>
 *   <li>y = row (vertical position)</li>
 *   <li>Board matrix: board[row][col] = board[y][x]</li>
 *   <li>GridPane: add(node, column, row) = add(node, x, y)</li>
 *   <li>Brick offset: (x=col, y=row)</li>
 * </ul>
 * </p>
 * <p>
 * Responsibilities:
 * <ul>
 *   <li>Initialize and manage JavaFX UI components (GridPanes, Groups)</li>
 *   <li>Handle keyboard input and translate to game events</li>
 *   <li>Render the game board and falling bricks</li>
 *   <li>Center the board in windowed and fullscreen modes</li>
 *   <li>Manage game state UI (pause, game over)</li>
 *   <li>Display score notifications and game over panel</li>
 * </ul>
 * </p>
 *
 * @author TetrisJFX Team
 * @version 1.0
 */
public class GuiController implements Initializable {

    /** Size of each brick cell in pixels. */
    private static final int BRICK_SIZE = 20;
    
    /** Gap between cells in GridPane (matches vgap and hgap). */
    private static final int CELL_GAP = 1;

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

    @FXML
    private VBox nextPieceContainer;

    @FXML
    private Label nextPieceLabel;

    @FXML
    private GridPane nextPanel;

    @FXML
    private VBox scoreboardContainer;

    @FXML
    private Label currentScoreLabel;

    @FXML
    private Label totalLinesLabel;

    @FXML
    private Label highScoreLabel;

    @FXML
    private Button restartButton;

    private Rectangle[][] displayMatrix;

    private InputEventListener eventListener;

    private Rectangle[][] rectangles;

    // Ghost piece components
    private GridPane ghostPanel;
    private Rectangle[][] ghostRectangles;

    // Next piece preview components
    private Rectangle[][] nextPieceMatrix;

    private Timeline timeLine;

    private final BooleanProperty isPause = new SimpleBooleanProperty();

    private final BooleanProperty isGameOver = new SimpleBooleanProperty();

    // Board dimensions for fullscreen centering
    private int numberOfColumns;
    private int numberOfRows;
    private double boardPixelWidth;
    private double boardPixelHeight;
    private ViewData lastViewData;

    // Pause overlay components
    private Group pauseOverlay;
    private Text pauseText;

    /**
     * Initializes the JavaFX controller after FXML loading.
     * <p>
     * Sets up keyboard event handlers, loads custom fonts, and initializes
     * UI component properties. This method is called automatically by JavaFX
     * after the FXML file is loaded.
     * </p>
     *
     * @param location  the location used to resolve relative paths for the
     *                  root object, or null if unknown
     * @param resources the resources used to localize the root object, or null
     *                  if unknown
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Font.loadFont(getClass().getClassLoader().getResource("digital.ttf")
                .toExternalForm(), 38);
        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();

        // Initialize pause overlay
        initializePauseOverlay();

        gamePanel.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                // P key toggles pause/unpause (works even when paused or game over)
                if (keyEvent.getCode() == KeyCode.P) {
                    togglePause();
                    keyEvent.consume();
                    return;
                }

                // R key restarts the game (works during game and after game over)
                if (keyEvent.getCode() == KeyCode.R) {
                    restartGame(null);
                    keyEvent.consume();
                    return;
                }

                // SPACE key performs hard drop (works when not paused and not game over)
                if (keyEvent.getCode() == KeyCode.SPACE) {
                    if (isPause.getValue() == Boolean.FALSE &&
                            isGameOver.getValue() == Boolean.FALSE &&
                            eventListener != null) {
                        handleHardDrop();
                        keyEvent.consume();
                        return;
                    }
                }

                // Only process movement keys when not paused and not game over
                if (isPause.getValue() == Boolean.FALSE &&
                        isGameOver.getValue() == Boolean.FALSE) {
                    if (keyEvent.getCode() == KeyCode.LEFT ||
                            keyEvent.getCode() == KeyCode.A) {
                        refreshBrick(eventListener.onLeftEvent(
                                new MoveEvent(EventType.LEFT, EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.RIGHT ||
                            keyEvent.getCode() == KeyCode.D) {
                        refreshBrick(eventListener.onRightEvent(
                                new MoveEvent(EventType.RIGHT, EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.UP ||
                            keyEvent.getCode() == KeyCode.W) {
                        refreshBrick(eventListener.onRotateEvent(
                                new MoveEvent(EventType.ROTATE, EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.DOWN ||
                            keyEvent.getCode() == KeyCode.S) {
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
     * Initializes the pause overlay components.
     * <p>
     * Creates a Group containing a "PAUSED" text label that will be displayed
     * when the game is paused. The overlay is initially hidden.
     * </p>
     */
    private void initializePauseOverlay() {
        pauseText = new Text("PAUSED");
        pauseText.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        pauseText.setFill(Color.WHITE);
        pauseText.setStroke(Color.BLACK);
        pauseText.setStrokeWidth(2);

        pauseOverlay = new Group(pauseText);
        pauseOverlay.setVisible(false);
        pauseOverlay.setMouseTransparent(true); // Allow clicks to pass through

        // Add pause overlay to the scene root (will be added in initGameView)
    }

    /**
     * Toggles the pause state of the game.
     * <p>
     * When pausing: stops the Timeline, disables movement, and shows the pause overlay.
     * When unpausing: restarts the Timeline, enables movement, and hides the pause overlay.
     * </p>
     */
    private void togglePause() {
        if (isGameOver.getValue() == Boolean.TRUE) {
            return; // Don't allow pausing when game is over
        }

        boolean newPauseState = !isPause.getValue();
        isPause.setValue(newPauseState);

        if (newPauseState) {
            // Pause: stop timeline and show overlay
            if (timeLine != null) {
                timeLine.pause();
            }
            showPauseOverlay();
        } else {
            // Unpause: resume timeline and hide overlay
            if (timeLine != null) {
                timeLine.play();
            }
            hidePauseOverlay();
        }
    }

    /**
     * Shows the pause overlay and centers it over the board.
     */
    private void showPauseOverlay() {
        if (pauseOverlay == null || gameBoard == null) return;

        pauseOverlay.setVisible(true);
        centerPauseOverlay();
    }

    /**
     * Hides the pause overlay.
     */
    private void hidePauseOverlay() {
        if (pauseOverlay == null) return;
        pauseOverlay.setVisible(false);
    }

    /**
     * Centers the pause overlay over the game board.
     * <p>
     * Calculates the center position based on the gameBoard's position and size,
     * accounting for the BorderPane border width.
     * </p>
     */
    private void centerPauseOverlay() {
        if (pauseOverlay == null || pauseText == null || gameBoard == null) return;

        // Get pause text bounds
        pauseText.applyCss();
        // Text nodes don't need explicit layout() - bounds are calculated automatically
        double textWidth = pauseText.getLayoutBounds().getWidth();
        double textHeight = pauseText.getLayoutBounds().getHeight();

        // Calculate center position relative to gameBoard
        double borderWidth = 12.0;
        double boardCenterX = gameBoard.getLayoutX() + borderWidth + boardPixelWidth / 2.0;
        double boardCenterY = gameBoard.getLayoutY() + borderWidth + boardPixelHeight / 2.0;

        // Center the text over the board
        double pauseX = boardCenterX - textWidth / 2.0;
        double pauseY = boardCenterY - textHeight / 2.0;

        pauseOverlay.setLayoutX(pauseX);
        pauseOverlay.setLayoutY(pauseY);
    }

    /**
     * Initializes the game view with the board matrix and initial brick state.
     * <p>
     * Creates Rectangle objects for each cell in the board and brick panels,
     * sets up the visual representation, and starts the automatic downward
     * movement timeline. The board is centered after initialization.
     * </p>
     * <p>
     * Coordinate system: x = column, y = row. Board matrix is indexed as
     * board[row][col] = board[y][x]. GridPane uses add(node, column, row).
     * </p>
     *
     * @param boardMatrix the initial board state matrix (board[row][col])
     * @param brick       the initial falling brick ViewData
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

        // Initialize ghost panel
        initializeGhostPanel(brick);

        // Initialize next piece preview
        initNextPiecePreview(brick.getNextBrickData());

        // Initialize scoreboard
        refreshScore(brick);

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
                    positionNextPanel();
                    positionScoreboard();
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
     * <p>
     * Registers listeners for fullscreen property changes and scene size
     * changes to keep the board centered both horizontally and vertically.
     * Also centers the board initially in windowed mode.
     * </p>
     *
     * @param stage the JavaFX Stage to monitor for fullscreen and size changes
     */
    public void setupFullscreenCentering(Stage stage) {
        Scene scene = stage.getScene();
        if (scene == null) return;

        // Add pause overlay and ghost panel to scene root if not already added
        javafx.scene.Parent root = scene.getRoot();
        if (root instanceof javafx.scene.layout.Pane) {
            javafx.scene.layout.Pane rootPane = (javafx.scene.layout.Pane) root;
            
            if (pauseOverlay != null && !rootPane.getChildren().contains(pauseOverlay)) {
                rootPane.getChildren().add(pauseOverlay);
            }
            
            if (ghostPanel != null && !rootPane.getChildren().contains(ghostPanel)) {
                rootPane.getChildren().add(ghostPanel);
            }
        }

        // Listen to fullscreen property changes
        stage.fullScreenProperty().addListener(
                new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable,
                                       Boolean oldValue, Boolean newValue) {
                        if (newValue) {
                            // Fullscreen activated - center the board
                            Platform.runLater(() -> {
                                centerBoardForFullscreen(stage);
                                if (isPause.getValue() == Boolean.TRUE) {
                                    centerPauseOverlay();
                                }
                            });
                        } else {
                            // Fullscreen deactivated - center in windowed mode
                            Platform.runLater(() -> {
                                centerBoardInWindow(stage);
                                if (isPause.getValue() == Boolean.TRUE) {
                                    centerPauseOverlay();
                                }
                            });
                        }
                    }
                });

        // Listen to scene size changes to keep board centered
        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable,
                               Number oldValue, Number newValue) {
                Platform.runLater(() -> {
                    if (stage.isFullScreen()) {
                        centerBoardForFullscreen(stage);
                    } else {
                        centerBoardInWindow(stage);
                    }
                    if (isPause.getValue() == Boolean.TRUE) {
                        centerPauseOverlay();
                    }
                });
            }
        });

        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable,
                               Number oldValue, Number newValue) {
                Platform.runLater(() -> {
                    if (stage.isFullScreen()) {
                        centerBoardForFullscreen(stage);
                    } else {
                        centerBoardInWindow(stage);
                    }
                    if (isPause.getValue() == Boolean.TRUE) {
                        centerPauseOverlay();
                    }
                });
            }
        });

        // Initial centering in windowed mode
        Platform.runLater(() -> centerBoardInWindow(stage));
    }

    /**
     * Centers the board horizontally and vertically in windowed mode.
     * <p>
     * Calculates the center position based on scene dimensions and board
     * pixel size, accounting for the BorderPane border width. Also centers
     * the game over panel and updates brick panel position.
     * </p>
     *
     * @param stage the JavaFX Stage containing the scene
     */
    private void centerBoardInWindow(Stage stage) {
        Scene scene = stage.getScene();
        if (scene == null || boardPixelWidth == 0 || boardPixelHeight == 0 ||
                gameBoard == null) return;

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

        // Center pause overlay if visible
        if (isPause.getValue() == Boolean.TRUE) {
            centerPauseOverlay();
        }

        // Update brick panel position
        if (lastViewData != null) {
            updateBrickPanelPosition(lastViewData);
            updateGhostPanelPosition(lastViewData);
        }

        // Update next panel position
        positionNextPanel();
        
        // Update scoreboard position
        positionScoreboard();
    }

    /**
     * Centers the board horizontally and vertically in fullscreen mode.
     * <p>
     * Uses the same centering logic as windowed mode but applies it to the
     * fullscreen scene dimensions.
     * </p>
     *
     * @param stage the JavaFX Stage in fullscreen mode
     */
    private void centerBoardForFullscreen(Stage stage) {
        Scene scene = stage.getScene();
        if (scene == null || boardPixelWidth == 0 || boardPixelHeight == 0 ||
                gameBoard == null) return;

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

        // Center pause overlay if visible
        if (isPause.getValue() == Boolean.TRUE) {
            centerPauseOverlay();
        }

        // Update brick panel position
        if (lastViewData != null) {
            updateBrickPanelPosition(lastViewData);
            updateGhostPanelPosition(lastViewData);
        }

        // Update next panel position
        positionNextPanel();
        
        // Update scoreboard position
        positionScoreboard();
    }

    /**
     * Centers the game over panel horizontally and vertically.
     * <p>
     * Calculates the center position based on scene dimensions and panel size.
     * Uses preferred size, bounds, or default size if preferred size is
     * unavailable.
     * </p>
     *
     * @param scene the JavaFX Scene containing the panel
     */
    private void centerGameOverPanel(Scene scene) {
        if (scene == null || groupNotification == null ||
                gameOverPanel == null) return;

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
     * Initializes the ghost panel for displaying the ghost piece.
     * <p>
     * Creates a GridPane with rectangles for the ghost piece. The ghost piece
     * shows where the active brick will land. It is rendered with semi-transparent
     * colors (opacity 0.3) behind the active brick.
     * </p>
     *
     * @param brick the ViewData containing the brick shape information
     */
    private void initializeGhostPanel(ViewData brick) {
        // Create ghost panel if it doesn't exist
        if (ghostPanel == null) {
            ghostPanel = new GridPane();
            ghostPanel.setHgap(1);
            ghostPanel.setVgap(1);
            ghostPanel.setMouseTransparent(true); // Allow clicks to pass through
            ghostPanel.setVisible(true); // Visible by default
            ghostPanel.setViewOrder(1.0); // Render behind active brick (lower view order = behind)

            // Add ghost panel to scene root
            if (gameBoard.getScene() != null) {
                javafx.scene.Parent root = gameBoard.getScene().getRoot();
                if (root instanceof javafx.scene.layout.Pane) {
                    javafx.scene.layout.Pane rootPane = (javafx.scene.layout.Pane) root;
                    if (!rootPane.getChildren().contains(ghostPanel)) {
                        rootPane.getChildren().add(ghostPanel);
                    }
                }
            }
        } else {
            // Ensure ghost panel is visible when reinitializing
            ghostPanel.setVisible(true);
        }

        // Clear old ghost rectangles
        ghostPanel.getChildren().clear();

        // Initialize ghost rectangles
        int[][] brickData = brick.getBrickData();
        int brickHeight = brickData.length;
        int brickWidth = brickData[0].length;
        ghostRectangles = new Rectangle[brickHeight][brickWidth];

        // Loop: y (row) as outer, x (col) as inner
        for (int y = 0; y < brickHeight; y++) {
            for (int x = 0; x < brickWidth; x++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                // Set ghost color with opacity 0.3
                Paint ghostColor = getGhostColor(brickData[y][x]);
                rectangle.setFill(ghostColor);
                rectangle.setArcHeight(9);
                rectangle.setArcWidth(9);
                ghostRectangles[y][x] = rectangle;
                // GridPane.add(node, column, row) = add(node, x, y)
                ghostPanel.add(rectangle, x, y);
            }
        }
    }

    /**
     * Updates the brick panel position relative to the centered gameBoard.
     * <p>
     * Positions the falling brick panel based on the brick's (x, y) position
     * and the gameBoard's layout position, accounting for the BorderPane
     * border width.
     * </p>
     * <p>
     * Coordinate system: x = column, y = row. The brickPanel must be
     * positioned inside the gameBoard.
     * </p>
     *
     * @param brick the ViewData containing the brick's current position
     */
    private void updateBrickPanelPosition(ViewData brick) {
        // x = column, y = row
        // Position brickPanel relative to gameBoard's position
        // Account for BorderPane's border width (12px from CSS)
        // Account for GridPane gaps: each cell is BRICK_SIZE + CELL_GAP apart
        double borderWidth = 12.0;
        double cellWidth = BRICK_SIZE + CELL_GAP;
        double cellHeight = BRICK_SIZE + CELL_GAP;
        double layoutX = gameBoard.getLayoutX() + borderWidth +
                brick.getxPosition() * cellWidth;
        double layoutY = gameBoard.getLayoutY() + borderWidth +
                brick.getyPosition() * cellHeight;
        brickPanel.setLayoutX(layoutX);
        brickPanel.setLayoutY(layoutY);
    }

    /**
     * Updates the ghost panel position relative to the centered gameBoard.
     * <p>
     * Positions the ghost panel at the ghost Y position (where the brick
     * would land) with the same X position as the active brick.
     * </p>
     * <p>
     * Coordinate system: x = column, y = row. The ghostPanel must be
     * positioned inside the gameBoard.
     * </p>
     *
     * @param brick the ViewData containing the brick's position and ghost position
     */
    private void updateGhostPanelPosition(ViewData brick) {
        if (ghostPanel == null || gameBoard == null) return;

        // x = column, y = row (ghost Y position)
        // Position ghostPanel relative to gameBoard's position
        // Account for BorderPane's border width (12px from CSS)
        // Account for GridPane gaps: each cell is BRICK_SIZE + CELL_GAP apart
        double borderWidth = 12.0;
        double cellWidth = BRICK_SIZE + CELL_GAP;
        double cellHeight = BRICK_SIZE + CELL_GAP;
        double layoutX = gameBoard.getLayoutX() + borderWidth +
                brick.getxPosition() * cellWidth;
        double layoutY = gameBoard.getLayoutY() + borderWidth +
                brick.getGhostYPosition() * cellHeight;
        ghostPanel.setLayoutX(layoutX);
        ghostPanel.setLayoutY(layoutY);
    }

    /**
     * Gets the ghost color for a given color value with 0.3 opacity.
     * <p>
     * Returns a semi-transparent version of the brick color to create
     * the ghost piece effect.
     * </p>
     *
     * @param colorValue the color value (0-7) from the brick
     * @return a Color with opacity 0.3, or TRANSPARENT if colorValue is 0
     */
    private Paint getGhostColor(int colorValue) {
        if (colorValue == 0) {
            return Color.TRANSPARENT;
        }

        // Get the base color and apply 0.3 opacity
        Color baseColor = (Color) getFillColor(colorValue);
        return new Color(baseColor.getRed(), baseColor.getGreen(),
                baseColor.getBlue(), 0.3);
    }

    /**
     * Maps a color value to a JavaFX Paint object.
     * <p>
     * Color values correspond to brick types: 0=transparent, 1=aqua, 2=blue
     * violet, 3=dark green, 4=yellow, 5=red, 6=beige, 7=burlywood.
     * </p>
     *
     * @param i the color value (0-7)
     * @return the corresponding Paint object for rendering
     */
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
     * Initializes the next piece preview panel.
     * <p>
     * Creates a GridPane with rectangles for displaying the next falling piece.
     * The preview shows the exact shape that will spawn next, centered within
     * the preview panel. The panel is sized to accommodate the largest possible
     * brick shape (6x6 grid for better visibility).
     * </p>
     *
     * @param nextBrick the 2D integer array representing the next brick shape
     *                  (nextBrick[row][col])
     */
    private void initNextPiecePreview(int[][] nextBrick) {
        if (nextPanel == null) return;

        // Clear old rectangles
        nextPanel.getChildren().clear();

        // Get dimensions of the next brick
        int brickHeight = nextBrick.length;
        int brickWidth = nextBrick[0].length;

        // Initialize next piece matrix
        nextPieceMatrix = new Rectangle[brickHeight][brickWidth];

        // Set fixed size for the preview panel to prevent distortion
        // Use a 6x6 grid to make the box bigger and accommodate all brick shapes
        int maxSize = 6;
        double cellSize = BRICK_SIZE + CELL_GAP;
        double panelWidth = maxSize * cellSize;
        double panelHeight = maxSize * cellSize;
        nextPanel.setPrefSize(panelWidth, panelHeight);
        nextPanel.setMinSize(panelWidth, panelHeight);
        nextPanel.setMaxSize(panelWidth, panelHeight);

        // Calculate centering offsets to center the brick in the preview panel
        int offsetX = (maxSize - brickWidth) / 2;
        int offsetY = (maxSize - brickHeight) / 2;

        // Create rectangles for the next brick
        // Loop: y (row) as outer, x (col) as inner
        for (int y = 0; y < brickHeight; y++) {
            for (int x = 0; x < brickWidth; x++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(getFillColor(nextBrick[y][x]));
                rectangle.setArcHeight(9);
                rectangle.setArcWidth(9);
                nextPieceMatrix[y][x] = rectangle;
                // Add to GridPane with centering offset
                // GridPane.add(node, column, row) = add(node, x + offsetX, y + offsetY)
                nextPanel.add(rectangle, x + offsetX, y + offsetY);
            }
        }

        // Position the next panel beside the game board
        positionNextPanel();
    }

    /**
     * Refreshes the next piece preview with a new brick shape.
     * <p>
     * Updates the visual representation of the next piece preview panel.
     * Reinitializes the preview if the brick dimensions have changed.
     * </p>
     *
     * @param nextBrick the 2D integer array representing the next brick shape
     *                  (nextBrick[row][col])
     */
    private void refreshNextPiece(int[][] nextBrick) {
        if (nextPanel == null) return;

        int brickHeight = nextBrick.length;
        int brickWidth = nextBrick[0].length;

        // Reinitialize if dimensions changed
        if (nextPieceMatrix == null || nextPieceMatrix.length != brickHeight ||
                (nextPieceMatrix.length > 0 && nextPieceMatrix[0].length != brickWidth)) {
            initNextPiecePreview(nextBrick);
            return;
        }

        // Update existing rectangles
        // Loop: y (row) as outer, x (col) as inner
        for (int y = 0; y < brickHeight; y++) {
            for (int x = 0; x < brickWidth; x++) {
                if (nextPieceMatrix[y][x] != null) {
                    setRectangleData(nextBrick[y][x], nextPieceMatrix[y][x]);
                }
            }
        }
    }

    /**
     * Positions the next piece preview panel beside the main game board.
     * <p>
     * Calculates the position based on the game board's location and places
     * the preview panel to the right of the board with appropriate spacing.
     * The panel is positioned to align with the top of the game board.
     * </p>
     */
    private void positionNextPanel() {
        if (nextPieceContainer == null || gameBoard == null) return;

        // Position next piece container (VBox with label and panel) to the right of the game board
        // Account for BorderPane's border width (12px from CSS)
        double borderWidth = 12.0;
        double spacing = 20.0; // Space between board and preview
        double nextPanelX = gameBoard.getLayoutX() + boardPixelWidth + (borderWidth * 2) + spacing;
        double nextPanelY = gameBoard.getLayoutY() + borderWidth;

        nextPieceContainer.setLayoutX(nextPanelX);
        nextPieceContainer.setLayoutY(nextPanelY);
    }

    /**
     * Positions the scoreboard panel to the left of the game board.
     * <p>
     * Calculates the position based on the game board's location and places
     * the scoreboard to the left of the board with appropriate spacing. The
     * scoreboard is aligned with the top of the game board vertically.
     * </p>
     */
    private void positionScoreboard() {
        if (scoreboardContainer == null || gameBoard == null) return;

        // Position scoreboard to the left of the game board
        // Account for BorderPane's border width (12px from CSS)
        double borderWidth = 12.0;
        double spacing = 20.0; // Space between board and scoreboard
        
        // Calculate X position: to the left of the game board
        double scoreboardX = gameBoard.getLayoutX() - spacing;
        
        // Get the width of the scoreboard container to position it correctly
        double scoreboardWidth = scoreboardContainer.getBoundsInLocal().getWidth();
        if (scoreboardWidth <= 0) {
            // Fallback: estimate width if bounds not available yet
            scoreboardWidth = 150.0; // Approximate width of scoreboard
        }
        
        // Position it so the right edge of scoreboard is spaced from the left edge of game board
        scoreboardX = scoreboardX - scoreboardWidth;
        
        // Align vertically with the top of the game board
        double scoreboardY = gameBoard.getLayoutY() + borderWidth;

        scoreboardContainer.setLayoutX(scoreboardX);
        scoreboardContainer.setLayoutY(scoreboardY);
    }

    /**
     * Refreshes the scoreboard display with current score information.
     * <p>
     * Updates the score labels (current score, total lines, high score) based
     * on the ViewData. This method is called whenever the game state changes
     * (brick locks, rows cleared, new game).
     * </p>
     * <p>
     * This method is public to allow external controllers (e.g., GameController)
     * to update the scoreboard when needed.
     * </p>
     *
     * @param viewData the ViewData containing current score information
     */
    public void refreshScoreboard(ViewData viewData) {
        if (currentScoreLabel != null) {
            currentScoreLabel.setText(String.valueOf(viewData.getScore()));
        }
        if (totalLinesLabel != null) {
            totalLinesLabel.setText(String.valueOf(viewData.getTotalLines()));
        }
        if (highScoreLabel != null) {
            highScoreLabel.setText(String.valueOf(viewData.getHighScore()));
        }
    }

    /**
     * Private helper method that calls refreshScoreboard.
     * <p>
     * This is kept for internal use within GuiController.
     * </p>
     *
     * @param viewData the ViewData containing current score information
     */
    private void refreshScore(ViewData viewData) {
        refreshScoreboard(viewData);
    }

    /**
     * Refreshes the brick visual representation and position.
     * <p>
     * Updates the brick panel position and visual appearance based on the
     * current ViewData. Also updates the ghost piece position and appearance,
     * and refreshes the next piece preview. Only updates if the game is not paused.
     * </p>
     * <p>
     * Coordinate system: x = column, y = row. Brick data is indexed as
     * brick[row][col].
     * </p>
     *
     * @param brick the ViewData containing the current brick state
     */
    private void refreshBrick(ViewData brick) {
        if (isPause.getValue() == Boolean.FALSE) {
            // Store ViewData for fullscreen updates
            lastViewData = brick;

            // Update brick panel position using clean coordinate translation
            updateBrickPanelPosition(brick);

            // Update ghost panel position
            updateGhostPanelPosition(brick);

            // Update next piece preview
            refreshNextPiece(brick.getNextBrickData());

            // Update scoreboard
            refreshScore(brick);

            // Update brick visual representation
            // brick.getBrickData() is [rows][cols] = [y][x]
            int brickHeight = brick.getBrickData().length;
            int brickWidth = brick.getBrickData()[0].length;
            
            // Reinitialize ghost panel if brick dimensions changed (e.g., after rotation)
            if (ghostRectangles == null || ghostRectangles.length != brickHeight ||
                    (ghostRectangles.length > 0 && ghostRectangles[0].length != brickWidth)) {
                initializeGhostPanel(brick);
            }
            
            // Loop: y (row) as outer, x (col) as inner
            for (int y = 0; y < brickHeight; y++) {
                for (int x = 0; x < brickWidth; x++) {
                    setRectangleData(brick.getBrickData()[y][x],
                            rectangles[y][x]);
                    // Update ghost rectangles with semi-transparent colors
                    if (ghostRectangles != null && y < ghostRectangles.length &&
                            x < ghostRectangles[y].length) {
                        Paint ghostColor = getGhostColor(brick.getBrickData()[y][x]);
                        ghostRectangles[y][x].setFill(ghostColor);
                    }
                }
            }
        }
    }

    /**
     * Refreshes the game background board display.
     * <p>
     * Updates all Rectangle objects in the displayMatrix to reflect the
     * current board state. This is called after bricks are merged or rows
     * are cleared.
     * </p>
     * <p>
     * Coordinate system: x = column, y = row. Board matrix is indexed as
     * board[row][col].
     * </p>
     *
     * @param board the current board state matrix (board[row][col])
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

    /**
     * Sets the visual properties of a Rectangle based on its color value.
     * <p>
     * Applies the fill color and rounded corner styling to the rectangle.
     * </p>
     *
     * @param color     the color value (0-7) to apply
     * @param rectangle the Rectangle to style
     */
    private void setRectangleData(int color, Rectangle rectangle) {
        rectangle.setFill(getFillColor(color));
        rectangle.setArcHeight(9);
        rectangle.setArcWidth(9);
    }

    /**
     * Handles the automatic downward movement of the brick.
     * <p>
     * Processes the down event, displays score notifications if rows were
     * cleared, and refreshes the brick display. This method is called
     * automatically by the Timeline animation. Movement is disabled when paused.
     * </p>
     *
     * @param event the MoveEvent containing event type and source
     */
    private void moveDown(MoveEvent event) {
        if (isPause.getValue() == Boolean.FALSE) {
            DownData downData = eventListener.onDownEvent(event);
            if (downData.getClearRow() != null &&
                    downData.getClearRow().getLinesRemoved() > 0) {
                NotificationPanel notificationPanel = new NotificationPanel(
                        "+" + downData.getClearRow().getScoreBonus());
                groupNotification.getChildren().add(notificationPanel);
                notificationPanel.showScore(groupNotification.getChildren());
            }
            refreshBrick(downData.getViewData());
        }
        gamePanel.requestFocus();
    }

    /**
     * Sets the event listener for handling game input events.
     * <p>
     * The event listener (typically a GameController) receives all user input
     * events and coordinates with the game board.
     * </p>
     *
     * @param eventListener the InputEventListener to handle game events
     */
    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
    }

    /**
     * Binds the score property to a UI component.
     * <p>
     * Currently not implemented. Reserved for future score display binding.
     * </p>
     *
     * @param integerProperty the IntegerProperty to bind (currently unused)
     */
    public void bindScore(IntegerProperty integerProperty) {
    }

    /**
     * Displays the game over state.
     * <p>
     * Stops the automatic movement timeline, shows the game over panel, sets
     * the game over flag, and centers the game over panel.
     * </p>
     */
    public void gameOver() {
        timeLine.stop();
        gameOverPanel.setVisible(true);
        isGameOver.setValue(Boolean.TRUE);

        // Hide pause overlay if visible
        hidePauseOverlay();
        isPause.setValue(Boolean.FALSE);

        // Hide ghost panel
        if (ghostPanel != null) {
            ghostPanel.setVisible(false);
        }

        // Center the game over panel when it's shown
        if (gameBoard.getScene() != null) {
            centerGameOverPanel(gameBoard.getScene());
        }
    }

    /**
     * Starts a new game.
     * <p>
     * Resets the game state, hides the game over panel, requests a new game
     * from the event listener, restarts the timeline, and resets pause and
     * game over flags.
     * </p>
     *
     * @param actionEvent the action event that triggered this method (can be
     *                    null)
     */
    public void newGame(ActionEvent actionEvent) {
        timeLine.stop();
        gameOverPanel.setVisible(false);
        hidePauseOverlay();
        
        // Show ghost panel for new game
        if (ghostPanel != null) {
            ghostPanel.setVisible(true);
        }
        
        eventListener.createNewGame();
        gamePanel.requestFocus();
        timeLine.play();
        isPause.setValue(Boolean.FALSE);
        isGameOver.setValue(Boolean.FALSE);
    }

    /**
     * Handles the pause game action.
     * <p>
     * Currently only requests focus for the game panel. Pause functionality
     * may be implemented in the future.
     * </p>
     *
     * @param actionEvent the action event that triggered this method (can be
     *                    null)
     */
    public void pauseGame(ActionEvent actionEvent) {
        togglePause();
    }

    /**
     * Handles the hard drop operation triggered by the SPACE key.
     * <p>
     * Performs an instant hard drop of the current brick to the lowest possible
     * position. The brick is locked immediately, rows are cleared if applicable,
     * and the board and score are updated. Displays score notifications if rows
     * were cleared.
     * </p>
     * <p>
     * This method is only called when the game is not paused and not game over.
     * </p>
     */
    private void handleHardDrop() {
        if (eventListener == null) return;

        // Cast eventListener to GameController to access onHardDropEvent()
        if (eventListener instanceof com.comp2042.controller.GameController) {
            com.comp2042.controller.GameController gameController =
                    (com.comp2042.controller.GameController) eventListener;
            
            com.comp2042.model.HardDropResult result = gameController.onHardDropEvent();
            
            // Display score notification if rows were cleared
            if (result.getClearRow() != null &&
                    result.getClearRow().getLinesRemoved() > 0) {
                NotificationPanel notificationPanel = new NotificationPanel(
                        "+" + result.getClearRow().getScoreBonus());
                groupNotification.getChildren().add(notificationPanel);
                notificationPanel.showScore(groupNotification.getChildren());
            }
            
            // Refresh the brick display with the new brick
            refreshBrick(result.getViewData());
        }
        
        gamePanel.requestFocus();
    }

    /**
     * Restarts the game completely.
     * <p>
     * This method performs a full game restart by:
     * <ul>
     *   <li>Stopping the current timeline</li>
     *   <li>Resetting pause and game over states</li>
     *   <li>Hiding the game over panel and pause overlay</li>
     *   <li>Resetting the board through the event listener</li>
     *   <li>Reinitializing the view with the reset board state</li>
     *   <li>Resetting the timeline to default speed (400ms)</li>
     *   <li>Clearing and reinitializing the ghost piece</li>
     *   <li>Centering all UI elements</li>
     * </ul>
     * This method works both during a running game and after game over.
     * </p>
     *
     * @param actionEvent the action event that triggered this method (can be
     *                    null, e.g., when called from keyboard shortcut)
     */
    public void restartGame(ActionEvent actionEvent) {
        // Stop the timeline
        if (timeLine != null) {
            timeLine.stop();
        }

        // Reset pause state
        isPause.setValue(Boolean.FALSE);
        hidePauseOverlay();

        // Reset game over state and hide game over panel
        isGameOver.setValue(Boolean.FALSE);
        gameOverPanel.setVisible(false);

        // Clear ghost panel
        if (ghostPanel != null) {
            ghostPanel.getChildren().clear();
            ghostRectangles = null;
            ghostPanel.setVisible(false);
        }

        // Reset the board through the event listener
        // createNewGame() already calls refreshGameBackground and refreshScoreboard
        if (eventListener != null) {
            eventListener.createNewGame();
            
            // Get the current view data after reset to refresh the brick display
            // Use a non-moving event (left) to get current state - at spawn position,
            // moving left should fail and return current ViewData without side effects
            ViewData resetViewData = eventListener.onLeftEvent(
                    new MoveEvent(EventType.LEFT, EventSource.THREAD));
            
            // Refresh the brick display with the reset state
            refreshBrick(resetViewData);
        }

        // Reinitialize the timeline with default speed (400ms)
        timeLine = new Timeline(new KeyFrame(
                Duration.millis(400),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();

        // Request focus for keyboard input
        gamePanel.requestFocus();

        // Center everything
        Platform.runLater(() -> {
            if (gameBoard.getScene() != null) {
                javafx.stage.Window window = gameBoard.getScene().getWindow();
                if (window instanceof Stage) {
                    if (((Stage) window).isFullScreen()) {
                        centerBoardForFullscreen((Stage) window);
                    } else {
                        centerBoardInWindow((Stage) window);
                    }
                }
            }
        });
    }
}
