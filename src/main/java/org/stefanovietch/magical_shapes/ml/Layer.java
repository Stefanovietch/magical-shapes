package org.stefanovietch.magical_shapes.ml;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.random.Weight;
import org.stefanovietch.magical_shapes.menus.SpellProject;

import java.util.function.DoubleUnaryOperator;

public class Layer implements LayerLike {
    public Matrix weights, biases;
    public Matrix lastInput, lastOutput; // cached for backprop

    private final DoubleUnaryOperator activation;
    final DoubleUnaryOperator activationDerivative;
    private final boolean isSoftmax;

    public Layer(int inputSize, int outputSize, DoubleUnaryOperator activation, DoubleUnaryOperator activationDerivative) {
        weights = new Matrix(inputSize, outputSize);
        biases  = new Matrix(1, outputSize);
        weights.randomize();
        this.activation = activation;
        this.activationDerivative = activationDerivative;
        this.isSoftmax = (activation == Activations.SOFTMAX);
    }

    public Layer(Matrix weights, Matrix biases, Matrix lastInput, Matrix lastOutput, DoubleUnaryOperator activation, DoubleUnaryOperator activationDerivative) {
        this.weights = weights;
        this.biases = biases;
        this.lastInput = lastInput;
        this.lastOutput = lastOutput;
        this.activation = activation;
        this.activationDerivative = activationDerivative;
        this.isSoftmax = (activation == Activations.SOFTMAX);
    }

    public Matrix forward(Matrix input) {
        lastInput = input;
        Matrix z = input.dot(weights).addBias(biases);
        lastOutput = isSoftmax ? z.softmaxRows() : z.apply(activation); // ← cache the activated output
        return lastOutput;
    }

    @Override
    public Matrix forward(Matrix input, boolean training) {
        return forward(input);
    }

    public Matrix backward(Matrix output, double learningRate) {
        Matrix delta = isSoftmax
                ? output                                                    // softmax+cross-entropy gradient already simplified
                : output.multiply(lastOutput.apply(activationDerivative));

        delta = delta.clip(-1.0, 1.0);

        Matrix dWeights = lastInput.transpose().dot(delta);
        Matrix dBiases = delta.sumRows();
        Matrix dInput = delta.dot(weights.transpose());

        weights = weights.subtract(dWeights.scale(learningRate));
        biases  = biases.subtract(dBiases.scale(learningRate));
        return dInput;
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putString("type", "layer");
        tag.putString("Activation", Activations.toString(activation));
        tag.putString("ActivationDerivative", Activations.toString(activationDerivative));
        tag.put("Weights", weights.save());
        tag.put("Biases", biases.save());
        if (lastInput != null) {
            tag.put("LastInput", lastInput.save());
            tag.put("LastOutput", lastOutput.save());
        }

        return tag;
    }

    // Load project from NBT
    public static LayerLike load(CompoundTag tag) {
        DoubleUnaryOperator activation = Activations.getFromString(tag.getString("Activation"));
        DoubleUnaryOperator activationDerivative = Activations.getFromString(tag.getString("ActivationDerivative"));
        Matrix weights = Matrix.load(tag.getCompound("Weights"));
        Matrix biases = Matrix.load(tag.getCompound("Biases"));
        Matrix lastInput = tag.contains("LastInput") ? Matrix.load(tag.getCompound("LastInput")) : null;
        Matrix lastOutput = tag.contains("LastOutput") ? Matrix.load(tag.getCompound("LastOutput")) : null;

        return new Layer(weights, biases, lastInput, lastOutput, activation, activationDerivative);
    }
}
