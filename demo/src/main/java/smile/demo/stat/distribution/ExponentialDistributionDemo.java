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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import smile.plot.swing.Histogram;
import smile.plot.swing.Canvas;
import smile.plot.swing.Line;
import smile.plot.swing.LinePlot;
import smile.plot.swing.QQPlot;
import smile.stat.distribution.ExponentialDistribution;

/**
 *
 * @author Haifeng Li
 */
@SuppressWarnings("serial")
public class ExponentialDistributionDemo extends JPanel implements ChangeListener {
    private JPanel optionPane;
    private JPanel canvas;
    private Canvas pdf;
    private Canvas cdf;
    private Canvas histogram;
    private Canvas qqplot;
    private JSlider lambdaSlider;
    private double lambda = 1;

    public ExponentialDistributionDemo() {
        super(new BorderLayout());

        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
        for (int i = 0; i <= 50; i+=10) {
            labelTable.put(i, new JLabel(String.valueOf(i/10)));
        }

        lambdaSlider = new JSlider(0, 50, (int) Math.round(lambda * 10));
        lambdaSlider.addChangeListener(this);
        lambdaSlider.setLabelTable(labelTable);
        lambdaSlider.setMajorTickSpacing(10);
        lambdaSlider.setMinorTickSpacing(2);
        lambdaSlider.setPaintTicks(true);
        lambdaSlider.setPaintLabels(true);

        optionPane = new JPanel(new FlowLayout(FlowLayout.LEFT));
        optionPane.setBorder(BorderFactory.createRaisedBevelBorder());
        optionPane.add(new JLabel("\u03BB:"));
        optionPane.add(lambdaSlider);

        add(optionPane, BorderLayout.NORTH);

        canvas = new JPanel(new GridLayout(2, 2));
        add(canvas, BorderLayout.CENTER);

        ExponentialDistribution dist = new ExponentialDistribution(lambda);

        double[][] p = new double[100][2];
        double[][] q = new double[100][2];
        for (int i = 0; i < p.length; i++) {
            p[i][0] = i / 20.0;
            p[i][1] = dist.p(p[i][0]);
            q[i][0] = i / 20.0;
            q[i][1] = dist.cdf(p[i][0]);
        }

        pdf = LinePlot.of(p, Color.BLUE).canvas();
        pdf.setTitle("PDF");
        canvas.add(pdf.panel());

        cdf = LinePlot.of(q, Color.BLUE).canvas();
        cdf.setTitle("CDF");
        canvas.add(cdf.panel());

        double[] data = new double[500];
        for (int i = 0; i < data.length; i++) {
            data[i] = dist.rand();
        }

        histogram = Histogram.of(data, 20, true).canvas();
        histogram.setTitle("Histogram");
        canvas.add(histogram.panel());

        qqplot = QQPlot.of(data, dist).canvas();
        qqplot.setTitle("Q-Q Plot");
        canvas.add(qqplot.panel());
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == lambdaSlider) {
            lambda = lambdaSlider.getValue() / 10.0;
            if (lambda == 0) lambda = 0.01;

            ExponentialDistribution dist = new ExponentialDistribution(lambda);

            double[][] p = new double[100][2];
            double[][] q = new double[100][2];
            for (int i = 0; i < p.length; i++) {
                p[i][0] = i / 20.0;
                p[i][1] = dist.p(p[i][0]);
                q[i][0] = i / 20.0;
                q[i][1] = dist.cdf(p[i][0]);
            }

            pdf.clear();
            pdf.add(LinePlot.of(p, Color.BLUE));

            cdf.clear();
            cdf.add(LinePlot.of(q, Color.BLUE));

            double[] data = new double[500];
            for (int i = 0; i < data.length; i++) {
                data[i] = dist.rand();
            }

            histogram.clear();
            histogram.add(Histogram.of(data, 20, true));

            qqplot.clear();
            qqplot.add(QQPlot.of(data, dist));
            canvas.repaint();
        }
    }

    @Override
    public String toString() {
        return "Exponential";
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Exponential Distribution");
        frame.setSize(1000, 1000);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().add(new ExponentialDistributionDemo());
        frame.setVisible(true);
    }
}
