package com.comp2042.board;

import com.comp2042.logic.CollisionHandler;
import com.comp2042.logic.MatrixOperations;
import com.comp2042.logic.MovementHandler;
import com.comp2042.logic.NextShapeInfo;
import com.comp2042.logic.RowClearer;
import com.comp2042.model.Brick;
import com.comp2042.model.BrickGenerator;
import com.comp2042.model.ClearRow;
import com.comp2042.model.HardDropResult;
import com.comp2042.model.RandomBrickGenerator;
import com.comp2042.model.Score;
import com.comp2042.view.ViewData;

import java.awt.*;

/**
 * Implementation of the game board logic for Tetris.
 * <p>
 * This class is part of the Model layer in the MVC architecture. It manages
 * the game state including the board matrix, current falling brick, brick
 * rotation, movement validation, and row clearing. It delegates specialized
 * operations to helper classes: CollisionHandler for collision detection,
 * MovementHandler for position calculations, BrickRotator for rotation logic,
 * and RowClearer for row clearing operations.
 * </p>
 * <p>
 * <strong>Coordinate System:</strong>
 * <ul>
 *   <li>x = column (horizontal position)</li>
 *   <li>y = row (vertical position)</li>
 *   <li>Matrix indexing: currentGameMatrix[row][col] = currentGameMatrix[y][x]</li>
 *   <li>Offset: currentOffset.x = column, currentOffset.y = row</li>
 * </ul>
 * The board matrix is allocated as int[height][width], meaning matrix[row][col].
 * </p>
 *
 * @author TetrisJFX Team
 * @version 1.0
 */
public class SimpleBoard implements Board {

    private final int width;
    private final int height;
    private final BrickGenerator brickGenerator;
    private final BrickRotator brickRotator;
    private int[][] currentGameMatrix;  // [rows][cols] = [height][width]
    private Point currentOffset;  // x = column, y = row
    private final Score score;

    /**
     * Constructs a new game board with the specified dimensions.
     *
     * @param width  the number of columns (horizontal dimension)
     * @param height the number of rows (vertical dimension)
     */
    public SimpleBoard(int width, int height) {
        this.width = width;
        this.height = height;
        // Matrix is row-major: [rows][cols] = [height][width]
        currentGameMatrix = new int[height][width];
        brickGenerator = new RandomBrickGenerator();
        brickRotator = new BrickRotator();
        score = new Score();
    }

    /**
     * Attempts to move the current brick down by one row.
     * <p>
     * Uses MovementHandler to calculate the new position and CollisionHandler
     * to validate the move. If valid, updates the current offset.
     * </p>
     *
     * @return true if the brick was successfully moved down, false if blocked
     *         by collision or boundary
     */
    @Override
    public boolean moveBrickDown() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        int[][] currentShape = brickRotator.getCurrentShape();
        int currentX = (int) currentOffset.getX();
        int currentY = (int) currentOffset.getY();

