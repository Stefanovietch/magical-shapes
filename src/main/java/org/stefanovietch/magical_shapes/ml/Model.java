package org.stefanovietch.magical_shapes.ml;

import org.stefanovietch.magical_shapes.menus.Drawing;
import org.stefanovietch.magical_shapes.menus.SpellDrawing;
import org.stefanovietch.magical_shapes.menus.SpellProject;
import smile.base.mlp.Layer;
import smile.classification.MLP;
import smile.util.IntSet;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Model {

    private final List<double[]> dataset = new ArrayList<>();
    private final List<Integer> target = new ArrayList<>();
    private final Set<Integer> ids = new HashSet<>();

    private MLP network;

    public Model(SpellProject spellProject) {
        for (SpellDrawing spellDrawing : spellProject.getSpellDrawings()) {
            ids.add(spellDrawing.getID());
            for (Drawing drawing : spellDrawing.getDrawings()) {
                AngleList al = drawing.toAngleList();
                for (int i = 0; i < 10; i++) {
                    dataset.add(al.copy().randomStart().randomScale().addSmoothNoise(1f,1f).extend().toDoubleArray());
                    target.add(spellDrawing.getID());
                }
            }
        }

        if (!dataset.isEmpty() && !target.isEmpty()) {
            create();
        } else {
            System.out.println("Model creation skipped: no data available.");
            network = null;
        }
    }

    private void create() {
        int[] t = target.stream().mapToInt(i -> i).toArray();
        network = new MLP(
                new IntSet(t),
                Layer.builder("relu",64,0.2d,0.001d),
                Layer.builder("relu",64,0.2d,0.001d)
        );

    }

    public void train() {
        double[][] X = dataset.toArray(new double[0][]);
        int[] y = target.stream().mapToInt(i -> i).toArray();

        network.update(X, y);
    }
}
