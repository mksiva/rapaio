package incubator.rapaio.data.filters;

import org.junit.Test;
import rapaio.data.IndexOneVector;
import rapaio.data.NominalVector;
import rapaio.data.Vector;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static rapaio.core.BaseFilters.sort;
import static rapaio.core.BaseFilters.toValue;
import static rapaio.core.BaseMath.pow;

/**
 * @author <a href="mailto:padreati@yahoo.com">Aurelian Tutuianu</a>
 */
public class FilterNominalToDoubleTest {

    @Test
    public void testNormalCase() {
        int n = 10;
        HashSet<String> dict = new HashSet<>();
        for (int i = 0; i < n; i++) {
            dict.add(String.valueOf(pow(i, 1.5)));
        }
        Vector v = new NominalVector("test", 10, dict);
        for (int i = 0; i < v.getRowCount(); i++) {
            String value = String.valueOf(pow(i, 1.5));
            v.setLabel(i, value);
        }
        Vector filtered = toValue(v);
        for (int i = 0; i < v.getRowCount(); i++) {
            double value = pow(i, 1.5);
            assertEquals(value, filtered.getValue(i), 1e-10);
        }
    }

    @Test
    public void testNullVector() {
        try {
            sort(null);
        } catch (Exception ex) {
            assertTrue(true);
        }
    }

    @Test
    public void testNFE() {
        Vector filtered = new NominalVector("test", 1, Arrays.asList(new String[]{"abc"}));
        filtered.setLabel(0, "abc");
        Vector numeric = toValue(filtered);
        assertEquals(numeric.getValue(0), numeric.getValue(0), 1e-10);
        assertTrue(numeric.isMissing(0));
    }

    @Test
    public void testNotNominal() {
        Vector filtered = new IndexOneVector(0);
        try {
            toValue(filtered);
        } catch (IllegalArgumentException ex) {
            assertTrue(true);
            return;
        }
        assertTrue(false);
    }
}