        if (CollisionHandler.canMoveDown(currentMatrix, currentShape, currentX, currentY)) {
            currentOffset = MovementHandler.moveDown(currentOffset);
            return true;
        }
        return false;
    }

    /**
     * Attempts to move the current brick left by one column.
     * <p>
     * Uses MovementHandler to calculate the new position and CollisionHandler
     * to validate the move. If valid, updates the current offset.
     * </p>
     *
     * @return true if the brick was successfully moved left, false if blocked
     *         by collision or boundary
     */
    @Override
    public boolean moveBrickLeft() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        int[][] currentShape = brickRotator.getCurrentShape();
        int currentX = (int) currentOffset.getX();
        int currentY = (int) currentOffset.getY();

        if (CollisionHandler.canMoveLeft(currentMatrix, currentShape, currentX, currentY)) {
            currentOffset = MovementHandler.moveLeft(currentOffset);
            return true;
        }
        return false;
    }

    /**
     * Attempts to move the current brick right by one column.
     * <p>
     * Uses MovementHandler to calculate the new position and CollisionHandler
     * to validate the move. If valid, updates the current offset.
     * </p>
     *
     * @return true if the brick was successfully moved right, false if blocked
     *         by collision or boundary
     */
    @Override
    public boolean moveBrickRight() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        int[][] currentShape = brickRotator.getCurrentShape();
        int currentX = (int) currentOffset.getX();
        int currentY = (int) currentOffset.getY();

        if (CollisionHandler.canMoveRight(currentMatrix, currentShape, currentX, currentY)) {
            currentOffset = MovementHandler.moveRight(currentOffset);
            return true;
        }
        return false;
    }

    /**
     * Attempts to rotate the current brick 90 degrees clockwise.
     * <p>
     * Delegates rotation logic to BrickRotator to get the next rotated shape.
     * Uses CollisionHandler to validate the rotation. If rotation is valid,
     * applies the rotated shape.
     * </p>
     *
     * @return true if the rotation was successful, false if rotation was blocked
     *         by collision or boundary
     */
    @Override
    public boolean rotateLeftBrick() {
        // Delegate rotation to BrickRotator - get the rotated shape
        NextShapeInfo rotatedShape = brickRotator.getNextShape();
        int[][] rotatedShapeMatrix = rotatedShape.getShape();

        // Use CollisionHandler to validate rotation
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        int currentX = (int) currentOffset.getX();
        int currentY = (int) currentOffset.getY();

        if (CollisionHandler.canRotate(currentMatrix, rotatedShapeMatrix, currentX, currentY)) {
            brickRotator.setCurrentShape(rotatedShape.getPosition());
            return true;
        }
        return false;
    }

    /**
     * Creates a new brick at the top center of the board.
     * <p>
     * Generates a new brick from the BrickGenerator, sets it in the
     * BrickRotator, and calculates the spawn position at the top center.
     * The spawn X position is clamped to ensure the brick fits within board
     * boundaries. Uses CollisionHandler to check if spawn is valid.
     * </p>
     *
     * @return true if the new brick collides immediately (game over condition),
     *         false if the brick was successfully spawned
     */
    @Override
    public boolean createNewBrick() {
        Brick currentBrick = brickGenerator.getBrick();
        brickRotator.setBrick(currentBrick);

        // Calculate proper spawn position: top center of the board
        int[][] brickMatrix = brickRotator.getCurrentShape();
        int brickWidth = brickMatrix[0].length;
        int spawnX = (width / 2) - (brickWidth / 2);
        int spawnY = 0;

        // Clamp spawnX to stay within [0, width - brickWidth]
        if (spawnX < 0) {
            spawnX = 0;
        }
        if (spawnX + brickWidth > width) {
            spawnX = width - brickWidth;
        }

        currentOffset = new Point(spawnX, spawnY);

        // Use CollisionHandler to check spawn validity
        return CollisionHandler.canSpawn(currentGameMatrix, brickRotator.getCurrentShape(), spawnX);
    }

    /**
     * Calculates the Y position where the current brick would land (ghost position).
     * <p>
     * Simulates downward movement until collision is detected, returning the
     * highest Y position where the brick can be placed without collision.
     * This is used to display the ghost piece preview.
     * </p>
     *
     * @return the Y position (row) where the brick would land, or the current
     *         Y position if already at the bottom
     */
    public int calculateGhostYPosition() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        int[][] currentShape = brickRotator.getCurrentShape();
        int currentX = (int) currentOffset.getX();
        int currentY = (int) currentOffset.getY();

        // Simulate downward movement until collision
        // Start from current position and find the lowest valid position
        int ghostY = currentY;
        // Keep moving down as long as we can
        // canMoveDown(x, y) checks if we can move FROM y to y+1
        // So if canMoveDown(y) is true, we can move to y+1, so increment ghostY
        // When canMoveDown(ghostY) is false, we cannot move from ghostY, so ghostY is the landing position
        while (ghostY < height - 1 && 
               CollisionHandler.canMoveDown(currentMatrix, currentShape, currentX, ghostY)) {
            ghostY++;
        }
        // ghostY is now the position where we cannot move down further
        // This is the landing position

        return ghostY;
    }

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
    @Override
    public int[][] getBoardMatrix() {
        return currentGameMatrix;
    }

    /**
     * Returns the current view data for rendering the game state.
     * <p>
     * Includes the current falling brick shape, its position (x, y), the
     * ghost Y position (where it would land), the next brick preview shape,
     * and score information (current score, total lines, high score).
     * </p>
     *
     * @return ViewData object containing brick shape, position, ghost position,
     *         next brick information, and score data
     */
    /**
     * Returns the preview data for the next N pieces.
     * <p>
     * Gets the shape matrices for the upcoming pieces without removing them
     * from the generator queue. Each brick's shape matrix at rotation 0 is returned.
     * </p>
     *
     * @return a list of shape matrices (int[][]) for the next pieces
     */
    public java.util.List<int[][]> getNextPreviewData() {
        java.util.List<int[][]> previewData = new java.util.ArrayList<>();
        
        // Check if brickGenerator is a RandomBrickGenerator to access getNextBricks()
        if (brickGenerator instanceof com.comp2042.model.RandomBrickGenerator rbg) {
            java.util.List<com.comp2042.model.Brick> nextBricks = rbg.getNextBricks(2);
            for (com.comp2042.model.Brick brick : nextBricks) {
                // Get the first rotation (index 0) of each brick
                previewData.add(brick.getShapeMatrix().get(0));
            }
        } else {
            // Fallback: use getNextBrick() for single brick
            previewData.add(brickGenerator.getNextBrick().getShapeMatrix().get(0));
        }
        
        return previewData;
    }

    @Override
    public ViewData getViewData() {
        int ghostY = calculateGhostYPosition();
        java.util.List<int[][]> nextPiecesData = getNextPreviewData();
        return new ViewData(brickRotator.getCurrentShape(),
                (int) currentOffset.getX(), (int) currentOffset.getY(),
                ghostY,
                nextPiecesData,
                score.getCurrentScore(),
                score.getTotalLines(),
                score.getHighScore(),
                score.getCurrentLevel());
    }

    /**
     * Merges the current falling brick into the board background.
     * <p>
     * This method is called when a brick can no longer move down. The brick's
     * cells are permanently added to the board matrix at its current position
     * using MatrixOperations.merge().
     * </p>
     */
    @Override
    public void mergeBrickToBackground() {
        currentGameMatrix = MatrixOperations.merge(currentGameMatrix,
                brickRotator.getCurrentShape(), (int) currentOffset.getX(),
                (int) currentOffset.getY());
    }

    /**
     * Clears all completed rows from the board and collapses remaining rows.
     * <p>
     * Delegates row clearing logic to RowClearer. Updates the board matrix
     * with the result. Updates the score with lines cleared and score bonus.
     * </p>
     *
     * @return ClearRow object containing the number of lines removed, updated
     *         board matrix, and score bonus
     */
    @Override
    public ClearRow clearRows() {
        ClearRow clearRow = RowClearer.clear(currentGameMatrix);
        currentGameMatrix = clearRow.getNewMatrix();
        
        // Update score: add lines cleared and score bonus
        if (clearRow.getLinesRemoved() > 0) {
            score.addLines(clearRow.getLinesRemoved());
            score.addScore(clearRow.getScoreBonus());
        }
        
        return clearRow;
    }

    /**
     * Returns the score object for this game board.
     *
     * @return Score object that manages the game score
     */
    @Override
    public Score getScore() {
        return score;
    }

    /**
     * Performs a hard drop operation, instantly dropping the current brick
     * to the lowest possible valid position.
     * <p>
     * This method:
     * <ol>
     *   <li>Calculates the lowest valid Y position using collision detection</li>
     *   <li>Moves the brick directly to that position</li>
     *   <li>Merges the brick into the board</li>
     *   <li>Clears any completed rows</li>
     *   <li>Spawns a new brick</li>
     *   <li>Calculates and applies hard drop bonus (2 points per row dropped)</li>
     * </ol>
     * </p>
     * <p>
     * Coordinate system: x = column, y = row. The brick is moved from its
     * current Y position to the lowest valid Y position without collision.
     * </p>
     *
     * @return HardDropResult containing the final ViewData, ClearRow result,
     *         and number of rows dropped
     */
    public HardDropResult hardDrop() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        int[][] currentShape = brickRotator.getCurrentShape();
        int currentX = (int) currentOffset.getX();
        int currentY = (int) currentOffset.getY();

        // Calculate the lowest valid Y position (same logic as calculateGhostYPosition)
        int dropY = currentY;
        while (dropY < height - 1 &&
               CollisionHandler.canMoveDown(currentMatrix, currentShape, currentX, dropY)) {
            dropY++;
        }
        // dropY is now the lowest valid position

        // Calculate number of rows dropped
        int rowsDropped = dropY - currentY;

        // Move the brick to the drop position
        currentOffset = new Point(currentX, dropY);

        // Merge the brick into the board
        mergeBrickToBackground();

        // Clear rows and get the result
        ClearRow clearRow = clearRows();

        // Add hard drop bonus: 2 points per row dropped
        if (rowsDropped > 0) {
            int hardDropBonus = rowsDropped * 2;
            score.addScore(hardDropBonus);
        }

        // Spawn new brick (createNewBrick returns true if game over)
        boolean gameOver = createNewBrick();

        // Get the updated view data (after new brick spawn)
        ViewData viewData = getViewData();

        return new HardDropResult(viewData, clearRow, rowsDropped, gameOver);
    }

    /**
     * Resets the game board to initial state.
     * <p>
     * Clears the board matrix, resets the score to zero, and spawns a new
     * brick to start a new game.
     * </p>
     */
    @Override
    public void newGame() {
        currentGameMatrix = new int[height][width];
        score.reset();
        createNewBrick();
    }
}
