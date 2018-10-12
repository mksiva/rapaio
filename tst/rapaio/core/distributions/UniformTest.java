/*
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 *    Copyright 2013 Aurelian Tutuianu
 *    Copyright 2014 Aurelian Tutuianu
 *    Copyright 2015 Aurelian Tutuianu
 *    Copyright 2016 Aurelian Tutuianu
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package rapaio.core.distributions;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

/**
 * Created by <a href="mailto:padreati@yahoo.com">Aurelian Tutuianu</a> at 11/4/14.
 */
public class UniformTest {

    private static final double TOL = 1e-12;

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testVariousFeatures() {
        Uniform u = Uniform.of(0, 10);

        assertFalse(u.discrete());
        assertEquals(0, u.a(), TOL);
        assertEquals(10, u.b(), TOL);
        assertEquals(0, u.min(), TOL);
        assertEquals(10, u.max(), TOL);
        assertEquals(5, u.mean(), TOL);
        assertEquals(5, u.mode(), TOL);
        assertEquals(8.333333333333334, u.var(), TOL);
        assertEquals(0, u.skewness(), TOL);
        assertEquals(-1.2, u.kurtosis(), TOL);
        assertEquals(2.302585092994046, u.entropy(), TOL);

        assertEquals(1, Uniform.of(9, 9).pdf(9), TOL);
    }

    @Test
    public void testUniformPdf() {
        Distribution u = Uniform.of(0, 2);

        assertEquals("Uniform(a=0,b=2)", u.name());

        assertEquals(0, u.pdf(-1), TOL);
        assertEquals(0.5, u.pdf(0), TOL);
        assertEquals(0.5, u.pdf(1), TOL);
        assertEquals(0.5, u.pdf(2), TOL);
        assertEquals(0, u.pdf(3), TOL);
    }

    @Test
    public void testUniformCdf() {
        Distribution u = Uniform.of(0, 2);

        assertEquals(0, u.cdf(-1), TOL);
        assertEquals(0.0, u.cdf(0), TOL);
        assertEquals(0.25, u.cdf(0.5), TOL);
        assertEquals(0.5, u.cdf(1), TOL);
        assertEquals(1.0, u.cdf(2), TOL);
        assertEquals(1.0, u.cdf(3), TOL);
    }

    @Test
    public void testUniformQuantile() {
        Distribution u = Uniform.of(0, 2);

        assertEquals(0, u.quantile(0), TOL);
        assertEquals(0.5, u.quantile(0.25), TOL);
        assertEquals(1, u.quantile(0.5), TOL);
        assertEquals(1.5, u.quantile(0.75), TOL);
        assertEquals(2.0, u.quantile(1), TOL);
    }

    @Test
    public void testLowQuantile() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("probability value should lie in [0,1] interval");
        Uniform.of(0, 10).quantile(-1);
    }

    @Test
    public void testHighQuantile() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("probability value should lie in [0,1] interval");
        Uniform.of(0, 10).quantile(1.1);
    }
}
