package com.comp2042.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Manages the game score, lines cleared, high score, and level progression using
 * JavaFX properties for reactive UI binding.
 * <p>
 * This class is part of the Model layer in the MVC architecture. It maintains
 * the current game score, total lines cleared, high score, and current level.
 * It provides JavaFX IntegerProperties that can be bound to UI components for
 * automatic score display updates.
 * </p>
 * <p>
 * The score properties are observable, allowing the View layer to automatically
 * update when the score changes. The high score is automatically updated when
 * the current score exceeds it. Level is calculated based on lines cleared:
 * level = (lines / 10) + 1, starting at level 1.
 * </p>
 *
 * @author TetrisJFX Team
 * @version 1.0
 */
public final class Score {

    private final IntegerProperty currentScore = new SimpleIntegerProperty(0);
    private final IntegerProperty totalLines = new SimpleIntegerProperty(0);
    private final IntegerProperty highScore = new SimpleIntegerProperty(0);
    private final IntegerProperty currentLevel = new SimpleIntegerProperty(1);

    /** Number of lines required per level increase. */
    private static final int LINES_PER_LEVEL = 10;

    /**
     * Returns the current score property for JavaFX binding.
     * <p>
     * This property can be bound to UI components to automatically update
     * the score display when the score changes.
     * </p>
     *
     * @return the IntegerProperty representing the current score
     */
    public IntegerProperty scoreProperty() {
        return currentScore;
    }

    /**
     * Returns the total lines cleared property for JavaFX binding.
     *
     * @return the IntegerProperty representing the total lines cleared
     */
    public IntegerProperty totalLinesProperty() {
        return totalLines;
    }

    /**
     * Returns the high score property for JavaFX binding.
     *
     * @return the IntegerProperty representing the high score
     */
    public IntegerProperty highScoreProperty() {
        return highScore;
    }

    /**
     * Returns the current level property for JavaFX binding.
     * <p>
     * The level is automatically calculated based on total lines cleared.
     * Level increases every 10 lines cleared (level = lines / 10 + 1).
     * </p>
     *
     * @return the IntegerProperty representing the current level
     */
    public IntegerProperty levelProperty() {
        return currentLevel;
    }

    /**
     * Gets the current score value.
     *
     * @return the current score
     */
    public int getCurrentScore() {
        return currentScore.get();
    }

    /**
     * Gets the total lines cleared value.
     *
     * @return the total lines cleared
     */
    public int getTotalLines() {
        return totalLines.get();
    }

    /**
     * Gets the high score value.
     *
     * @return the high score
     */
    public int getHighScore() {
        return highScore.get();
    }

    /**
     * Gets the current level value.
     * <p>
     * The level is calculated based on total lines cleared:
     * level = (lines / 10) + 1, with minimum level of 1.
     * </p>
     *
     * @return the current level (starts at 1)
     */
    public int getCurrentLevel() {
        return currentLevel.get();
    }

    /**
     * Adds the specified value to the current score.
     * <p>
     * The score is incremented by the provided value. This method is used
     * for both regular movement points and row clearing bonuses. The high
     * score is automatically updated if the new score exceeds it.
     * </p>
     *
     * @param points the value to add to the score (should be positive)
     */
    public void addScore(int points) {
        currentScore.setValue(currentScore.getValue() + points);
        // Update high score if current score exceeds it
        if (currentScore.get() > highScore.get()) {
            highScore.setValue(currentScore.get());
        }
    }

    /**
     * Adds the specified number of lines to the total lines cleared.
     * <p>
     * This method is called after rows are cleared to track the total
     * number of lines cleared in the current game. Automatically updates
     * the level based on the new total lines count.
     * </p>
     *
     * @param n the number of lines cleared (should be positive)
     */
    public void addLines(int n) {
        int oldLevel = currentLevel.get();
        totalLines.setValue(totalLines.getValue() + n);
        // Update level based on total lines: level = (lines / 10) + 1
        int newLevel = (totalLines.get() / LINES_PER_LEVEL) + 1;
        currentLevel.setValue(newLevel);
        
        // Level increased - this can be used by listeners to update game speed
        if (newLevel > oldLevel) {
            // Level up occurred - the property change will notify listeners
        }
    }

    /**
     * Adds the specified value to the current score (legacy method for compatibility).
     * <p>
     * This method is kept for backward compatibility. It calls addScore() internally.
     * </p>
     *
     * @param i the value to add to the score
     */
    public void add(int i) {
        addScore(i);
    }

    /**
     * Resets the current score, total lines, and level to initial values.
     * <p>
     * This method is called when starting a new game. The high score is
     * preserved across games. Level resets to 1.
     * </p>
     */
    public void reset() {
        currentScore.setValue(0);
        totalLines.setValue(0);
        currentLevel.setValue(1);
        // High score is NOT reset - it persists across games
    }

    /**
     * Calculates the game speed in milliseconds based on the current level.
     * <p>
     * The game speed decreases (pieces fall faster) as the level increases.
     * Formula: speed = max(50, 400 - (level - 1) * 50) milliseconds.
     * This means:
     * - Level 1: 400ms
     * - Level 2: 350ms
     * - Level 3: 300ms
     * - ...continuing until...
     * - Level 8: 50ms (minimum speed)
     * </p>
     *
     * @return the game speed in milliseconds (lower = faster)
     */
    public long getGameSpeedMillis() {
        int level = currentLevel.get();
        // Speed decreases by 50ms per level, minimum 50ms
        long speed = Math.max(50, 400 - (level - 1) * 50);
        return speed;
    }
}
