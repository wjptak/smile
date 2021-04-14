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
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import smile.plot.swing.Canvas;
import smile.plot.swing.ScatterPlot;
import smile.plot.swing.ScreePlot;
import smile.plot.swing.TextPlot;
import smile.projection.PCA;

/**
 *
 * @author Haifeng Li
 */
@SuppressWarnings("serial")
public class PCADemo extends ProjectionDemo {
    JComboBox<String> corBox;

    public PCADemo() {
        corBox = new JComboBox<>();
        corBox.addItem("Covariance");
        corBox.addItem("Correlation");
        corBox.setSelectedIndex(0);

        optionPane.add(new JLabel("Scaling:"));
        optionPane.add(corBox);
    }

    @Override
    public JComponent learn(double[][] data, int[] labels, String[] names) {
        boolean cor = corBox.getSelectedIndex() != 0;

        long clock = System.currentTimeMillis();
        PCA pca = cor ? PCA.cor(data) : PCA.fit(data);
        System.out.format("Learn PCA from %d samples in %dms\n", data.length, System.currentTimeMillis()-clock);

        JPanel pane = new JPanel(new GridLayout(1, 2));
        Canvas scree = new ScreePlot(pca).canvas();
        scree.setTitle("Variance");
        pane.add(scree.panel());

        pca.setProjection(3);
        double[][] y = pca.project(data);

        Canvas plot;
        if (names != null) {
            plot = TextPlot.of(names, y).canvas();
        } else if (labels != null) {
            plot = ScatterPlot.of(y, labels, mark).canvas();
        } else {
            plot = ScatterPlot.of(y).canvas();
        }

        plot.setTitle("Scatter Plot");
        pane.add(plot.panel());
        return pane;
    }

    @Override
    public String toString() {
        return "Principal Component Analysis";
    }

    public static void main(String[] args) {
        PCADemo demo = new PCADemo();
        JFrame f = new JFrame("Principal Component Analysis");
        f.setSize(new Dimension(1000, 1000));
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(demo);
        f.setVisible(true);
    }
}
