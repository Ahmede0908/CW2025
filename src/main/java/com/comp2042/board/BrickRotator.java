package com.comp2042.board;

import com.comp2042.board.rotation.NoRotationStrategy;
import com.comp2042.board.rotation.RotationStrategy;
import com.comp2042.board.rotation.StandardRotationStrategy;
import com.comp2042.logic.NextShapeInfo;
import com.comp2042.model.Brick;

/**
 * Manages brick rotation using the Strategy design pattern.
 * <p>
 * This class is part of the Model layer in the MVC architecture. It handles
 * the rotation state of the current brick and delegates the actual rotation
 * logic to RotationStrategy implementations. The strategy is selected based
 * on the brick type: square bricks (like OBrick) use NoRotationStrategy,
 * while other bricks use StandardRotationStrategy for 90-degree clockwise
 * rotation.
 * </p>
 * <p>
 * The class maintains the current rotation state (which shape variant is
 * active) and provides methods to get the next rotated shape and the current
 * shape. Rotation validation (collision and bounds checking) is handled by
 * the Board implementation, not by this class.
 * </p>
 *
 * @author TetrisJFX Team
 * @version 1.0
 */
public class BrickRotator {

    private Brick brick;
    private int currentShape = 0;
    private RotationStrategy rotationStrategy;

    /**
     * Gets the next shape in the rotation sequence.
     * <p>
     * Calculates the next rotation state by incrementing the current shape
     * index and wrapping around using modulo arithmetic. Returns both the
     * shape matrix and the new position index.
     * </p>
     *
     * @return NextShapeInfo containing the next rotated shape matrix and
     *         its position index
     */
    public NextShapeInfo getNextShape() {
        int nextShape = currentShape;
        nextShape = (++nextShape) % brick.getShapeMatrix().size();
        return new NextShapeInfo(brick.getShapeMatrix().get(nextShape),
                nextShape);
    }

    /**
     * Returns the current shape matrix of the brick.
     * <p>
     * The shape matrix is indexed as shape[row][col]. Non-zero values
     * represent filled cells in the brick.
     * </p>
     *
     * @return 2D array representing the current brick shape, where
     *         shape[row][col] contains the cell value
     */
    public int[][] getCurrentShape() {
        return brick.getShapeMatrix().get(currentShape);
    }

    /**
     * Sets the current rotation state to the specified shape index.
     * <p>
     * This method is called after rotation validation succeeds to update
     * the rotation state.
     * </p>
     *
     * @param currentShape the index of the shape variant to set as current
     */
    public void setCurrentShape(int currentShape) {
        this.currentShape = currentShape;
    }

    /**
     * Sets the brick and initializes rotation state.
     * <p>
     * Resets the current shape to 0 (initial rotation) and selects the
     * appropriate RotationStrategy based on the brick type. Square bricks
     * (with only 1 shape variant) use NoRotationStrategy, while others use
     * StandardRotationStrategy.
     * </p>
     *
     * @param brick the brick to set for rotation management
     */
    public void setBrick(Brick brick) {
        this.brick = brick;
        currentShape = 0;

        // Determine rotation strategy based on brick type
        // Square bricks (like OBrick) have only 1 shape, so they don't rotate
        if (brick.getShapeMatrix().size() == 1) {
            this.rotationStrategy = new NoRotationStrategy();
        } else {
            this.rotationStrategy = new StandardRotationStrategy();
        }
    }
}
