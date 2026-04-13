package org.stefanovietch.magical_shapes.ml;

import java.util.ArrayList;
import java.util.List;

public class Network {
    private final List<Layer> layers = new ArrayList<>();
    private final double learningRate;

    public Network(double learningRate) {
        this.learningRate = learningRate;
    }

    public void addLayer(Layer layer) { layers.add(layer); }

    public Matrix forward(Matrix input) {
        Matrix output = input;
        for (Layer layer : layers) output = layer.forward(output);
        return output;
    }

    public void train(Matrix input, Matrix target) {
        Matrix output = forward(input);

        // Backprop
        Matrix error = output.subtract(target); // dL/dOutput
        for (int i = layers.size() - 1; i >= 0; i--) {
            Layer layer = layers.get(i);
            Matrix delta = error.multiply(layer.lastOutput.apply(layer.activationDerivative));
            Matrix weightGrad = layer.lastInput.transpose().dot(delta);

            // Update weights and biases
            layer.weights = layer.weights.subtract(weightGrad.apply(v -> v * learningRate));
            layer.biases  = layer.biases.subtract(delta.apply(v -> v * learningRate));

            // Pass error to previous layer
            error = delta.dot(layer.weights.transpose());
        }
    }
}
