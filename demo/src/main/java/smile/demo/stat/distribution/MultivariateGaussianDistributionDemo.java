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
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import smile.plot.swing.Canvas;
import smile.math.MathEx;
import smile.plot.swing.Surface;
import smile.stat.distribution.MultivariateGaussianDistribution;

/**
 *
 * @author Haifeng Li
 */
@SuppressWarnings("serial")
public class MultivariateGaussianDistributionDemo extends JPanel implements ChangeListener {
    private JPanel optionPane;
    private Canvas pdf;
    private JSlider sigma1Slider;
    private JSlider sigma2Slider;
    private double[] mu = {0.0, 0.0};
    private double[] sigma = {1.0, 1.0};
    private int n = 30;
    private int m = 30;
    private double[][][] z = new double[m][n][3];

    public MultivariateGaussianDistributionDemo() {
        super(new BorderLayout());

        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
        for (int i = 0; i <= 30; i+=10) {
            labelTable.put(i, new JLabel(String.valueOf(i/10.0)));
        }

        sigma1Slider = new JSlider(0, 30, (int) (sigma[0]*10));
        sigma1Slider.addChangeListener(this);
        sigma1Slider.setLabelTable(labelTable);
        sigma1Slider.setMajorTickSpacing(10);
        sigma1Slider.setMinorTickSpacing(2);
        sigma1Slider.setPaintTicks(true);
        sigma1Slider.setPaintLabels(true);

        sigma2Slider = new JSlider(0, 30, (int) (sigma[1]*10));
        sigma2Slider.addChangeListener(this);
        sigma2Slider.setLabelTable(labelTable);
        sigma2Slider.setMajorTickSpacing(10);
        sigma2Slider.setMinorTickSpacing(2);
        sigma2Slider.setPaintTicks(true);
        sigma2Slider.setPaintLabels(true);

        optionPane = new JPanel();
        optionPane.setBorder(BorderFactory.createRaisedBevelBorder());
        optionPane.add(new JLabel("<html>\u03C3<sub>1</sub>:</html>"));
        optionPane.add(sigma1Slider);
        optionPane.add(new JLabel("<html>\u03C3<sub>2</sub>:</html>"));
        optionPane.add(sigma2Slider);

        add(optionPane, BorderLayout.NORTH);

        MultivariateGaussianDistribution g = new MultivariateGaussianDistribution(mu, sigma);

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                z[i][j][0] = 6.0 * (i - m/2) / m;
                z[i][j][1] = 6.0 * (j - n/2) / n;
                double[] point = {z[i][j][0], z[i][j][1]};
                z[i][j][2] = g.p(point);
            }
        }

        pdf = new Surface(z).canvas();
        pdf.setTitle("Multivariate Gaussian");
        add(pdf.panel(), BorderLayout.CENTER);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == sigma1Slider || e.getSource() == sigma2Slider) {
            sigma[0] = MathEx.pow2(sigma1Slider.getValue() / 10.0);
            sigma[1] = MathEx.pow2(sigma2Slider.getValue() / 10.0);
            if (sigma[0] == 0) sigma[0] = 0.01;
            if (sigma[1] == 0) sigma[1] = 0.01;

            MultivariateGaussianDistribution g = new MultivariateGaussianDistribution(mu, sigma);

            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    z[i][j][0] = 6.0 * (i - m / 2) / m;
                    z[i][j][1] = 6.0 * (j - n / 2) / n;
                    double[] point = {z[i][j][0], z[i][j][1]};
                    z[i][j][2] = g.p(point);
                }
            }

            pdf = new Surface(z).canvas();
            pdf.setTitle("Multivariate Gaussian");
            add(pdf.panel(), BorderLayout.CENTER);
            repaint();
        }
    }

    @Override
    public String toString() {
        return "Multivariate Gaussian";
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("MultivariateGaussian Distribution");
        frame.setSize(1000, 1000);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().add(new MultivariateGaussianDistributionDemo());
        frame.setVisible(true);
    }
}
