package com.comp2042.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Manages the game score using JavaFX properties for reactive UI binding.
 * <p>
 * This class is part of the Model layer in the MVC architecture. It maintains
 * the current game score and provides a JavaFX IntegerProperty that can be
 * bound to UI components for automatic score display updates. The score can
 * be incremented and reset.
 * </p>
 * <p>
 * The score property is observable, allowing the View layer to automatically
 * update when the score changes.
 * </p>
 *
 * @author TetrisJFX Team
 * @version 1.0
 */
public final class Score {

    private final IntegerProperty score = new SimpleIntegerProperty(0);

    /**
     * Returns the score property for JavaFX binding.
     * <p>
     * This property can be bound to UI components to automatically update
     * the score display when the score changes.
     * </p>
     *
     * @return the IntegerProperty representing the current score
     */
    public IntegerProperty scoreProperty() {
        return score;
    }

    /**
     * Adds the specified value to the current score.
     * <p>
     * The score is incremented by the provided value. This method is used
     * for both regular movement points and row clearing bonuses.
     * </p>
     *
     * @param i the value to add to the score (can be positive or negative)
     */
    public void add(int i) {
        score.setValue(score.getValue() + i);
    }

    /**
     * Resets the score to zero.
     * <p>
     * This method is called when starting a new game.
     * </p>
     */
    public void reset() {
        score.setValue(0);
    }
}
