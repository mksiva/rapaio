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

package rapaio.data.filter.var;

import org.junit.Assert;
import org.junit.Test;
import rapaio.data.Var;
import rapaio.data.VarNominal;
import rapaio.data.filter.VFilter;
import rapaio.data.solid.SolidVarDouble;

public class VFUpdateTest {

    @Test
    public void testUpdate() {

        VFilter vf = VFUpdate.with(spot -> {
            if (spot.isMissing())
                spot.setDouble(0);
            if (spot.getDouble() > 0)
                spot.setDouble(spot.getDouble() * spot.getDouble());
            else
                spot.setDouble(-spot.getDouble() * spot.getDouble());
        });

        Var x = SolidVarDouble.wrap(0, Double.NaN, 1, Double.NaN, -12, 3.1);

        Var y = x.solidCopy().fitApply(vf);
        Assert.assertEquals(0, y.getDouble(0), 1e-20);
        Assert.assertEquals(0, y.getDouble(1), 1e-20);
        Assert.assertEquals(1, y.getDouble(2), 1e-20);
        Assert.assertEquals(0, y.getDouble(3), 1e-20);
        Assert.assertEquals(-144, y.getDouble(4), 1e-20);
        Assert.assertEquals(3.1 * 3.1, y.getDouble(5), 1e-20);

        Var l1 = VarNominal.copy("ana", "?", "are", "?", "mere");
        Var l2 = l1.fitApply(VFUpdate.with(s -> {
            if (s.isMissing()) {
                s.setLabel("missing");
                return;
            }

            char[] msg = s.getLabel().toCharArray();
            for (int i = 0; i < msg.length / 2; i++) {
                char tmp = msg[i];
                msg[i] = msg[msg.length - i - 1];
                msg[msg.length - i - 1] = tmp;
            }
            s.setLabel(String.copyValueOf(msg));
        }));

        Assert.assertEquals("ana", l2.getLabel(0));
        Assert.assertEquals("missing", l2.getLabel(1));
        Assert.assertEquals("era", l2.getLabel(2));
        Assert.assertEquals("missing", l2.getLabel(3));
        Assert.assertEquals("erem", l2.getLabel(4));
    }

    @Test
    public void testUpdateValue() {

        VFilter vf = VFUpdateValue.with(x -> {
            if (Double.isNaN(x))
                return 0.0;
            return (x > 0) ? (x * x) : (-x * x);
        });

        Var x = SolidVarDouble.wrap(0, Double.NaN, 1, Double.NaN, -12, 3.1);

        Var y = x.solidCopy().fitApply(vf);
        Assert.assertEquals(0, y.getDouble(0), 1e-20);
        Assert.assertEquals(0, y.getDouble(1), 1e-20);
        Assert.assertEquals(1, y.getDouble(2), 1e-20);
        Assert.assertEquals(0, y.getDouble(3), 1e-20);
        Assert.assertEquals(-144, y.getDouble(4), 1e-20);
        Assert.assertEquals(3.1 * 3.1, y.getDouble(5), 1e-20);
    }

    @Test
    public void testUpdateIndex() {

        VFilter vf = VFUpdateIndex.with(x -> {
            if (x == Integer.MIN_VALUE)
                return 0;
            return (x > 0) ? (x * x) : (-x * x);
        });

        Var x = SolidVarDouble.wrap(0, Double.NaN, 1, Double.NaN, -12, 3);

        Var y = x.solidCopy().fitApply(vf);
        Assert.assertEquals(0, y.getDouble(0), 1e-20);
        Assert.assertEquals(0, y.getDouble(1), 1e-20);
        Assert.assertEquals(1, y.getDouble(2), 1e-20);
        Assert.assertEquals(0, y.getDouble(3), 1e-20);
        Assert.assertEquals(-144, y.getDouble(4), 1e-20);
        Assert.assertEquals(9, y.getDouble(5), 1e-20);
    }

    @Test
    public void testUpdateLabel() {

        Var l1 = VarNominal.copy("ana", "?", "are", "?", "mere");
        Var l2 = l1.fitApply(VFUpdateLabel.with(l -> {
            if (l.equals("?")) {
                return "missing";
            }

            char[] msg = l.toCharArray();
            for (int i = 0; i < msg.length / 2; i++) {
                char tmp = msg[i];
                msg[i] = msg[msg.length - i - 1];
                msg[msg.length - i - 1] = tmp;
            }
            return String.copyValueOf(msg);
        }));

        Assert.assertEquals("ana", l2.getLabel(0));
        Assert.assertEquals("missing", l2.getLabel(1));
        Assert.assertEquals("era", l2.getLabel(2));
        Assert.assertEquals("missing", l2.getLabel(3));
        Assert.assertEquals("erem", l2.getLabel(4));
    }
}
