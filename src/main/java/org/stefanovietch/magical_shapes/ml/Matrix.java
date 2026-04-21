package org.stefanovietch.magical_shapes.ml;

import net.minecraft.nbt.*;

import java.util.Arrays;
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

    public static Matrix randomMatrix() {
        Matrix m = new Matrix(1, 400);
        m.randomize();
        return m;
    }

    // Matrix dot
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

    public Matrix add(Matrix other) { return elementWise(other,  (a, b) -> a + b); }
    public Matrix subtract(Matrix other) { return elementWise(other, (a, b) -> a - b); }
    public Matrix multiply(Matrix other) { return elementWise(other, (a, b) -> a * b); } // Hadamard

    private Matrix elementWise(Matrix other, DoubleBinaryOperator fn) {
        Matrix result = new Matrix(rows, cols);
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                result.data[i][j] = fn.applyAsDouble(data[i][j], other.data[i][j]);
        return result;
    }

    public Matrix addBias(Matrix bias) {
        Matrix result = new Matrix(rows, cols);
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                result.data[i][j] = data[i][j] + bias.data[0][j];
        return result;
    }

    public Matrix softmaxRows() {
        Matrix result = new Matrix(rows, cols);
        for (int i = 0; i < rows; i++) {
            // Subtract max for numerical stability
            double max = Arrays.stream(data[i]).max().orElse(0);
            double sum = 0;
            for (int j = 0; j < cols; j++) {
                result.data[i][j] = Math.exp(data[i][j] - max);
                sum += result.data[i][j];
            }
            for (int j = 0; j < cols; j++)
                result.data[i][j] /= sum;
        }
        return result;
    }

    public Matrix clip(double min, double max) {
        Matrix result = new Matrix(rows, cols);
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                result.data[i][j] = Math.max(min, Math.min(max, data[i][j]));
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
                data[i][j] = rand.nextGaussian() * Math.sqrt(2.0 / rows);
    }

    public Matrix scale(double scale) {
        Matrix result = new Matrix(rows, cols);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result.data[i][j] = data[i][j] * scale;
            }
        }
        return result;
    }

    public Matrix sumRows() {
        double[] result = new double[cols];
        for (int j = 0; j < cols; j++) {
            double sum = 0;
            for (int i = 0; i < rows; i++) {
                sum += data[i][j];
            }
            result[j] = sum;
        }
        Matrix out = new Matrix(1, cols);
        out.data[0] = result;
        return out;
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();

        ListTag m = new ListTag();
        for (double[] rows : data) {
            ListTag row = new ListTag();
            for (double value : rows) {
                row.add(DoubleTag.valueOf(value));
            }
            m.add(row);
        }

        tag.putInt("rows", rows);
        tag.putInt("cols", cols);
        tag.put("data", m);

        return tag;
    }

    // Load project from NBT
    public static Matrix load(CompoundTag tag) {
        Matrix m = new Matrix(tag.getInt("rows"), tag.getInt("cols"));

        ListTag matrixTag = tag.getList("data", Tag.TAG_LIST);
        for (int i = 0; i < tag.getInt("rows"); i++) {
            ListTag rowTag = (ListTag) matrixTag.get(i);
            for (int j = 0; j < tag.getInt("cols"); j++) {
                m.data[i][j] = rowTag.getDouble(j);
            }
        }
        return m;
    }
}
