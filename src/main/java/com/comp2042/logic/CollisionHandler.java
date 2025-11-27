package com.comp2042.logic;

/**
 * Complete collision detection engine for the Tetris game.
 * <p>
 * This class serves as the single source of truth for all collision detection
 * operations in the game. It provides methods for validating movement, rotation,
 * and spawning operations. All collision decisions should go through this class.
 * </p>
 * <p>
 * <strong>Coordinate System:</strong>
 * <ul>
 *   <li>x = column (horizontal position)</li>
 *   <li>y = row (vertical position)</li>
 *   <li>Matrix indexing: matrix[row][column] = matrix[y][x]</li>
 * </ul>
 * </p>
 * <p>
 * This class cannot be instantiated; all methods are static.
 * </p>
 *
 * @author TetrisJFX Team
 * @version 1.0
 */
public class CollisionHandler {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private CollisionHandler() {
    }

    /**
     * Checks if a shape collides with the board at the given position.
     * <p>
     * A collision occurs if:
     * <ul>
     *   <li>Any non-zero cell of the shape is out of bounds, OR</li>
     *   <li>Any non-zero cell of the shape overlaps with a non-zero cell on the board</li>
     * </ul>
     * </p>
     *
     * @param board the game board matrix (matrix[row][column])
     * @param shape the shape matrix to check (shape[row][column])
     * @param x     the column position (x coordinate)
     * @param y     the row position (y coordinate)
     * @return true if there is a collision, false otherwise
     */
    public static boolean hasCollision(int[][] board, int[][] shape, int x, int y) {
        // x = column, y = row
        // matrix[row][column] = matrix[y][x]
        // shape is indexed as shape[row][column]
        for (int row = 0; row < shape.length; row++) {  // row = shape row
            for (int col = 0; col < shape[row].length; col++) {  // col = shape column
                if (shape[row][col] != 0) {
                    int targetRow = y + row;  // row = y + row
                    int targetCol = x + col;  // column = x + col
                    // Check if out of bounds or overlaps with board
                    if (outOfBounds(board, targetCol, targetRow) ||
                            board[targetRow][targetCol] != 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Checks if a single coordinate point is out of bounds for the given board.
     *
     * @param board the game board matrix (matrix[row][column])
     * @param x     the column index (x coordinate)
     * @param y     the row index (y coordinate)
     * @return true if the point is out of bounds, false otherwise
     */
    public static boolean outOfBounds(int[][] board, int x, int y) {
        // x = column, y = row
        // matrix[row][column] = matrix[y][x]
        // Bounds: row < matrix.length, column < matrix[0].length
        if (y < 0 || y >= board.length) {
            return true;
        }
        if (x < 0 || x >= board[0].length) {
            return true;
        }
        return false;
    }

    /**
     * Validates if a shape can move down by one row.
     * <p>
     * Checks for collision at the position one row below the current position.
     * </p>
     *
     * @param board the game board matrix (matrix[row][column])
     * @param shape the shape matrix to check (shape[row][column])
     * @param x     the current column position (x coordinate)
     * @param y     the current row position (y coordinate)
     * @return true if the shape can move down (no collision), false otherwise
     */
    public static boolean canMoveDown(int[][] board, int[][] shape, int x, int y) {
        return !hasCollision(board, shape, x, y + 1);
    }

    /**
     * Validates if a shape can move left by one column.
     * <p>
     * Checks for collision at the position one column to the left of the current position.
     * </p>
     *
     * @param board the game board matrix (matrix[row][column])
     * @param shape the shape matrix to check (shape[row][column])
     * @param x     the current column position (x coordinate)
     * @param y     the current row position (y coordinate)
     * @return true if the shape can move left (no collision), false otherwise
     */
    public static boolean canMoveLeft(int[][] board, int[][] shape, int x, int y) {
        return !hasCollision(board, shape, x - 1, y);
    }

    /**
     * Validates if a shape can move right by one column.
     * <p>
     * Checks for collision at the position one column to the right of the current position.
     * </p>
     *
     * @param board the game board matrix (matrix[row][column])
     * @param shape the shape matrix to check (shape[row][column])
     * @param x     the current column position (x coordinate)
     * @param y     the current row position (y coordinate)
     * @return true if the shape can move right (no collision), false otherwise
     */
    public static boolean canMoveRight(int[][] board, int[][] shape, int x, int y) {
        return !hasCollision(board, shape, x + 1, y);
    }

    /**
     * Validates if a shape can be rotated at the current position.
     * <p>
     * Checks for collision with the rotated shape at the same position.
     * </p>
     *
     * @param board        the game board matrix (matrix[row][column])
     * @param rotatedShape the rotated shape matrix to check (shape[row][column])
     * @param x            the current column position (x coordinate)
     * @param y            the current row position (y coordinate)
     * @return true if the shape can rotate (no collision), false otherwise
     */
    public static boolean canRotate(int[][] board, int[][] rotatedShape, int x, int y) {
        return !hasCollision(board, rotatedShape, x, y);
    }

    /**
     * Validates if a shape can spawn at the specified horizontal position.
     * <p>
     * Checks for collision at the top of the board (row 0) at the given column position.
     * This is used to determine if a new brick can be spawned without immediately
     * colliding with existing blocks (game over condition).
     * </p>
     *
     * @param board   the game board matrix (matrix[row][column])
     * @param shape   the shape matrix to check (shape[row][column])
     * @param spawnX  the column position where the shape should spawn (x coordinate)
     * @return true if the shape collides at spawn position (game over), false if spawn is valid
     */
    public static boolean canSpawn(int[][] board, int[][] shape, int spawnX) {
        int spawnY = 0;  // Always spawn at top row
        return hasCollision(board, shape, spawnX, spawnY);
    }
}
