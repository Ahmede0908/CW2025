package com.comp2042.view;

import com.comp2042.view.GameOverPanel;

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

    @FXML private BorderPane gameBoard;
    @FXML private GridPane gamePanel;
    @FXML private GridPane brickPanel;
    @FXML private GridPane ghostPanel;

    @FXML private VBox nextPieceContainer;
    @FXML private GridPane nextPanel;

    @FXML private VBox scoreboardContainer;
    @FXML private Label currentLevelLabel;
    @FXML private Label currentScoreLabel;
    @FXML private Label totalLinesLabel;
    @FXML private Label highScoreLabel;
    
    @FXML private VBox controlsContainer;

    @FXML private Group groupNotification;

    @FXML private GameOverPanel gameOverPanelFXML;
    
    private GameOverPanel gameOverPanel;


    private Rectangle[][] displayMatrix;

    private InputEventListener eventListener;

    private Rectangle[][] rectangles;

    // Ghost piece components
    private Rectangle[][] ghostRectangles;

    // Next piece preview components
    private Rectangle[][] nextPieceMatrix;

    private Timeline timeLine;

    private final BooleanProperty isPause = new SimpleBooleanProperty();
    
    // Reference to Score object for level-based speed calculation
    private com.comp2042.model.Score scoreReference;
    
    // Global settings reference
    private GlobalSettings globalSettings;
    
    // Settings flags
    private boolean ghostPieceEnabled = true;
    private boolean hardDropEnabled = true;
    private long difficultyBaseSpeed = 400; // Default NORMAL speed

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
    private Text resumeText;
    private javafx.scene.layout.Pane dimOverlay;

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
        // Use Platform.runLater to prevent focus issues that might exit fullscreen
        javafx.application.Platform.runLater(() -> gamePanel.requestFocus());

        // Initialize pause overlay
        initializePauseOverlay();

        // Initialize game over panel - use FXML injected one if available, otherwise create new
        if (gameOverPanelFXML != null) {
            gameOverPanel = gameOverPanelFXML;
        } else if (gameOverPanel == null) {
            gameOverPanel = new GameOverPanel();
            if (groupNotification != null) {
                groupNotification.getChildren().add(gameOverPanel);
            }
        }
        
        // Prevent buttons from stealing focus or exiting fullscreen
        // Will be set up after FXML injection completes
        javafx.application.Platform.runLater(() -> {
            setupButtonFocusHandling();
        });

        gamePanel.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                // F11 key toggles fullscreen (only way to exit fullscreen)
                if (keyEvent.getCode() == KeyCode.F11) {
                    if (sceneManager != null) {
                        sceneManager.toggleFullscreen();
                    }
                    keyEvent.consume();
                    return;
                }
                
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
                            eventListener != null &&
                            hardDropEnabled) { // Check if hard drop is enabled
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
        
        // Set game over panel invisible initially (with null check)
        if (gameOverPanel != null) {
            gameOverPanel.setVisible(false);
        }

        final Reflection reflection = new Reflection();
        reflection.setFraction(0.8);
        reflection.setTopOpacity(0.9);
        reflection.setTopOffset(-12);
    }

    private SceneManager sceneManager;

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }
    
    /**
     * Sets up button focus handling to prevent buttons from exiting fullscreen.
     */
    private void setupButtonFocusHandling() {
        // Find all buttons in the scene and disable focus traversal
        if (gameBoard != null && gameBoard.getScene() != null) {
            javafx.scene.Parent root = gameBoard.getScene().getRoot();
            if (root != null) {
                setupButtonFocusRecursive(root);
            }
        }
    }
    
    /**
     * Recursively sets focusTraversable(false) on all buttons to prevent focus issues.
     */
    private void setupButtonFocusRecursive(javafx.scene.Node node) {
        if (node instanceof javafx.scene.control.Button) {
            javafx.scene.control.Button button = (javafx.scene.control.Button) node;
            button.setFocusTraversable(false);
        }
        if (node instanceof javafx.scene.Parent) {
            javafx.scene.Parent parent = (javafx.scene.Parent) node;
            for (javafx.scene.Node child : parent.getChildrenUnmodifiable()) {
                setupButtonFocusRecursive(child);
            }
        }
    }

    /**
     * Applies global settings to the game controller.
     * <p>
     * This method is called by SceneManager before creating a new GameController
     * to ensure all settings are applied correctly. It updates:
     * - Ghost piece visibility
     * - Hard drop functionality
     * - Game difficulty (fall speed)
     * </p>
     *
     * @param settings the GlobalSettings instance containing current settings
     */
    public void applySettings(GlobalSettings settings) {
        if (settings == null) return;
        
        this.globalSettings = settings;
        
        // Update ghost piece setting
        ghostPieceEnabled = settings.isGhostPieceEnabled();
        
        // Update hard drop setting
        hardDropEnabled = settings.isHardDropEnabled();
        
        // Update difficulty base speed
        difficultyBaseSpeed = settings.getFallSpeedMillis();
        
        // Update ghost panel visibility immediately
        if (ghostPanel != null) {
            ghostPanel.setVisible(ghostPieceEnabled);
        }
        
        // Update game speed if timeline exists
        if (timeLine != null && scoreReference != null) {
            updateGameSpeed();
        } else if (timeLine == null && scoreReference != null) {
            // Timeline not created yet, but score reference exists - will be set when timeline is created
            // The initial timeline creation should use difficultyBaseSpeed
        }
    }

    /**
     * Initializes the pause overlay components.
     * <p>
     * Creates a Group containing "PAUSED" text and "Press P to resume" instructions
     * with a semi-transparent background. The overlay is initially hidden.
     * </p>
     */
    private void initializePauseOverlay() {
        // Create full-screen dim overlay that will cover entire scene
        dimOverlay = new javafx.scene.layout.Pane();
        dimOverlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.75);");
        
        // Create "PAUSED" text with bright, visible styling
        pauseText = new Text("PAUSED");
        pauseText.setFont(Font.loadFont(getClass().getClassLoader().getResource("digital.ttf").toExternalForm(), 64));
        pauseText.setFill(Color.web("#00ffff"));
        pauseText.setStroke(Color.web("#ffffff"));
        pauseText.setStrokeWidth(4);
        pauseText.getStyleClass().add("pause-text");
        pauseText.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        
        // Create "Press P to resume" text - brighter and more visible
        resumeText = new Text("PRESS P TO RESUME");
        resumeText.setFont(Font.loadFont(getClass().getClassLoader().getResource("digital.ttf").toExternalForm(), 26));
        resumeText.setFill(Color.web("#ffffff"));
        resumeText.setStroke(Color.web("#00ffff"));
        resumeText.setStrokeWidth(3);
        resumeText.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        
        // Create VBox container for vertical layout with spacing
        VBox pauseContainer = new VBox(30);
        pauseContainer.setAlignment(javafx.geometry.Pos.CENTER);
        pauseContainer.getChildren().addAll(pauseText, resumeText);
        pauseContainer.setStyle("-fx-padding: 20px 40px;");
        pauseContainer.setMaxWidth(javafx.scene.layout.Region.USE_PREF_SIZE);
        pauseContainer.setMaxHeight(javafx.scene.layout.Region.USE_PREF_SIZE);
        
        // Create StackPane to layer dim overlay behind text
        javafx.scene.layout.StackPane stackPane = new javafx.scene.layout.StackPane();
        stackPane.getChildren().addAll(dimOverlay, pauseContainer);
        
        pauseOverlay = new Group(stackPane);
        pauseOverlay.setVisible(false);
        pauseOverlay.setMouseTransparent(true); // Allow clicks to pass through
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
        if (pauseOverlay == null) return;

        pauseOverlay.setVisible(true);
        
        // Bring to front to ensure it's above everything
        pauseOverlay.toFront();
        
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
     * Positions the pause overlay to cover the entire screen with dim overlay.
     * <p>
     * Makes the dim overlay cover the entire scene and centers the text in the middle.
     * </p>
     */
    private void centerPauseOverlay() {
        if (pauseOverlay == null || dimOverlay == null) return;
        
        // Get scene reference
        if (gameBoard == null || gameBoard.getScene() == null) {
            return;
        }
        
        final javafx.scene.Scene scene = gameBoard.getScene();

        // Use Platform.runLater to ensure layout is complete
        javafx.application.Platform.runLater(() -> {
            double sceneWidth = scene.getWidth();
            double sceneHeight = scene.getHeight();
            
            // If scene dimensions are not ready, try again
            if (sceneWidth <= 0 || sceneHeight <= 0) {
                javafx.application.Platform.runLater(() -> centerPauseOverlay());
                return;
            }
            
            // Set dim overlay to cover entire scene
            dimOverlay.setPrefWidth(sceneWidth);
            dimOverlay.setPrefHeight(sceneHeight);
            dimOverlay.setLayoutX(0);
            dimOverlay.setLayoutY(0);
            
            // Position the overlay group at (0, 0) to cover the scene
            pauseOverlay.setLayoutX(0);
            pauseOverlay.setLayoutY(0);
            
            // Ensure overlay is on top by bringing it to front
            if (pauseOverlay.getParent() != null) {
                pauseOverlay.toFront();
            }
        });
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
                // Add rounded corners for NES-style blocks
                rectangle.setArcHeight(2);
                rectangle.setArcWidth(2);
                rectangles[y][x] = rectangle;
                // GridPane.add(node, column, row) = add(node, x, y)
                brickPanel.add(rectangle, x, y);
            }
        }
        
        // Ensure brickPanel is visible and on top (critical for seeing falling blocks)
        if (brickPanel != null) {
            brickPanel.setVisible(true);
            brickPanel.setViewOrder(-1.0); // Negative view order = render on top of gameBoard
            brickPanel.toFront(); // Force to front of z-order
            brickPanel.setMouseTransparent(false); // Allow interaction
        }

        // Initialize ghost panel
        initializeGhostPanel(brick);

        // Initialize next piece preview
        initNextPiecePreview(brick.getNextBrickData());

        // Initialize scoreboard
        refreshScore(brick);

        // Store ViewData for centering updates
        lastViewData = brick;

        // Ensure brick panel and ghost panel are added to root and visible
        if (gameBoard.getScene() != null) {
            javafx.scene.Parent root = gameBoard.getScene().getRoot();
            if (root instanceof javafx.scene.layout.Pane) {
                javafx.scene.layout.Pane rootPane = (javafx.scene.layout.Pane) root;
                if (brickPanel != null && !rootPane.getChildren().contains(brickPanel)) {
                    rootPane.getChildren().add(brickPanel);
                    brickPanel.setVisible(true);
                }
                if (ghostPanel != null && !rootPane.getChildren().contains(ghostPanel)) {
                    rootPane.getChildren().add(ghostPanel);
                    ghostPanel.setVisible(ghostPieceEnabled);
                }
            }
        }

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
                    // Ensure panels are visible after centering
                    if (brickPanel != null) {
                        brickPanel.setVisible(true);
                    }
                    if (ghostPanel != null) {
                        ghostPanel.setVisible(ghostPieceEnabled);
                    }
                }
            }
        });

        // Use difficulty base speed if available, otherwise default to 400ms
        long initialSpeed = (difficultyBaseSpeed > 0) ? difficultyBaseSpeed : 400;
        timeLine = new Timeline(new KeyFrame(
                Duration.millis(initialSpeed),
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
    /**
     * Forces re-centering of the game board.
     * Useful when fullscreen state changes or scene is resized.
     *
     * @param stage the Stage to center for
     */
    public void forceRecenter(Stage stage) {
        if (stage == null || stage.getScene() == null) return;
        
        Scene scene = stage.getScene();
        
        // Ensure scene dimensions are valid before centering
        if (scene.getWidth() <= 0 || scene.getHeight() <= 0) {
            // Wait for dimensions to be set
            javafx.application.Platform.runLater(() -> {
                forceRecenter(stage);
            });
            return;
        }
        
        // Ensure scene dimensions are updated
        scene.getRoot().applyCss();
        scene.getRoot().layout();
        
        if (stage.isFullScreen()) {
            centerBoardForFullscreen(stage);
        } else {
            centerBoardInWindow(stage);
        }
    }

    public void setupFullscreenCentering(Stage stage) {
        Scene scene = stage.getScene();
        if (scene == null) return;

        // Add pause overlay, ghost panel, and brick panel to scene root if not already added
        javafx.scene.Parent root = scene.getRoot();
        if (root instanceof javafx.scene.layout.Pane) {
            javafx.scene.layout.Pane rootPane = (javafx.scene.layout.Pane) root;
            
            if (pauseOverlay != null && !rootPane.getChildren().contains(pauseOverlay)) {
                rootPane.getChildren().add(pauseOverlay);
                // Ensure pause overlay is always on top
                pauseOverlay.toFront();
            }
            
            // Ensure ghost panel is added and visible
            if (ghostPanel != null && !rootPane.getChildren().contains(ghostPanel)) {
                rootPane.getChildren().add(ghostPanel);
                ghostPanel.setVisible(ghostPieceEnabled);
            }
            
            // Ensure brick panel is added and visible (critical for seeing falling blocks)
            if (brickPanel != null && !rootPane.getChildren().contains(brickPanel)) {
                rootPane.getChildren().add(brickPanel);
                brickPanel.setVisible(true);
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
        // Account for BorderPane's border width (8px from CSS - NES style)
        double borderWidth = 8.0;
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
        
        // Update controls position
        positionControlsPanel();
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
        // Account for BorderPane's border width (8px from CSS - NES style)
        double borderWidth = 8.0;
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
        
        // Update controls position
        positionControlsPanel();
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
            ghostPanel.setVisible(ghostPieceEnabled); // Visible based on settings
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
            // Ensure ghost panel visibility matches settings when reinitializing
            ghostPanel.setVisible(ghostPieceEnabled);
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
        if (brickPanel == null || gameBoard == null) return;
        
        // x = column, y = row
        // Position brickPanel relative to gameBoard's position
        // Account for BorderPane's border width (8px from CSS - NES style)
        // Account for GridPane gaps: each cell is BRICK_SIZE + CELL_GAP apart
        double borderWidth = 8.0;
        double cellWidth = BRICK_SIZE + CELL_GAP;
        double cellHeight = BRICK_SIZE + CELL_GAP;
        double layoutX = gameBoard.getLayoutX() + borderWidth +
                brick.getxPosition() * cellWidth;
        double layoutY = gameBoard.getLayoutY() + borderWidth +
                brick.getyPosition() * cellHeight;
        brickPanel.setLayoutX(layoutX);
        brickPanel.setLayoutY(layoutY);
        
        // Ensure brickPanel is visible and on top (critical for seeing falling blocks)
        brickPanel.setVisible(true);
        brickPanel.setViewOrder(-1.0); // Negative view order = render on top
        brickPanel.toFront(); // Force to front of z-order
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
        
        // Don't update ghost panel if ghost piece is disabled
        if (!ghostPieceEnabled) {
            ghostPanel.setVisible(false);
            return;
        }

        // x = column, y = row (ghost Y position)
        // Position ghostPanel relative to gameBoard's position
        // Account for BorderPane's border width (8px from CSS - NES style)
        // Account for GridPane gaps: each cell is BRICK_SIZE + CELL_GAP apart
        double borderWidth = 8.0;
        double cellWidth = BRICK_SIZE + CELL_GAP;
        double cellHeight = BRICK_SIZE + CELL_GAP;
        double layoutX = gameBoard.getLayoutX() + borderWidth +
                brick.getxPosition() * cellWidth;
        double layoutY = gameBoard.getLayoutY() + borderWidth +
                brick.getGhostYPosition() * cellHeight;
        ghostPanel.setLayoutX(layoutX);
        ghostPanel.setLayoutY(layoutY);
        ghostPanel.setVisible(true);
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
    /**
     * Gets the ghost color for a given color value with dimmed transparency.
     * <p>
     * Returns a semi-transparent, dimmed version of the brick color to create
     * the ghost piece effect. Uses 0.25 opacity for a subtle NES-style ghost.
     * </p>
     *
     * @param colorValue the color value (0-7) from the brick
     * @return a Color with opacity 0.25 (dimmed), or TRANSPARENT if colorValue is 0
     */
    private Paint getGhostColor(int colorValue) {
        if (colorValue == 0) {
            return Color.TRANSPARENT;
        }

        // Get the base color and apply dimmed opacity (0.25 for subtle ghost effect)
        Color baseColor = (Color) getFillColor(colorValue);
        // Dim the color slightly and reduce opacity for classic ghost piece look
        return new Color(
            Math.max(0, baseColor.getRed() * 0.7),      // Dim red channel
            Math.max(0, baseColor.getGreen() * 0.7),    // Dim green channel
            Math.max(0, baseColor.getBlue() * 0.7),     // Dim blue channel
            0.25                                         // Low opacity for transparency
        );
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
    /**
     * Maps a color value to a JavaFX Paint object using NES Tetris colors.
     * <p>
     * Color values correspond to brick types with classic NES Tetris colors:
     * 0=transparent, 1=cyan (I-piece), 2=blue (J-piece), 3=orange (L-piece),
     * 4=yellow (O-piece), 5=green (S-piece), 6=purple (T-piece), 7=red (Z-piece).
     * </p>
     *
     * @param i the color value (0-7)
     * @return the corresponding Paint object for rendering with NES-style colors
     */
    private Paint getFillColor(int i) {
        Paint returnPaint;
        switch (i) {
            case 0:
                returnPaint = Color.TRANSPARENT;
                break;
            case 1:
                // CYAN - I-piece (classic NES cyan)
                returnPaint = Color.CYAN;
                break;
            case 2:
                // BLUE - J-piece (classic NES blue)
                returnPaint = Color.BLUE;
                break;
            case 3:
                // ORANGE - L-piece (classic NES orange)
                returnPaint = Color.ORANGE;
                break;
            case 4:
                // YELLOW - O-piece (classic NES yellow)
                returnPaint = Color.YELLOW;
                break;
            case 5:
                // GREEN - S-piece (classic NES green)
                returnPaint = Color.LIME;
                break;
            case 6:
                // PURPLE - T-piece (classic NES purple/magenta)
                returnPaint = Color.MAGENTA;
                break;
            case 7:
                // RED - Z-piece (classic NES red)
                returnPaint = Color.RED;
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
        // Account for BorderPane's border width (8px from CSS - NES style)
        double borderWidth = 8.0;
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
        // Account for BorderPane's border width (8px from CSS - NES style)
        double borderWidth = 8.0;
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
     * Positions the controls panel below the next piece panel.
     * <p>
     * Calculates the position based on the next piece container's location and places
     * the controls panel directly below it with appropriate spacing.
     * </p>
     */
    private void positionControlsPanel() {
        if (controlsContainer == null || nextPieceContainer == null) return;

        // Position controls panel below the next piece panel
        double spacing = 15.0; // Space between next piece panel and controls
        
        // Get next piece container position and dimensions
        double nextPanelX = nextPieceContainer.getLayoutX();
        double nextPanelY = nextPieceContainer.getLayoutY();
        double nextPanelWidth = nextPieceContainer.getBoundsInLocal().getWidth();
        double nextPanelHeight = nextPieceContainer.getBoundsInLocal().getHeight();
        
        if (nextPanelHeight <= 0) {
            // Fallback: estimate height if bounds not available yet
            nextPanelHeight = 150.0; // Approximate height of next piece panel
        }
        if (nextPanelWidth <= 0) {
            // Fallback: estimate width if bounds not available yet
            nextPanelWidth = 150.0; // Approximate width of next piece panel
        }
        
        // Align horizontally with next piece panel (same X position)
        double controlsX = nextPanelX;
        
        // Position vertically below next piece panel
        double controlsY = nextPanelY + nextPanelHeight + spacing;
        
        // Match the width of the next piece panel for better alignment
        controlsContainer.setPrefWidth(nextPanelWidth);
        controlsContainer.setMaxWidth(nextPanelWidth);

        controlsContainer.setLayoutX(controlsX);
        controlsContainer.setLayoutY(controlsY);
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
        if (currentLevelLabel != null) {
            currentLevelLabel.setText(String.valueOf(viewData.getLevel()));
        }
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
    public void refreshBrick(ViewData brick) {
        if (isPause.getValue() == Boolean.FALSE) {
            // Store ViewData for fullscreen updates
            lastViewData = brick;

            // Ensure brickPanel is visible and on top before updating
            if (brickPanel != null) {
                brickPanel.setVisible(true);
                brickPanel.setViewOrder(-1.0); // Negative = render on top
                brickPanel.toFront(); // Force to front of z-order
            }

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
            
            // Ensure rectangles array is valid
            if (rectangles == null || rectangles.length != brickHeight ||
                    (rectangles.length > 0 && rectangles[0].length != brickWidth)) {
                // Reinitialize rectangles if dimensions changed
                rectangles = new Rectangle[brickHeight][brickWidth];
                brickPanel.getChildren().clear();
                for (int y = 0; y < brickHeight; y++) {
                    for (int x = 0; x < brickWidth; x++) {
                        Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                        rectangle.setArcHeight(2);
                        rectangle.setArcWidth(2);
                        rectangle.setVisible(true);
                        // Set initial color from brick data
                        int colorValue = brick.getBrickData()[y][x];
                        rectangle.setFill(getFillColor(colorValue));
                        rectangles[y][x] = rectangle;
                        brickPanel.add(rectangle, x, y);
                    }
                }
            }
            
            // Loop: y (row) as outer, x (col) as inner
            for (int y = 0; y < brickHeight; y++) {
                for (int x = 0; x < brickWidth; x++) {
                    if (rectangles != null && y < rectangles.length && 
                        x < rectangles[y].length && rectangles[y][x] != null) {
                        setRectangleData(brick.getBrickData()[y][x],
                                rectangles[y][x]);
                    }
                    // Update ghost rectangles with semi-transparent colors
                    if (ghostRectangles != null && y < ghostRectangles.length &&
                            x < ghostRectangles[y].length) {
                        Paint ghostColor = getGhostColor(brick.getBrickData()[y][x]);
                        ghostRectangles[y][x].setFill(ghostColor);
                    }
                }
            }
            
            // Final check: ensure brickPanel is on top and visible after all updates
            if (brickPanel != null) {
                brickPanel.setVisible(true);
                brickPanel.toFront();
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
     * Animates a line clear effect by flashing the specified rows white for ~150ms.
     * <p>
     * This method temporarily paints all rectangles in the cleared rows white.
     * The white flash will be visible until refreshGameBackground() is called
     * to update the board display. The animation runs immediately and does not
     * block the calling thread.
     * </p>
     * <p>
     * Coordinate system: row indices correspond to the y-coordinate in the board matrix.
     * displayMatrix is indexed as displayMatrix[row][col] = displayMatrix[y][x].
     * </p>
     *
     * @param clearedRows the list of row indices (0-based) that will be cleared
     */
    public void animateLineClear(java.util.List<Integer> clearedRows) {
        if (clearedRows == null || clearedRows.isEmpty() || displayMatrix == null) {
            return;
        }

        // Flash cleared rows white immediately
        int width = displayMatrix[0].length;
        for (Integer rowIndex : clearedRows) {
            if (rowIndex >= 0 && rowIndex < displayMatrix.length) {
                for (int x = 0; x < width; x++) {
                    javafx.scene.shape.Rectangle rect = displayMatrix[rowIndex][x];
                    if (rect != null && rect.getFill() != null) {
                        // Only flash non-empty cells (cells with color value > 0)
                        // Check if the cell has a non-transparent fill
                        javafx.scene.paint.Paint fill = rect.getFill();
                        if (!fill.equals(javafx.scene.paint.Color.TRANSPARENT)) {
                            rect.setFill(javafx.scene.paint.Color.WHITE);
                        }
                    }
                }
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
        if (rectangle == null) return;
        rectangle.setFill(getFillColor(color));
        // Use smaller arc for NES-style blocks
        rectangle.setArcHeight(2);
        rectangle.setArcWidth(2);
        // Ensure rectangle is visible
        rectangle.setVisible(true);
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
        javafx.application.Platform.runLater(() -> gamePanel.requestFocus());
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
     * Currently score updates are handled via refreshScoreboard() instead
     * of property binding.
     * </p>
     *
     * @param integerProperty the IntegerProperty to bind (currently unused)
     */
    public void bindScore(IntegerProperty integerProperty) {
        // Score updates are handled via refreshScoreboard()
    }

    /**
     * Binds the level property to update game speed automatically.
     * <p>
     * When the level changes, the timeline speed is automatically adjusted
     * to make pieces fall faster. Listens to level changes and updates the
     * timeline speed accordingly.
     * </p>
     *
     * @param levelProperty the IntegerProperty for the current level
     * @param score the Score object to calculate speed from
     */
    public void bindLevel(IntegerProperty levelProperty, com.comp2042.model.Score score) {
        if (levelProperty == null || score == null) return;
        
        // Store score reference for speed updates
        this.scoreReference = score;
        
        // Add listener to update timeline speed when level changes
        levelProperty.addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.equals(oldValue)) {
                updateGameSpeed();
            }
        });
        
        // Initial speed setup
        updateGameSpeed();
    }

    /**
     * Updates the timeline speed based on the current level.
     * <p>
     * Calculates the new game speed from the score object and updates
     * the timeline keyframe duration. This makes pieces fall faster as
     * the level increases.
     * </p>
     */
    private void updateGameSpeed() {
        if (timeLine == null || scoreReference == null) return;
        
        // Get base speed from difficulty setting (EASY=550, NORMAL=400, HARD=250)
        long baseSpeed = difficultyBaseSpeed;
        
        // Apply level progression: decrease by 50ms per level, minimum 50ms
        // Formula: baseSpeed - (level - 1) * 50, with minimum of 50ms
        int level = scoreReference.getCurrentLevel();
        long newSpeedMillis = Math.max(50, baseSpeed - (level - 1) * 50);
        
        // Stop the timeline
        boolean wasPlaying = timeLine.getStatus() == javafx.animation.Animation.Status.RUNNING;
        timeLine.stop();
        
        // Create new timeline with updated speed
        timeLine = new Timeline(new KeyFrame(
                Duration.millis(newSpeedMillis),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        
        // Resume if it was playing (and not paused or game over)
        if (wasPlaying && isPause.getValue() == Boolean.FALSE && 
            isGameOver.getValue() == Boolean.FALSE) {
            timeLine.play();
        }
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
        if (gameOverPanel != null) {
            gameOverPanel.setVisible(true);
        }
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
        timeLine.stop(); // stop old timeline

        if (gameOverPanel != null) {
            gameOverPanel.setVisible(false);
        }
        
        // Reload settings to get current difficulty
        if (globalSettings != null) {
            difficultyBaseSpeed = globalSettings.getFallSpeedMillis();
        } else if (sceneManager != null) {
            globalSettings = sceneManager.getSettings();
            if (globalSettings != null) {
                difficultyBaseSpeed = globalSettings.getFallSpeedMillis();
            }
        }
        
        eventListener.createNewGame();
        javafx.application.Platform.runLater(() -> gamePanel.requestFocus());

        // 🟢 recreate Timeline for new game with current difficulty setting
        long newGameSpeed = (difficultyBaseSpeed > 0) ? difficultyBaseSpeed : 400;
        timeLine = new Timeline(new KeyFrame(Duration.millis(newGameSpeed),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();

        isPause.set(false);
        isGameOver.set(false);
    }

    public void moveDownFromController() {
        moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD));
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
        
        javafx.application.Platform.runLater(() -> gamePanel.requestFocus());
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
        if (gameOverPanel != null) {
            gameOverPanel.setVisible(false);
        }

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

        // Reload settings to ensure difficulty is current
        if (globalSettings != null) {
            difficultyBaseSpeed = globalSettings.getFallSpeedMillis();
        } else if (sceneManager != null) {
            globalSettings = sceneManager.getSettings();
            if (globalSettings != null) {
                difficultyBaseSpeed = globalSettings.getFallSpeedMillis();
            }
        }
        
        // Reinitialize the timeline with difficulty and level-appropriate speed
        if (scoreReference != null) {
            updateGameSpeed();
        } else {
            // Fallback to difficulty base speed if score reference not available
            long fallbackSpeed = (difficultyBaseSpeed > 0) ? difficultyBaseSpeed : 400;
            timeLine = new Timeline(new KeyFrame(
                    Duration.millis(fallbackSpeed),
                    ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
            ));
            timeLine.setCycleCount(Timeline.INDEFINITE);
            timeLine.play();
        }

        // Request focus for keyboard input - use Platform.runLater to prevent fullscreen exit
        javafx.application.Platform.runLater(() -> gamePanel.requestFocus());

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

    @FXML
    private void returnToMainMenu() {
        if (timeLine != null) {
            timeLine.stop();  // stop falling pieces
        }
        // Preserve fullscreen - SceneManager will handle it
        SceneManager.showMenu();
    }

    @FXML
    private void openSettings() {
        if (timeLine != null) {
            timeLine.stop();
        }
        // Preserve fullscreen - SceneManager will handle it
        SceneManager.showSettings();
    }

    @FXML
    public void goToMainMenu() {
        if (timeLine != null) {
            timeLine.stop();
        }
        // Preserve fullscreen - SceneManager will handle it
        SceneManager.showMenu();
    }

    @FXML
    public void goToSettings() {
        if (timeLine != null) {
            timeLine.stop();
        }
        // Preserve fullscreen - SceneManager will handle it
        SceneManager.showSettings();
    }





}
