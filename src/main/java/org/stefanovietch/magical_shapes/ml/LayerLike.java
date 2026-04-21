package org.stefanovietch.magical_shapes.ml;

import net.minecraft.nbt.CompoundTag;

public interface LayerLike {
    Matrix forward(Matrix input, boolean training);
    Matrix backward(Matrix gradient, double learningRate);
    CompoundTag save();
}