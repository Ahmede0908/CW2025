package com.comp2042.model;

import com.comp2042.logic.MatrixOperations;

import java.util.ArrayList;
import java.util.List;

/**
 * Test brick implementation for deterministic testing.
 * Creates a simple 2x2 square brick for use in unit tests.
 */
class TestBrick implements Brick {
    private final List<int[][]> shapes;

    TestBrick(int[][] shape) {
        shapes = new ArrayList<>();
        shapes.add(shape);
    }

    TestBrick(List<int[][]> shapes) {
        this.shapes = new ArrayList<>(shapes);
    }

    @Override
    public List<int[][]> getShapeMatrix() {
        return MatrixOperations.deepCopyList(shapes);
    }
}

