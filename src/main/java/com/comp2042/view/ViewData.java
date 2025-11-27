package com.comp2042.view;

import com.comp2042.logic.MatrixOperations;

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
    private final int[][] nextBrickData;

    /**
     * Constructs a new ViewData object with the specified brick information.
     *
     * @param brickData     the current falling brick shape matrix
     *                      (brickData[row][col])
     * @param xPosition     the column position of the brick (x coordinate)
     * @param yPosition     the row position of the brick (y coordinate)
     * @param nextBrickData the next brick preview shape matrix
     *                      (nextBrickData[row][col])
     */
    public ViewData(int[][] brickData, int xPosition, int yPosition,
                    int[][] nextBrickData) {
        this.brickData = brickData;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.nextBrickData = nextBrickData;
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
     * Returns a defensive copy of the next brick preview shape matrix.
     * <p>
     * The matrix is indexed as nextBrickData[row][col]. Non-zero values
     * represent filled cells in the next brick.
     * </p>
     *
     * @return a deep copy of the next brick shape matrix
     */
    public int[][] getNextBrickData() {
        return MatrixOperations.copy(nextBrickData);
    }
}
