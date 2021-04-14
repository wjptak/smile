/*
 * Copyright (c) 2010-2020 Haifeng Li. All rights reserved.
 *
 * Smile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Smile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Smile.  If not, see <https://www.gnu.org/licenses/>.
 */

package smile.demo.vq;

import java.awt.*;
import javax.swing.*;

import smile.math.MathEx;
import smile.math.TimeFunction;
import smile.plot.swing.Wireframe;
import smile.vq.NeuralGas;
import smile.plot.swing.Canvas;
import smile.plot.swing.ScatterPlot;

/**
 *
 * @author Haifeng Li
 */
@SuppressWarnings("serial")
public class NeuralGasDemo extends VQDemo {
    static int numNeurons = 200;
    private JTextField neuronField = new JTextField(Integer.toString(numNeurons), 5);

    public NeuralGasDemo() {
        optionPane.add(new JLabel("Neurons:"));
        optionPane.add(neuronField);
    }

    @Override
    public JComponent learn() {
        try {
            numNeurons = Integer.parseInt(neuronField.getText().trim());
            if (numNeurons < 1) {
                JOptionPane.showMessageDialog(this, "Invalid number of neurons: " + numNeurons, "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid number of neurons: " + numNeurons, "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        NeuralGas gas = new NeuralGas(NeuralGas.seed(numNeurons, dataset[datasetIndex]),
                TimeFunction.exp(learningRate, dataset[datasetIndex].length * epochs / 2),
                TimeFunction.exp(neighborhood, dataset[datasetIndex].length * epochs / 8),
                TimeFunction.constant(dataset[datasetIndex].length * 2));

        Canvas plot = ScatterPlot.of(dataset[datasetIndex], pointLegend).canvas();
        plot.add(ScatterPlot.of(gas.neurons(), '@'));

        JPanel panel = plot.panel();
        int period = dataset[datasetIndex].length / 10;
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }

            for (int i = 0, k = 0; i < epochs; i++) {
                for (int j : MathEx.permutate(dataset[datasetIndex].length)) {
                    gas.update(dataset[datasetIndex][j]);

                    if (++k % period == 0) {
                        plot.clear();
                        plot.add(ScatterPlot.of(dataset[datasetIndex], pointLegend));
                        double[][] neurons = gas.neurons();
                        plot.add(ScatterPlot.of(neurons, '@'));
                        plot.add(Wireframe.of(neurons, gas.network().getEdges().stream().map(edge -> new int[]{edge.v1, edge.v2}).toArray(int[][]::new)));
                        panel.repaint();

                        try {
                            Thread.sleep(100);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                System.out.format("%s epoch finishes%n", smile.util.Strings.ordinal(i+1));
            }
        });
        thread.start();

        return panel;
    }

    @Override
    public String toString() {
        return "Neural Gas";
    }

    public static void main(String[] args) {
        VQDemo demo = new NeuralGasDemo();
        JFrame f = new JFrame("Neural Gas");
        f.setSize(new Dimension(1000, 1000));
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(demo);
        f.setVisible(true);
    }
}
