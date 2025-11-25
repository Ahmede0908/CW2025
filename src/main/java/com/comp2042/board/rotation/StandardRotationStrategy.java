package com.comp2042.board.rotation;



import com.comp2042.logic.MatrixOperations;



public class StandardRotationStrategy implements RotationStrategy {

    @Override

    public int[][] rotate(int[][] shapeMatrix) {

        // rotate the matrix using MatrixOperations

        return MatrixOperations.rotate90(shapeMatrix);

    }

}

