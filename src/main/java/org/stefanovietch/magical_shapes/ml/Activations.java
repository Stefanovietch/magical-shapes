package org.stefanovietch.magical_shapes.ml;

import java.util.HashMap;
import java.util.Map;
import java.util.function.DoubleUnaryOperator;

public class Activations {
    public static final DoubleUnaryOperator SIGMOID     = v -> 1 / (1 + Math.exp(-v));
    public static final DoubleUnaryOperator SIGMOID_D   = v -> v * (1 - v);
    public static final DoubleUnaryOperator RELU        = v -> Math.max(0, v);
    public static final DoubleUnaryOperator RELU_D      = v -> v > 0 ? 1 : 0;
    public static final DoubleUnaryOperator SOFTMAX     = Math::exp;
    public static final DoubleUnaryOperator SOFTMAX_D   = null;

    private static final Map<String, DoubleUnaryOperator> activations = new HashMap<>();

    static {
        activations.put("Sigmoid", SIGMOID);
        activations.put("SigmoidD", SIGMOID_D);
        activations.put("Relu", RELU);
        activations.put("ReluD", RELU_D);
        activations.put("Softmax", SOFTMAX);
        activations.put("SoftmaxD", SOFTMAX_D);
    }

    public static DoubleUnaryOperator getFromString(String fromString) {
        return activations.get(fromString);
    }

    public static String toString(DoubleUnaryOperator doubleUnaryOperator) {
        if  (doubleUnaryOperator == null) {
            return "SoftmaxD";
        }
        for (Map.Entry<String, DoubleUnaryOperator> entry : activations.entrySet()) {
            if (entry.getValue() == doubleUnaryOperator) {
                return entry.getKey();
            }
        }
        return null;
    }
}