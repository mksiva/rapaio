/*
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 *    Copyright 2013 Aurelian Tutuianu
 *    Copyright 2014 Aurelian Tutuianu
 *    Copyright 2015 Aurelian Tutuianu
 *    Copyright 2016 Aurelian Tutuianu
 *    Copyright 2017 Aurelian Tutuianu
 *    Copyright 2018 Aurelian Tutuianu
 *    Copyright 2019 Aurelian Tutuianu
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

package rapaio.ml.regression.simple;

import rapaio.core.distributions.*;
import rapaio.data.*;
import rapaio.ml.common.*;
import rapaio.ml.regression.*;
import rapaio.printer.*;

/**
 * A trivial regression which predicts using random
 * values provided by a distribution.
 * <p>
 * User: Aurelian Tutuianu <padreati@yahoo.com>
 */
public class RandomValueRegression extends AbstractRegression implements Printable {

    private static final long serialVersionUID = 819192240406617594L;

    public static RandomValueRegression create() {
        return new RandomValueRegression(Uniform.of(0, 1));
    }

    public static RandomValueRegression create(Distribution distribution) {
        return new RandomValueRegression(distribution);
    }

    private final Distribution distribution;

    private RandomValueRegression(Distribution distribution) {
        this.distribution = distribution;
    }

    @Override
    public Regression newInstance() {
        return new RandomValueRegression(distribution);
    }

    @Override
    public String name() {
        return "RandomValueRegression";
    }

    @Override
    public String fullName() {
        return name() + String.format("(distribution:%s)", distribution.name());
    }

    @Override
    public Capabilities capabilities() {
        return new Capabilities()
                .withInputCount(0, 1_000_000)
                .withTargetCount(1, 1)
                .withInputTypes(VType.DOUBLE, VType.BINARY, VType.INT, VType.NOMINAL, VType.LONG, VType.TEXT)
                .withTargetTypes(VType.DOUBLE)
                .withAllowMissingInputValues(true)
                .withAllowMissingTargetValues(true);
    }

    public Distribution distribution() {
        return distribution;
    }

    @Override
    protected boolean coreFit(Frame df, Var weights) {
        return true;
    }

    @Override
    protected RPrediction corePredict(final Frame df, final boolean withResiduals) {
        RPrediction pred = RPrediction.build(this, df, withResiduals);
        for (String targetName : targetNames()) {
            pred.fit(targetName).stream().forEach(s -> s.setDouble(distribution.sampleNext()));
        }
        pred.buildComplete();
        return pred;
    }

    @Override
    public String toString() {
        return fullName();
    }

    @Override
    public String content() {
        StringBuilder sb = new StringBuilder();
        sb.append(headerSummary());
        if (isFitted()) {
            sb.append("Model is trained.\n");
        } else {
            sb.append("Model is not trained.\n");
        }
        return sb.toString();
    }

    @Override
    public String fullContent() {
        return content();
    }

    @Override
    public String summary() {
        return content();
    }
}
