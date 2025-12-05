package com.comp2042.logic;

import com.comp2042.model.ClearRow;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for RowClearer utility class.
 * Tests row detection, removal, and score bonus calculation.
 */
@DisplayName("RowClearer Tests")
class RowClearerTest {

    @Test
    @DisplayName("clear() removes single full row")
    void testClear_SingleRow() {
        // Arrange
        int[][] board = {
                {0, 0, 0, 0},
                {1, 1, 1, 1}, // Full row
                {0, 2, 0, 0},
                {0, 0, 0, 0}
        };

        // Act
        ClearRow result = RowClearer.clear(board);

        // Assert
        assertEquals(1, result.getLinesRemoved(), "Should remove 1 line");
        assertEquals(50, result.getScoreBonus(), "Score bonus should be 50 for 1 line");

        int[][] expected = {
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 2, 0, 0},
                {0, 0, 0, 0}
        };
        assertArrayEquals(expected, result.getNewMatrix(),
                "Full row should be removed and rows collapsed");
    }

    @Test
    @DisplayName("clear() removes two full rows")
    void testClear_TwoRows() {
        // Arrange
        int[][] board = {
                {0, 0, 0, 0},
                {1, 1, 1, 1}, // Full row 1
                {2, 2, 2, 2}, // Full row 2
                {0, 3, 0, 0},
                {0, 0, 0, 0}
        };

        // Act
        ClearRow result = RowClearer.clear(board);

        // Assert
        assertEquals(2, result.getLinesRemoved(), "Should remove 2 lines");
        assertEquals(200, result.getScoreBonus(), "Score bonus should be 200 for 2 lines (50*2^2)");

        int[][] expected = {
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 3, 0, 0},
                {0, 0, 0, 0}
        };
        assertArrayEquals(expected, result.getNewMatrix(),
                "Two full rows should be removed");
    }

    @Test
    @DisplayName("clear() removes no rows when none are full")
    void testClear_NoRows() {
        // Arrange
        int[][] board = {
                {0, 0, 0, 0},
                {1, 0, 1, 0}, // Not full
                {0, 2, 0, 0},
                {0, 0, 0, 0}
        };

        // Act
        ClearRow result = RowClearer.clear(board);

        // Assert
        assertEquals(0, result.getLinesRemoved(), "Should remove 0 lines");
        assertEquals(0, result.getScoreBonus(), "Score bonus should be 0");

        int[][] expected = {
                {0, 0, 0, 0},
                {1, 0, 1, 0},
                {0, 2, 0, 0},
                {0, 0, 0, 0}
        };
        assertArrayEquals(expected, result.getNewMatrix(),
                "Board should remain unchanged");
    }

    @Test
    @DisplayName("clear() handles all rows full")
    void testClear_AllRowsFull() {
        // Arrange
        int[][] board = {
                {1, 1, 1},
                {2, 2, 2},
                {3, 3, 3}
        };

        // Act
        ClearRow result = RowClearer.clear(board);

        // Assert
        assertEquals(3, result.getLinesRemoved(), "Should remove 3 lines");
        assertEquals(450, result.getScoreBonus(), "Score bonus should be 450 (50*3^2)");

        int[][] expected = {
                {0, 0, 0},
                {0, 0, 0},
                {0, 0, 0}
        };
        assertArrayEquals(expected, result.getNewMatrix(),
                "All rows should be cleared");
    }

    @Test
    @DisplayName("clear() collapses remaining rows downward")
    void testClear_CollapseRows() {
        // Arrange
        int[][] board = {
                {0, 0, 0},
                {1, 1, 1}, // Full row
                {0, 2, 0},
                {3, 0, 3}
        };

        // Act
        ClearRow result = RowClearer.clear(board);

        // Assert
        int[][] expected = {
                {0, 0, 0},
                {0, 0, 0},
                {0, 2, 0},
                {3, 0, 3}
        };
        assertArrayEquals(expected, result.getNewMatrix(),
                "Remaining rows should collapse downward");
    }

    @Test
    @DisplayName("clear() handles bottom row full")
    void testClear_BottomRowFull() {
        // Arrange
        int[][] board = {
                {0, 0, 0},
                {0, 1, 0},
                {2, 2, 2} // Full row at bottom
        };

        // Act
        ClearRow result = RowClearer.clear(board);

        // Assert
        assertEquals(1, result.getLinesRemoved());
        int[][] expected = {
                {0, 0, 0},
                {0, 0, 0},
                {0, 1, 0}
        };
        assertArrayEquals(expected, result.getNewMatrix());
    }

    @Test
    @DisplayName("clear() handles top row full")
    void testClear_TopRowFull() {
        // Arrange
        int[][] board = {
                {1, 1, 1}, // Full row at top
                {0, 0, 0},
                {0, 2, 0}
        };

        // Act
        ClearRow result = RowClearer.clear(board);

        // Assert
        assertEquals(1, result.getLinesRemoved());
        int[][] expected = {
                {0, 0, 0},
                {0, 0, 0},
                {0, 2, 0}
        };
        assertArrayEquals(expected, result.getNewMatrix());
    }
}





