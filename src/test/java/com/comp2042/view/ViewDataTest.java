package com.comp2042.view;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for ViewData class.
 * Tests immutability and deep copying behavior.
 */
@DisplayName("ViewData Tests")
class ViewDataTest {

    @Test
    @DisplayName("getBrickData() returns deep copy")
    void testGetBrickData_ReturnsDeepCopy() {
        // Arrange
        int[][] originalBrick = {
                {1, 1},
                {1, 0}
        };
        int[][] nextBrick = {
                {2, 2}
        };
        ViewData viewData = new ViewData(originalBrick, 0, 0, nextBrick);

        // Act
        int[][] copy1 = viewData.getBrickData();
        int[][] copy2 = viewData.getBrickData();

        // Assert
        assertNotSame(originalBrick, copy1,
                "Returned array should be a different object");
        assertNotSame(copy1, copy2,
                "Each call should return a new copy");
        assertArrayEquals(originalBrick, copy1,
                "Copy should have same content as original");

        // Modify copy and verify original is unchanged
        copy1[0][0] = 99;
        int[][] copy3 = viewData.getBrickData();
        assertEquals(1, copy3[0][0],
                "Modifying copy should not affect subsequent calls");
    }

    @Test
    @DisplayName("getNextBrickData() returns deep copy")
    void testGetNextBrickData_ReturnsDeepCopy() {
        // Arrange
        int[][] brick = {{1, 1}};
        int[][] originalNext = {
                {2, 2, 2}
        };
        ViewData viewData = new ViewData(brick, 0, 0, originalNext);

        // Act
        int[][] copy1 = viewData.getNextBrickData();
        int[][] copy2 = viewData.getNextBrickData();

        // Assert
        assertNotSame(originalNext, copy1,
                "Returned array should be a different object");
        assertNotSame(copy1, copy2,
                "Each call should return a new copy");
        assertArrayEquals(originalNext, copy1,
                "Copy should have same content as original");

        // Modify copy and verify original is unchanged
        copy1[0][0] = 99;
        int[][] copy3 = viewData.getNextBrickData();
        assertEquals(2, copy3[0][0],
                "Modifying copy should not affect subsequent calls");
    }

    @Test
    @DisplayName("getxPosition() returns correct x position")
    void testGetxPosition() {
        // Arrange
        int[][] brick = {{1, 1}};
        int[][] next = {{2}};
        int expectedX = 3;

        // Act
        ViewData viewData = new ViewData(brick, expectedX, 0, next);

        // Assert
        assertEquals(expectedX, viewData.getxPosition(),
                "X position should match constructor parameter");
    }

    @Test
    @DisplayName("getyPosition() returns correct y position")
    void testGetyPosition() {
        // Arrange
        int[][] brick = {{1, 1}};
        int[][] next = {{2}};
        int expectedY = 5;

        // Act
        ViewData viewData = new ViewData(brick, 0, expectedY, next);

        // Assert
        assertEquals(expectedY, viewData.getyPosition(),
                "Y position should match constructor parameter");
    }

    @Test
    @DisplayName("ViewData is immutable - modifying returned arrays doesn't affect internal state")
    void testImmutability_ModifyingReturnedArray() {
        // Arrange
        int[][] brick = {
                {1, 2},
                {3, 4}
        };
        int[][] next = {{5}};
        ViewData viewData = new ViewData(brick, 0, 0, next);

        // Act - Get copy and modify it
        int[][] copy = viewData.getBrickData();
        copy[0][0] = 99;
        copy[1][1] = 88;

        // Assert - Get another copy and verify it's unchanged
        int[][] anotherCopy = viewData.getBrickData();
        assertEquals(1, anotherCopy[0][0],
                "Internal state should not be affected by modifying returned copy");
        assertEquals(4, anotherCopy[1][1],
                "Internal state should not be affected by modifying returned copy");
    }

    @Test
    @DisplayName("ViewData handles empty brick matrices")
    void testViewData_EmptyMatrices() {
        // Arrange
        int[][] emptyBrick = {};
        int[][] emptyNext = {};

        // Act
        ViewData viewData = new ViewData(emptyBrick, 0, 0, emptyNext);

        // Assert
        assertNotNull(viewData.getBrickData());
        assertNotNull(viewData.getNextBrickData());
    }

    @Test
    @DisplayName("ViewData handles large matrices")
    void testViewData_LargeMatrices() {
        // Arrange
        int[][] largeBrick = new int[10][10];
        largeBrick[5][5] = 7;
        int[][] largeNext = new int[8][8];
        largeNext[4][4] = 3;

        // Act
        ViewData viewData = new ViewData(largeBrick, 0, 0, largeNext);

        // Assert
        int[][] copy = viewData.getBrickData();
        assertEquals(10, copy.length);
        assertEquals(7, copy[5][5]);

        int[][] nextCopy = viewData.getNextBrickData();
        assertEquals(8, nextCopy.length);
        assertEquals(3, nextCopy[4][4]);
    }
}

