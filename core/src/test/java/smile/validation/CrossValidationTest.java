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

package smile.validation;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import smile.math.MathEx;

import static org.junit.Assert.*;

/**
 *
 * @author Haifeng Li
 */
public class CrossValidationTest {

    public CrossValidationTest() {
    }

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

    @Test
    public void testComplete() {
        System.out.println("Complete");
        int n = 57;
        int k = 5;
        Bag[] bags = CrossValidation.of(n, k);
        boolean[] hit = new boolean[n];
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < n; j++) {
                hit[j] = false;
            }

            int[] train = bags[i].samples;
            for (int j = 0; j < train.length; j++) {
                assertFalse(hit[train[j]]);
                hit[train[j]] = true;
            }

            int[] test = bags[i].oob;
            for (int j = 0; j < test.length; j++) {
                assertFalse(hit[test[j]]);
                hit[test[j]] = true;
            }

            for (int j = 0; j < n; j++) {
                assertTrue(hit[j]);
            }
        }
    }

    @Test
    public void testOrthogonal() {
        System.out.println("Orthogonal");
        int n = 57;
        int k = 5;
        Bag[] bags = CrossValidation.of(n, k);
        boolean[] hit = new boolean[n];
        for (int i = 0; i < k; i++) {
            int[] test = bags[i].oob;
            for (int j = 0; j < test.length; j++) {
                assertFalse(hit[test[j]]);
                hit[test[j]] = true;
            }
        }

        for (int j = 0; j < n; j++) {
            assertTrue(hit[j]);
        }
    }

    @Test
    public void testStratifiedComplete() {
        System.out.println("Stratified complete");
        int n = 57;
        int k = 5;

        int[] stratum = new int[n];
        for (int i = 0; i < n; i++) {
            stratum[i] = MathEx.randomInt(3);
        }

        Bag[] bags = CrossValidation.of(stratum, k);
        boolean[] hit = new boolean[n];
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < n; j++) {
                hit[j] = false;
            }

            int[] train = bags[i].samples;
            for (int j = 0; j < train.length; j++) {
                assertFalse(hit[train[j]]);
                hit[train[j]] = true;
            }

            int[] test = bags[i].oob;
            for (int j = 0; j < test.length; j++) {
                assertFalse(hit[test[j]]);
                hit[test[j]] = true;
            }

            for (int j = 0; j < n; j++) {
                assertTrue(hit[j]);
            }
        }
    }

    @Test
    public void testStratifiedOrthogonal() {
        System.out.println("Stratified orthogonal");
        int n = 57;
        int k = 5;

        int[] stratum = new int[n];
        for (int i = 0; i < n; i++) {
            stratum[i] = MathEx.randomInt(3);
        }

        Bag[] bags = CrossValidation.of(stratum, k);
        boolean[] hit = new boolean[n];
        for (int i = 0; i < k; i++) {
            int[] test = bags[i].oob;
            for (int j = 0; j < test.length; j++) {
                assertFalse(hit[test[j]]);
                hit[test[j]] = true;
            }
        }

        for (int j = 0; j < n; j++) {
            assertTrue(hit[j]);
        }
    }
}