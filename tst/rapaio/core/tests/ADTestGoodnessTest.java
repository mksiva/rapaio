package rapaio.core.tests;

import org.junit.Test;
import rapaio.data.VarDouble;
import rapaio.data.solid.SolidVarDouble;

import static org.junit.Assert.assertEquals;

/**
 * Created by <a href="mailto:padreati@yahoo.com">Aurelian Tutuianu</a> on 8/9/17.
 */
public class ADTestGoodnessTest {

    private static final double TOL = 1e-5;

    @Test
    public void basicTest() {
        VarDouble x = SolidVarDouble.wrap(6.0747159,  -8.9637424,  -1.1363964,   1.5831864,  -3.4660379,   2.6695147,   3.0571496,   0.8348192, -11.3294910,  13.8572907);
        x.printLines();

        ADTestGoodness test = ADTestGoodness.from(x, 1, 5);
        // verified with R
        test.printSummary();

        assertEquals(0.7283345, test.a2, TOL);
        assertEquals(0.5318083, test.pValue, TOL);

        ADTestGoodness test2 = ADTestGoodness.from(x);
        test2.printSummary();

        ADTestGoodness test3 = ADTestGoodness.from(x, 1, Double.NaN);
        test3.printSummary();
    }
}
