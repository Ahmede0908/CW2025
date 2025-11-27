package com.comp2042.logic;

/**
 * Handles collision detection between shapes and the game board.
 * Coordinate system: x = column, y = row
 * Matrix indexing: matrix[row][column] = matrix[y][x]
 */
public class CollisionHandler {

    /**
     * We don't want to instantiate this utility class
     */
    private CollisionHandler() {
    }

    /**
     * Checks if a shape collides with the board at the given position.
     * A collision occurs if:
     * - Any non-zero cell of the shape is out of bounds, OR
     * - Any non-zero cell of the shape overlaps with a non-zero cell on the board
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
                    if (outOfBounds(board, targetCol, targetRow) || board[targetRow][targetCol] != 0) {
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
}

