package com.comp2042.board;

import com.comp2042.logic.CollisionHandler;
import com.comp2042.logic.MatrixOperations;
import com.comp2042.logic.NextShapeInfo;
import com.comp2042.model.Brick;
import com.comp2042.model.BrickGenerator;
import com.comp2042.model.ClearRow;
import com.comp2042.model.RandomBrickGenerator;
import com.comp2042.model.Score;
import com.comp2042.view.ViewData;

import java.awt.*;

/**
 * SimpleBoard implements the game board logic.
 * Coordinate system: x = column, y = row
 * Matrix: currentGameMatrix[row][col] = currentGameMatrix[y][x]
 * Offset: currentOffset.x = column, currentOffset.y = row
 */
public class SimpleBoard implements Board {

    private final int width;
    private final int height;
    private final BrickGenerator brickGenerator;
    private final BrickRotator brickRotator;
    private int[][] currentGameMatrix;  // [rows][cols] = [height][width]
    private Point currentOffset;  // x = column, y = row
    private final Score score;

    public SimpleBoard(int width, int height) {
        this.width = width;
        this.height = height;
        // Matrix is row-major: [rows][cols] = [height][width]
        currentGameMatrix = new int[height][width];
        brickGenerator = new RandomBrickGenerator();
        brickRotator = new BrickRotator();
        score = new Score();
    }

    @Override
    public boolean moveBrickDown() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(0, 1);
        boolean conflict = CollisionHandler.hasCollision(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }


    @Override
    public boolean moveBrickLeft() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(-1, 0);
        boolean conflict = CollisionHandler.hasCollision(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }

    @Override
    public boolean moveBrickRight() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(1, 0);
        boolean conflict = CollisionHandler.hasCollision(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }

    @Override
    public boolean rotateLeftBrick() {
        // Delegate rotation to BrickRotator - get the rotated shape
        NextShapeInfo rotatedShape = brickRotator.getNextShape();
        int[][] rotatedShapeMatrix = rotatedShape.getShape();
        
        // SimpleBoard is responsible ONLY for validation (collision + bounds checking)
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        boolean conflict = CollisionHandler.hasCollision(currentMatrix, rotatedShapeMatrix, (int) currentOffset.getX(), (int) currentOffset.getY());
        
        // If rotation is valid, apply the rotated shape returned by brickRotator
        if (conflict) {
            return false;
        } else {
            brickRotator.setCurrentShape(rotatedShape.getPosition());
            return true;
        }
    }

    @Override
    public boolean createNewBrick() {
        Brick currentBrick = brickGenerator.getBrick();
        brickRotator.setBrick(currentBrick);
        
        // Calculate proper spawn position: top center of the board
        int[][] brickMatrix = brickRotator.getCurrentShape();
        int brickWidth = brickMatrix[0].length;
        int spawnX = (width / 2) - (brickWidth / 2);
        int spawnY = 0;
        
        // Clamp spawnX to stay within [0, width - brickWidth]
        if (spawnX < 0) {
            spawnX = 0;
        }
        if (spawnX + brickWidth > width) {
            spawnX = width - brickWidth;
        }
        
        currentOffset = new Point(spawnX, spawnY);
        return CollisionHandler.hasCollision(currentGameMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
    }

    @Override
    public int[][] getBoardMatrix() {
        return currentGameMatrix;
    }

    @Override
    public ViewData getViewData() {
        return new ViewData(brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY(), brickGenerator.getNextBrick().getShapeMatrix().get(0));
    }

    @Override
    public void mergeBrickToBackground() {
        currentGameMatrix = MatrixOperations.merge(currentGameMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
    }

    @Override
    public ClearRow clearRows() {
        ClearRow clearRow = MatrixOperations.checkRemoving(currentGameMatrix);
        currentGameMatrix = clearRow.getNewMatrix();
        return clearRow;

    }

    @Override
    public Score getScore() {
        return score;
    }


    @Override
    public void newGame() {
        currentGameMatrix = new int[height][width];
        score.reset();
        createNewBrick();
    }
}
