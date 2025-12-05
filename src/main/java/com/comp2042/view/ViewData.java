package com.comp2042.view;

import com.comp2042.logic.MatrixOperations;

import java.util.ArrayList;
import java.util.List;

/**
 * Immutable data transfer object containing view information for rendering
 * the game state.
 * <p>
 * This class is part of the View layer in the MVC architecture. It serves as
 * a data container that transfers information from the Model (Board) to the
 * View (GuiController) without exposing internal model details. All data
 * returned by getter methods are defensive copies to maintain immutability.
 * </p>
 * <p>
 * <strong>Coordinate System:</strong>
 * <ul>
 *   <li>xPosition = column (horizontal position)</li>
 *   <li>yPosition = row (vertical position)</li>
 *   <li>ghostYPosition = row where the brick would land (ghost piece position)</li>
 *   <li>Brick data is indexed as brickData[row][col]</li>
 * </ul>
 * </p>
 *
 * @author TetrisJFX Team
 * @version 1.0
 */
public final class ViewData {

    private final int[][] brickData;
    private final int xPosition;
    private final int yPosition;
    private final int ghostYPosition;
    private final List<int[][]> nextPiecesData;
    private final int score;
    private final int totalLines;
    private final int highScore;
    private final int level;

    /**
     * Constructs a new ViewData object with the specified brick and score information.
     *
     * @param brickData       the current falling brick shape matrix
     *                        (brickData[row][col])
     * @param xPosition       the column position of the brick (x coordinate)
     * @param yPosition       the row position of the brick (y coordinate)
     * @param ghostYPosition  the row position where the brick would land
     *                        (ghost piece Y coordinate)
     * @param nextPiecesData  list of next brick preview shape matrices
     *                        (each matrix is indexed as nextPiecesData[i][row][col])
     * @param score           the current game score
     * @param totalLines      the total number of lines cleared
     * @param highScore       the high score (persists across games)
     * @param level           the current level (calculated from lines cleared)
     */
    public ViewData(int[][] brickData, int xPosition, int yPosition,
                    int ghostYPosition, List<int[][]> nextPiecesData,
                    int score, int totalLines, int highScore, int level) {
        this.brickData = brickData;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.ghostYPosition = ghostYPosition;
        // Create defensive copy of the list and its contents
        this.nextPiecesData = new ArrayList<>();
        if (nextPiecesData != null) {
            for (int[][] piece : nextPiecesData) {
                this.nextPiecesData.add(MatrixOperations.copy(piece));
            }
        }
        this.score = score;
        this.totalLines = totalLines;
        this.highScore = highScore;
        this.level = level;
    }

    /**
     * Returns a defensive copy of the current falling brick shape matrix.
     * <p>
     * The matrix is indexed as brickData[row][col]. Non-zero values represent
     * filled cells in the brick.
     * </p>
     *
     * @return a deep copy of the brick shape matrix
     */
    public int[][] getBrickData() {
        return MatrixOperations.copy(brickData);
    }

    /**
     * Returns the column position (x coordinate) of the current falling brick.
     *
     * @return the x position (column index)
     */
    public int getxPosition() {
        return xPosition;
    }

    /**
     * Returns the row position (y coordinate) of the current falling brick.
     *
     * @return the y position (row index)
     */
    public int getyPosition() {
        return yPosition;
    }

    /**
     * Returns the row position (y coordinate) where the brick would land
     * (ghost piece position).
     * <p>
     * This is calculated by simulating downward movement until collision.
     * The ghost piece is displayed at this position to show where the brick
     * will land.
     * </p>
     *
     * @return the ghost Y position (row index where brick would land)
     */
    public int getGhostYPosition() {
        return ghostYPosition;
    }

    /**
     * Returns a defensive copy of the next brick preview shape matrices.
     * <p>
     * Returns a list containing shape matrices for the next pieces.
     * Each matrix is indexed as nextPiecesData[i][row][col]. Non-zero values
     * represent filled cells in the next bricks.
     * </p>
     *
     * @return a list of deep copies of the next brick shape matrices
     */
    public List<int[][]> getNextPiecesData() {
        List<int[][]> result = new ArrayList<>();
        if (nextPiecesData != null) {
            for (int[][] piece : nextPiecesData) {
                result.add(MatrixOperations.copy(piece));
            }
        }
        return result;
    }

    /**
     * Returns a defensive copy of the first next brick preview shape matrix.
     * <p>
     * Maintains backward compatibility with code expecting a single next brick.
     * </p>
     *
     * @return a deep copy of the first next brick shape matrix, or null if none
     */
    public int[][] getNextBrickData() {
        if (nextPiecesData == null || nextPiecesData.isEmpty()) {
            return null;
        }
        return MatrixOperations.copy(nextPiecesData.get(0));
    }

    /**
     * Returns the current game score.
     *
     * @return the current score
     */
    public int getScore() {
        return score;
    }

    /**
     * Returns the total number of lines cleared in the current game.
     *
     * @return the total lines cleared
     */
    public int getTotalLines() {
        return totalLines;
    }

    /**
     * Returns the high score (persists across games).
     *
     * @return the high score
     */
    public int getHighScore() {
        return highScore;
    }

    /**
     * Returns the current level (calculated from lines cleared).
     * <p>
     * Level increases every 10 lines cleared. Level starts at 1.
     * </p>
     *
     * @return the current level
     */
    public int getLevel() {
        return level;
    }
}
