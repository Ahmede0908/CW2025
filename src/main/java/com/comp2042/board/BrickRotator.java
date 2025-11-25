package com.comp2042.board;

import com.comp2042.board.rotation.NoRotationStrategy;
import com.comp2042.board.rotation.RotationStrategy;
import com.comp2042.board.rotation.StandardRotationStrategy;
import com.comp2042.logic.NextShapeInfo;
import com.comp2042.model.Brick;

public class BrickRotator {

    private Brick brick;
    private int currentShape = 0;
    private RotationStrategy rotationStrategy;

    public NextShapeInfo getNextShape() {
        int nextShape = currentShape;
        nextShape = (++nextShape) % brick.getShapeMatrix().size();
        return new NextShapeInfo(brick.getShapeMatrix().get(nextShape), nextShape);
    }

    public int[][] getCurrentShape() {
        return brick.getShapeMatrix().get(currentShape);
    }

    public void setCurrentShape(int currentShape) {
        this.currentShape = currentShape;
    }

    public void setBrick(Brick brick) {
        this.brick = brick;
        currentShape = 0;
        
        // Determine rotation strategy based on brick type
        // Square bricks (like OBrick) have only 1 shape, so they don't rotate
        if (brick.getShapeMatrix().size() == 1) {
            this.rotationStrategy = new NoRotationStrategy();
        } else {
            this.rotationStrategy = new StandardRotationStrategy();
        }
    }


}
