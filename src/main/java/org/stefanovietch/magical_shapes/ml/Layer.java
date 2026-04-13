package org.stefanovietch.magical_shapes.ml;

import java.util.function.DoubleUnaryOperator;

public class Layer {
    public Matrix weights, biases;
    public Matrix lastInput, lastOutput; // cached for backprop

    private final DoubleUnaryOperator activation;
    final DoubleUnaryOperator activationDerivative;

    public Layer(int inputSize, int outputSize, DoubleUnaryOperator activation, DoubleUnaryOperator activationDerivative) {
        weights = new Matrix(inputSize, outputSize);
        biases  = new Matrix(1, outputSize);
        weights.randomize();
        this.activation = activation;
        this.activationDerivative = activationDerivative;
    }

    public Matrix forward(Matrix input) {
        lastInput = input;
        lastOutput = input.dot(weights).add(biases).apply(activation);
        return lastOutput;
    }
}
