package com.comp2042.logic;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for MatrixOperations utility class.
 * Tests matrix copying, merging, rotation, and deep copying operations.
 */
@DisplayName("MatrixOperations Tests")
class MatrixOperationsTest {

    @Test
    @DisplayName("copy() creates deep copy of matrix")
    void testCopy() {
        // Arrange
        int[][] original = {
                {1, 2, 3},
                {4, 5, 6}
        };

        // Act
        int[][] copy = MatrixOperations.copy(original);

        // Assert
        assertNotSame(original, copy, "Copy should be a different object");
        assertArrayEquals(original, copy, "Copy should have same content");

        // Modify copy and verify original is unchanged
        copy[0][0] = 99;
        assertEquals(1, original[0][0], "Original should not be modified");
        assertEquals(99, copy[0][0], "Copy should be modified");
    }

    @Test
    @DisplayName("merge() merges brick into board at valid position")
    void testMergeValidPosition() {
        // Arrange
        int[][] board = {
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        };
        int[][] brick = {
                {1, 1},
                {1, 0}
        };
        int x = 1; // column
        int y = 1; // row

        // Act
        int[][] result = MatrixOperations.merge(board, brick, x, y);

        // Assert
        int[][] expected = {
                {0, 0, 0, 0},
                {0, 1, 1, 0},
                {0, 1, 0, 0}
        };
        assertArrayEquals(expected, result, "Brick should be merged at position (1,1)");
    }

    @Test
    @DisplayName("merge() handles brick at origin (0,0)")
    void testMergeAtOrigin() {
        // Arrange
        int[][] board = {
                {0, 0, 0},
                {0, 0, 0}
        };
        int[][] brick = {
                {2, 2}
        };
        int x = 0;
        int y = 0;

        // Act
        int[][] result = MatrixOperations.merge(board, brick, x, y);

        // Assert
        int[][] expected = {
                {2, 2, 0},
                {0, 0, 0}
        };
        assertArrayEquals(expected, result);
    }

    @Test
    @DisplayName("merge() ignores zero cells in brick")
    void testMergeIgnoresZeros() {
        // Arrange
        int[][] board = {
                {0, 0, 0},
                {0, 0, 0}
        };
        int[][] brick = {
                {0, 3, 0}
        };
        int x = 0;
        int y = 0;

        // Act
        int[][] result = MatrixOperations.merge(board, brick, x, y);

        // Assert
        int[][] expected = {
                {0, 3, 0},
                {0, 0, 0}
        };
        assertArrayEquals(expected, result, "Only non-zero cells should be merged");
    }

    @Test
    @DisplayName("merge() handles brick partially outside bounds")
    void testMergePartialBounds() {
        // Arrange
        int[][] board = {
                {0, 0, 0},
                {0, 0, 0}
        };
        int[][] brick = {
                {4, 4, 4}
        };
        int x = 2; // Brick extends beyond board width
        int y = 0;

        // Act
        int[][] result = MatrixOperations.merge(board, brick, x, y);

        // Assert - Only cells within bounds should be written
        assertEquals(4, result[0][2], "Cell within bounds should be written");
        // Cells outside bounds should not cause exception
        assertNotNull(result);
    }

    @Test
    @DisplayName("rotate90() rotates 2x2 matrix correctly")
    void testRotate90_2x2() {
        // Arrange
        int[][] matrix = {
                {1, 2},
                {3, 4}
        };

        // Act
        int[][] rotated = MatrixOperations.rotate90(matrix);

        // Assert
        int[][] expected = {
                {3, 1},
                {4, 2}
        };
        assertArrayEquals(expected, rotated, "2x2 matrix should rotate correctly");
    }

    @Test
    @DisplayName("rotate90() rotates 3x2 matrix correctly")
    void testRotate90_3x2() {
        // Arrange
        int[][] matrix = {
                {1, 2},
                {3, 4},
                {5, 6}
        };

        // Act
        int[][] rotated = MatrixOperations.rotate90(matrix);

        // Assert
        int[][] expected = {
                {5, 3, 1},
                {6, 4, 2}
        };
        assertArrayEquals(expected, rotated, "3x2 matrix should rotate to 2x3");
    }

    @Test
    @DisplayName("rotate90() handles 1x4 matrix (horizontal line)")
    void testRotate90_1x4() {
        // Arrange
        int[][] matrix = {
                {1, 1, 1, 1}
        };

        // Act
        int[][] rotated = MatrixOperations.rotate90(matrix);

        // Assert
        int[][] expected = {
                {1},
                {1},
                {1},
                {1}
        };
        assertArrayEquals(expected, rotated, "1x4 should rotate to 4x1");
    }

    @Test
    @DisplayName("rotate90() handles empty matrix")
    void testRotate90_Empty() {
        // Arrange
        int[][] matrix = {};

        // Act
        int[][] rotated = MatrixOperations.rotate90(matrix);

        // Assert
        assertNotNull(rotated);
        assertEquals(0, rotated.length);
    }

    @Test
    @DisplayName("rotate90() handles null matrix")
    void testRotate90_Null() {
        // Act
        int[][] rotated = MatrixOperations.rotate90(null);

        // Assert
        assertNotNull(rotated);
        assertEquals(0, rotated.length);
    }

    @Test
    @DisplayName("deepCopyList() creates independent copies")
    void testDeepCopyList() {
        // Arrange
        int[][] matrix1 = {{1, 2}, {3, 4}};
        int[][] matrix2 = {{5, 6}, {7, 8}};
        java.util.List<int[][]> original = new java.util.ArrayList<>();
        original.add(matrix1);
        original.add(matrix2);

        // Act
        java.util.List<int[][]> copy = MatrixOperations.deepCopyList(original);

        // Assert
        assertNotSame(original, copy, "List should be different object");
        assertEquals(original.size(), copy.size(), "Lists should have same size");

        // Modify copy and verify original is unchanged
        copy.get(0)[0][0] = 99;
        assertEquals(1, original.get(0)[0][0], "Original should not be modified");
        assertEquals(99, copy.get(0)[0][0], "Copy should be modified");
    }
}





