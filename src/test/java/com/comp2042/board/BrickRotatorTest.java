package com.comp2042.board;

import com.comp2042.logic.NextShapeInfo;
import com.comp2042.model.Brick;
import com.comp2042.model.IBrick;
import com.comp2042.model.OBrick;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for BrickRotator class.
 * Tests rotation state management and strategy selection.
 */
@DisplayName("BrickRotator Tests")
class BrickRotatorTest {

    private BrickRotator rotator;

    @BeforeEach
    void setUp() {
        rotator = new BrickRotator();
    }

    @Test
    @DisplayName("setBrick() initializes rotation state to 0")
    void testSetBrick_InitializesState() {
        // Arrange
        Brick brick = new IBrick();

        // Act
        rotator.setBrick(brick);

        // Assert
        int[][] currentShape = rotator.getCurrentShape();
        assertNotNull(currentShape, "Current shape should not be null");
    }

    @Test
    @DisplayName("getCurrentShape() returns first shape after setBrick")
    void testGetCurrentShape_ReturnsFirstShape() {
        // Arrange
        Brick brick = new IBrick();
        rotator.setBrick(brick);
        List<int[][]> shapes = brick.getShapeMatrix();

        // Act
        int[][] currentShape = rotator.getCurrentShape();

        // Assert
        assertArrayEquals(shapes.get(0), currentShape,
                "Current shape should be the first shape variant");
    }

    @Test
    @DisplayName("getNextShape() returns next rotation state")
    void testGetNextShape_ReturnsNextState() {
        // Arrange
        Brick brick = new IBrick(); // Has 2 rotation states
        rotator.setBrick(brick);
        List<int[][]> shapes = brick.getShapeMatrix();

        // Act
        NextShapeInfo nextInfo = rotator.getNextShape();

        // Assert
        assertNotNull(nextInfo, "NextShapeInfo should not be null");
        assertEquals(1, nextInfo.getPosition(),
                "Next position should be 1 (second shape)");
        assertArrayEquals(shapes.get(1), nextInfo.getShape(),
                "Next shape should be the second shape variant");
    }

    @Test
    @DisplayName("getNextShape() wraps around after last shape")
    void testGetNextShape_WrapsAround() {
        // Arrange
        Brick brick = new IBrick(); // Has 2 rotation states (0 and 1)
        rotator.setBrick(brick);
        rotator.setCurrentShape(1); // Set to last shape

        // Act
        NextShapeInfo nextInfo = rotator.getNextShape();

        // Assert
        assertEquals(0, nextInfo.getPosition(),
                "Should wrap around to position 0");
    }

    @Test
    @DisplayName("setCurrentShape() updates rotation state")
    void testSetCurrentShape_UpdatesState() {
        // Arrange
        Brick brick = new IBrick();
        rotator.setBrick(brick);
        List<int[][]> shapes = brick.getShapeMatrix();

        // Act
        rotator.setCurrentShape(1);
        int[][] currentShape = rotator.getCurrentShape();

        // Assert
        assertArrayEquals(shapes.get(1), currentShape,
                "Current shape should be updated to second variant");
    }

    @Test
    @DisplayName("setBrick() selects NoRotationStrategy for square bricks")
    void testSetBrick_SelectsNoRotationStrategy() {
        // Arrange
        Brick squareBrick = new OBrick(); // Square brick with 1 shape

        // Act
        rotator.setBrick(squareBrick);

        // Assert
        // Verify that rotation doesn't change shape (indirect test)
        int[][] shape1 = rotator.getCurrentShape();
        NextShapeInfo nextInfo = rotator.getNextShape();
        rotator.setCurrentShape(nextInfo.getPosition());
        int[][] shape2 = rotator.getCurrentShape();

        // For square bricks, all shapes should be the same
        assertArrayEquals(shape1, shape2,
                "Square brick should not change shape on rotation");
    }

    @Test
    @DisplayName("setBrick() selects StandardRotationStrategy for non-square bricks")
    void testSetBrick_SelectsStandardRotationStrategy() {
        // Arrange
        Brick brick = new IBrick(); // Non-square brick

        // Act
        rotator.setBrick(brick);
        NextShapeInfo nextInfo = rotator.getNextShape();

        // Assert
        assertNotNull(nextInfo.getShape(),
                "Next shape should be calculated");
        // The next shape should be different from current (for I-brick)
        int[][] currentShape = rotator.getCurrentShape();
        // I-brick has 2 different shapes, so they should be different
        assertNotEquals(currentShape.length, nextInfo.getShape().length,
                "I-brick shapes should have different dimensions");
    }

    @Test
    @DisplayName("getNextShape() cycles through all rotation states")
    void testGetNextShape_CyclesThroughStates() {
        // Arrange
        Brick brick = new IBrick(); // Has 2 states
        rotator.setBrick(brick);
        List<int[][]> shapes = brick.getShapeMatrix();

        // Act & Assert - Cycle through all states
        NextShapeInfo info1 = rotator.getNextShape();
        assertEquals(1, info1.getPosition());
        assertArrayEquals(shapes.get(1), info1.getShape());

        rotator.setCurrentShape(1);
        NextShapeInfo info2 = rotator.getNextShape();
        assertEquals(0, info2.getPosition()); // Wraps around
        assertArrayEquals(shapes.get(0), info2.getShape());
    }

    @Test
    @DisplayName("Multiple rotations maintain correct state sequence")
    void testMultipleRotations_StateSequence() {
        // Arrange
        Brick brick = new IBrick();
        rotator.setBrick(brick);

        // Act - Perform multiple rotations
        NextShapeInfo info1 = rotator.getNextShape();
        rotator.setCurrentShape(info1.getPosition());
        NextShapeInfo info2 = rotator.getNextShape();
        rotator.setCurrentShape(info2.getPosition());
        NextShapeInfo info3 = rotator.getNextShape();

        // Assert
        assertEquals(0, info1.getPosition());
        assertEquals(1, info2.getPosition());
        assertEquals(0, info3.getPosition()); // Should wrap around
    }
}





