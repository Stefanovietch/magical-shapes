package org.stefanovietch.magical_shapes.ml;

import org.deeplearning4j.datasets.iterator.utilty.ListDataSetIterator;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.stefanovietch.magical_shapes.menus.Drawing;
import org.stefanovietch.magical_shapes.menus.SpellDrawing;
import org.stefanovietch.magical_shapes.menus.SpellProject;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Model {
    Random rand;

    private final List<INDArray> dataset = new ArrayList<>();
    private final List<Integer> target = new ArrayList<>();
    private MultiLayerNetwork network;
    Set<Integer> ids = new HashSet<>();

    public Model(SpellProject spellProject) {
        for (SpellDrawing spellDrawing : spellProject.getSpellDrawings()) {
            ids.add(spellDrawing.getID());
            for (Drawing drawing : spellDrawing.getDrawings()) {
                AngleList al = drawing.toAngleList();
                for (int i = 0; i < 10; i++) {
                    dataset.add(al.copy().randomStart().randomScale().addSmoothNoise(1f,1f).extend().toINDArray());
                    target.add(spellDrawing.getID());
                }
            }
        }
        create();
    }

    private void create() {
        int numPoints = 200;          // fixed size after extend()
        int inputSize = numPoints * 2; // x and y

        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(rand.nextInt())
                .updater(new Adam(0.001))
                .list()
                .layer(new DenseLayer.Builder().nIn(inputSize).nOut(512)
                        .activation(Activation.RELU).build())
                .layer(new DenseLayer.Builder().nIn(512).nOut(256)
                        .activation(Activation.RELU).build())
                .layer(new OutputLayer.Builder(LossFunctions.LossFunction.MCXENT)
                        .activation(Activation.SOFTMAX)
                        .nIn(256).nOut(ids.size()).build())
                .build();

        network = new MultiLayerNetwork(conf);
        network.init();
    }

    public void train(int epochs, int batchSize) {
        // Convert dataset to INDArrays
        INDArray X = Nd4j.create(dataset.size());
        INDArray Y = Nd4j.zeros(dataset.size(), ids.size());

        for (int i = 0; i < dataset.size(); i++) {
            X.putRow(i, dataset.get(i));
            Y.putScalar(i, target.get(i), 1.0);
        }

        // Labels = same as input for autoencoder
        DataSet allData = new DataSet(X, Y);
        DataSetIterator iterator = new ListDataSetIterator<>(allData.asList(), batchSize);

        // Training loop
        for (int i = 0; i < epochs; i++) {
            iterator.reset();
            network.fit(iterator);
            System.out.println("Epoch " + i + " complete");
        }
    }
}
