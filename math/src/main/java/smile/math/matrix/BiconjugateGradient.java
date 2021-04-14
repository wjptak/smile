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

package smile.math.matrix;

import smile.math.MathEx;

/**
 * The biconjugate gradient method is an algorithm to
 * solve systems of linear equations.
 *
 * @author Haifeng Li
 */
public class BiconjugateGradient {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(BiconjugateGradient.class);

    /** Private constructor to prevent instance creation. */
    private BiconjugateGradient() {

    }

    /**
     * Returns a simple preconditioner matrix that is the
     * trivial diagonal part of A in some cases.
     * @param A the matrix.
     * @return the preconditioner matrix.
     */
    public static Preconditioner Jacobi(DMatrix A) {
        return (b, x) -> {
                double[] diag = A.diag();
                int n = diag.length;

                for (int i = 0; i < n; i++) {
                    x[i] = diag[i] != 0.0 ? b[i] / diag[i] : b[i];
                }
            };
    }
    /**
     * Solves A * x = b by iterative biconjugate gradient method with Jacobi
     * preconditioner matrix.
     *
     * @param A the matrix.
     * @param b the right hand side of linear equations.
     * @param x on input, x should be set to an initial guess of the solution
     * (or all zeros). On output, x is reset to the improved solution.
     * @return the estimated error.
     */
    public static double solve(DMatrix A, double[] b, double[] x) {
        return solve(A, b, x, Jacobi(A), 1E-6, 1, 2 * Math.max(A.nrow(), A.ncol()));
    }

    /**
     * Solves A * x = b by iterative biconjugate gradient method.
     *
     * @param A the matrix.
     * @param b the right hand side of linear equations.
     * @param x on input, x should be set to an initial guess of the solution
     * (or all zeros). On output, x is reset to the improved solution.
     * @param preconditioner The preconditioner matrix.
     * @param tol The desired convergence tolerance.
     * @param itol Which convergence test is applied.
     *             If itol = 1, iteration stops when |Ax - b| / |b| is less
     *             than the parameter tolerance.
     *             If itol = 2, the stop criterion is that |A<sup>-1</sup> (Ax - b)| / |A<sup>-1</sup>b|
     *             is less than tolerance.
     *             If tol = 3, |x<sub>k+1</sub> - x<sub>k</sub>|<sub>2</sub> is less than
     *             tolerance.
     *             The setting of tol = 4 is same as tol = 3 except that the
     *             L<sub>&infin;</sub> norm instead of L<sub>2</sub>.
     * @param maxIter The maximum number of iterations.
     * @return the estimated error.
     */
    public static double solve(DMatrix A, double[] b, double[] x, Preconditioner preconditioner, double tol, int itol, int maxIter) {
        if (tol <= 0.0) {
            throw new IllegalArgumentException("Invalid tolerance: " + tol);
        }

        if (itol < 1 || itol > 4) {
            throw new IllegalArgumentException("Invalid itol: " + itol);
        }

        if (maxIter <= 0) {
            throw new IllegalArgumentException("Invalid maximum iterations: " + maxIter);
        }

        double err = 0.0;
        double ak, akden, bk, bkden = 1.0, bknum, bnrm, dxnrm, xnrm, zm1nrm, znrm = 0.0;
        int j, n = b.length;

        double[] p = new double[n];
        double[] pp = new double[n];
        double[] r = new double[n];
        double[] rr = new double[n];
        double[] z = new double[n];
        double[] zz = new double[n];

        A.mv(x, r);
        for (j = 0; j < n; j++) {
            r[j] = b[j] - r[j];
            rr[j] = r[j];
        }

        if (itol == 1) {
            bnrm = norm(b, itol);
            preconditioner.solve(r, z);
        } else if (itol == 2) {
            preconditioner.solve(b, z);
            bnrm = norm(z, itol);
            preconditioner.solve(r, z);
        } else if (itol == 3 || itol == 4) {
            preconditioner.solve(b, z);
            bnrm = norm(z, itol);
            preconditioner.solve(r, z);
            znrm = norm(z, itol);
        } else {
            throw new IllegalArgumentException(String.format("Illegal itol: %d", itol));
        }

        for (int iter = 1; iter <= maxIter; iter++) {
            preconditioner.solve(rr, zz);
            for (bknum = 0.0, j = 0; j < n; j++) {
                bknum += z[j] * rr[j];
            }
            if (iter == 1) {
                for (j = 0; j < n; j++) {
                    p[j] = z[j];
                    pp[j] = zz[j];
                }
            } else {
                bk = bknum / bkden;
                for (j = 0; j < n; j++) {
                    p[j] = bk * p[j] + z[j];
                    pp[j] = bk * pp[j] + zz[j];
                }
            }
            bkden = bknum;
            A.mv(p, z);
            for (akden = 0.0, j = 0; j < n; j++) {
                akden += z[j] * pp[j];
            }
            ak = bknum / akden;
            A.tv(pp, zz);
            for (j = 0; j < n; j++) {
                x[j] += ak * p[j];
                r[j] -= ak * z[j];
                rr[j] -= ak * zz[j];
            }
            preconditioner.solve(r, z);
            if (itol == 1) {
                err = norm(r, itol) / bnrm;
            } else if (itol == 2) {
                err = norm(z, itol) / bnrm;
            } else if (itol == 3 || itol == 4) {
                zm1nrm = znrm;
                znrm = norm(z, itol);
                if (Math.abs(zm1nrm - znrm) > MathEx.EPSILON * znrm) {
                    dxnrm = Math.abs(ak) * norm(p, itol);
                    err = znrm / Math.abs(zm1nrm - znrm) * dxnrm;
                } else {
                    err = znrm / bnrm;
                    continue;
                }
                xnrm = norm(x, itol);
                if (err <= 0.5 * xnrm) {
                    err /= xnrm;
                } else {
                    err = znrm / bnrm;
                    continue;
                }
            }

            if (iter % 10 == 0) {
                logger.info(String.format("BCG: the error after %3d iterations: %.5g", iter, err));
            }

            if (err <= tol) {
                logger.info(String.format("BCG: the error after %3d iterations: %.5g", iter, err));
                break;
            }
        }

        return err;
    }

    /**
     * Computes L2 or L-infinity norms for a vector x, as signaled by itol.
     */
    private static double norm(double[] x, int itol) {
        int n = x.length;

        if (itol <= 3) {
            double ans = 0.0;
            for (double v : x) {
                ans += v * v;
            }
            return Math.sqrt(ans);
        } else {
            int isamax = 0;
            for (int i = 0; i < n; i++) {
                if (Math.abs(x[i]) > Math.abs(x[isamax])) {
                    isamax = i;
                }
            }

            return Math.abs(x[isamax]);
        }
    }
}
