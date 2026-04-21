package org.stefanovietch.magical_shapes.ml;

import java.util.ArrayList;
import java.util.List;

public class Network {
    private final List<LayerLike> layers = new ArrayList<>();
    private double learningRate;

    public Network(double learningRate) {
        this.learningRate = learningRate;
    }

    public void addLayer(LayerLike layer) { layers.add(layer); }

    public List<LayerLike> getLayers() { return layers; }

    public Matrix forward(Matrix input, boolean training) {
        Matrix output = input;
        for (LayerLike layer : layers) output = layer.forward(output, training);
        return output;
    }

    public void backwards(Matrix input) {
        Matrix output = input;
        for (int i = layers.size() - 1; i >= 0; i--) {
            output = layers.get(i).backward(output, learningRate);
        }
    }

    public void train(Matrix input, Matrix target) {
        Matrix output = forward(input, true);

        Matrix error = output.subtract(target).scale(1.0 / input.rows);

        backwards(error);
    }

    public void addDropout(double v) {
        layers.add(new Dropout(v));
    }

    public Matrix forward(Matrix input) {
        return forward(input, false);
    }

    public void reduceLearningRate(float x) {
        this.learningRate *= x;
    }
}
