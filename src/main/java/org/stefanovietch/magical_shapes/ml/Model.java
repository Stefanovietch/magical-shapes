package org.stefanovietch.magical_shapes.ml;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import org.stefanovietch.magical_shapes.menus.Drawing;
import org.stefanovietch.magical_shapes.menus.SpellDrawing;
import org.stefanovietch.magical_shapes.menus.SpellProject;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Model {

    private final List<Matrix> dataset = new ArrayList<>();
    private final List<Integer> target = new ArrayList<>();
    private final Map<Integer, String> ids = new HashMap<>();
    private final Map<Integer, Integer> idIndex = new HashMap<>();
    private final List<Integer> idList; // ordered, for one-hot encoding
    private Network nn;

    List<Matrix> trainData = new ArrayList<>();
    List<Integer> trainTargets = new ArrayList<>();
    List<Matrix> valData = new ArrayList<>();
    List<Integer> valTargets = new ArrayList<>();

    public volatile boolean isTraining = false;
    public volatile double lastLoss = 0;
    public volatile int lastEpoch  = 0;

    public Model(SpellProject spellProject) {
        int noneID = -1;
        ids.put(noneID, "None");
        for (SpellDrawing spellDrawing : spellProject.getSpellDrawings()) {
            ids.put(spellDrawing.getID(), spellDrawing.getName());
            for (Drawing drawing : spellDrawing.getDrawings()) {
                AngleList al = drawing.toAngleList();

                System.out.println(Arrays.toString(al.copy().startZero().getAngleList().toArray()));
                System.out.println(Arrays.toString(al.copy().startZero().addSmoothNoise(1f,1f).getAngleList().toArray()));
                System.out.println(Arrays.toString(al.copy().startZero().addSmoothNoise(1f,10f).getAngleList().toArray()));

                for (int i = 0; i < 30; i++) {
                    dataset.add(
                            Spline.buildToMatrix(al.copy().randomStart().randomScale().addSmoothNoise(1f,1f).getAngleList(), 100)
                    );
                    target.add(spellDrawing.getID());

                    dataset.add(
                            Spline.buildToMatrix(al.copy().randomStart().randomScale().addSmoothNoise(1f,10f).getAngleList(), 100)
                    );
                    target.add(noneID);
                }
            }
        }




        for (int i = 0; i < dataset.size(); i += 10) {
            if (Math.random() < 0.2) {
                valData.addAll(dataset.subList(i, i + 10));
                valTargets.addAll(target.subList(i, i + 10));
            } else {
                trainData.addAll(dataset.subList(i, i + 10));
                trainTargets.addAll(target.subList(i, i + 10));
            }
        }

        idList = new ArrayList<>(ids.keySet());
        Collections.sort(idList);

        for (int i = 0; i < idList.size(); i++) idIndex.put(idList.get(i), i);
    }

    public void build() {

        int outputSize = idList.size();

        // Build network
        nn = new Network(0.01);

        nn.addLayer(new Layer(dataset.get(0).cols,  100,  Activations.RELU,    Activations.RELU_D));
        nn.addDropout(0.3);
        nn.addLayer(new Layer(100, 100,  Activations.RELU,    Activations.RELU_D));
        nn.addDropout(0.3);
        nn.addLayer(new Layer(100, outputSize,  Activations.SOFTMAX, Activations.SOFTMAX_D));

        System.out.println("Model Created");
    }

    public void build(Network network) {
        nn = network;
    }

    public void train(int epochs) {
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < trainData.size(); i++) indices.add(i);

        double bestValLoss = 100.0;
        double bestEpoch = 0;

        for (int epoch = 0; epoch < epochs; epoch++) {
            Collections.shuffle(indices);
            List<Matrix> shuffledData = new ArrayList<>();
            List<Integer> shuffledTargets = new ArrayList<>();
            for (int i : indices) {
                shuffledData.add(trainData.get(i));
                shuffledTargets.add(trainTargets.get(i));
            }

            nn.train(stack(shuffledData), toOneHot(shuffledTargets));

            lastEpoch = epoch;

            if (epoch % 10 == 0) {
                lastLoss = computeLoss(nn.forward(stack(shuffledData)), toOneHot(shuffledTargets));
                double valLoss = computeLoss(nn.forward(stack(valData)), toOneHot(valTargets));
                System.out.printf("Epoch %d — Train Loss: %.4f | Val Loss: %.4f%n", epoch, lastLoss, valLoss);

                if (valLoss < bestValLoss) {
                    bestValLoss = valLoss;
                    bestEpoch = epoch;

                } else if (epoch - bestEpoch > 20) {
                    break;
                }

                nn.reduceLearningRate(0.99f);
            }
        }
    }

    public void trainAsync(int epochs, Runnable onComplete) {
        if (isTraining) return;
        isTraining = true;

        Thread thread = new Thread(() -> {
            train(epochs);
            isTraining = false;
            if (onComplete != null) onComplete.run();
        }, "ml-training-thread");

        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
    }

    public String predict(Matrix input) {
        Matrix out = nn.forward(input);

        int best = 0;
        for (int i = 1; i < out.cols; i++)
            if (out.data[0][i] > out.data[0][best]) best = i;

        return ids.get(idList.get(best));
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
            m.data[i][idIndex.get(targets.get(i))] = 1.0;
        return m;
    }

    private double computeLoss(Matrix output, Matrix target) {
        double sum = 0;
        for (int i = 0; i < output.rows; i++)
            for (int j = 0; j < output.cols; j++)
                sum -= target.data[i][j] * Math.log(Math.max(output.data[i][j], 1e-9));
        return sum / output.rows;
    }


    // Save project to NBT
    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();

        ListTag layers = new ListTag();
        for (LayerLike layer : nn.getLayers()) {
            layers.add(layer.save());
        }
        tag.put("Layers", layers);

        return tag;
    }

    // Load project from NBT
    public static Model load(CompoundTag tag, SpellProject spellProject) {
        Model model = new Model(spellProject);
        Network network = new Network(0.01);
        ListTag layers = tag.getList("Layers", 10);

        for (int i = 0; i < layers.size(); i++) {
            if (layers.getCompound(i).getString("type").equals("layer")) {
                network.addDropout(layers.getCompound(i).getDouble("rate"));
            } else if (layers.getCompound(i).getString("type").equals("dropout")) {
                network.addLayer(Layer.load(layers.getCompound(i)));
            }
        }

        model.build(network);

        return model;
    }
}
