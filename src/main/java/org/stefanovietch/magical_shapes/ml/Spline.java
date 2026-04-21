package org.stefanovietch.magical_shapes.ml;

import org.joml.Vector2f;

import java.util.List;

public class Spline {
    public static float[][] buildSpline(List<Vector2f> points, int outputSize) {
        float[][] spline = new float[outputSize][2];

        for (int i = 0; i < outputSize; i++) {
            float globalT = i / (float)(outputSize - 1) * (points.size() - 1);
            int segment = (int) globalT;
            float t = globalT - segment;

            segment = Math.min(segment, points.size() - 2);

            int i0 = Math.max(segment - 1, 0);
            int i1 = segment;
            int i2 = Math.min(segment + 1, points.size() - 1);
            int i3 = Math.min(segment + 2, points.size() - 1);

            spline[i] = catmullRom(points.get(i0), points.get(i1), points.get(i2), points.get(i3), t);
        }

        return spline;
    }

    private static float[] catmullRom(Vector2f p0, Vector2f p1, Vector2f p2, Vector2f p3, float t) {
        float t2 = t * t;
        float t3 = t2 * t;

        float x = 0.5f * ((2 * p1.x)
                + (-p0.x + p2.x) * t
                + (2*p0.x - 5*p1.x + 4*p2.x - p3.x) * t2
                + (-p0.x + 3*p1.x - 3*p2.x + p3.x) * t3);

        float y = 0.5f * ((2 * p1.y)
                + (-p0.y + p2.y) * t
                + (2*p0.y - 5*p1.y + 4*p2.y - p3.y) * t2
                + (-p0.y + 3*p1.y - 3*p2.y + p3.y) * t3);

        return new float[]{x, y};
    }

    public static float[] toTurningAngles(float[][] points) {
        int n = points.length - 2;
        float[] angles = new float[n];

        for (int i = 1; i < points.length - 1; i++) {
            float dx1 = points[i][0] - points[i-1][0];
            float dy1 = points[i][1] - points[i-1][1];

            float dx2 = points[i+1][0] - points[i][0];
            float dy2 = points[i+1][1] - points[i][1];

            float angle1 = (float) Math.atan2(dy1, dx1);
            float angle2 = (float) Math.atan2(dy2, dx2);

            // Normalize to [-π, π]
            float delta = angle2 - angle1;
            delta = (float) Math.atan2(Math.sin(delta), Math.cos(delta));

            angles[i - 1] = delta;
        }

        return angles;
    }

    public static Matrix toMatrix(float[][] angles) {
        Matrix matrix = new Matrix(1, angles.length * 2);
        for (int i = 0; i < angles.length; i+=2) {
            matrix.data[0][i*2] = angles[i][0];
            matrix.data[0][i*2+1] = angles[i+1][1];
        }
        return matrix;
    }

    public static Matrix buildToMatrix(List<Vector2f> points, int outputSize) {
        return toMatrix(buildSpline(points, outputSize));
    }

}
