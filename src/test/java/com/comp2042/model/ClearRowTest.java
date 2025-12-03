package com.comp2042.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for ClearRow data container class.
 * Tests line removal count, score bonus calculation, and matrix content.
 */
@DisplayName("ClearRow Tests")
class ClearRowTest {

    @Test
    @DisplayName("getLinesRemoved() returns correct count")
    void testGetLinesRemoved() {
        // Arrange
        int[][] matrix = {
                {0, 0, 0},
                {1, 1, 1}
        };
        int linesRemoved = 1;
        int scoreBonus = 50;

        // Act
        ClearRow clearRow = new ClearRow(linesRemoved, matrix, scoreBonus);

        // Assert
        assertEquals(1, clearRow.getLinesRemoved(),
                "Should return correct number of lines removed");
    }

    @Test
    @DisplayName("getScoreBonus() returns correct bonus")
    void testGetScoreBonus() {
        // Arrange
        int[][] matrix = {{0, 0}};
        int linesRemoved = 2;
        int scoreBonus = 200; // 50 * 2^2

        // Act
        ClearRow clearRow = new ClearRow(linesRemoved, matrix, scoreBonus);

        // Assert
        assertEquals(200, clearRow.getScoreBonus(),
                "Should return correct score bonus");
    }

    @Test
    @DisplayName("getNewMatrix() returns deep copy")
    void testGetNewMatrix_ReturnsDeepCopy() {
        // Arrange
        int[][] originalMatrix = {
                {0, 0, 0},
                {1, 2, 3}
        };
        ClearRow clearRow = new ClearRow(0, originalMatrix, 0);

        // Act
        int[][] copy1 = clearRow.getNewMatrix();
        int[][] copy2 = clearRow.getNewMatrix();

        // Assert
        assertNotSame(originalMatrix, copy1,
                "Returned matrix should be a different object");
        assertNotSame(copy1, copy2,
                "Each call should return a new copy");
        assertArrayEquals(originalMatrix, copy1,
                "Copy should have same content as original");

        // Modify copy and verify it doesn't affect subsequent calls
        copy1[1][0] = 99;
        int[][] copy3 = clearRow.getNewMatrix();
        assertEquals(1, copy3[1][0],
                "Modifying copy should not affect subsequent calls");
    }

    @Test
    @DisplayName("ClearRow with zero lines removed")
    void testClearRow_ZeroLines() {
        // Arrange
        int[][] matrix = {
                {0, 0, 0},
                {1, 0, 1}
        };

        // Act
        ClearRow clearRow = new ClearRow(0, matrix, 0);

        // Assert
        assertEquals(0, clearRow.getLinesRemoved());
        assertEquals(0, clearRow.getScoreBonus());
        assertArrayEquals(matrix, clearRow.getNewMatrix());
    }

    @Test
    @DisplayName("ClearRow with multiple lines removed")
    void testClearRow_MultipleLines() {
        // Arrange
        int[][] matrix = {
                {0, 0, 0},
                {0, 0, 0}
        };
        int linesRemoved = 3;
        int scoreBonus = 450; // 50 * 3^2

        // Act
        ClearRow clearRow = new ClearRow(linesRemoved, matrix, scoreBonus);

        // Assert
        assertEquals(3, clearRow.getLinesRemoved());
        assertEquals(450, clearRow.getScoreBonus());
    }

    @Test
    @DisplayName("getNewMatrix() content matches expected cleared state")
    void testGetNewMatrix_ContentMatchesExpected() {
        // Arrange
        int[][] expectedMatrix = {
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {1, 2, 0, 0}
        };
        int linesRemoved = 2;
        int scoreBonus = 200;

        // Act
        ClearRow clearRow = new ClearRow(linesRemoved, expectedMatrix, scoreBonus);

        // Assert
        int[][] actualMatrix = clearRow.getNewMatrix();
        assertArrayEquals(expectedMatrix, actualMatrix,
                "Matrix content should match expected cleared state");
    }

    @Test
    @DisplayName("ClearRow handles empty matrix")
    void testClearRow_EmptyMatrix() {
        // Arrange
        int[][] emptyMatrix = {};

        // Act
        ClearRow clearRow = new ClearRow(0, emptyMatrix, 0);

        // Assert
        assertNotNull(clearRow.getNewMatrix());
        assertEquals(0, clearRow.getLinesRemoved());
    }

    @Test
    @DisplayName("Score bonus calculation: 1 line = 50 points")
    void testScoreBonus_OneLine() {
        // Arrange
        int[][] matrix = {{0}};
        int linesRemoved = 1;

        // Act
        ClearRow clearRow = new ClearRow(linesRemoved, matrix, 50);

        // Assert
        assertEquals(50, clearRow.getScoreBonus(),
                "1 line should give 50 points (50 * 1^2)");
    }

    @Test
    @DisplayName("Score bonus calculation: 2 lines = 200 points")
    void testScoreBonus_TwoLines() {
        // Arrange
        int[][] matrix = {{0}};
        int linesRemoved = 2;

        // Act
        ClearRow clearRow = new ClearRow(linesRemoved, matrix, 200);

        // Assert
        assertEquals(200, clearRow.getScoreBonus(),
                "2 lines should give 200 points (50 * 2^2)");
    }

    @Test
    @DisplayName("Score bonus calculation: 4 lines = 800 points")
    void testScoreBonus_FourLines() {
        // Arrange
        int[][] matrix = {{0}};
        int linesRemoved = 4;

        // Act
        ClearRow clearRow = new ClearRow(linesRemoved, matrix, 800);

        // Assert
        assertEquals(800, clearRow.getScoreBonus(),
                "4 lines should give 800 points (50 * 4^2)");
    }
}


