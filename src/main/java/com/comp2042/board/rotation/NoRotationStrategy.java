package com.comp2042.board.rotation;



public class NoRotationStrategy implements RotationStrategy {

    @Override

    public int[][] rotate(int[][] shapeMatrix) {

        // square bricks do not rotate

        return shapeMatrix;

    }

}

