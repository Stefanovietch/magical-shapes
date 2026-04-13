package org.stefanovietch.magical_shapes.ml;

import org.joml.Vector2f;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.joml.SimplexNoise.noise;

public class AngleList {
    private final List<Vector2f> angleList;
    private final Random rand = new Random();

    public AngleList(List<Vector2f> angles) {
        this.angleList = new ArrayList<>(angles);
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
        return new AngleList(new ArrayList<>(
                this.angleList.stream().map(Vector2f::new).toList()
        ));
    }

    public Matrix toMatrix() {
        Matrix m = new Matrix(1, angleList.size() * 2);
        for (int i = 0; i < angleList.size(); i++) {
            m.data[0][i*2] = angleList.get(i).x;
            m.data[0][i*2+1] = angleList.get(i).y;
        }
        return m;
    }
}
