package com.comp2042.logic;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class providing matrix operations for the Tetris game board.
 * <p>
 * This class is part of the Logic layer in the MVC architecture. It provides
 * static utility methods for matrix manipulation including copying, merging
 * bricks into the board, and rotating matrices. All methods operate on 2D
 * integer arrays representing the game board or brick shapes.
 * </p>
 * <p>
 * <strong>Matrix Indexing Convention:</strong>
 * <ul>
 *   <li>All matrices are indexed as matrix[row][column]</li>
 *   <li>Row corresponds to the vertical dimension (y-axis)</li>
 *   <li>Column corresponds to the horizontal dimension (x-axis)</li>
 *   <li>For board matrices: matrix[row][col] = matrix[y][x]</li>
 * </ul>
 * </p>
 * <p>
 * This class cannot be instantiated; all methods are static.
 * </p>
 *
 * @author TetrisJFX Team
 * @version 1.0
 */
public class MatrixOperations {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private MatrixOperations() {
    }

    /**
     * Creates a deep copy of a 2D integer array.
     * <p>
     * Allocates new arrays and copies all elements, ensuring no shared
     * references between the original and copy.
     * </p>
     *
     * @param original the matrix to copy
     * @return a new 2D array containing a deep copy of the original matrix
     */
    public static int[][] copy(int[][] original) {
        int[][] myInt = new int[original.length][];
        for (int i = 0; i < original.length; i++) {
            int[] aMatrix = original[i];
            int aLength = aMatrix.length;
            myInt[i] = new int[aLength];
            System.arraycopy(aMatrix, 0, myInt[i], 0, aLength);
        }
        return myInt;
    }

    /**
     * Merges a brick shape into the board matrix at the specified position.
     * <p>
     * Copies non-zero cells from the brick matrix into the board matrix.
     * The brick is positioned at (x, y) where x is the column and y is the
     * row. Only cells within board boundaries are written.
     * </p>
     * <p>
     * Matrix indexing: matrix[row][column] = matrix[y][x]
     * Brick cells are written to board[y + row][x + col]
     * </p>
     *
     * @param filledFields the board matrix to merge into (matrix[row][col])
     * @param brick        the brick shape to merge (brick[row][col])
     * @param x            the column position (x coordinate)
     * @param y            the row position (y coordinate)
     * @return a new board matrix with the brick merged into it
     */
    public static int[][] merge(int[][] filledFields, int[][] brick, int x,
                                 int y) {
        int[][] copy = copy(filledFields);
        // x = column, y = row
        // matrix[row][column] = matrix[y][x]
        // brick is indexed as brick[row][column]
        for (int row = 0; row < brick.length; row++) {  // row = brick row
            for (int col = 0; col < brick[row].length; col++) {  // col = brick column
                if (brick[row][col] != 0) {
                    // Write to matrix[y + row][x + col]
                    // Bounds check: row < matrix.length, column < matrix[0].length
                    int targetRow = y + row;
                    int targetCol = x + col;
                    if (targetRow >= 0 && targetRow < copy.length &&
                            targetCol >= 0 && targetCol < copy[0].length) {
                        copy[targetRow][targetCol] = brick[row][col];
                    }
                }
            }
        }
        return copy;
    }

    /**
     * Creates a deep copy of a list of 2D integer arrays.
     * <p>
     * Uses MatrixOperations.copy() for each matrix in the list to ensure
     * complete independence from the original list.
     * </p>
     *
     * @param list the list of matrices to copy
     * @return a new list containing deep copies of all matrices
     */
    public static List<int[][]> deepCopyList(List<int[][]> list) {
        return list.stream().map(MatrixOperations::copy)
                .collect(Collectors.toList());
    }

    /**
     * Rotates a 2D matrix 90 degrees clockwise.
     * <p>
     * For a matrix of size rows x cols, returns a new matrix of size cols x
     * rows. The rotation transformation is: new[col][rows-1-row] = original[row][col]
     * </p>
     * <p>
     * Matrix indexing: matrix[row][column]
     * </p>
     *
     * @param matrix the matrix to rotate (matrix[row][col])
     * @return a new matrix rotated 90 degrees clockwise (rotated[new_row][new_col])
     */
    public static int[][] rotate90(int[][] matrix) {
        if (matrix == null || matrix.length == 0) {
            return new int[0][0];
        }

        int rows = matrix.length;
        int cols = matrix[0].length;
        int[][] rotated = new int[cols][rows];

        // Rotate 90 degrees clockwise: new[i][j] = original[rows-1-j][i]
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                rotated[j][rows - 1 - i] = matrix[i][j];
            }
        }

        return rotated;
    }
}
