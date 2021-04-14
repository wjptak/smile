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

package smile.demo.stat.distribution;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import smile.math.MathEx;
import smile.plot.swing.Histogram;
import smile.plot.swing.Canvas;
import smile.plot.swing.LinePlot;
import smile.plot.swing.QQPlot;
import smile.stat.distribution.ExponentialFamilyMixture;
import smile.stat.distribution.GaussianDistribution;
import smile.stat.distribution.GaussianMixture;

/**
 *
 * @author Haifeng Li
 */
@SuppressWarnings("serial")
public class GaussianMixtureDemo extends JPanel {
    public GaussianMixtureDemo() {
        super(new GridLayout(2, 2));

        // Gaussian mixture with singular component.
        double[] data = {
            23.0, 23.0, 22.0, 22.0, 21.0, 24.0, 24.0, 24.0, 24.0,
            24.0, 24.0, 24.0, 24.0, 22.0, 22.0, 16.0, 16.0, 16.0,
            23.0, 23.0, 15.0, 21.0, 21.0, 21.0, 21.0, 24.0, 24.0,
            21.0, 21.0, 24.0, 24.0, 24.0, 24.0,  1.0,  1.0, 23.0,
            23.0, 22.0, 22.0, 14.0, 24.0, 24.0, 23.0, 23.0, 18.0,
            18.0, 23.0, 23.0, 24.0, 24.0, 22.0, 22.0, 17.0, 17.0,
            17.0, 21.0, 21.0, 15.0, 14.0
        };

        ExponentialFamilyMixture mixture = GaussianMixture.fit(data);

        Canvas canvas = Histogram.of(data, 24, true).canvas();
        canvas.setTitle("Gaussian Mixture with Singular Component");
        add(canvas.panel());

        double width = (MathEx.max(data) - MathEx.min(data)) / 24;
        double[][] p = new double[50][2];
        for (int i = 0; i < p.length; i++) {
            p[i][0] = i*0.5;
            p[i][1] = mixture.p(p[i][0]) * width;
        }

        canvas.add(LinePlot.of(p, Color.RED));

        canvas = QQPlot.of(data, mixture).canvas();
        canvas.setTitle("Q-Q Plot");
        add(canvas.panel());

        // Gaussian mixture of five components.
        data = new double[3000];

        GaussianDistribution g1 = new GaussianDistribution(1.0, 1.0);
        for (int i = 0; i < 500; i++)
            data[i] = g1.rand();

        GaussianDistribution g2 = new GaussianDistribution(4.0, 1.0);
        for (int i = 500; i < 1000; i++)
            data[i] = g2.rand();

        GaussianDistribution g3 = new GaussianDistribution(8.0, 1.0);
        for (int i = 1000; i < 2000; i++)
            data[i] = g3.rand();

        GaussianDistribution g4 = new GaussianDistribution(-3.0, 1.0);
        for (int i = 2000; i < 2500; i++)
            data[i] = g4.rand();

        GaussianDistribution g5 = new GaussianDistribution(-6.0, 1.0);
        for (int i = 2500; i < 3000; i++)
            data[i] = g5.rand();

        mixture = GaussianMixture.fit(5, data);

        canvas = Histogram.of(data, 50, true).canvas();
        canvas.setTitle("Gaussian Mixture of Five Components");
        add(canvas.panel());

        width = (MathEx.max(data) - MathEx.min(data)) / 50;
        p = new double[220][2];
        for (int i = 0; i < p.length; i++) {
            p[i][0] = -10 + i*0.1;
            p[i][1] = mixture.p(p[i][0]) * width;
        }

        canvas.add(LinePlot.of(p, Color.RED));

        canvas = QQPlot.of(data, mixture).canvas();
        canvas.setTitle("Q-Q Plot");
        add(canvas.panel());
    }
    
    @Override
    public String toString() {
        return "Gaussian Mixture";
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Gaussian Mixture");
        frame.setSize(1000, 1000);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().add(new GaussianMixtureDemo());
        frame.setVisible(true);
    }
}
