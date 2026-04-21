package org.stefanovietch.magical_shapes.ml;

import net.minecraft.nbt.CompoundTag;

import java.util.Random;

public class Dropout implements LayerLike {
    private final double rate;
    private Matrix mask;

    public Dropout(double rate) {
        this.rate = rate; // e.g. 0.3 = drop 30% of neurons
    }

    public Matrix forward(Matrix input, boolean training) {
        if (!training) return input; // no dropout at inference time

        mask = new Matrix(input.rows, input.cols);
        Random rand = new Random();
        double scale = 1.0 / (1.0 - rate); // inverted dropout to keep scale consistent

        for (int i = 0; i < input.rows; i++)
            for (int j = 0; j < input.cols; j++)
                mask.data[i][j] = rand.nextDouble() > rate ? scale : 0.0;

        return input.multiply(mask);
    }

    @Override
    public Matrix backward(Matrix gradient, double learningRate) {
        return gradient.multiply(mask);
    }

    @Override
    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putString("type", "dropout");
        tag.putDouble("Rate", rate);
        return tag;
    }

    public LayerLike load(CompoundTag tag) {
        return new Dropout(tag.getDouble("Rate"));
    }
}