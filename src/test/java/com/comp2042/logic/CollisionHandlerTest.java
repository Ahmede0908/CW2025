package com.comp2042.logic;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for CollisionHandler utility class.
 * Tests collision detection and bounds checking.
 */
@DisplayName("CollisionHandler Tests")
class CollisionHandlerTest {

    @Test
    @DisplayName("hasCollision() detects collision with existing blocks")
    void testHasCollision_WithBlocks() {
        // Arrange
        int[][] board = {
                {0, 0, 0, 0},
                {0, 1, 1, 0},
                {0, 0, 0, 0}
        };
        int[][] shape = {
                {2, 2}
        };
        int x = 1; // column
        int y = 1; // row - overlaps with existing block

        // Act
        boolean collision = CollisionHandler.hasCollision(board, shape, x, y);

        // Assert
        assertTrue(collision, "Should detect collision with existing blocks");
    }

    @Test
    @DisplayName("hasCollision() returns false when no collision")
    void testHasCollision_NoCollision() {
        // Arrange
        int[][] board = {
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        };
        int[][] shape = {
                {1, 1}
        };
        int x = 1;
        int y = 1;

        // Act
        boolean collision = CollisionHandler.hasCollision(board, shape, x, y);

        // Assert
        assertFalse(collision, "Should not detect collision in empty space");
    }

    @Test
    @DisplayName("hasCollision() detects out of bounds (negative row)")
    void testHasCollision_OutOfBounds_NegativeRow() {
        // Arrange
        int[][] board = {
                {0, 0, 0},
                {0, 0, 0}
        };
        int[][] shape = {
                {1, 1}
        };
        int x = 0;
        int y = -1; // Negative row

        // Act
        boolean collision = CollisionHandler.hasCollision(board, shape, x, y);

        // Assert
        assertTrue(collision, "Should detect out of bounds (negative row)");
    }

    @Test
    @DisplayName("hasCollision() detects out of bounds (negative column)")
    void testHasCollision_OutOfBounds_NegativeColumn() {
        // Arrange
        int[][] board = {
                {0, 0, 0},
                {0, 0, 0}
        };
        int[][] shape = {
                {1, 1}
        };
        int x = -1; // Negative column
        int y = 0;

        // Act
        boolean collision = CollisionHandler.hasCollision(board, shape, x, y);

        // Assert
        assertTrue(collision, "Should detect out of bounds (negative column)");
    }

    @Test
    @DisplayName("hasCollision() detects out of bounds (row too large)")
    void testHasCollision_OutOfBounds_RowTooLarge() {
        // Arrange
        int[][] board = {
                {0, 0, 0},
                {0, 0, 0}
        };
        int[][] shape = {
                {1, 1}
        };
        int x = 0;
        int y = 2; // Row beyond board height

        // Act
        boolean collision = CollisionHandler.hasCollision(board, shape, x, y);

        // Assert
        assertTrue(collision, "Should detect out of bounds (row too large)");
    }

    @Test
    @DisplayName("hasCollision() detects out of bounds (column too large)")
    void testHasCollision_OutOfBounds_ColumnTooLarge() {
        // Arrange
        int[][] board = {
                {0, 0, 0},
                {0, 0, 0}
        };
        int[][] shape = {
                {1, 1}
        };
        int x = 2; // Column beyond board width
        int y = 0;

        // Act
        boolean collision = CollisionHandler.hasCollision(board, shape, x, y);

        // Assert
        assertTrue(collision, "Should detect out of bounds (column too large)");
    }

    @Test
    @DisplayName("hasCollision() ignores zero cells in shape")
    void testHasCollision_IgnoresZeros() {
        // Arrange
        int[][] board = {
                {0, 1, 0},
                {0, 0, 0}
        };
        int[][] shape = {
                {0, 2, 0} // Zero cells should not cause collision
        };
        int x = 0;
        int y = 0;

        // Act
        boolean collision = CollisionHandler.hasCollision(board, shape, x, y);

        // Assert
        assertFalse(collision, "Zero cells should not cause collision");
    }

    @Test
    @DisplayName("outOfBounds() returns true for negative row")
    void testOutOfBounds_NegativeRow() {
        // Arrange
        int[][] board = {
                {0, 0},
                {0, 0}
        };

        // Act & Assert
        assertTrue(CollisionHandler.outOfBounds(board, 0, -1),
                "Negative row should be out of bounds");
    }

    @Test
    @DisplayName("outOfBounds() returns true for negative column")
    void testOutOfBounds_NegativeColumn() {
        // Arrange
        int[][] board = {
                {0, 0},
                {0, 0}
        };

        // Act & Assert
        assertTrue(CollisionHandler.outOfBounds(board, -1, 0),
                "Negative column should be out of bounds");
    }

    @Test
    @DisplayName("outOfBounds() returns true for row too large")
    void testOutOfBounds_RowTooLarge() {
        // Arrange
        int[][] board = {
                {0, 0},
                {0, 0}
        };

        // Act & Assert
        assertTrue(CollisionHandler.outOfBounds(board, 0, 2),
                "Row >= board.length should be out of bounds");
    }

    @Test
    @DisplayName("outOfBounds() returns true for column too large")
    void testOutOfBounds_ColumnTooLarge() {
        // Arrange
        int[][] board = {
                {0, 0},
                {0, 0}
        };

        // Act & Assert
        assertTrue(CollisionHandler.outOfBounds(board, 2, 0),
                "Column >= board[0].length should be out of bounds");
    }

    @Test
    @DisplayName("outOfBounds() returns false for valid position")
    void testOutOfBounds_ValidPosition() {
        // Arrange
        int[][] board = {
                {0, 0},
                {0, 0}
        };

        // Act & Assert
        assertFalse(CollisionHandler.outOfBounds(board, 0, 0),
                "Position (0,0) should be in bounds");
        assertFalse(CollisionHandler.outOfBounds(board, 1, 1),
                "Position (1,1) should be in bounds");
    }
}




