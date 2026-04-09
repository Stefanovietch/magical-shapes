package org.stefanovietch.magical_shapes.ml;

import org.joml.Vector2f;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.joml.SimplexNoise.noise;

public class AngleList {
    private final List<Vector2f> angleList;
    private Random rand;

    public AngleList(List<Vector2f> angles) {
        this.angleList = angles;
    }

    public AngleList randomStart() {
        Collections.rotate(angleList, rand.nextInt(angleList.size()));
        return this;
    }

    public AngleList addSmoothNoise(float scale, float amplitude) {
        for (int i = 0; i < angleList.size(); i++) {
            Vector2f v = angleList.get(i);

            float noiseX = noise(i * scale, 0);
            float noiseY = noise(0, i * scale);

            v.x += noiseX * amplitude;
            v.y += noiseY * amplitude;
        }
        return this;
    }

    public AngleList scaleShape(float scale) {
        for (Vector2f v : angleList) {
            v.mul(scale);
        }
        return this;
    }

    public AngleList randomScale() {
        return scaleShape(rand.nextFloat(0.5f,2f));
    }

    public AngleList extend() {
        while (angleList.size() < 200) {
            angleList.add(new Vector2f(0, 0));
        }
        return this;
    }

    public AngleList copy() {
        return new AngleList(this.angleList.stream()
                .map(Vector2f::new)
                .toList());
    }

    public INDArray toINDArray() {
        int N = angleList.size(); // should be 200
        INDArray array = Nd4j.create(1, N * 2); // flatten x,y coordinates

        for (int i = 0; i < N; i++) {
            Vector2f v = angleList.get(i);
            array.putScalar(0, i * 2L, v.x);
            array.putScalar(0, i * 2L + 1, v.y);
        }
        return array;
    }
}
