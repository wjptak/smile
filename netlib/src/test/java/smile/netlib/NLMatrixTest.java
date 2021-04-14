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

package smile.netlib;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import smile.math.MathEx;
import smile.math.matrix.DenseMatrix;

import static org.junit.Assert.*;

/**
 *
 * @author Haifeng Li
 */
public class NLMatrixTest {

    double[][] A = {
            {0.9000, 0.4000, 0.0000},
            {0.4000, 0.5000, 0.3000},
            {0.0000, 0.3000, 0.8000}
    };
    double[] b = {0.5, 0.5, 0.5};
    double[][] C = {
            {0.97, 0.56, 0.12},
            {0.56, 0.50, 0.39},
            {0.12, 0.39, 0.73}
    };

    NLMatrix matrix = new NLMatrix(A);

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of nrow method, of class NLMatrix.
     */
    @Test
    public void testNrows() {
        System.out.println("nrow");
        assertEquals(3, matrix.nrow());
    }

    /**
     * Test of ncol method, of class NLMatrix.
     */
    @Test
    public void testNcols() {
        System.out.println("ncol");
        assertEquals(3, matrix.ncol());
    }

    /**
     * Test of get method, of class NLMatrix.
     */
    @Test
    public void testGet() {
        System.out.println("get");
        assertEquals(0.9, matrix.get(0, 0), 1E-7);
        assertEquals(0.8, matrix.get(2, 2), 1E-7);
        assertEquals(0.5, matrix.get(1, 1), 1E-7);
        assertEquals(0.0, matrix.get(2, 0), 1E-7);
        assertEquals(0.0, matrix.get(0, 2), 1E-7);
        assertEquals(0.4, matrix.get(0, 1), 1E-7);
    }

    /**
     * Test of ax method, of class NLMatrix.
     */
    @Test
    public void testAx() {
        System.out.println("ax");
        double[] d = new double[matrix.nrow()];
        matrix.ax(b, d);
        assertEquals(0.65, d[0], 1E-7);
        assertEquals(0.60, d[1], 1E-7);
        assertEquals(0.55, d[2], 1E-7);
    }

    /**
     * Test of axpy method, of class NLMatrix.
     */
    @Test
    public void testAxpy() {
        System.out.println("axpy");
        double[] d = new double[matrix.nrow()];
        for (int i = 0; i < d.length; i++) d[i] = 1.0;
        matrix.axpy(b, d);
        assertEquals(1.65, d[0], 1E-10);
        assertEquals(1.60, d[1], 1E-10);
        assertEquals(1.55, d[2], 1E-10);
    }

    /**
     * Test of axpy method, of class NLMatrix.
     */
    @Test
    public void testAxpy2() {
        System.out.println("axpy b = 2");
        double[] d = new double[matrix.nrow()];
        for (int i = 0; i < d.length; i++) d[i] = 1.0;
        matrix.axpy(b, d, 2.0);
        assertEquals(2.65, d[0], 1E-10);
        assertEquals(2.60, d[1], 1E-10);
        assertEquals(2.55, d[2], 1E-10);
    }

    /**
     * Test of atx method, of class NLMatrix.
     */
    @Test
    public void testAtx() {
        System.out.println("atx");
        double[] d = new double[matrix.nrow()];
        matrix.atx(b, d);
        assertEquals(0.65, d[0], 1E-7);
        assertEquals(0.60, d[1], 1E-7);
        assertEquals(0.55, d[2], 1E-7);
    }

    /**
     * Test of atxpy method, of class NLMatrix.
     */
    @Test
    public void testAtxpy() {
        System.out.println("atxpy");
        double[] d = new double[matrix.nrow()];
        for (int i = 0; i < d.length; i++) d[i] = 1.0;
        matrix.atxpy(b, d);
        assertEquals(1.65, d[0], 1E-10);
        assertEquals(1.60, d[1], 1E-10);
        assertEquals(1.55, d[2], 1E-10);
    }

    /**
     * Test of atxpy method, of class NLMatrix.
     */
    @Test
    public void testAtxpy2() {
        System.out.println("atxpy b = 2");
        double[] d = new double[matrix.nrow()];
        for (int i = 0; i < d.length; i++) d[i] = 1.0;
        matrix.atxpy(b, d, 2.0);
        assertEquals(2.65, d[0], 1E-10);
        assertEquals(2.60, d[1], 1E-10);
        assertEquals(2.55, d[2], 1E-10);
    }

