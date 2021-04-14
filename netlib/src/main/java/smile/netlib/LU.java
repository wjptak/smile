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

import smile.math.matrix.DenseMatrix;
import com.github.fommil.netlib.LAPACK;
import org.netlib.util.intW;
import smile.math.matrix.Matrix;

/**
 * For an m-by-n matrix A with {@code m >= n}, the LU decomposition is an m-by-n
 * unit lower triangular matrix L, an n-by-n upper triangular matrix U,
 * and a permutation vector piv of length m so that A(piv,:) = L*U.
 * If {@code m < n}, then L is m-by-m and U is m-by-n.
 * <p>
 * The LU decomposition with pivoting always exists, even if the matrix is
 * singular. The primary use of the LU decomposition is in the solution of
 * square systems of simultaneous linear equations if it is not singular.
 * <p>
 * This decomposition can also be used to calculate the determinant.
 *
 * @author Haifeng Li
 */
class LU extends smile.math.matrix.LU {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(LU.class);

    /**
     * Constructor.
     * @param lu       LU decomposition matrix
     * @param piv      pivot vector
     * @param singular True if the matrix is singular
     */
    public LU(NLMatrix lu, int[] piv, boolean singular) {
        super(lu, piv, pivsign(piv, Math.min(lu.nrow(), lu.ncol())), singular);
    }

    /** Returns the pivot sign. */
    private static int pivsign(int[] piv, int n) {
        int pivsign = 1;
        for (int i = 0; i < n; i++) {
            if (piv[i] != (i+1))
                pivsign = -pivsign;
        }

        return pivsign;
    }

    /**
     * Returns the matrix inverse. The LU matrix will overwritten with
     * the inverse of the original matrix.
     */
    @Override
    public DenseMatrix inverse() {
        int m = lu.nrow();
        int n = lu.ncol();

        if (m != n) {
            throw new IllegalArgumentException(String.format("Matrix is not square: %d x %d", m, n));
        }

        int nb = LAPACK.getInstance().ilaenv(1, "DGETRI", "", n, -1, -1, -1);
        if (nb < 0) {
            logger.warn("LAPACK ILAENV error code: {}", nb);
        }

        if (nb < 1) nb = 1;

        int lwork = lu.ncol() * nb;
        double[] work = new double[lwork];
        intW info = new intW(0);
        LAPACK.getInstance().dgetri(lu.ncol(), lu.data(), lu.ld(), piv, work, lwork, info);

        if (info.val != 0) {
            logger.error("LAPACK DGETRI error code: {}", info.val);
            throw new IllegalArgumentException("LAPACK DGETRI error code: " + info.val);
        }

        return lu;
    }

    @Override
    public void solve(double[] b) {
        // B use b as the internal storage. Therefore b will contains the results.
        DenseMatrix B = Matrix.of(b);
        solve(B);
    }

    @Override
    public void solve(DenseMatrix B) {
        int m = lu.nrow();
        int n = lu.ncol();

        if (B.nrow() != m) {
            throw new IllegalArgumentException(String.format("Row dimensions do not agree: A is %d x %d, but B is %d x %d", lu.nrow(), lu.ncol(), B.nrow(), B.ncol()));
        }

        if (isSingular()) {
            throw new RuntimeException("Matrix is singular.");
        }

        intW info = new intW(0);
        LAPACK.getInstance().dgetrs(NLMatrix.Transpose, lu.nrow(), B.ncol(), lu.data(), lu.ld(), piv, B.data(), B.ld(), info);

        if (info.val < 0) {
            logger.error("LAPACK DGETRS error code: {}", info.val);
            throw new IllegalArgumentException("LAPACK DGETRS error code: " + info.val);
        }
    }
}
