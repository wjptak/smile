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

package smile.demo.interpolation;

import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import smile.plot.swing.Canvas;
import smile.interpolation.CubicSplineInterpolation1D;
import smile.interpolation.KrigingInterpolation1D;
import smile.interpolation.LinearInterpolation;
import smile.interpolation.RBFInterpolation1D;
import smile.interpolation.ShepardInterpolation1D;
import smile.math.rbf.GaussianRadialBasis;
import smile.plot.swing.LinePlot;
import smile.plot.swing.ScatterPlot;

/**
 *
 * @author Haifeng Li
 */
@SuppressWarnings("serial")
public class Interpolation1Demo extends JPanel {
    public Interpolation1Demo() {
        super(new GridLayout(2,3));
        setBackground(Color.WHITE);

        double[] x = {0, 1, 2, 3, 4, 5, 6};
        double[] y = {0, 0.8415, 0.9093, 0.1411, -0.7568, -0.9589, -0.2794};

        double[][] controls = new double[x.length][2];
        for (int i = 0; i < controls.length; i++) {
            controls[i][0] = x[i];
            controls[i][1] = y[i];
        }

        Canvas canvas = ScatterPlot.of(controls, '@').canvas();
        canvas.setTitle("Linear");
        LinearInterpolation linear = new LinearInterpolation(x, y);
        double[][] yy = new double[61][2];
        for (int i = 0; i <= 60; i++) {
            yy[i][0] = i * 0.1;
            yy[i][1] = linear.interpolate(yy[i][0]);
        }
        canvas.add(LinePlot.of(yy, Color.RED));
        add(canvas.panel());

        canvas = ScatterPlot.of(controls, '@').canvas();
        canvas.setTitle("Cubic Spline");
        CubicSplineInterpolation1D spline = new CubicSplineInterpolation1D(x, y);
        double[][] zz = new double[61][2];
        for (int i = 0; i <= 60; i++) {
            zz[i][0] = i * 0.1;
            zz[i][1] = spline.interpolate(zz[i][0]);
        }
        canvas.add(LinePlot.of(zz, Color.BLUE));
        add(canvas.panel());

        canvas = ScatterPlot.of(controls, '@').canvas();
        canvas.setTitle("RBF");
        RBFInterpolation1D rbf = new RBFInterpolation1D(x, y, new GaussianRadialBasis());
        double[][] ww = new double[61][2];
        for (int i = 0; i <= 60; i++) {
            ww[i][0] = i * 0.1;
            ww[i][1] = rbf.interpolate(zz[i][0]);
        }
        canvas.add(LinePlot.of(ww, Color.GREEN));
        add(canvas.panel());

        canvas = ScatterPlot.of(controls, '@').canvas();
        canvas.setTitle("Kriging");
        KrigingInterpolation1D kriging = new KrigingInterpolation1D(x, y);
        double[][] uu = new double[61][2];
        for (int i = 0; i <= 60; i++) {
            uu[i][0] = i * 0.1;
            uu[i][1] = kriging.interpolate(zz[i][0]);
        }
        canvas.add(LinePlot.of(uu, Color.PINK));
        add(canvas.panel());

        canvas = ScatterPlot.of(controls, '@').canvas();
        canvas.setTitle("Shepard");
        ShepardInterpolation1D shepard = new ShepardInterpolation1D(x, y, 3);
        double[][] vv = new double[61][2];
        for (int i = 0; i <= 60; i++) {
            vv[i][0] = i * 0.1;
            vv[i][1] = shepard.interpolate(zz[i][0]);
        }
        canvas.add(LinePlot.of(vv, Color.CYAN));
        add(canvas.panel());
    }

    @Override
    public String toString() {
        return "1D";
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Interpolation 1D");
        frame.setSize(1000, 1000);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().add(new Interpolation1Demo());
        frame.setVisible(true);
    }
}
