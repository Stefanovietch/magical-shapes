package org.stefanovietch.magical_shapes.ml;

import java.util.Random;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;

public class Matrix {
    public final int rows, cols;
    public final double[][] data;

    public Matrix(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.data = new double[rows][cols];
    }

    // Matrix multiply
    public Matrix dot(Matrix other) {
        Matrix result = new Matrix(this.rows, other.cols);
        for (int i = 0; i < this.rows; i++)
            for (int j = 0; j < other.cols; j++)
                for (int k = 0; k < this.cols; k++)
                    result.data[i][j] += this.data[i][k] * other.data[k][j];
        return result;
    }

    // Element-wise operations
    public Matrix apply(DoubleUnaryOperator fn) {
        Matrix result = new Matrix(rows, cols);
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                result.data[i][j] = fn.applyAsDouble(data[i][j]);
        return result;
    }

    public Matrix add(Matrix other) { return elementWise(other, Double::sum); }
    public Matrix subtract(Matrix other) { return elementWise(other, (a, b) -> a - b); }
    public Matrix multiply(Matrix other) { return elementWise(other, (a, b) -> a * b); } // Hadamard

    private Matrix elementWise(Matrix other, DoubleBinaryOperator fn) {
        Matrix result = new Matrix(rows, cols);
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                result.data[i][j] = fn.applyAsDouble(data[i][j], other.data[i][j]);
        return result;
    }

    public Matrix transpose() {
        Matrix result = new Matrix(cols, rows);
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                result.data[j][i] = data[i][j];
        return result;
    }

    public void randomize() {
        Random rand = new Random();
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                data[i][j] = rand.nextGaussian() * Math.sqrt(2.0 / rows); // He init
    }
}
