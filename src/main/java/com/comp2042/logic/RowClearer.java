package com.comp2042.logic;

import com.comp2042.model.ClearRow;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Handles row-clearing logic for the Tetris game board.
 * Detects full rows, removes them, and collapses remaining rows downward.
 * Matrix indexing: matrix[row][column]
 */
public class RowClearer {

    /**
     * We don't want to instantiate this utility class
     */
    private RowClearer() {
    }

    /**
     * Clears full rows from the board and collapses remaining rows downward.
     * 
     * @param board the game board matrix (matrix[row][column])
     * @return a ClearRow object containing:
     *         - number of lines removed
     *         - updated board matrix with cleared rows removed and remaining rows collapsed
     *         - score bonus calculated as 50 * (linesRemoved)^2
     */
    public static ClearRow clear(int[][] board) {
        int rows = board.length;
        int cols = board[0].length;
        int[][] newBoard = new int[rows][cols];
        Deque<int[]> remainingRows = new ArrayDeque<>();
        List<Integer> clearedRows = new ArrayList<>();

        // Detect full rows and collect remaining rows
        // matrix[row][column] - i is row, j is column
        for (int i = 0; i < rows; i++) {  // i = row
            int[] currentRow = new int[cols];
            boolean isFullRow = true;
            for (int j = 0; j < cols; j++) {  // j = column
                if (board[i][j] == 0) {
                    isFullRow = false;
                }
                currentRow[j] = board[i][j];
            }
            if (isFullRow) {
                clearedRows.add(i);
            } else {
                remainingRows.add(currentRow);
            }
        }

        // Collapse remaining rows downward (fill from bottom)
        for (int i = rows - 1; i >= 0; i--) {
            int[] row = remainingRows.pollLast();
            if (row != null) {
                newBoard[i] = row;
            } else {
                // Fill remaining rows with empty rows (all zeros)
                newBoard[i] = new int[cols];
            }
        }

        // Calculate score bonus: 50 * (number of lines removed)^2
        int scoreBonus = 50 * clearedRows.size() * clearedRows.size();
        
        return new ClearRow(clearedRows.size(), newBoard, scoreBonus);
    }
}

