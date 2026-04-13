package org.stefanovietch.magical_shapes.ml;

import java.util.function.DoubleUnaryOperator;

public class Activations {
    public static final DoubleUnaryOperator SIGMOID     = v -> 1 / (1 + Math.exp(-v));
    public static final DoubleUnaryOperator SIGMOID_D   = v -> v * (1 - v);
    public static final DoubleUnaryOperator RELU        = v -> Math.max(0, v);
    public static final DoubleUnaryOperator RELU_D      = v -> v > 0 ? 1 : 0;
    public static final DoubleUnaryOperator TANH        = Math::tanh;
    public static final DoubleUnaryOperator TANH_D      = v -> 1 - v * v;
}