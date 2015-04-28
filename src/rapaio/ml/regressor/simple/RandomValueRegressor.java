/*
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 *    Copyright 2013 Aurelian Tutuianu
 *    Copyright 2014 Aurelian Tutuianu
 *    Copyright 2015 Aurelian Tutuianu
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

package rapaio.ml.regressor.simple;

import rapaio.core.distributions.Distribution;
import rapaio.core.distributions.Uniform;
import rapaio.data.Frame;
import rapaio.data.Var;
import rapaio.ml.regressor.AbstractRegressor;
import rapaio.ml.regressor.Regressor;
import rapaio.ml.regressor.RegressorFit;

/**
 * User: Aurelian Tutuianu <padreati@yahoo.com>
 */
public class RandomValueRegressor extends AbstractRegressor {
    private Distribution distribution = new Uniform(0, 1);

    @Override
    public Regressor newInstance() {
        return new RandomValueRegressor();
    }

    @Override
    public String name() {
        return "RandomValueRegressor";
    }

    @Override
    public String fullName() {
        return name() + String.format("(%s)", distribution.name());
    }

    public Distribution distribution() {
        return distribution;
    }

    public RandomValueRegressor withDistribution(final Distribution distribution) {
        this.distribution = distribution;
        return this;
    }

    @Override
    public void learn(Frame df, Var weights, String... targetVarNames) {
        prepareLearning(df, weights, targetVarNames);
    }

    @Override
    public RegressorFit predict(final Frame df, final boolean withResiduals) {
        RegressorFit pred = RegressorFit.newEmpty(this, df, withResiduals);
        for (String targetName : targetNames()) {
            pred.addTarget(targetName);
            pred.fit(targetName).stream().forEach(s -> s.setValue(distribution.sampleNext()));
        }
        pred.buildComplete();
        return pred;
    }
}
