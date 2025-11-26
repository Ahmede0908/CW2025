package com.comp2042.logic;

import java.util.List;
import java.util.stream.Collectors;

public class MatrixOperations {


    //We don't want to instantiate this utility class
    private MatrixOperations(){

    }

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
     * Merges a brick into the board matrix.
     * Matrix indexing: matrix[row][column] = matrix[y][x]
     * Uses: matrix[y + row][x + col] where row is brick row, col is brick column
     */
    public static int[][] merge(int[][] filledFields, int[][] brick, int x, int y) {
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

    public static List<int[][]> deepCopyList(List<int[][]> list){
        return list.stream().map(MatrixOperations::copy).collect(Collectors.toList());
    }

    /**
     * Rotates a 2D matrix 90 degrees clockwise.
     * For a matrix of size rows x cols, returns a new matrix of size cols x rows.
     * 
     * @param matrix the matrix to rotate
     * @return a new matrix rotated 90 degrees clockwise
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
