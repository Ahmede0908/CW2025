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
 * level = 1 + (totalLinesCleared / 10), starting at level 1.
 * </p>
 * <p>
 * Scoring follows Tetris Guideline rules:
 * - Soft Drop: +1 point per cell moved down manually
 * - Hard Drop: +2 points per cell moved down
 * - Line clears: 100/300/500/800 × level for 1/2/3/4 lines
 * </p>
 *
 * @author TetrisJFX Team
 * @version 2.0
 */
public final class Score {

    private final IntegerProperty currentScore = new SimpleIntegerProperty(0);
    private final IntegerProperty totalLines = new SimpleIntegerProperty(0);
    private final IntegerProperty highScore = new SimpleIntegerProperty(0);
    private final IntegerProperty currentLevel = new SimpleIntegerProperty(1);
    
    // Tracking fields (optional, for debugging/statistics)
    private int softDropPoints = 0;
    private int hardDropPoints = 0;

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
     * Level increases every 10 lines cleared (level = 1 + totalLines / 10).
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
     * level = 1 + (totalLinesCleared / 10), with minimum level of 1.
     * </p>
     *
     * @return the current level (starts at 1)
     */
    public int getCurrentLevel() {
        return currentLevel.get();
    }
    
    /**
     * Gets the total soft drop points earned.
     *
     * @return the total soft drop points
     */
    public int getSoftDropPoints() {
        return softDropPoints;
    }
    
    /**
     * Gets the total hard drop points earned.
     *
     * @return the total hard drop points
     */
    public int getHardDropPoints() {
        return hardDropPoints;
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
     * Adds points for a soft drop (manual piece movement down).
     * <p>
     * Tetris Guideline: +1 point per cell moved down manually.
     * Each call to this method typically represents one row moved down.
     * </p>
     *
     * @param cells the number of cells moved down (typically 1)
     */
    public void addSoftDropPoints(int cells) {
        int points = cells * 1; // 1 point per cell
        softDropPoints += points;
        addScore(points);
    }
    
    /**
     * Adds points for a hard drop (instant drop).
     * <p>
     * Tetris Guideline: +2 points per cell moved down.
     * </p>
     *
     * @param cells the number of cells the piece dropped
     */
    public void addHardDropPoints(int cells) {
        int points = cells * 2; // 2 points per cell
        hardDropPoints += points;
        addScore(points);
    }
    
    /**
     * Adds points for clearing lines using Tetris Guideline scoring.
     * <p>
     * This method:
     * 1. Adds the lines to the total lines cleared counter
     * 2. Updates the level (level = 1 + totalLines / 10)
     * 3. Calculates and awards points based on Tetris Guideline:
     *    - Single: 100 × level
     *    - Double: 300 × level
     *    - Triple: 500 × level
     *    - Tetris (4 lines): 800 × level
     * </p>
     * <p>
     * The level used for scoring is the level BEFORE adding these lines
     * (the level at which the lines were cleared).
     * </p>
     *
     * @param linesCleared the number of lines cleared in a single lock (1-4)
     */
    public void addLineClear(int linesCleared) {
        if (linesCleared <= 0) {
            return; // No lines cleared, no points
        }
        
        // Get level BEFORE adding lines (level might increase after)
        // Score is based on the level at which lines were cleared
        int level = currentLevel.get();
        
        // Add lines to total (this will update the level)
        int oldLevel = currentLevel.get();
        totalLines.setValue(totalLines.getValue() + linesCleared);
        // Update level: level = 1 + (totalLines / 10)
        int newLevel = 1 + (totalLines.get() / LINES_PER_LEVEL);
        currentLevel.setValue(newLevel);
        
        // Calculate score based on Tetris Guideline
        int lineClearScore;
        switch (linesCleared) {
            case 1:
                lineClearScore = 100 * level; // Single: 100 × level
                break;
            case 2:
                lineClearScore = 300 * level; // Double: 300 × level
                break;
            case 3:
                lineClearScore = 500 * level; // Triple: 500 × level
                break;
            case 4:
                lineClearScore = 800 * level; // Tetris: 800 × level
                break;
            default:
                // For more than 4 lines (shouldn't happen, but handle gracefully)
                lineClearScore = 800 * level;
                break;
        }
        
        addScore(lineClearScore);
        
        // Level increased - this can be used by listeners to update game speed
        if (newLevel > oldLevel) {
            // Level up occurred - the property change will notify listeners
        }
    }
    
    /**
     * Adds the specified number of lines to the total lines cleared (legacy method).
     * <p>
     * This method is kept for backward compatibility. It now calls addLineClear()
     * but does not award points (points should be awarded separately via addLineClear()).
     * </p>
     *
     * @param n the number of lines cleared (should be positive)
     * @deprecated Use addLineClear() instead for proper Tetris Guideline scoring
     */
    @Deprecated
    public void addLines(int n) {
        if (n > 0) {
            totalLines.setValue(totalLines.getValue() + n);
            // Update level: level = 1 + (totalLines / 10)
            int newLevel = 1 + (totalLines.get() / LINES_PER_LEVEL);
            currentLevel.setValue(newLevel);
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
     * preserved across games. Level resets to 1. Tracking fields are also reset.
     * </p>
     */
    public void reset() {
        currentScore.setValue(0);
        totalLines.setValue(0);
        currentLevel.setValue(1);
        softDropPoints = 0;
        hardDropPoints = 0;
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
