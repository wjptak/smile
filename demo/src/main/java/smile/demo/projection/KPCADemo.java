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

package smile.demo.projection;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import smile.plot.swing.Canvas;
import smile.plot.swing.ScatterPlot;
import smile.plot.swing.TextPlot;
import smile.projection.KPCA;
import smile.projection.PCA;
import smile.math.MathEx;
import smile.math.kernel.GaussianKernel;

/**
 *
 * @author Haifeng Li
 */
@SuppressWarnings("serial")
public class KPCADemo extends ProjectionDemo {

    private double[] gamma = new double[dataset.length];
    JTextField gammaNumberField;

    public KPCADemo() {
        gammaNumberField = new JTextField(Double.toString(gamma[datasetIndex]), 5);
        optionPane.add(new JLabel("Gaussian Kernel gamma:"));
        optionPane.add(gammaNumberField);
    }

    @Override
    public JComponent learn(double[][] data, int[] labels, String[] names) {
        JPanel pane = new JPanel(new GridLayout(2, 2));

        if (gamma[datasetIndex] == 0.0) {
            int n = 0;
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < i; j++, n++) {
                    gamma[datasetIndex] += MathEx.squaredDistance(data[i], data[j]);
                }
            }

            gamma[datasetIndex] = Math.sqrt(gamma[datasetIndex] / n) / 4;
        } else {
            try {
                gamma[datasetIndex] = Double.parseDouble(gammaNumberField.getText().trim());
                if (gamma[datasetIndex] <= 0) {
                    JOptionPane.showMessageDialog(this, "Invalid parameter: " + gamma[datasetIndex], "Error", JOptionPane.ERROR_MESSAGE);
                    return null;
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Invalid parameter: " + gammaNumberField.getText(), "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }
        }
        gammaNumberField.setText(String.format("%.4f", gamma[datasetIndex]));

        long clock = System.currentTimeMillis();
        PCA pca = PCA.cor(data);
        System.out.format("Learn PCA from %d samples in %dms\n", data.length, System.currentTimeMillis() - clock);

        pca.setProjection(2);
        double[][] y = pca.project(data);

        Canvas plot;
        if (names != null) {
            plot = TextPlot.of(names, y).canvas();
        } else if (labels != null) {
            plot = ScatterPlot.of(y, labels, mark).canvas();
        } else {
            plot = ScatterPlot.of(y).canvas();
        }

        plot.setTitle("PCA");
        pane.add(plot.panel());

        pca.setProjection(3);
        y = pca.project(data);

        if (names != null) {
            plot = TextPlot.of(names, y).canvas();
        } else if (labels != null) {
            plot = ScatterPlot.of(y, labels, mark).canvas();
        } else {
            plot = ScatterPlot.of(y).canvas();
        }

        plot.setTitle("PCA");
        pane.add(plot.panel());

        KPCA<double[]> kpca = KPCA.fit(data, new GaussianKernel(gamma[datasetIndex]), 2);

        y = kpca.coordinates();
        if (names != null) {
            plot = TextPlot.of(names, y).canvas();
        } else if (labels != null) {
            plot = ScatterPlot.of(y, labels, mark).canvas();
        } else {
            plot = ScatterPlot.of(y).canvas();
        }

        plot.setTitle("KPCA");
        pane.add(plot.panel());

        clock = System.currentTimeMillis();
        kpca = KPCA.fit(data, new GaussianKernel(gamma[datasetIndex]), 3);
        System.out.format("Learn KPCA from %d samples in %dms\n", data.length, System.currentTimeMillis() - clock);

        y = kpca.coordinates();
        if (names != null) {
            plot = TextPlot.of(names, y).canvas();
        } else if (labels != null) {
            plot = ScatterPlot.of(y, labels, mark).canvas();
        } else {
            plot = ScatterPlot.of(y).canvas();
        }

        plot.setTitle("KPCA");
        pane.add(plot.panel());

        return pane;
    }

    @Override
    public String toString() {
        return "Kernel Principal Component Analysis";
    }

    public static void main(String[] args) {
        KPCADemo demo = new KPCADemo();
        JFrame f = new JFrame("Kernel Principal Component Analysis");
        f.setSize(new Dimension(1000, 1000));
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(demo);
        f.setVisible(true);
    }
}
