package org.stefanovietch.magical_shapes.ml;

import org.stefanovietch.magical_shapes.menus.Drawing;
import org.stefanovietch.magical_shapes.menus.SpellDrawing;
import org.stefanovietch.magical_shapes.menus.SpellProject;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Model {

    private final List<Matrix> dataset = new ArrayList<>();
    private final List<Integer> target = new ArrayList<>();
    private final Set<Integer> ids = new HashSet<>();
    private final List<Integer> idList; // ordered, for one-hot encoding
    private final Network nn;



    public Model(SpellProject spellProject, int hiddenSize) {
        for (SpellDrawing spellDrawing : spellProject.getSpellDrawings()) {
            ids.add(spellDrawing.getID());
            for (Drawing drawing : spellDrawing.getDrawings()) {
                AngleList al = drawing.toAngleList();
                for (int i = 0; i < 10; i++) {
                    dataset.add(al.copy().randomStart().randomScale().addSmoothNoise(1f,1f).extend().toMatrix());
                    target.add(spellDrawing.getID());
                }
            }
        }

        // Freeze ID order for consistent one-hot encoding
        idList = new ArrayList<>(ids);
        Collections.sort(idList);

        int inputSize  = dataset.get(0).cols;
        int outputSize = idList.size();

        // Build network
        nn = new Network(0.01);
        nn.addLayer(new Layer(inputSize,  hiddenSize,  Activations.RELU,    Activations.RELU_D));
        nn.addLayer(new Layer(hiddenSize, hiddenSize,  Activations.RELU,    Activations.RELU_D));
        nn.addLayer(new Layer(hiddenSize, outputSize,  Activations.SIGMOID, Activations.SIGMOID_D));

    }

    public void train(int epochs) {
        Matrix input  = stack(dataset);
        Matrix labels = toOneHot(target);

        for (int epoch = 0; epoch < epochs; epoch++) {
            nn.train(input, labels);

            if (epoch % 100 == 0) {
                double loss = computeLoss(nn.forward(input), labels);
                System.out.printf("Epoch %d — Loss: %.4f%n", epoch, loss);
            }
        }
    }

    public int predict(Matrix input) {
        Matrix out = nn.forward(input);

        int best = 0;
        for (int i = 1; i < out.cols; i++)
            if (out.data[0][i] > out.data[0][best]) best = i;

        return idList.get(best);
    }

    private Matrix stack(List<Matrix> data) {
        Matrix m = new Matrix(data.size(), data.get(0).cols);
        for (int i = 0; i < data.size(); i++)
            m.data[i] = data.get(i).data[0];
        return m;
    }

    private Matrix toOneHot(List<Integer> targets) {
        Matrix m = new Matrix(targets.size(), idList.size());
        for (int i = 0; i < targets.size(); i++)
            m.data[i][idList.indexOf(targets.get(i))] = 1.0;
        return m;
    }

    private double computeLoss(Matrix output, Matrix target) {
        // Mean squared error
        double sum = 0;
        for (int i = 0; i < output.rows; i++)
            for (int j = 0; j < output.cols; j++) {
                double diff = output.data[i][j] - target.data[i][j];
                sum += diff * diff;
            }
        return sum / output.rows;
    }
}
