/*
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 *    Copyright 2013 Aurelian Tutuianu
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
 */

package rapaio.core;

import org.junit.Test;
import rapaio.core.stat.Mean;
import rapaio.data.Frame;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:padreati@yahoo.com">Aurelian Tutuianu</a>
 */
public class MeanTest extends CoreStatTestUtil {

    public MeanTest() throws IOException, URISyntaxException {
    }

    @Test
    public void testRReferenceMean() throws IOException {
        Frame df = getDataFrame();
        assertEquals(Double.valueOf("999.98132402093892779"), new Mean(df.col(0)).getValue(), 1e-12);
    }
}
