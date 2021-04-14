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
import smile.stat.distribution.GammaDistribution;

/**
 *
 * @author Haifeng Li
 */
@SuppressWarnings("serial")
public class GammaDistributionDemo extends JPanel implements ChangeListener {
    private JPanel optionPane;
    private JPanel canvas;
    private Canvas pdf;
    private Canvas cdf;
    private Canvas histogram;
    private Canvas qqplot;
    private JSlider scaleSlider;
    private JSlider shapeSlider;
    private double scale = 2;
    private double shape = 3;

    public GammaDistributionDemo() {
        super(new BorderLayout());

        Hashtable<Integer, JLabel> shapeLabelTable = new Hashtable<>();
        for (int i = 0; i <= 10; i+=2) {
            shapeLabelTable.put(i, new JLabel(String.valueOf(i)));
        }

        shapeSlider = new JSlider(0, 10, (int) Math.round(shape));
        shapeSlider.addChangeListener(this);
        shapeSlider.setLabelTable(shapeLabelTable);
        shapeSlider.setMajorTickSpacing(2);
        shapeSlider.setMinorTickSpacing(1);
        shapeSlider.setPaintTicks(true);
        shapeSlider.setPaintLabels(true);

        Hashtable<Integer, JLabel> scaleLabelTable = new Hashtable<>();
        for (int i = 0; i <= 50; i+=10) {
            scaleLabelTable.put(i, new JLabel(String.valueOf(i/10)));
        }

        scaleSlider = new JSlider(0, 50, (int) Math.round(scale*10));
        scaleSlider.addChangeListener(this);
        scaleSlider.setLabelTable(scaleLabelTable);
        scaleSlider.setMajorTickSpacing(10);
        scaleSlider.setMinorTickSpacing(2);
        scaleSlider.setPaintTicks(true);
        scaleSlider.setPaintLabels(true);

        optionPane = new JPanel(new FlowLayout(FlowLayout.LEFT));
        optionPane.setBorder(BorderFactory.createRaisedBevelBorder());
        optionPane.add(new JLabel("Shape:"));
        optionPane.add(shapeSlider);
        optionPane.add(new JLabel("Scale:"));
        optionPane.add(scaleSlider);

        add(optionPane, BorderLayout.NORTH);

        canvas = new JPanel(new GridLayout(2, 2));
        add(canvas, BorderLayout.CENTER);

        GammaDistribution dist = new GammaDistribution(shape, scale);

        double[][] p = new double[100][2];
        double[][] q = new double[100][2];
        for (int i = 0; i < p.length; i++) {
            p[i][0] = i / 5.0;
            p[i][1] = dist.p(p[i][0]);
            q[i][0] = i / 5.0;
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
        if (e.getSource() == scaleSlider || e.getSource() == shapeSlider) {
            scale = scaleSlider.getValue() / 10.0;
            shape = shapeSlider.getValue();
            if (scale == 0) scale = 0.01;
            if (shape == 0) shape = 0.01;

            GammaDistribution dist = new GammaDistribution(shape, scale);

            double[][] p = new double[100][2];
            double[][] q = new double[100][2];
            for (int i = 0; i < p.length; i++) {
                p[i][0] = i / 5.0;
                p[i][1] = dist.p(p[i][0]);
                q[i][0] = i / 5.0;
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
        return "Gamma";
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Gamma Distributions");
        frame.setSize(1000, 1000);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().add(new GammaDistributionDemo());
        frame.setVisible(true);
    }
}
