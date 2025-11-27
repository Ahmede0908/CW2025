package com.comp2042.board;

import com.comp2042.logic.CollisionHandler;
import com.comp2042.logic.MatrixOperations;
import com.comp2042.logic.MovementHandler;
import com.comp2042.logic.NextShapeInfo;
import com.comp2042.logic.RowClearer;
import com.comp2042.model.Brick;
import com.comp2042.model.BrickGenerator;
import com.comp2042.model.ClearRow;
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
        Point newOffset = MovementHandler.moveDown(currentOffset);
        boolean conflict = CollisionHandler.hasCollision(currentMatrix,
                brickRotator.getCurrentShape(), (int) newOffset.getX(),
                (int) newOffset.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = newOffset;
            return true;
        }
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
        Point newOffset = MovementHandler.moveLeft(currentOffset);
        boolean conflict = CollisionHandler.hasCollision(currentMatrix,
                brickRotator.getCurrentShape(), (int) newOffset.getX(),
                (int) newOffset.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = newOffset;
            return true;
        }
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
        Point newOffset = MovementHandler.moveRight(currentOffset);
        boolean conflict = CollisionHandler.hasCollision(currentMatrix,
                brickRotator.getCurrentShape(), (int) newOffset.getX(),
                (int) newOffset.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = newOffset;
            return true;
        }
    }

    /**
     * Attempts to rotate the current brick 90 degrees clockwise.
     * <p>
     * Delegates rotation logic to BrickRotator to get the next rotated shape.
     * This class is responsible only for validation (collision and bounds
     * checking). If rotation is valid, applies the rotated shape.
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

        // SimpleBoard is responsible ONLY for validation (collision + bounds checking)
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        boolean conflict = CollisionHandler.hasCollision(currentMatrix,
                rotatedShapeMatrix, (int) currentOffset.getX(),
                (int) currentOffset.getY());

        // If rotation is valid, apply the rotated shape returned by brickRotator
        if (conflict) {
            return false;
        } else {
            brickRotator.setCurrentShape(rotatedShape.getPosition());
            return true;
        }
    }

    /**
     * Creates a new brick at the top center of the board.
     * <p>
     * Generates a new brick from the BrickGenerator, sets it in the
     * BrickRotator, and calculates the spawn position at the top center.
     * The spawn X position is clamped to ensure the brick fits within board
     * boundaries.
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
        return CollisionHandler.hasCollision(currentGameMatrix,
                brickRotator.getCurrentShape(), (int) currentOffset.getX(),
                (int) currentOffset.getY());
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
     * Includes the current falling brick shape, its position (x, y), and the
     * next brick preview shape.
     * </p>
     *
     * @return ViewData object containing brick shape, position, and next brick
     *         information
     */
    @Override
    public ViewData getViewData() {
        return new ViewData(brickRotator.getCurrentShape(),
                (int) currentOffset.getX(), (int) currentOffset.getY(),
                brickGenerator.getNextBrick().getShapeMatrix().get(0));
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
     * with the result.
     * </p>
     *
     * @return ClearRow object containing the number of lines removed, updated
     *         board matrix, and score bonus
     */
    @Override
    public ClearRow clearRows() {
        ClearRow clearRow = RowClearer.clear(currentGameMatrix);
        currentGameMatrix = clearRow.getNewMatrix();
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
