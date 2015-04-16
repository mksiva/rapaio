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

package rapaio.ml.classifier.ensemble;

import rapaio.core.sample.Sampler;
import rapaio.ml.classifier.Classifier;
import rapaio.ml.classifier.ensemble.impl.BaggingMode;
import rapaio.ml.classifier.ensemble.impl.CEnsemble;
import rapaio.ml.classifier.tree.CTree;
import rapaio.ml.common.VarSelector;

/**
 * Breiman random forest implementation.
 * <p>
 * Created by <a href="mailto:padreati@yahoo.com">Aurelian Tutuianu</a> on 4/16/15.
 */
public class CRForest extends CEnsemble {

    private static final long serialVersionUID = 5249758525162975861L;

    public CRForest() {
        this.runs = 10;
        this.baggingMode = BaggingMode.VOTING;
        this.c = CTree.newCART().withVarSelector(VarSelector.AUTO);
        this.oobComp = false;
        this.sampler = new Sampler.Bootstrap(1);
    }

    @Override
    public String name() {
        return "CRForest";
    }

    @Override
    public String fullName() {
        StringBuilder sb = new StringBuilder();
        sb.append(name());
        sb.append(" {");
        sb.append("runs:").append(runs).append(",");
        sb.append("baggingMode:").append(baggingMode.name()).append(",");
        sb.append("oob:").append(oobComp).append(",");
        sb.append("sampler:").append(sampler.name()).append(",");
        sb.append("tree:").append(c.fullName()).append("");
        sb.append("}");
        return sb.toString();
    }

    @Override
    public Classifier newInstance() {
        return new CRForest()
                .withRuns(runs)
                .withBaggingMode(baggingMode)
                .withClassifier((CTree) c)
                .withSampler(sampler);
    }

    @Override
    public CRForest withRuns(int runs) {
        this.runs = runs;
        return this;
    }

    @Override
    public CRForest withOobComp(boolean oobCompute) {
        return (CRForest) super.withOobComp(oobCompute);
    }

    public CRForest withBaggingMode(BaggingMode baggingMode) {
        return (CRForest) super.withBaggingMode(baggingMode);
    }

    public CRForest withBootstrap() {
        return withSampler(new Sampler.Bootstrap(1));
    }

    public CRForest withBootstrap(double p) {
        return withSampler(new Sampler.Bootstrap(p));
    }

    public CRForest withNoSampling() {
        return withSampler(new Sampler.Identity());
    }

    @Override
    public CRForest withSampler(Sampler sampler) {
        return (CRForest) super.withSampler(sampler);
    }

    public CRForest withClassifier(CTree c) {
        this.c = c;
        return this;
    }

    public CRForest withMCols() {
        if (c instanceof CTree) {
            ((CTree) c).withMCols();
        }
        return this;
    }

    public CRForest withMCols(int mcols) {
        if (c instanceof CTree) {
            ((CTree) c).withMCols(mcols);
        }
        return this;
    }

    public CRForest withVarSelector(VarSelector varSelector) {
        if (c instanceof CTree) {
            ((CTree) c).withVarSelector(varSelector);
        }
        return this;
    }
}