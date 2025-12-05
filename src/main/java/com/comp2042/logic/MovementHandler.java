package com.comp2042.logic;

import java.awt.Point;

/**
 * Handles movement calculations for brick positions.
 * This class computes new positions but does NOT check collisions.
 * Coordinate system: x = column, y = row
 */
public class MovementHandler {

    /**
     * We don't want to instantiate this utility class
     */
    private MovementHandler() {
    }

    /**
     * Computes the new position after moving left.
     * Moves one column to the left (decreases x by 1).
     *
     * @param currentOffset the current brick offset position
     * @return a new Point representing the position after moving left
     */
    public static Point moveLeft(Point currentOffset) {
        return translate(currentOffset, -1, 0);
    }

    /**
     * Computes the new position after moving right.
     * Moves one column to the right (increases x by 1).
     *
     * @param currentOffset the current brick offset position
     * @return a new Point representing the position after moving right
     */
    public static Point moveRight(Point currentOffset) {
        return translate(currentOffset, 1, 0);
    }

    /**
     * Computes the new position after moving down.
     * Moves one row down (increases y by 1).
     *
     * @param currentOffset the current brick offset position
     * @return a new Point representing the position after moving down
     */
    public static Point moveDown(Point currentOffset) {
        return translate(currentOffset, 0, 1);
    }

    /**
     * Computes a new position by translating the current offset by the given deltas.
     * Creates a new Point object without modifying the original.
     *
     * @param currentOffset the current brick offset position
     * @param dx            the change in x (column) direction
     * @param dy            the change in y (row) direction
     * @return a new Point representing the translated position
     */
    public static Point translate(Point currentOffset, int dx, int dy) {
        return new Point((int) currentOffset.getX() + dx, (int) currentOffset.getY() + dy);
    }
}