    /**
     * Test of AAT method, of class NLMatrix.
     */
    @Test
    public void testAAT() {
        System.out.println("AAT");
        NLMatrix c = matrix.aat();
        assertEquals(c.nrow(), 3);
        assertEquals(c.ncol(), 3);
        for (int i = 0; i < C.length; i++) {
            for (int j = 0; j < C[i].length; j++) {
                assertEquals(C[i][j], c.get(i, j), 1E-7);
            }
        }
    }


    /**
     * Test of plusEquals method, of class JMatrix.
     */
    @Test
    public void testAdd() {
        System.out.println("add");
        double[][] A = {
                {0.7220180, 0.07121225, 0.6881997},
                {-0.2648886, -0.89044952, 0.3700456},
                {-0.6391588, 0.44947578, 0.6240573}
        };
        double[][] B = {
                {0.6881997, -0.07121225, 0.7220180},
                {0.3700456, 0.89044952, -0.2648886},
                {0.6240573, -0.44947578, -0.6391588}
        };
        double[][] C = {
                {1.4102177, 0, 1.4102177},
                {0.1051570, 0, 0.1051570},
                {-0.0151015, 0, -0.0151015}
        };
        NLMatrix a = new NLMatrix(A);
        NLMatrix b = new NLMatrix(B);
        DenseMatrix c = a.add(b);
        assertTrue(MathEx.equals(C, c.toArray(), 1E-7));
    }

    /**
     * Test of minusEquals method, of class JMatrix.
     */
    @Test
    public void testSub() {
        System.out.println("sub");
        double[][] A = {
                {0.7220180, 0.07121225, 0.6881997},
                {-0.2648886, -0.89044952, 0.3700456},
                {-0.6391588, 0.44947578, 0.6240573}
        };
        double[][] B = {
                {0.6881997, -0.07121225, 0.7220180},
                {0.3700456, 0.89044952, -0.2648886},
                {0.6240573, -0.44947578, -0.6391588}
        };
        double[][] C = {
                {0.0338183, 0.1424245, -0.0338183},
                {-0.6349342, -1.7808990, 0.6349342},
                {-1.2632161, 0.8989516, 1.2632161}
        };
        NLMatrix a = new NLMatrix(A);
        NLMatrix b = new NLMatrix(B);
        DenseMatrix c = a.sub(b);
        assertTrue(MathEx.equals(C, c.toArray(), 1E-7));
    }

    /**
     * Test of mm method, of class NLMatrix.
     */
    @Test
    public void testMm() {
        System.out.println("mm");
        double[][] A = {
                {0.7220180, 0.07121225, 0.6881997},
                {-0.2648886, -0.89044952, 0.3700456},
                {-0.6391588, 0.44947578, 0.6240573}
        };
        double[][] B = {
                {0.6881997, -0.07121225, 0.7220180},
                {0.3700456, 0.89044952, -0.2648886},
                {0.6240573, -0.44947578, -0.6391588}
        };
        double[][] C = {
                {0.9527204, -0.2973347, 0.06257778},
                {-0.2808735, -0.9403636, -0.19190231},
                {0.1159052, 0.1652528, -0.97941688}
        };
        double[][] D = {
                { 0.9887140,  0.1482942, -0.0212965},
                { 0.1482942, -0.9889421, -0.0015881},
                {-0.0212965, -0.0015881, -0.9997719 }
        };
        double[][] E = {
                {0.0000,  0.0000, 1.0000},
                {0.0000, -1.0000, 0.0000},
                {1.0000,  0.0000, 0.0000}
        };

        NLMatrix a = new NLMatrix(A);
        NLMatrix b = new NLMatrix(B);
        double[][] F = b.abmm(a).transpose().toArray();

        assertTrue(MathEx.equals(a.abmm(b).toArray(), C, 1E-7));
        assertTrue(MathEx.equals(a.abtmm(b).toArray(), D, 1E-7));
        assertTrue(MathEx.equals(a.atbmm(b).toArray(), E, 1E-7));
        assertTrue(MathEx.equals(a.atbtmm(b).toArray(), F, 1E-7));
    }
}
